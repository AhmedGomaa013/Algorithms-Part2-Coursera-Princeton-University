 package Module03;

import java.util.ArrayList;
import java.util.HashMap;
import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.StdOut;

public class WordNet {
    private final HashMap<String, ArrayList<Integer>> dict = new HashMap<>();
    private int count = 0;
    private final Digraph graph;
    private final SAP sap;
    // constructor takes the name of the two input files
    public WordNet(String synsets, String hypernyms) {
        validateArguments(synsets);
        validateArguments(hypernyms);
        
        readSynsets(synsets);
        this.graph = new Digraph(count);

        readHypernums(hypernyms);
        sap = new SAP(graph);
    }
 
    // returns all WordNet nouns
    public Iterable<String> nouns() {
        return dict.keySet();
    }
 
    // is the word a WordNet noun?
    public boolean isNoun(String word) {
        validateArguments(word);
        return dict.containsKey(word);
    }
 
    // distance between nounA and nounB (defined below)
    public int distance(String nounA, String nounB) {
        validateArguments(nounA);
        checkIsNoun(nounA);
        validateArguments(nounB);
        checkIsNoun(nounB);
        return sap.length(dict.get(nounA), dict.get(nounB));
    }
 
    // a synset (second field of synsets.txt) that is the common ancestor of nounA and nounB
    // in a shortest ancestral path (defined below)
    public String sap(String nounA, String nounB) {
        validateArguments(nounA);
        checkIsNoun(nounA);
        validateArguments(nounB);
        checkIsNoun(nounB);
        int ancestor = sap.ancestor(dict.get(nounA), dict.get(nounB));
        if (ancestor == -1) return null;
        Queue<String> keys = new Queue<>();
        for (var entry : dict.entrySet()) {
            if (entry.getValue().contains(ancestor)) keys.enqueue(entry.getKey());
        }
        String key = keys.dequeue();
        while (!keys.isEmpty()) {
            String newKey = keys.dequeue();
            if (newKey.length() > key.length()) {
                key = newKey;
            }
        }
        return key;
    }
 
    private void validateArguments(String str) {
        if (str == null) throw new IllegalArgumentException();
    }

    private void checkIsNoun(String str) {
        if (!isNoun(str)) throw new IllegalArgumentException();
    }

    private void readSynsets(String synsets) {
        var in = new In(synsets);
        while (!in.isEmpty()) {
            var line = in.readLine().split(",");
            int value = Integer.parseInt(line[0]);
            count++;
            if (!dict.containsKey(line[1])) dict.put(line[1], new ArrayList<>());
            var list = dict.get(line[1]);
            list.add(value);
            dict.put(line[1], list);
            var nouns = line[1].split(" ");
            if (nouns.length > 1) {
                for (int i = 0; i < nouns.length; i++) {
                    if (!dict.containsKey(nouns[i])) dict.put(nouns[i], new ArrayList<>());
                    list = dict.get(nouns[i]);
                    list.add(value);
                    dict.put(nouns[i], list);
                }
            }
        }
    }

    private void readHypernums(String hypernyms) {
        var in = new In(hypernyms);
        while (!in.isEmpty()) {
            var line = in.readLine().split(",");
            if (line.length == 1) continue;
            int v = Integer.parseInt(line[0]);
            for (int i = 1; i < line.length; i++) {
                graph.addEdge(v, Integer.parseInt(line[i]));
            }
        }
    }

    // do unit testing of this class
    public static void main(String[] args) {
        var word = new WordNet("synsets.txt", "hypernyms.txt");
        int length = word.distance("wood_pulp", "antihaemophilic_factor");
        String ancestor = word.sap("wood_pulp", "antihaemophilic_factor");
        StdOut.printf("length = %d, ancestor = %s\n", length, ancestor);
    }
 }