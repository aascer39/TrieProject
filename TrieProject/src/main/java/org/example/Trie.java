package org.example;

public class Trie {
    public class TrieNode {
        public char data;//字符
        public TrieNode[] children = new TrieNode[26];
        public boolean isEndingChar = false;

        public TrieNode(char data) {
            this.data = data;
        }
    }

    final TrieNode root = new TrieNode('/');

    //插入字符串
    public void insert(String word) {
        char[] charArray = word.toLowerCase().toCharArray();
        TrieNode cur = root;
        for (char item : charArray) {
            int index = item - 'a';
            if (cur.children[index] == null) {
                TrieNode newNode = new TrieNode(item);
                cur.children[index] = newNode;
            }
            cur = cur.children[index];
        }
        cur.isEndingChar = true;
    }

    //查找字符串
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

    //字符前缀匹配
    public boolean startWith(String word) {
        char[] charArray = word.toLowerCase().toCharArray();
        TrieNode cur = root;
        for (char item : charArray) {
            int index = item - 'a';
            if (cur.children[index] == null) {
                return false;
            }
            cur = cur.children[index];
        }
        return true;
    }

    // 删除字符串操作
    public void delete(String word) {
        delete(root, word.toLowerCase().toCharArray(), 0);
    }

    private boolean delete(TrieNode current, char[] word, int index) {
        if (index == word.length) {
            // 当遍历到字符串的末尾时，标记为非结尾字符
            if (!current.isEndingChar) {
                return false;
            }
            current.isEndingChar = false;
            // 如果当前节点没有子节点，返回 true 表示可以删除该节点
            return current.children.length == 0;
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
            return current.children.length == 0 && !current.isEndingChar;
        }
        return false;
    }
}
