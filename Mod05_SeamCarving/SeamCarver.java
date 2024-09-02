package Mod05_SeamCarving;

import edu.princeton.cs.algs4.Picture;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.Stack;

public class SeamCarver {

    private Picture currentPicture;
    private int width;
    private int height;

    // create a seam carver object based on the given picture
    public SeamCarver(Picture picture) {
        if (picture == null) throw new IllegalArgumentException();
        this.currentPicture = new Picture(picture);
        this.width = this.currentPicture.width();
        this.height = this.currentPicture.height();
    }
 
    // current picture
    public Picture picture() {
        return currentPicture;
    }
 
    // width of current picture
    public int width() {
        return this.width;
    }
 
    // height of current picture
    public int height() {
        return this.height;
    }
 
    // energy of pixel at column x and row y
    public double energy(int x, int y) {
        return getEnergy(x, y);
    }
 
    // sequence of indices for horizontal seam
    public int[] findHorizontalSeam() {
        int count = height * width;
        double[] distTo = new double[count];
        int[] edgeTo = new int[count];
        
        var sorted = performTopSortHorizontally(distTo, edgeTo, height, width);
        traceTopSortHorizontally(distTo, edgeTo, height, width, sorted);
        double maxValue = Double.POSITIVE_INFINITY;
        int nodeTostart = -1;
        for (int i = 0; i < height; i++) {
            int endNode = (width - 1) + (width * i);
            if (distTo[endNode] < maxValue) {
                maxValue = distTo[endNode];
                nodeTostart = endNode;
            }
        }
        int[] seam = constructHorizontalSeam(nodeTostart, edgeTo, height, width);
        return seam;
    }
 
    // sequence of indices for vertical seam
    public int[] findVerticalSeam() {
        int count = height * width;
        double[] distTo = new double[count];
        int[] edgeTo = new int[count];
        Stack<Integer> sort = performTopSort(distTo, edgeTo, width, height);
        traceTopSort(distTo, edgeTo, width, height, sort);
        double maxValue = Double.POSITIVE_INFINITY;
        int nodeTostart = -1;
        for (int i = 0; i < width; i++) {
            int endNode = (height - 1) * width + i;
            if (distTo[endNode] < maxValue) {
                maxValue = distTo[endNode];
                nodeTostart = endNode;
            }
        }
        int[] seam = constructSeam(nodeTostart, edgeTo, width, height);
        return seam;
    }

    private int[] constructSeam(int endnode, int[] edgeTo, int size, int length) {
        int[] seam = new int[length];
        for (int i = length - 1; i >= 0; i--) {
            var dims = oneDTo2D(endnode, size);
            seam[i] = dims[1];
            if (edgeTo[endnode] != -1) endnode = edgeTo[endnode];
        }
        return seam;
    }
    private int[] constructHorizontalSeam(int endnode, int[] edgeTo, int size, int length) {
        int[] seam = new int[length];
        for (int i = length - 1; i >= 0; i--) {
            var dims = oneDTo2D(endnode, length);
            seam[i] = dims[0];
            if (edgeTo[endnode] != -1) endnode = edgeTo[endnode];
        }
        return seam;
    }

    private Stack<Integer> performTopSort(double[] distTo, int[] edgeTo, int size, int length) {
        Stack<Integer> stack = new Stack<>();
        Queue<Integer> queue = new Queue<>();
        boolean[] visited = new boolean[width * height];
        for (int i = size - 1; i >= 0; i--) {
            int index = twoDTo1D(0, i, size);
            distTo[index] = energy(i, 0);
            edgeTo[index] = -1;
            stack.push(index);
            while (!stack.isEmpty()) {
                int vertex = stack.pop();
                if (visited[vertex]) {
                    queue.enqueue(vertex);
                    continue;
                }
                visited[vertex] =  true;
                var adj = getUnvisitedAdj(vertex, size, length, visited);
                if (adj.isEmpty()) {
                    queue.enqueue(vertex);
                    continue;
                }
                stack.push(vertex);
                for (var node : adj) {
                    if (visited[node]) continue;
                    stack.push(node);
                }
            }
        }
        for (var toAdd : queue) {
            stack.push(toAdd);
        }

        return stack;
    }
    private Stack<Integer> performTopSortHorizontally(double[] distTo, int[] edgeTo, int size, int length) {
        Stack<Integer> stack = new Stack<>();
        Queue<Integer> queue = new Queue<>();
        boolean[] visited = new boolean[width * height];
        for (int i = size - 1; i >= 0; i--) {
            int index = twoDTo1D(i, 0, length);
            distTo[index] = energy(0, i);
            edgeTo[index] = -1;
            stack.push(index);
            while (!stack.isEmpty()) {
                int vertex = stack.pop();
                if (visited[vertex]) {
                    queue.enqueue(vertex);
                    continue;
                }
                visited[vertex] = true;
                var adj = getUnvisitedAdjHorizontally(vertex, size, length, visited);
                if (adj.isEmpty()) {
                    queue.enqueue(vertex);
                    continue;
                }
                stack.push(vertex);
                for (var node : adj) {
                    if (visited[node]) continue;
                    stack.push(node);
                }
            }
        }
        for (var toAdd : queue) {
            stack.push(toAdd);
        }

        return stack;
    }
    
    private void traceTopSort(double[] distTo, int[] edgeTo, int size, int length, Stack<Integer> sorted) {
        for (int graphNode : sorted) {
            for (int adjNode : getAdj(graphNode, size, length)) {
                if (distTo[adjNode] != 0) {
                    double value = distTo[graphNode];
                    var dims = oneDTo2D(adjNode, size);
                    value += energy(dims[1], dims[0]);
                    if (distTo[adjNode] > value) {
                        distTo[adjNode] = value;
                        edgeTo[adjNode] = graphNode;
                    }
                }
                else {
                    double value = distTo[graphNode];
                    var dims = oneDTo2D(adjNode, size);
                    value += energy(dims[1], dims[0]);
                    distTo[adjNode] = value;
                    edgeTo[adjNode] = graphNode;
                }
            }
        }
    }
    private void traceTopSortHorizontally(double[] distTo, int[] edgeTo, int size, int length, Stack<Integer> sorted) {
        for (int graphNode : sorted) {
            for (int adjNode : getAdjHorizontally(graphNode, size, length)) {
                if (distTo[adjNode] != 0) {
                    double value = distTo[graphNode];
                    var dims = oneDTo2D(adjNode, length);
                    value += energy(dims[1], dims[0]);
                    if (distTo[adjNode] > value) {
                        distTo[adjNode] = value;
                        edgeTo[adjNode] = graphNode;
                    }
                }
                else {
                    double value = distTo[graphNode];
                    var dims = oneDTo2D(adjNode, length);
                    value += energy(dims[1], dims[0]);
                    distTo[adjNode] = value;
                    edgeTo[adjNode] = graphNode;
                }
            }
        }
    }

    private Queue<Integer> getUnvisitedAdj(int index, int size, int length, boolean[] visited) {
        Queue<Integer> queue = new Queue<>();
        if ((index + size) / size >= length) return queue;
        int nodeToAdd = index;
        if ((index % size) != 0) {
            nodeToAdd = index + size - 1;
            if (!visited[nodeToAdd]) queue.enqueue(nodeToAdd);
        }
        nodeToAdd = index + size;
        if (!visited[nodeToAdd]) queue.enqueue(nodeToAdd);
        if ((index % size) != (size - 1)) {
            nodeToAdd = index + size + 1;
            if (!visited[nodeToAdd]) queue.enqueue(nodeToAdd);
        }
        return queue;
    }
    private Queue<Integer> getUnvisitedAdjHorizontally(int index, int size, int length, boolean[] visited) {
        Queue<Integer> queue = new Queue<>();
        int[] dims = oneDTo2D(index, length);
        if (dims[1] >= length - 1) return queue;
        int nodeToAdd = index;
        if ((dims[0]) != 0) {
            nodeToAdd = index - length + 1;
            if (!visited[nodeToAdd]) queue.enqueue(nodeToAdd);
        }
        nodeToAdd = index + 1;
        if (!visited[nodeToAdd]) queue.enqueue(nodeToAdd);
        if ((dims[0]) != (size - 1)) {
            nodeToAdd = index + length + 1;
            if (!visited[nodeToAdd]) queue.enqueue(nodeToAdd);
        }
        return queue;
    }

    private Queue<Integer> getAdj(int index, int size, int length) {
        Queue<Integer> queue = new Queue<>();
        if ((index + size) / size >= length) return queue;
        if ((index % size) != 0) queue.enqueue(index + size - 1);
        queue.enqueue(index + size);
        if ((index % size) != (size - 1)) queue.enqueue(index + size + 1);
        return queue;
    }
    private Queue<Integer> getAdjHorizontally(int index, int size, int length) {
        Queue<Integer> queue = new Queue<>();
        int[] dims = oneDTo2D(index, length);
        if (dims[1] >= length - 1) return queue;
        if ((dims[0]) != 0) {
            queue.enqueue(index - length + 1);
        }
        queue.enqueue(index + 1);
        if ((dims[0]) != (size - 1)) {
            queue.enqueue(index + length + 1);
        }
        return queue;
    }
    
    private void validateEnergyArguments(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height) throw new IllegalArgumentException("(x: " + x +",\ty: " + y + ")");
    }

    // x col width => y row height
    private double getEnergy(int x, int y) {
        validateEnergyArguments(x, y);
        double delta = 0;
        if (x == 0 || x == width - 1 || y == 0 || y == height - 1) {
            delta = 1000;
        }
        else {
            var color1 = currentPicture.get(x - 1, y);
            var color2 = currentPicture.get(x + 1, y);
            int red = color1.getRed() - color2.getRed();
            int blue = color1.getBlue() - color2.getBlue();
            int green = color1.getGreen() - color2.getGreen();
            delta = Math.pow(red, 2) + Math.pow(blue, 2) + Math.pow(green, 2);

            color1 = this.currentPicture.get(x, y - 1);
            color2 = this.currentPicture.get(x, y + 1);
            red = color1.getRed() - color2.getRed();
            blue = color1.getBlue() - color2.getBlue();
            green = color1.getGreen() - color2.getGreen();
            delta += Math.pow(red, 2) + Math.pow(blue, 2) + Math.pow(green, 2);

            delta = Math.sqrt(delta);
        }

        return delta;
    }
    // remove horizontal seam from current picture
    public void removeHorizontalSeam(int[] seam) {
        if (seam == null || height <= 1 || seam.length != width || !isValidSeam(seam, height)) {
            throw new IllegalArgumentException();
        }
        height--;
        Picture newPic = new Picture(width, height);
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (j < seam[i]) {
                    newPic.set(i, j, currentPicture.get(i, j));
                }
                else {
                    newPic.set(i, j, currentPicture.get(i, j + 1));
                }
            }
        }
        this.currentPicture = new Picture(newPic);
    }
 
    // remove vertical seam from current picture
    public void removeVerticalSeam(int[] seam) {
        if (seam == null || width <= 1 || seam.length != height || !isValidSeam(seam, width)) {
            throw new IllegalArgumentException();
        }
        width--;
        Picture newPic = new Picture(width, height);
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (j < seam[i]) {
                    newPic.set(j, i, currentPicture.get(j, i));
                }
                else {
                    newPic.set(j, i, currentPicture.get(j + 1, i));
                }
            }
        }
        this.currentPicture = new Picture(newPic);
    }

    private int twoDTo1D(int row, int col, int length) {
        return row * length + col;
    }

    private int[] oneDTo2D(int index, int length) {
        int[] newArr = new int[2];
        newArr[0] = index / length;
        newArr[1] = index % length;
        return newArr;
    }

    private boolean isValidSeam(int[] seam, int maxValue) {
        int length = seam.length;
        for (int i = 0; i < length - 1; i++) {
            if (Math.abs(seam[i] - seam[i + 1]) > 1) return false;
        }
        for (int i = 0; i< length; i++) {
            if (seam[i] < 0 || seam[i] >= maxValue) return false;
        }
        return true;
    }
    //  unit testing (optional)
    public static void main(String[] args) {
        Picture pic = new Picture("3x4.png");
        var s = new SeamCarver(pic);
        int[] horizontalSeam = s.findHorizontalSeam();
        for(int i=0; i< horizontalSeam.length; i++) {
            StdOut.print(horizontalSeam[i] + "\t");
        }
        StdOut.print("\n");
        int[] verticalSeam = s.findVerticalSeam();
        for(int i=0; i< verticalSeam.length; i++) {
            StdOut.print(verticalSeam[i] + "\t");
        }
        int[] seam = new int[] {0, 1, 2, 3};
        s.removeVerticalSeam(seam);
        Picture newPic = s.picture();
        newPic.save("newPic.png");
        
        
    }
 
 }