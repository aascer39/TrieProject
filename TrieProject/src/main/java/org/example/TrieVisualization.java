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
    private final Button insertButton = new Button("Insert Next Letter");
    private String currentWord = "hello";  // 当前插入的单词
    private int currentCharIndex = 0;  // 当前插入的位置
    private double startX = 400, startY = 50;
    private double offsetX = 50, offsetY = 100; // 节点间距

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        Scene scene = new Scene(pane, 800, 600);
        primaryStage.setTitle("Trie Visualization");
        primaryStage.setScene(scene);

        // 设置按钮位置
        insertButton.setLayoutX(350);
        insertButton.setLayoutY(550);
        insertButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                insertNextCharacter();
            }
        });

        pane.getChildren().add(insertButton);

        // 默认展示根节点"/"
        drawRootNode();

        primaryStage.show();
    }

    // 默认绘制根节点 "/"
    private void drawRootNode() {
        double x = startX, y = startY - 50;  // 根节点稍微上方
        Circle circle = new Circle(x, y, 20);
        circle.setFill(Color.LIGHTGRAY);  // 根节点颜色
        Text text = new Text(x - 5, y + 5, "/");
        text.setFill(Color.BLACK);
        pane.getChildren().addAll(circle, text);
    }

    // 插入下一个字符
    private void insertNextCharacter() {
        if (currentCharIndex < currentWord.length()) {
            char c = currentWord.charAt(currentCharIndex);
            insertStringWithAnimation(String.valueOf(c));
            currentCharIndex++;
        }
    }

    private void insertStringWithAnimation(String character) {
        Trie.TrieNode cur = trie.root;
        double x = startX + currentCharIndex * offsetX, y = startY + currentCharIndex * offsetY;

        int index = character.charAt(0) - 'a';
        if (cur.children[index] == null) {
            cur.children[index] = trie.new TrieNode(character.charAt(0));
        }
        cur = cur.children[index];

        // 无论是否已经存在，始终绘制节点
        drawNodeWithAnimation(x, y, character.charAt(0));

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
}
