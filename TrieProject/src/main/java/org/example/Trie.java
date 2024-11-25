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

    private TrieNode root = new TrieNode('/');

    //插入节点
    public void insert(String word) {
        char[] charArray = word.toLowerCase().toCharArray();
        TrieNode cur = root;
        for (char item : charArray) {
            int Index = item - 'a';
            if (cur.children[Index] == null) {
                TrieNode newNode = new TrieNode(item);
                cur.children[Index] = newNode;
            }
            cur = cur.children[Index];
        }
        cur.isEndingChar = true;
    }

    //查找单词
    public boolean findString(String word) {
        char[] charArray = word.toLowerCase().toCharArray();
        TrieNode cur = root;
        for (char item : charArray) {
            int Index = item - 'a';
            if (cur.children[Index] == null) {
                return false;
            }
            cur = cur.children[Index];
        }
        return cur.isEndingChar;
    }

    //字符前缀匹配
    public boolean startWith(String word) {
        char[] charArray = word.toLowerCase().toCharArray();
        TrieNode cur = root;
        for (char item : charArray) {
            int Index = item - 'a';
            if (cur.children[Index] == null) {
                return false;
            }
            cur = cur.children[Index];
        }
        return true;
    }
}
