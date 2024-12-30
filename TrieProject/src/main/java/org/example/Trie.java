package org.example;

import java.util.HashSet;
import java.util.Set;

public class Trie {
    // 存储所有节点的位置，用于快速检查重叠
    public Set<NodePosition> nodePositions = new HashSet<>();
    TrieNode root;

    public Trie() {
        root = new TrieNode('/', null, 0);  // 为根节点设置层级 0
        root.setX(root, 700);
        root.setY(root, 100); // 为 root 设置 Y 坐标，假设根节点的 Y 坐标是 50
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
                // 设置当前节点的层数为父节点的层数 + 1
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

    private boolean delete(TrieNode current, char[] word, int index) {
        if (index == word.length) {
            // 当遍历到字符串的末尾时，标记为非结尾字符
            if (!current.isEndingChar) {
                return false;
            }
            current.isEndingChar = false;
            // 如果当前节点没有子节点，返回 true 表示可以删除该节点
            for (TrieNode child : current.children) {
                if (child != null) {
                    return false;
                }
            }
            return true;
        }
        char ch = word[index];
        int charIndex = ch - 'a';
        TrieNode node = current.children[charIndex];
        if (node == null) {
            return false;
        }
        boolean shouldDeleteCurrentNode = delete(node, word, index + 1);

        // 如果应当删除子节点
        if (shouldDeleteCurrentNode) {
            current.children[charIndex] = null;
            // 当前节点是否可以删除
            for (TrieNode child : current.children) {
                if (child != null) {
                    return false;
                }
            }
            return !current.isEndingChar;
        }
        return false;
    }

    // 删除字符串操作
    public void delete(String word) {
        delete(root, word.toLowerCase().toCharArray(), 0);
    }

    public TrieNode getStart(String w) {
        TrieNode cur = root;
        char[] charArray = w.toLowerCase().toCharArray();
        for (char item : charArray) {
            int index = item - 'a';
            if (cur.children[index] == null) {
                System.out.println("字符 " + item + " 的节点未找到。");
                return null;  // 如果节点不存在，返回 null
            }
            cur = cur.children[index];
        }
        // 确保最终 cur 不为 null
        if (cur == null) {
            return null;
        }

        // 向上遍历直到找到根节点
        while (cur.parent != null && cur.parent.data != '/') {
            cur = cur.parent;
        }

        return cur;
    }

    // 查找新单词的最长公共祖先节点
    public TrieNode findLCA(String newWord) {
        char[] newCharArray = newWord.toLowerCase().toCharArray();
        TrieNode cur = root;
        TrieNode lcaNode = root;

        for (int i = 0; i < newCharArray.length; i++) {
            int index = newCharArray[i] - 'a';
            if (cur.children[index] == null) {
                break;  // No match found, return the last common ancestor node
            }
            cur = cur.children[index];
            lcaNode = cur; // Update the LCA node to the current node
        }

        return lcaNode;
    }

    public class TrieNode {
        public char data;  // 字符
        public TrieNode[] children = new TrieNode[26];  // 孩子节点数组
        public boolean isEndingChar = false;  // 标记是否为单词的结束字符
        public TrieNode parent;  // 指向父节点的指针
        double x, y;  // 在图形界面的位置
        int size;  // 在插入过程中记录的大小
        int level;  // 记录该节点的层数

        public TrieNode(char data, TrieNode parent, int level) {
            this.data = data;
            this.parent = parent;
            this.size = 0;
            this.level = level;  // 设置节点的层数
        }

        public double getSize() {
            return 0;
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
            // 如果当前节点是根节点（parent父节点为 null），则直接返回当前节点的 X 坐标
            if (current.parent == null) {
                return 700 ;  // 或者可以设置一个固定值
            }

            // 否则，按照原本的规则计算 X 坐标
            if (current.parent.data > current.data) {
                return current.parent.x - 50 * hash;
            } else if (current.parent.data < current.data) {
                return current.parent.x + 50 * hash;
            } else {
                return current.parent.x;
            }
        }

        public double calculateY(TrieNode current) {
            return 100 + current.level * 100;
        }
    }
}
