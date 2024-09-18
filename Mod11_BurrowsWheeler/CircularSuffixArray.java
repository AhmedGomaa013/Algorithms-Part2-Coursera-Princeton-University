package Mod11_BurrowsWheeler;

import java.util.Arrays;

public class CircularSuffixArray {

    private class CircularSuffix implements Comparable<CircularSuffix> {
        private String str;
        private int index;
        private int offset;
        private int n;

        public CircularSuffix(String s, int index, int offset) {
            this.str = s;
            this.index = index;
            this.offset = offset;
            this.n = s.length();
        }

        public int compareTo(CircularSuffix that) {
            int thisOffset = this.offset;
            int thatOffset = that.offset;
            int cmp = this.str.charAt(thisOffset) - this.str.charAt(thatOffset);
            if (cmp != 0) return cmp;
            for (int i = 0; i < n; i++) {
                thisOffset = (this.offset + i) % n;
                thatOffset = (that.offset + i) % n;
                cmp = this.str.charAt(thisOffset) - this.str.charAt(thatOffset);
                if (cmp != 0) break;
            }
            return cmp;
        }
    }
    private int length;
    private CircularSuffix[] suffixs;
    // circular suffix array of s
    public CircularSuffixArray(String s) {
        if (s == null) throw new IllegalArgumentException();
        length = s.length();
        suffixs = new CircularSuffix[length];
        for (int i = 0; i < length; i++) {
            suffixs[i] = new CircularSuffix(s, i, i);
        }
        Arrays.sort(suffixs);
    }

    // length of s
    public int length() {
        return length;
    }

    // returns index of ith sorted suffix
    public int index(int i) {
        if (i < 0 || i >= length) throw new IllegalArgumentException();
        return suffixs[i].index;
    }

    // unit testing (required)
    public static void main(String[] args) {
    }
}
