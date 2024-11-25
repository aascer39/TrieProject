package org.example;

public class Main {
    public static void main(String[] args) {
        Trie trie = new Trie();
        String[] words = {"how", "hello", "hi"};
        for(String word: words){
            trie.insert(word);
        }
        System.out.println(trie.findString("h"));
    }
}