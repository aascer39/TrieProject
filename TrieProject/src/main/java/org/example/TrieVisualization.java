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
    Circle circle = null;
    Text promptText = new Text();
    Text resultText = new Text();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        // 创建场景，设置面板大小
        Scene scene = new Scene(pane, 1400, 850);
        stage.setTitle("键树算法展示");
        stage.setScene(scene);

        //加载CSS文件
        scene.getStylesheets().add(getClass().getResource("/TrieGUIStyle.css").toExternalForm());

        // 创建TextField输入框
        TextField textField = new TextField();
        textField.setPromptText("请输入单词，以空格分隔");
        textField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                words = textField.getText().split(" ");
                currentWordIndex = 0;  // 每次按下Enter时，重置字符串索引
                currentCharIndex = 0;  // 重置字符索引
            }
        });

        // 设置TextField展示位置
        textField.setLayoutX(200);
        textField.setLayoutY(50);

        //设置TextField的样式
        textField.getStyleClass().add("text-field");

        // 将TextField添加到pane中
        pane.getChildren().add(textField);

        // 初始化 text1 和 text2，并设置它们的位置
        promptText = new Text();
        resultText = new Text();

        // 设置提示文本的位置
        promptText.setLayoutX(1200);
        promptText.setLayoutY(50);

        // 设置结果文本的位置
        resultText.setLayoutX(1200);
        resultText.setLayoutY(100);

        // 设置插入按钮及位置
        Button insertButton = new Button("插入下一个字符串");
        insertButton.setLayoutX(200);
        insertButton.setLayoutY(100);
        insertButton.setOnAction(event -> {
            // 清除之前的文本提示
            pane.getChildren().remove(promptText);
            pane.getChildren().remove(resultText);

            String w = words[currentWordIndex].toLowerCase();
            Trie.TrieNode LCA = trie.findLCA(w);

            // 判断字符串是否已插入
            if (LCA.data != '/' && LCA.level == w.length()) {
                LCA.getCircle().setFill(Color.RED);  // 高亮显示
                resultText = new Text("字符串" + w + "已插入");
                resultText.setLayoutX(1200);
                resultText.setLayoutY(100);
                resultText.getStyleClass().add("text");
                pane.getChildren().add(resultText);
            }

            trie.insert(w);  // 插入字符串

            // 更新正在插入文本
            promptText = new Text("正在插入字符串：" + words[currentWordIndex]);
            promptText.setLayoutX(1200);
            promptText.setLayoutY(50);
            promptText.getStyleClass().add("text");
            pane.getChildren().add(promptText);

            // 执行插入字符串的动画
            insertStringWithAnimation(w, LCA);


            currentWordIndex++;  // 更新字符串索引
        });

        // 设置查找按钮及位置
        Button searchButton = new Button("查找该字符串");
        searchButton.setLayoutX(200);
        searchButton.setLayoutY(150);
        searchButton.setOnAction(event -> {
            // 清除之前的文本提示
            pane.getChildren().remove(promptText);
            pane.getChildren().remove(resultText);

            // 获取当前字符串并设置正在查找的文本
            String w = words[currentWordIndex].toLowerCase();
            promptText = new Text("正在查找字符串：" + w);
            promptText.setLayoutX(1200);
            promptText.setLayoutY(50);
            promptText.getStyleClass().add("text");
            pane.getChildren().add(promptText);

            // 执行查找字符串并展示动画
            searchStringWithAnimation(w);

            currentCharIndex = 0;
            currentWordIndex++;  // 更新字符串索引
        });

        // 将插入按钮和查找按钮添加到界面
        pane.getChildren().add(insertButton);
        pane.getChildren().add(searchButton);

        // 默认展示占位根节点"/"
        drawRootNode();
        stage.show();

        // 设置焦点到TextField
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

    //插入字符串演示动画
    private void insertStringWithAnimation(String w, Trie.TrieNode LCA) {
        Trie.TrieNode cur = LCA;
        int i = LCA.level;

        // 创建一个Timeline来依次插入字符
        Timeline timeline = new Timeline();

        while (i < w.length()) {
            char c = w.charAt(i);
            int index = c - 'a';

            // 检查字符是否是有效的字母（a-z）
            if (index < 0 || index >= 26) {
                System.out.println("字符 '" + c + "' 无效，跳过该字符。");
                i++; // 移动到下一个字符
                continue; // 如果字符不在 a-z 范围内，则跳过
            }

            // 检查是否存在子节点
            Trie.TrieNode nextNode = cur.children[index];
            if (nextNode == null) {
                System.out.println("字符 '" + c + "' 的子节点为 null，结束插入。");
                break;
            }

            cur = nextNode;
            i++; // 移动到下一个字符

            // 计算新节点的位置
            int j = 0;
            double newX = cur.calculateX(cur, j);
            double newY = cur.calculateY(cur);

            // 碰撞交叉检测并调整位置
            while (true) {
                if (trie.isPositionOverlap(newX, newY)) {
                    System.out.println("位置重叠，调整位置");
                    j++;
                    newX = cur.calculateX(cur, j);
                } else {
                    System.out.println("位置不会重叠，检查即将的连线的交叉情况");
                    while (trie.isPositionOverlap(newX, newY) || trie.checkForIntersectingEdges(new Trie.LineSegment(new NodePosition(cur.parent.getX(cur.parent), cur.parent.getY(cur.parent)), new NodePosition(newX, newY)))) {
                        System.out.println("连线交叉，调整位置");//检查交叉的方法貌似有点bug
                        j--;
                        newX = cur.calculateX(cur, j);
                    }
                    System.out.println("连线不会交叉，将会把字符" + c + "放在X: " + newX + " Y: " + newY);
                    break;
                }
            }

            // 添加新节点位置到集合中
            trie.nodePositions.add(new NodePosition(newX, newY));

            // 将新的连线添加到集合中
            trie.addEdge(cur.parent, cur);

            cur.setX(cur, newX);
            cur.setY(cur, newY);

            // 动画效果：每次插入后暂停0.5秒再插入下一个字符
            double finalNewX = newX;
            Trie.TrieNode finalCur = cur;
            KeyFrame keyFrame = new KeyFrame(Duration.seconds((i - LCA.level) * 0.8), event -> {
                drawNodeWithAnimation(finalNewX, newY, c, finalCur);  // 绘制节点
                // 绘制连线
                double parentX = finalCur.parent.getX(finalCur.parent);
                double parentY = finalCur.parent.getY(finalCur.parent);
                drawEdge(parentX, parentY, finalNewX, newY);
            });
            KeyFrame keyFrame1 = new KeyFrame(Duration.seconds((w.length() - LCA.level) * 0.8 + 0.5), event -> {
                resultText.setText("字符串" + w + "插入成功");
                resultText.setLayoutX(1200);
                resultText.setLayoutY(100);
                resultText.getStyleClass().add("text");
                pane.getChildren().add(resultText);
            });
            timeline.getKeyFrames().add(keyFrame);
            timeline.getKeyFrames().add(keyFrame1);
        }

        // 开始执行动画
        timeline.play();
    }

    // 查找字符串演示动画
    private void searchStringWithAnimation(String word) {
        Trie.TrieNode current = trie.root;  // Start from the root node
        int wordLength = word.length();
        Timeline timeline = new Timeline();

        for (int i = 0; i < wordLength; i++) {
            char c = word.charAt(i);
            int index = c - 'a';

            if (index < 0 || index >= 26) {
                System.out.println("Invalid character '" + c + "', skipping.");
                break;
            }

            Trie.TrieNode nextNode = current.children[index];
            if (nextNode == null) {
                KeyFrame keyFrame = new KeyFrame(Duration.seconds(i * 1.5), event -> {
                    System.out.println("Search failed, string " + word + " does not exist.");
                });
                timeline.getKeyFrames().add(keyFrame);
                break;
            }

            final Trie.TrieNode node = nextNode;
            Trie.TrieNode finalCurrent1 = current;
            KeyFrame keyFrame = new KeyFrame(Duration.seconds(i * 1.5), event -> {
//                drawHighlightCircle(finalCurrent1);
                node.getCircle().setFill(Color.YELLOW);
                Timeline restoreColorTimeline = new Timeline();
                KeyFrame restoreColorKeyFrame = new KeyFrame(Duration.seconds(0.1), restoreEvent -> {
                    if (finalCurrent1.isEndingChar) {
                        finalCurrent1.getCircle().setFill(Color.RED);
                    } else {
                        finalCurrent1.getCircle().setFill(Color.DEEPSKYBLUE);
                    }
                });
                restoreColorTimeline.getKeyFrames().add(restoreColorKeyFrame);
                restoreColorTimeline.play();

            });
            timeline.getKeyFrames().add(keyFrame);
            current = nextNode;
        }

        Trie.TrieNode finalCurrent = current;
        KeyFrame lastKeyFrame = new KeyFrame(Duration.seconds(wordLength * 1.6), event -> {
            if (finalCurrent.isEndingChar) {
                finalCurrent.getCircle().setFill(Color.GREEN);
                System.out.println("String " + word + " exists.");
            } else {
                finalCurrent.getCircle().setFill(Color.ORANGE);
                System.out.println("String " + word + " does not exist.");
            }
            // 显示查找结果
            if (trie.findString(word)) {
                resultText = new Text("字符串" + word + "存在");
            } else {
                resultText = new Text("字符串" + word + "不存在");
            }
            resultText.setLayoutX(1200);
            resultText.setLayoutY(100);
            resultText.getStyleClass().add("text");
            pane.getChildren().add(resultText);

            // Restore the original color after 2 seconds
            Timeline restoreColorTimeline = new Timeline();
            KeyFrame restoreColorKeyFrame = new KeyFrame(Duration.seconds(2), restoreEvent -> {
                if (finalCurrent.isEndingChar) {
                    finalCurrent.getCircle().setFill(Color.RED);
                } else {
                    finalCurrent.getCircle().setFill(Color.DEEPSKYBLUE);
                }
            });
            restoreColorTimeline.getKeyFrames().add(restoreColorKeyFrame);
            restoreColorTimeline.play();
        });
        timeline.getKeyFrames().add(lastKeyFrame);

        timeline.play();
    }

//    // 遍历节点并绘制黄圈，1秒后移除
//    private void drawHighlightCircle(Trie.TrieNode node) {
//        // 创建黄圈
//        double x = node.getX(node);
//        double y = node.getY(node);
//        Circle highlightCircle = new Circle(x, y, 20);
//        highlightCircle.setFill(Color.YELLOW);
//        highlightCircle.setStroke(Color.BLACK);  // 可选：设置边框颜色
//        char value = node.data;
//        Text text = new Text(x - 5, y + 5, String.valueOf(value));
//        text.setFill(Color.BLACK);
//        if (circle != null && text != null) {
//            pane.getChildren().addAll(circle, text);
//        }
//
//        // 将黄圈添加到 pane 中
//        pane.getChildren().add(highlightCircle);
//
//        // 创建动画效果：1秒后移除黄圈
//        Timeline timeline = new Timeline();
//        KeyFrame keyFrame = new KeyFrame(Duration.seconds(1), event -> {
//            pane.getChildren().remove(highlightCircle);  // 移除黄圈
//        });
//        timeline.getKeyFrames().add(keyFrame);
//        timeline.play();  // 播放动画
//    }

    private void drawNodeWithAnimation(double x, double y, char value, Trie.TrieNode current) {
        Circle circle = new Circle(x, y, 20);
        circle.setFill(Color.LIGHTBLUE);
        Text text = new Text(x - 5, y + 5, String.valueOf(value));
        text.setFill(Color.BLACK);
        if (circle != null && text != null) {
            pane.getChildren().addAll(circle, text);
        }

        // 将 Circle 存储到 TrieNode 中
        current.setCircle(circle);

        // 动画效果：逐渐改变颜色
        Timeline timeline = new Timeline();
        KeyFrame keyFrame1 = new KeyFrame(Duration.seconds(0.5), event -> {
            if (current.isEndingChar) {
                circle.setFill(Color.RED);
            } else {
                circle.setFill(Color.DEEPSKYBLUE);
            }
        });
        timeline.getKeyFrames().add(keyFrame1);
        timeline.play();
    }

    private void drawEdge(double parentX, double parentY, double x, double y) {
        // 计算父节点的外部位置（偏移一定的距离）
        double parentRadius = 20;  // 假设圆的半径为20
        double angle = Math.atan2(y - parentY, x - parentX);  // 计算父节点到子节点的角度

        // 计算从圆外的起始点（通过偏移圆心位置）
        double startX = parentX + parentRadius * Math.cos(angle);
        double startY = parentY + parentRadius * Math.sin(angle);
        double endX = x - parentRadius * Math.cos(angle);
        double endY = y - parentRadius * Math.sin(angle);
        Line line = new Line(startX, startY, endX, endY);  // 连接父节点和当前节点
        line.setStroke(Color.BLACK);  // 连线颜色
        pane.getChildren().add(line);
        System.out.println("绘制连线: (" + parentX + ", " + parentY + ") -> (" + x + ", " + y + ")");
    }
}