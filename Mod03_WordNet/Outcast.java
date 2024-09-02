package Mod03_WordNet;

public class Outcast {
    private final WordNet wordNet;
    public Outcast(WordNet wordnet) {
        this.wordNet = wordnet;
    }

    // given an array of WordNet nouns, return an outcast
    public String outcast(String[] nouns) {
        int maximumDistance = Integer.MIN_VALUE;
        String word = null;
        for (var v : nouns) {
            int temp = 0;
            for (var w : nouns) {
                if (v.equals(w)) continue;
                temp += wordNet.distance(v, w);
            }
            if (temp > maximumDistance) {
                maximumDistance = temp;
                word = v;
            }
        }

        return word;
    }
    public static void main(String[] args) {
        //
    }
 }