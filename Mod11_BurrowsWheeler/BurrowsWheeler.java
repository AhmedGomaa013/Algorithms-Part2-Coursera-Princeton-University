package Mod11_BurrowsWheeler;

import java.util.Arrays;
import java.util.HashMap;

import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;
import edu.princeton.cs.algs4.Queue;

public class BurrowsWheeler {

    // apply Burrows-Wheeler transform,
    // reading from standard input and writing to standard output 
    public static void transform() {
        String str = BinaryStdIn.readString();
        CircularSuffixArray arr = new CircularSuffixArray(str);

        for (int i = 0; i < str.length(); i++) {
            int index = arr.index(i);
            if (index == 0) {
                BinaryStdOut.write(i);
                break;
            }
        }
        int n = str.length();
        for (int i = 0; i < n; i++) {
            int index = arr.index(i);
            int lastCharIndex = (index + str.length() - 1) % n;

            BinaryStdOut.write(str.charAt(lastCharIndex));
        }
        BinaryStdOut.close();
    }

    // apply Burrows-Wheeler inverse transform,
    // reading from standard input and writing to standard output
    public static void inverseTransform() {
        int first = BinaryStdIn.readInt();
        var t = BinaryStdIn.readString();
        BinaryStdIn.close();
        var sorted = t.toCharArray();
        int n = sorted.length;
        HashMap<Character, Queue<Integer>> rr = new HashMap<>();
        for (int i = 0; i < n; i++) {
            if (!rr.containsKey(sorted[i])) {
                rr.put(sorted[i], new Queue<Integer>());
            }
            var qu = rr.get(sorted[i]);
            qu.enqueue(i);
            rr.put(sorted[i], qu);
        }
        Arrays.sort(sorted);        
        int[] next = new int[n];

        for (int i = 0; i < n; i++) {
            var qu = rr.get(sorted[i]);
            next[i] = qu.dequeue();
        }
        for (int i = 0; i < n; i++) {
            first = next[first];
            BinaryStdOut.write(t.charAt(first));
        }
        BinaryStdOut.close();
    }

    // if args[0] is "-", apply Burrows-Wheeler transform
    // if args[0] is "+", apply Burrows-Wheeler inverse transform
    public static void main(String[] args) {
        String operation = args[0];
        if (operation == "-") transform();
        else if (operation == "+") inverseTransform();
    }

}
