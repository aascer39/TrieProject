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

import java.util.ArrayList;
import java.util.List;

public class TrieVisualization extends Application {

    private final Pane pane = new Pane();
    private final Trie trie = new Trie();

    private String[] words;  // 当前要插入的单词
    private int currentCharIndex = 0;  // 当前字符索引
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
        // 创建场景并设置面板大小
        Scene scene = new Scene(pane, 1400, 850);
        stage.setTitle("Trie 算法可视化");
        stage.setScene(scene);

        // 加载 CSS 文件
        scene.getStylesheets().add(getClass().getResource("/TrieGUIStyle.css").toExternalForm());

        // 创建输入框
        TextField textField = new TextField();
        textField.setPromptText("输入用空格分隔的单词");
        textField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                words = textField.getText().split(" ");
                currentWordIndex = 0;  // 按下回车键时重置单词索引
                currentCharIndex = 0;  // 重置字符索引
            }
        });

        // 设置输入框位置
        textField.setLayoutX(200);
        textField.setLayoutY(50);

        // 设置输入框样式
        textField.getStyleClass().add("text-field");

        // 将输入框添加到面板
        pane.getChildren().add(textField);

        // 初始化提示文本和结果文本，并设置它们的位置
        promptText = new Text();
        resultText = new Text();

        // 设置提示文本位置
        promptText.setLayoutX(1000);
        promptText.setLayoutY(50);

        // 设置结果文本位置
        resultText.setLayoutX(1000);
        resultText.setLayoutY(100);

        // 设置插入按钮及其位置
        Button insertButton = new Button("插入下一个单词");
        insertButton.setLayoutX(200);
        insertButton.setLayoutY(100);
        insertButton.setOnAction(event -> {
            // 清除之前的提示文本
            pane.getChildren().remove(promptText);
            pane.getChildren().remove(resultText);

            String w = words[currentWordIndex].toLowerCase();
            Trie.TrieNode LCA = trie.findLCA(w);

            // 检查单词是否已插入
            if (LCA.data != '/' && LCA.level == w.length()) {
                LCA.getCircle().setFill(Color.RED);  // 高亮显示
                resultText = new Text("单词 " + w + " 已经插入");
                resultText.setLayoutX(1000);
                resultText.setLayoutY(100);
                resultText.getStyleClass().add("text");
                pane.getChildren().add(resultText);
            }

            trie.insert(w);  // 插入单词

            // 更新插入提示文本
            promptText = new Text("正在插入单词: " + words[currentWordIndex]);
            promptText.setLayoutX(1000);
            promptText.setLayoutY(50);
            promptText.getStyleClass().add("text");
            pane.getChildren().add(promptText);

            // 执行插入动画
            insertStringWithAnimation(w, LCA);

            currentWordIndex++;  // 更新单词索引
        });

        // 设置搜索按钮及其位置
        Button searchButton = new Button("搜索下一个单词");
        searchButton.setLayoutX(200);
        searchButton.setLayoutY(170);
        searchButton.setOnAction(event -> {
            // 清除之前的提示文本
            pane.getChildren().remove(promptText);
            pane.getChildren().remove(resultText);

            // 获取当前单词并设置搜索提示文本
            String w = words[currentWordIndex].toLowerCase();
            promptText = new Text("正在搜索单词: " + w);
            promptText.setLayoutX(1000);
            promptText.setLayoutY(50);
            promptText.getStyleClass().add("text");
            pane.getChildren().add(promptText);

            // 执行搜索动画
            searchStringWithAnimation(w);

            currentCharIndex = 0;
            currentWordIndex++;  // 更新单词索引
        });

        Button deleteButton = new Button("删除下一个单词");
        deleteButton.setLayoutX(200);
        deleteButton.setLayoutY(240);
        deleteButton.setOnAction(event -> {
            pane.getChildren().remove(promptText);
            pane.getChildren().remove(resultText);
            String w = words[currentWordIndex].toLowerCase();
            Trie.TrieNode  current= trie.findStringLCA(w);
            int childCount = 0;
            for (Trie.TrieNode child : current.children) {
                if (child != null) {
                    childCount++;
                }
            }
            if (!trie.findString(w)){
                promptText = new Text("键树中不存在字符串: " + w);
                promptText.setLayoutX(1000);
                promptText.setLayoutY(50);
                promptText.getStyleClass().add("text");
                pane.getChildren().add(promptText);
            }else if (childCount>0&&current.isEndingChar){
                current.getCircle().setFill(Color.DEEPSKYBLUE);
                current.isEndingChar = false;
                promptText = new Text("正在删除字符串: " + w);
                promptText.setLayoutX(1000);
                promptText.setLayoutY(50);
                promptText.getStyleClass().add("text");
                pane.getChildren().add(promptText);
                resultText.setText("单词 " + w + " 删除成功");
                resultText.setLayoutX(1000);
                resultText.setLayoutY(100);
                resultText.getStyleClass().add("text");
                if (!pane.getChildren().contains(resultText)) {
                    pane.getChildren().add(resultText);
                }
            }else {
                promptText = new Text("正在删除字符串: " + w);
                promptText.setLayoutX(1000);
                promptText.setLayoutY(50);
                promptText.getStyleClass().add("text");
                pane.getChildren().add(promptText);

                deleteStringWithAnimation(w);
            }

            currentCharIndex = 0;
            currentWordIndex++;
        });

        // 将按钮添加到面板
        pane.getChildren().add(insertButton);
        pane.getChildren().add(searchButton);
        pane.getChildren().add(deleteButton);

        // 绘制根节点 "/"
        drawRootNode();
        stage.show();

        // 将焦点设置到输入框
        textField.requestFocus();
    }

    // 绘制根节点 "/"
    private void drawRootNode() {
        Circle circle = new Circle(startX, startY, 20);
        circle.setFill(Color.LIGHTGRAY);  // 根节点颜色
        Text text = new Text(startX - 5, startY + 5, "/");
        text.setFill(Color.BLACK);
        pane.getChildren().addAll(circle, text);
    }

    // 插入字符串动画
    private void insertStringWithAnimation(String w, Trie.TrieNode LCA) {
        Trie.TrieNode cur = LCA;
        int i = LCA.level;

        // 创建一个时间轴以顺序插入字符
        Timeline timeline = new Timeline();

        while (i < w.length()) {
            char c = w.charAt(i);
            int index = c - 'a';

            // 检查字符是否为有效字母（a-z）
            if (index < 0 || index >= 26) {
                System.out.println("无效字符 '" + c + "'，跳过。");
                i++; // 移动到下一个字符
                continue; // 如果字符不在 a-z 范围内则跳过
            }

            // 检查子节点是否存在
            Trie.TrieNode nextNode = cur.children[index];
            if (nextNode == null) {
                System.out.println("字符 '" + c + "' 的子节点为空，结束插入。");
                break;
            }

            cur = nextNode;
            i++; // 移动到下一个字符

            // 计算新节点位置
            int j = 0;
            double newX = cur.calculateX(cur, j);
            double newY = cur.calculateY(cur);

            // 碰撞和交叉检测及位置调整
            while (true) {
                if (trie.isPositionOverlap(newX, newY)) {
                    System.out.println("位置重叠，调整位置");
                    j++;
                    newX = cur.calculateX(cur, j);
                } else {
                    System.out.println("位置不重叠，检查是否有交叉边");
                    while (trie.isPositionOverlap(newX, newY) || trie.checkForIntersectingEdges(new Trie.LineSegment(new NodePosition(cur.parent.getX(cur.parent), cur.parent.getY(cur.parent)), new NodePosition(newX, newY)))) {
                        System.out.println("边交叉，调整位置");
                        j--;
                        newX = cur.calculateX(cur, j);
                    }
                    System.out.println("边不交叉，将字符 " + c + " 放置在 X: " + newX + " Y: " + newY);
                    break;
                }
            }

            // 将新节点位置添加到集合中
            trie.nodePositions.add(new NodePosition(newX, newY));

            // 将新边添加到集合中
            trie.addEdge(cur.parent, cur);

            cur.setX(cur, newX);
            cur.setY(cur, newY);

            // 动画效果：每次插入后暂停 0.5 秒
            double finalNewX = newX;
            Trie.TrieNode finalCur = cur;
            KeyFrame keyFrame = new KeyFrame(Duration.seconds((i - LCA.level) * 0.8), event -> {
                drawNodeWithAnimation(finalNewX, newY, c, finalCur);  // 绘制节点
                // 绘制边
                double parentX = finalCur.parent.getX(finalCur.parent);
                double parentY = finalCur.parent.getY(finalCur.parent);
                drawEdge(parentX, parentY, finalCur);
            });
            KeyFrame keyFrame1 = new KeyFrame(Duration.seconds((w.length() - LCA.level) * 0.8 + 0.5), event -> {
                resultText.setText("单词 " + w + " 插入成功");
                resultText.setLayoutX(1000);
                resultText.setLayoutY(100);
                resultText.getStyleClass().add("text");
                pane.getChildren().add(resultText);
            });
            timeline.getKeyFrames().add(keyFrame);
            timeline.getKeyFrames().add(keyFrame1);
        }

        // 开始动画
        timeline.play();
    }

    // 搜索字符串动画
    private void searchStringWithAnimation(String word) {
        Trie.TrieNode current = trie.root;  // 从根节点开始
        int wordLength = word.length();
        Timeline timeline = new Timeline();

        for (int i = 0; i < wordLength; i++) {
            char c = word.charAt(i);
            int index = c - 'a';

            if (index < 0 || index >= 26) {
                System.out.println("无效字符 '" + c + "'，跳过。");
                break;
            }

            Trie.TrieNode nextNode = current.children[index];
            if (nextNode == null) {
                KeyFrame keyFrame = new KeyFrame(Duration.seconds(i * 1.5), event -> {
                    System.out.println("搜索失败，单词 " + word + " 不存在。");
                });
                timeline.getKeyFrames().add(keyFrame);
                break;
            }

            final Trie.TrieNode node = nextNode;
            Trie.TrieNode finalCurrent1 = current;
            KeyFrame keyFrame = new KeyFrame(Duration.seconds(i * 1.5), event -> {
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
                System.out.println("单词 " + word + " 存在。");
            } else {
                finalCurrent.getCircle().setFill(Color.ORANGE);
                System.out.println("单词 " + word + " 不存在。");
            }
            // 显示搜索结果
            if (trie.findString(word)) {
                resultText = new Text("单词 " + word + " 存在");
            } else {
                resultText = new Text("单词 " + word + " 不存在");
            }
            resultText.setLayoutX(1000);
            resultText.setLayoutY(100);
            resultText.getStyleClass().add("text");
            pane.getChildren().add(resultText);

            // 2 秒后恢复原始颜色
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

    private void deleteStringWithAnimation(String word) {
        Trie.TrieNode current;
        int wordLength = word.length();
        Trie.TrieNode LCA = trie.findStringLCA(word);  // 找到最小公共祖先
        current = LCA;

        // 第一步：遍历单词，找到所有节点（从根节点到目标单词的结束节点）
        List<Trie.TrieNode> nodesToDelete = new ArrayList<>();
        for (int i = LCA.level; i < wordLength; i++) {
            char c = word.charAt(i);
            int index = c - 'a';


            Trie.TrieNode nextNode = current.children[index];

            nodesToDelete.add(nextNode);  // 记录需要删除的节点
            current = nextNode;
        }

        // 第二步：从最后一个节点开始删除，直到LCA节点（不删除LCA本身及其下方的节点）
        Timeline timeline = new Timeline();

        // 从末尾开始删除节点
        for (int i = nodesToDelete.size() - 1; i >= 0; i--) {  // 确保不删除LCA节点
            Trie.TrieNode nodeToDelete = nodesToDelete.get(i);
            Trie.TrieNode finalNode = nodeToDelete;

            // 逐步动画：删除节点和连线
            KeyFrame keyFrame = new KeyFrame(Duration.seconds((nodesToDelete.size() - 1 - i) * 1.5), event -> {
                // 1. 删除连线
                Line lineToRemove = finalNode.getLineToParent();
                if (lineToRemove != null) {
                    pane.getChildren().remove(lineToRemove);
                }

                // 2. 删除对应节点的文本
                Text textToRemove = findText(finalNode);
                if (textToRemove != null) {
                    pane.getChildren().remove(textToRemove);
                }

                // 3. 从视图中移除节点圆圈
                if (finalNode.getCircle() != null) {
                    pane.getChildren().remove(finalNode.getCircle());
                }
            });

            timeline.getKeyFrames().add(keyFrame);
        }

        // 第三步：更新删除操作的提示信息
        KeyFrame keyFrame2 = new KeyFrame(Duration.seconds((nodesToDelete.size()) * 1.5), event -> {
            resultText.setText("单词 " + word + " 删除成功");
            resultText.setLayoutX(1000);
            resultText.setLayoutY(100);
            resultText.getStyleClass().add("text");
            if (!pane.getChildren().contains(resultText)) {
                pane.getChildren().add(resultText);
            }
        });

        timeline.getKeyFrames().add(keyFrame2);

        // 开始动画
        timeline.play();

        // 删除实际的数据结构中的节点
        trie.delete(word);
    }

    private Text findText(Trie.TrieNode node) {
        for (javafx.scene.Node n : pane.getChildren()) {
            if (n instanceof Text) {
                Text text = (Text) n;
                if (text.getX() == node.getX(node) - 5 && text.getY() == node.getY(node) + 5) {
                    return text;
                }
            }
        }
        return null;
    }

    private void drawNodeWithAnimation(double x, double y, char value, Trie.TrieNode current) {
        Circle circle = new Circle(x, y, 20);
        circle.setFill(Color.LIGHTBLUE);
        Text text = new Text(x - 5, y + 5, String.valueOf(value));
        text.setFill(Color.BLACK);
        if (circle != null && text != null) {
            pane.getChildren().addAll(circle, text);
        }

        // 将圆圈存储在 TrieNode 中
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

    private void drawEdge(double parentX, double parentY, Trie.TrieNode current) {
        // 计算父节点的外部位置（偏移一定距离）
        double parentRadius = 20;  // 假设圆的半径为 20
        double angle = Math.atan2(current.y - parentY, current.x - parentX);  // 计算从父节点到子节点的角度

        // 计算从圆外部的起点（通过偏移中心位置）
        double startX = parentX + parentRadius * Math.cos(angle);
        double startY = parentY + parentRadius * Math.sin(angle);
        double endX = current.x - parentRadius * Math.cos(angle);
        double endY = current.y - parentRadius * Math.sin(angle);
        Line line = new Line(startX, startY, endX, endY);  // 连接父节点和当前节点
        line.setStroke(Color.BLACK);  // 线条颜色
        current.setLineToParent(line);  // 将线条存储在 TrieNode 中
        pane.getChildren().add(line);
        System.out.println("绘制线条: (" + parentX + ", " + parentY + ") -> (" + current.x + ", " + current.y + ")");
    }
}