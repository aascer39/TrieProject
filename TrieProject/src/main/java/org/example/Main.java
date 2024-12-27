package org.example;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Trie trie = new Trie();
        String[] words = {"how", "hello", "hi", "however"};
        for (String word : words) {
            trie.insert(word);
        }
        Scanner scanner = new Scanner(System.in);
        String word = scanner.nextLine();
        System.out.println(trie.findString(word));
        System.out.println(trie.startWith(word));
        trie.delete(word);
        for (String w :
                words) {
            System.out.println("字符串" + w + "是否存在:" + trie.findString(w));
        }
    }
}