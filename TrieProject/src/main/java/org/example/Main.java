package org.example;

public class Main {
    public static void main(String[] args) {
        Trie trie = new Trie();
        String[] words = { "hello", "hi", "however", "apple"};
        for (String word : words) {
            trie.insert(word);
        }
//        Scanner scanner = new Scanner(System.in);
//        String word = scanner.nextLine();
        System.out.println(trie.findString("apple"));
        System.out.println(trie.findString("app"));
        System.out.println(trie.findLCA("application").level);
        System.out.println("application".charAt(4));
        System.out.println(trie.findLCA("how").data + "  " + trie.findLCA("how").level);
//        trie.delete(word);
//        for (String w :
//                words) {
//            System.out.println("字符串" + w + "是否存在:" + trie.findString(w));
//        }
    }
}