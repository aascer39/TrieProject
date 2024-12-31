package org.example;

import javafx.scene.shape.Circle;

import java.util.HashSet;
import java.util.Set;

public class Trie {

    // 存储所有节点的位置，用于快速检查重叠
    public Set<NodePosition> nodePositions = new HashSet<>();
    // 存储所有的连线
    public Set<LineSegment> edges = new HashSet<>();
    TrieNode root;

    public Trie() {
        root = new TrieNode('/', null, 0);  // 为根节点设置层级 0
        root.setX(root, 700);
        root.setY(root, 100); // 为 root 设置 Y 坐标，假设根节点的 Y 坐标是 100
    }

    // 查找新单词的最长公共祖先节点
    public TrieNode findLCA(String newWord) {
        char[] newCharArray = newWord.toLowerCase().toCharArray();
        TrieNode cur = root;
        TrieNode lcaNode = root;

        for (int i = 0; i < newCharArray.length; i++) {
            int index = newCharArray[i] - 'a';
            if (cur.children[index] == null) {
                break;
            }
            cur = cur.children[index];
            lcaNode = cur;
        }

        return lcaNode;
    }

    // 检查位置是否重叠
    public boolean isPositionOverlap(double x, double y) {
        return nodePositions.contains(new NodePosition(x, y));
    }

    // 插入字符串并自动设置每个节点的层数
    public void insert(String word) {
        char[] charArray = word.toLowerCase().toCharArray();
        TrieNode cur = root;
        for (int i = 0; i < word.length(); i++) {
            int index = charArray[i] - 'a';
            if (cur.children[index] == null) {
                TrieNode newNode = new TrieNode(charArray[i], cur, cur.level + 1);
                cur.children[index] = newNode;
            }
            cur = cur.children[index];
            cur.size = i;
        }
        cur.isEndingChar = true;
    }

    // 查找字符串
    public boolean findString(String word) {
        char[] charArray = word.toLowerCase().toCharArray();
        TrieNode cur = root;
        for (char item : charArray) {
            int index = item - 'a';
            if (cur.children[index] == null) {
                return false;
            }
            cur = cur.children[index];
        }
        return cur.isEndingChar;
    }

    // 新插入的连线与现有连线交叉检查
    public boolean checkForIntersectingEdges(LineSegment newEdge) {
        for (LineSegment edge1 : edges) {
            if (edge1.isIntersecting(newEdge)) {
                return true;  // 如果有交点，返回 true
            }
        }
        return false;  // 如果没有交点，返回 false
    }

    // 插入一个边（父节点与子节点之间的连线）
    public void addEdge(TrieNode parent, TrieNode child) {
        LineSegment edge = new LineSegment(new NodePosition(parent.x, parent.y), new NodePosition(child.x, child.y));
        // 如果没有交叉，插入新边
        edges.add(edge);
    }

    // 线段类，表示两个节点之间的连线
    public static class LineSegment {
        private final NodePosition start;
        private final NodePosition end;

        public LineSegment(NodePosition start, NodePosition end) {
            this.start = start;
            this.end = end;
        }

        // 判断两条线段是否相交
        public boolean isIntersecting(LineSegment other) {
            return NodePosition.isIntersecting(start, end, other.start, other.end);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            LineSegment that = (LineSegment) obj;
            return start.equals(that.start) && end.equals(that.end);
        }

        @Override
        public int hashCode() {
            return start.hashCode() * 31 + end.hashCode();
        }
    }

    public class TrieNode {
        public char data;  // 字符
        public TrieNode[] children = new TrieNode[26];  // 孩子节点数组
        public boolean isEndingChar = false;  // 标记是否为单词的结束字符
        public TrieNode parent;  // 指向父节点的指针
        double x, y;  // 在图形界面的位置
        int size;  // 在插入过程中记录的大小
        int level;  // 记录该节点的层数
        Circle circle;  // 用于显示的圆

        public TrieNode(char data, TrieNode parent, int level) {
            this.data = data;
            this.parent = parent;
            this.level = level;  // 设置节点的层数
        }

        public Circle getCircle() {
            return circle;
        }

        public void setCircle(Circle circle) {
            this.circle = circle;
        }

        public void setX(TrieNode node, double x) {
            node.x = x;
        }

        public void setY(TrieNode node, double y) {
            node.y = y;
        }

        public double getX(TrieNode node) {
            return node.x;
        }

        public double getY(TrieNode node) {
            return node.y;
        }

        // 计算新节点的X坐标
        public double calculateX(TrieNode current, int hash) {
            if (current.parent == null) {
                return 700;  // 根节点的 X 坐标
            }

            // 按照字母顺序或偏移量调整 X 坐标
            if (current.data >= 'm') {
                return current.parent.x + 50 * hash;
            } else {
                return current.parent.x - 50 * hash;
            }
        }

        public double calculateY(TrieNode current) {
            return 100 + current.level * 100;  // 每一层的 Y 坐标间距为 100
        }

    }
}
