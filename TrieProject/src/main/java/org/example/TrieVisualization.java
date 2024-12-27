package org.example;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

public class TrieVisualization extends Application {

    private final Pane pane = new Pane();
    private final Trie trie = new Trie();
    private final Button insertButton = new Button("插入下一个字符");
    private String[] words = {"apple", "hello"};  // 当前插入的单词
    private int currentCharIndex = 0;  // 当前插入的位置
    private int currentWordIndex = 0;
    private double startX = 700, startY = 100;
    private double offsetX = 50, offsetY = 100; // 节点间距

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        Scene scene = new Scene(pane, 1400, 850);
        stage.setTitle("键树算法展示");
        stage.setScene(scene);

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
    }

    // 默认绘制根节点 "/"
    private void drawRootNode() {
        double x = 700, y = 50;  // 根节点稍微上方
        Circle circle = new Circle(x, y, 20);
        circle.setFill(Color.LIGHTGRAY);  // 根节点颜色
        Text text = new Text(x - 5, y + 5, "/");
        text.setFill(Color.BLACK);
        pane.getChildren().addAll(circle, text);
    }

    // 插入下一个字符
    private void insertNextCharacter() {
        if (currentWordIndex<words.length){
            String currentWord = words[currentWordIndex];
            if (currentCharIndex < currentWord.length()) {
                char c = currentWord.charAt(currentCharIndex);
                insertStringWithAnimation(String.valueOf(c));
                currentCharIndex++;
            }
            if (currentCharIndex >= currentWord.length()) {
                currentCharIndex = 0;
                currentWordIndex++;
            }
        }
    }

    private void insertStringWithAnimation(String character) {
        Trie.TrieNode cur = trie.root;
        double x = startX, y = startY + currentWordIndex * offsetY;

        int index = character.charAt(0) - 'a';  // 将字符转为数组索引
        Trie.TrieNode nextNode = cur.children[index];

        // 检测该位置是否有元素，如果没有，创建新节点
        if (nextNode == null) {
            nextNode = trie.new TrieNode(character.charAt(0));
            cur.children[index] = nextNode;
        } else {
            // 如果该位置已有节点，检测字符是否相同
            if (nextNode.data != character.charAt(0)) {
                System.out.println("该位置已有不同字符的节点");
                return;  // 如果是不同字符，返回避免插入
            }
        }

        // 检测节点是否重叠，如果重叠则稍微调整位置
        while (isPositionOccupied(x, y)) {
            x += offsetX; // 如果重叠了，调整位置
            y += offsetY;
        }

        // 绘制节点
        drawNodeWithAnimation(x, y, character.charAt(0));

        // 更新当前节点
        cur = nextNode;
        cur.isEndingChar = true;
    }

    // 检查给定位置(x, y)是否已经有节点
    private boolean isPositionOccupied(double x, double y) {
        for (javafx.scene.Node node : pane.getChildren()) {
            if (node instanceof Circle) {
                Circle circle = (Circle) node;
                double nodeX = circle.getCenterX();
                double nodeY = circle.getCenterY();
                // 判断当前位置与已存在节点的位置是否接近，避免重叠
                if (Math.abs(nodeX - x) < 30 && Math.abs(nodeY - y) < 30) {
                    return true;  // 认为重叠
                }
            }
        }
        return false;  // 没有重叠
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

}
