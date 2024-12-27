package org.example;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

public class TrieVisualization extends Application {

    private final Pane pane = new Pane();
    private final Trie trie = new Trie();
    private final Button insertButton = new Button("插入下一个字符");
    private String[] words;  // 当前插入的单词
    private int currentCharIndex = 0;  // 当前插入的位置
    private int currentWordIndex = 0;
    private double startX = 700, startY = 100;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        Scene scene = new Scene(pane, 1400, 850);
        stage.setTitle("键树算法展示");
        stage.setScene(scene);

        TextField textField = new TextField();
        textField.setPromptText("请输入要插入的字符串");
        textField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                words = textField.getText().split(" ");
                currentWordIndex = 0;  // 每次按下Enter时，重置字符串索引
                currentCharIndex = 0;  // 重置字符索引
            }
        });

        //设置TextField展示位置
        textField.setLayoutX(200);
        textField.setLayoutY(50);

        // 将TextField添加到pane中
        pane.getChildren().add(textField);

        // 设置按钮位置
        insertButton.setLayoutX(700);
        insertButton.setLayoutY(700);
        insertButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                insertNextCharacter();
            }
        });

        pane.getChildren().add(insertButton);

        // 默认展示占位根节点"/"
        drawRootNode();

        stage.show();

        textField.requestFocus();
    }

    // 默认绘制根节点 "/"
    private void drawRootNode() {
        Circle circle = new Circle(startX, 100, 20);
        circle.setFill(Color.LIGHTGRAY);  // 根节点颜色
        Text text = new Text(startX - 5, startY + 5, "/");
        text.setFill(Color.BLACK);
        pane.getChildren().addAll(circle, text);
    }

    // 插入下一个字符
    private void insertNextCharacter() {
        if (currentWordIndex < words.length) {
            String currentWord = words[currentWordIndex];
            if (currentCharIndex < currentWord.length()) {
                char c = currentWord.charAt(currentCharIndex);
                insertStringWithAnimation(c, currentCharIndex == 0 ? '/' : currentWord.charAt(currentCharIndex - 1));
                currentCharIndex++;
            }
            if (currentCharIndex >= currentWord.length()) {
                currentCharIndex = 0;
                currentWordIndex++;
            }
        }
    }

    private void insertStringWithAnimation(char c, char lastChar) {
        Trie.TrieNode cur = trie.root;
        double firstX = 100;
        double x , y = startY + 100 + currentCharIndex * 100;  // 当前节点的坐标
        double lastX = startX, lastY = startY;  // 上一个节点的坐标

        if (lastChar != '/') {
            // 如果不是根节点，则计算上一节点的位置
            lastX = firstX + (lastChar - 'a') * 50;  // 上一节点的x坐标
            lastY = startY + 100 + (currentCharIndex - 1) * 100;  // 上一节点的y坐标
        }

        // 计算当前字符的索引
        int index = c - 'a';  // 将字符转为数组索引

        // 如果字符不在 'a' 到 'z' 范围内，直接返回
        if (index < 0 || index >= 26) {
            System.out.println("无效字符：" + c);
            return;
        }

        Trie.TrieNode nextNode = cur.children[index];
        x = firstX + index * 50;  // 计算插入的x位置

        // 检测该位置是否有元素，如果没有，创建新节点
        if (nextNode == null) {
            nextNode = trie.new TrieNode(c);
            cur.children[index] = nextNode;
        } else {
            // 如果该位置已有节点，检测字符是否相同
            if (nextNode.data != c) {
                System.out.println("该位置已有不同字符的节点");
                return;  // 如果是不同字符，返回避免插入
            }
        }

        // 绘制连线
        drawEdge(lastX, lastY, x, y);

        // 绘制节点
        drawNodeWithAnimation(x, y, c);

        // 更新当前节点
        cur = nextNode;
        cur.isEndingChar = true;
    }

    private void drawNodeWithAnimation(double x, double y, char value) {
        Circle circle = new Circle(x, y, 20);
        circle.setFill(Color.LIGHTBLUE);
        Text text = new Text(x - 5, y + 5, String.valueOf(value));
        text.setFill(Color.BLACK);
        pane.getChildren().addAll(circle, text);

        // 动画效果：逐渐改变颜色
        Timeline timeline = new Timeline();
        KeyFrame keyFrame = new KeyFrame(Duration.seconds(0.5), event -> {
            circle.setFill(Color.DEEPSKYBLUE);
        });
        timeline.getKeyFrames().add(keyFrame);
        timeline.play();
    }


    private void drawEdge(double lastX, double lastY, double x, double y) {
        Line line = new Line(lastX, lastY, x, y);  // 连接父节点和当前节点
        line.setStroke(Color.BLACK);  // 连线颜色
        pane.getChildren().add(line);
    }
}
