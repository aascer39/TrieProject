package org.example;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
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

    private String[] words;  // 当前插入的单词
    private int currentCharIndex = 0;  // 当前插入的位置
    private int currentWordIndex = 0;
    private final double startX = 700;
    private final double startY = 100;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        Scene scene = new Scene(pane, 1400, 850);
        stage.setTitle("键树算法展示");
        stage.setScene(scene);

        TextField textField = new TextField();
        textField.setPromptText("请输入单词，以空格分隔");
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

        // 设置插入按钮及位置
        Button insertButton = new Button("插入下一个单词");
        insertButton.setLayoutX(700);
        insertButton.setLayoutY(700);
        insertButton.setOnAction(event -> {
            Trie.TrieNode LCA = trie.findLCA(words[currentWordIndex].toLowerCase());
            trie.insert(words[currentWordIndex].toLowerCase());
            insertStringWithAnimation(words[currentWordIndex].toLowerCase(), LCA);
            currentWordIndex++;
        });

        pane.getChildren().add(insertButton);

        // 默认展示占位根节点"/"
        drawRootNode();

        stage.show();

        textField.requestFocus();
    }

    // 默认绘制根节点 "/"
    private void drawRootNode() {
        Circle circle = new Circle(startX, startY, 20);
        circle.setFill(Color.LIGHTGRAY);  // 根节点颜色
        Text text = new Text(startX - 5, startY + 5, "/");
        text.setFill(Color.BLACK);
        pane.getChildren().addAll(circle, text);
    }


    private void insertStringWithAnimation(String w, Trie.TrieNode LCA) {
        Trie.TrieNode cur = LCA;

        if (LCA.data =='/'){
            System.out.println("这是根节点/");
        }

        int i = LCA.level;
        char c = w.charAt(i);
        int index = c - 'a';
        cur = cur.children[index];
        System.out.println("公共祖先"+LCA.data+"下面第一个字符"+cur.data);

        for (i = LCA.level; i < w.length(); i++) {
            // 检查字符是否是有效的字母（a-z）
            if (index < 0 || index >= 26) {
                System.out.println("字符 '" + c + "' 无效，跳过该字符。");
                continue;  // 如果字符不在 a-z 范围内，则跳过
            }

            int j = 0;
            double newX = cur.calculateX(cur, j);
            double newY = cur.calculateY(cur);
            System.out.println("将会把字符"+c+"放在X: "+newX+" Y: "+newY);

            // 碰撞检测并调整位置
            while (trie.isPositionOverlap(newX, newY)) {
                System.out.println("位置重叠，尝试新的位置,重叠位置为：X: "+newX+" Y: "+newY);
                j++;  // 增加偏移量，尝试新的位置
                newX = cur.calculateX(cur, j);
            }

            cur.setX(cur, newX);
            cur.setY(cur, newY);

            // 添加新节点位置到集合中
            trie.nodePositions.add(new NodePosition(newX, newY));

            drawNodeWithAnimation(newX, newY, c);

            // 确保如果 cur.parent 为 null，使用当前节点的坐标
            double parentX = (cur.parent != null) ? cur.parent.getX(cur.parent) : newX;
            double parentY = (cur.parent != null) ? cur.parent.getY(cur.parent) : newY;

            drawEdge(parentX, parentY, newX, newY);

            // 检查是否存在子节点
            if (i + 1 < w.length()) {
                c = w.charAt(i + 1);
                index = c - 'a';
                if (cur.children[index] != null) {
                    cur = cur.children[index];
                } else {
                    System.out.println("字符 '" + c + "' 的子节点为 null，结束插入。");
                    break;
                }
            }
        }
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
