package org.example;

import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

import java.util.HashSet;
import java.util.Set;

public class Trie {

    // 存储所有节点位置以快速检查重叠
    public Set<NodePosition> nodePositions = new HashSet<>();
    // 存储所有边
    public Set<LineSegment> edges = new HashSet<>();
    TrieNode root;

    public Trie() {
        root = new TrieNode('/', null, 0);  // 设置根节点的层级为0
        root.setX(root, 700);
        root.setY(root, 100); // 设置根节点的Y坐标，假设根节点的Y坐标为100
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

    // 插入字符串并自动设置每个节点的层级
    public void insert(String word) {
        char[] charArray = word.toLowerCase().toCharArray();
        TrieNode cur = root;
        for (int i = 0; i < word.length(); i++) {
            int index = charArray[i] - 'a';
            if (cur.children[index] == null) {
                TrieNode newNode = new TrieNode(charArray[i], cur, cur.level + 1);
                cur.children[index] = newNode;
                // 创建父子节点之间的可视化连线
                addEdge(cur, newNode);
                newNode.setLineToParent(new Line(cur.x, cur.y, newNode.x, newNode.y)); // 创建连线
            }
            cur = cur.children[index];
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

    // 检查新边与现有边是否相交
    public boolean checkForIntersectingEdges(LineSegment newEdge) {
        for (LineSegment edge1 : edges) {
            if (edge1.isIntersecting(newEdge)) {
                return true;  // 如果有相交则返回true
            }
        }
        return false;  // 如果没有相交则返回false
    }

    // 插入一条边（父节点和子节点之间的连线）
    public void addEdge(TrieNode parent, TrieNode child) {
        // 创建父子节点之间的连线段（用于检查是否相交）
        LineSegment edge = new LineSegment(new NodePosition(parent.x, parent.y), new NodePosition(child.x, child.y));
        // 如果没有相交，则插入新的连线段
        if (!checkForIntersectingEdges(edge)) {
            edges.add(edge);  // 将连线段添加到集合中
        }
    }

    public void delete(String word) {
        delete(root, word.toLowerCase(), 0);
    }

    private boolean delete(TrieNode current, String word, int index) {
        if (index == word.length()) {
            if (!current.isEndingChar) {
                return false;
            }
            current.isEndingChar = false;
            return current.children.length == 0;
        }
        char ch = word.charAt(index);
        int charIndex = ch - 'a';
        TrieNode node = current.children[charIndex];
        if (node == null) {
            return false;
        }
        boolean shouldDeleteCurrentNode = delete(node, word, index + 1);

        if (shouldDeleteCurrentNode) {
            current.children[charIndex] = null;
            return current.children.length == 0 && !current.isEndingChar;
        }
        return false;
    }

    // 查找给定字符串的LCA节点
    public TrieNode findStringLCA(String s) {
        TrieNode current = root;
        TrieNode lcaNode = root; // 默认LCA为根节点
        boolean isBranchPoint = false; // 用于标记是否是分叉点

        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            int index = c - 'a';

            // 如果没有该字符的子节点，则返回当前LCA节点
            if (index < 0 || index >= 26 || current.children[index] == null) {
                break;
            }

            current = current.children[index];

            // 统计当前节点的子节点数
            int childCount = 0;
            for (TrieNode child : current.children) {
                if (child != null) {
                    childCount++;
                }
            }

            // 如果当前节点有多个子节点，说明是一个分叉点
            if (childCount > 1) {
                lcaNode = current;  // 更新为当前节点
                isBranchPoint = true;  // 标记为分叉点
            }

            // 如果当前节点是结束字符，并且不是叶子节点
            if (current.isEndingChar && childCount > 0) {
                lcaNode = current;
                isBranchPoint = true;  // 标记为分叉点
            }
        }

        // 返回最接近叶子节点的公共祖先
        return lcaNode;
    }

    // 线段类，表示两个节点之间的连线
    public static class LineSegment {
        private final NodePosition start;
        private final NodePosition end;

        public LineSegment(NodePosition start, NodePosition end) {
            this.start = start;
            this.end = end;
        }

        // 检查两条线段是否相交
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
        public TrieNode[] children = new TrieNode[26];  // 子节点数组
        public boolean isEndingChar = false;  // 标记是否为单词的结束字符
        public TrieNode parent;  // 指向父节点的指针
        double x, y;  // 在图形界面中的位置
        int level;  // 记录节点的层级
        Circle circle;  // 用于显示的圆圈
        Line lineToParent;  // 连接此节点与其父节点的线

        public TrieNode(char data, TrieNode parent, int level) {
            this.data = data;
            this.parent = parent;
            this.level = level;  // 设置节点的层级
        }

        public Circle getCircle() {
            return circle;
        }

        public void setCircle(Circle circle) {
            this.circle = circle;
        }

        public Line getLineToParent() {
            return lineToParent;
        }

        public void setLineToParent(Line lineToParent) {
            this.lineToParent = lineToParent;
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
                return 700;  // 根节点的X坐标
            }

            // 根据字母顺序或偏移调整X坐标
            if (current.data >= 'm') {
                return current.parent.x + 50 * hash;
            } else {
                return current.parent.x - 50 * hash;
            }
        }

        public double calculateY(TrieNode current) {
            return 100 + current.level * 100;  // 每层的Y坐标间距为100
        }

        // 在TrieNode类中添加一个方法来判断是否没有子节点
        public boolean hasNoChildren() {
            for (TrieNode child : children) {
                if (child != null) {
                    return false;
                }
            }
            return true;
        }
    }
}