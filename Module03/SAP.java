 package Module03;

import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.Queue;

public class SAP {
    private final Digraph graph;
    private int length = 0;
    private int[] distToV;
    private boolean[] markedV;
    private int[] distToW;
    private boolean[] markedW;
    
    // constructor takes a digraph (not necessarily a DAG)
    public SAP(Digraph G) {
        if (G == null) throw new IllegalArgumentException();
        this.graph = new Digraph(G);
        length = this.graph.V();
    }
 
    // length of shortest ancestral path between v and w; -1 if no such path
    public int length(int v, int w) {
        validateArguments(v);
        validateArguments(w);
        int ancestor = ancestor(v, w);
        if (ancestor == -1) return -1;
        return distToV[ancestor] + distToW[ancestor];
    }
 
    // a common ancestor of v and w that participates in a shortest ancestral path; -1 if no such path
    public int ancestor(int v, int w) {
        validateArguments(v);
        validateArguments(w);
        reinitiateArrays();
        bfs(v, distToV, markedV);
        bfs(w, distToW, markedW);
        int shortest = Integer.MAX_VALUE;
        int ancestor = Integer.MAX_VALUE;
        for (int i = 0; i < this.length; i++) {
            if (markedV[i] && markedV[i] == markedW[i]) {
                int temp = distToV[i] + distToW[i];
                if (temp < shortest) {
                    shortest = temp;
                    ancestor = i;
                }
            }
        }
        return ancestor == Integer.MAX_VALUE ? -1 : ancestor;
    }
 
    // length of shortest ancestral path between any vertex in v and any vertex in w; -1 if no such path
    public int length(Iterable<Integer> v, Iterable<Integer> w) {
        validateArguments(v);
        validateArguments(w);
        int ancestor = ancestor(v, w);
        if (ancestor == -1) return -1;
        return distToV[ancestor] + distToW[ancestor];
    }
 
    // a common ancestor that participates in shortest ancestral path; -1 if no such path
    public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
        validateArguments(v);
        validateArguments(w);
        reinitiateArrays();
        int shortest = Integer.MAX_VALUE;
        int ancestor = Integer.MAX_VALUE;
        bfs(v, distToV, markedV);
        bfs(w, distToW, markedW);
        for (int i = 0; i < this.length; i++) {
            if (markedV[i] && markedV[i] == markedW[i]) {
                int temp = distToV[i] + distToW[i];
                if (temp < shortest) {
                    shortest = temp;
                    ancestor = i;
                }
            }
        }   
        return ancestor == Integer.MAX_VALUE ? -1 : ancestor;
    }

    private void bfs(int start, int[] distTo, boolean[] marked) {
        Queue<Integer> queue = new Queue<>();
        queue.enqueue(start);
        marked[start] = true;
        distTo[start] = 0;
        while (!queue.isEmpty()) {
            int node = queue.dequeue();
            for (var nextNode : this.graph.adj(node)) {
                if (!marked[nextNode]) {
                queue.enqueue(nextNode);
                marked[nextNode] = true;
                distTo[nextNode] = distTo[node] + 1;
                }
            }
        }
    }

    private void bfs(Iterable<Integer> start, int[] distTo, boolean[] marked) {
        Queue<Integer> queue = new Queue<>();
        for (var v : start) {
            queue.enqueue(v);
            marked[v] = true;
            distTo[v] = 0;
        }
        while (!queue.isEmpty()) {
            int node = queue.dequeue();
            for (var nextNode : this.graph.adj(node)) {
                if (!marked[nextNode]) {
                queue.enqueue(nextNode);
                marked[nextNode] = true;
                distTo[nextNode] = distTo[node] + 1;
                }
            }
        }
    }
    
    private void reinitiateArrays() {
        distToV = new int[this.length];
        markedV = new boolean[this.length];
        distToW = new int[this.length];
        markedW = new boolean[this.length];
    }
    
    private void validateArguments(int v) {
        if (v < 0 || v >= this.length) throw new IllegalArgumentException();
    }

    private void validateArguments(Iterable<Integer> v) {
        if (v == null) throw new IllegalArgumentException();
        for (var x : v) {
            if (x == null) throw new IllegalArgumentException();
            validateArguments(x);
        }
    }
    // do unit testing of this class
    public static void main(String[] args) {
        //
    }
}
    