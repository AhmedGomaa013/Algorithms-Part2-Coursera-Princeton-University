package Module09_Boggle;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.SET;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.TST;

public class BoggleSolver {
    private TST<String> trie;
    private SET<String> validWords;
    // Initializes the data structure using the given array of strings as the dictionary.
    // (You can assume each word in the dictionary contains only the uppercase letters A through Z.)
    public BoggleSolver(String[] dictionary) {
        trie = new TST<>();
        for(String str : dictionary) {
            trie.put(str, String.format("%d", str.length()));
        }
    }

    // Returns the set of all valid words in the given Boggle board, as an Iterable.
    public Iterable<String> getAllValidWords(BoggleBoard board) {
        validWords = new SET<>();
        for (int i = 0; i < board.rows(); i++) {
            for (int j = 0; j < board.cols(); j++) {
                boolean[][] marked = new boolean[board.rows()][board.cols()];
                StringBuilder sb = new StringBuilder();
                sb.append(board.getLetter(i, j));
                if (board.getLetter(i, j) == 'Q') sb.append('U');
                marked[i][j] = true;
                dfs(board, i, j, marked, sb);
            }
        }
        return validWords;
    }

    private void dfs(BoggleBoard board, int i, int j, boolean[][] marked, StringBuilder prefix) {
        if (prefix.length() > 2) {
            var wordsInDict = (Queue<String>) trie.keysWithPrefix(prefix.toString());
            if (wordsInDict.isEmpty()) return;
            if (trie.contains(prefix.toString()) && !validWords.contains(prefix.toString())) { 
                validWords.add(prefix.toString());
            }
        }
        int[][] adjs = getAdjs(i, j);
        for (int[] adj : adjs) {
            if (!isValid(adj[0], adj[1], board.rows(), board.cols())) continue;
            if (!marked[adj[0]][adj[1]]) {
                marked[adj[0]][adj[1]] = true;
                char current = board.getLetter(adj[0], adj[1]);
                prefix.append(current);
                if (current == 'Q') prefix.append('U');
                dfs(board, adj[0], adj[1], marked, prefix);
                if (current == 'Q') prefix.deleteCharAt(prefix.length() - 1);
                prefix.deleteCharAt(prefix.length() - 1);
                marked[adj[0]][adj[1]] = false;
            }
        }
        // marked[i][j] = false;
        // int len = prefix.length();
        // if (len > 0) {
        //     prefix.deleteCharAt(len - 1);
        //     len = prefix.length();
        //     if (len > 0 && prefix.charAt(len - 1) == 'Q') prefix.deleteCharAt(len - 1);
        // }
    }

    private int[][] getAdjs(int i, int j) {
        int[][] adjs = {
            {i, j - 1},
            {i, j + 1},
            {i - 1, j - 1},
            {i - 1, j},
            {i - 1, j + 1},
            {i + 1, j - 1},
            {i + 1, j},
            {i + 1, j + 1}
        };
        return adjs;
    }
    
    private boolean isValid(int i, int j, int height, int width) {
        return (i >= 0) && (i < height) && (j >= 0) && (j < width);
    }
    // Returns the score of the given word if it is in the dictionary, zero otherwise.
    // (You can assume the word contains only the uppercase letters A through Z.)
    public int scoreOf(String word) {
        int value = 0;
        if (trie.contains(word)) {
            int len = word.length();
            if (len == 3 || len == 4) value = 1;
            else if (len == 5) value = 2;
            else if (len == 6) value = 3;
            else if (len == 7) value = 5;
            else if (len >= 8) value = 11;
        }
        return value;
    }

    public static void main(String[] args) {
        In in = new In("dictionary-yawl.txt");
        String[] dictionary = in.readAllStrings();
    BoggleSolver solver = new BoggleSolver(dictionary);
    BoggleBoard board = new BoggleBoard("board4x4.txt");
    int score = 0;
    var words = solver.getAllValidWords(board);
    int count = 0;
    for (String word : words) {
        score += solver.scoreOf(word);
        count++;
    }
    StdOut.println(count);
    StdOut.println("Score = " + score);
    }
}
