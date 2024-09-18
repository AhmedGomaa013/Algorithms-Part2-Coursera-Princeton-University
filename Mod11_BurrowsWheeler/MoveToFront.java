package Mod11_BurrowsWheeler;

import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

public class MoveToFront {
    private static final int R = 256;
    private static final int LG_R = 8;
    private static char[] indexToChar;
    private static int[] charToIndex;

    // init the alphabet
    private static void init() {
        indexToChar = new char[R];
        charToIndex = new int[R];
        for (int i = 0; i < R; i++) {
            indexToChar[i] = (char) i;
            charToIndex[i] = i;
        }
    }

    // move the char c to the front of the alphabet
    private static void moveChar(char c) {
        int index = charToIndex[c];
        for (int i = index; i > 0; i--) {
            // charToIndex[i] = charToIndex[i - 1];
            // indexToChar[charToIndex[i]] = (char) i;
            indexToChar[i] = indexToChar[i - 1];
            charToIndex[indexToChar[i]] = i;
        }
        charToIndex[c] = 0;
        indexToChar[0] = c;
    }
    // apply move-to-front encoding, reading from standard input and writing to standard output
    public static void encode() {
         init();
        while (!BinaryStdIn.isEmpty()) {
            char c = BinaryStdIn.readChar();
            // System.out.println("C is, " + c);
            BinaryStdOut.write((byte) charToIndex[c]);
            moveChar(c);
        }
        BinaryStdOut.close();
    }

    // apply move-to-front decoding, reading from standard input and writing to standard output
    public static void decode() {

    }

    // if args[0] is "-", apply move-to-front encoding
    // if args[0] is "+", apply move-to-front decoding
    public static void main(String[] args) {
        String op = "-";
        if (op.equals("-")) encode();
        else if (op.equals("+")) decode();
    }

}
