import java.lang.Math;
/**
 * FibonacciHeap
 *
 * An implementation of a Fibonacci Heap over integers.
 *
 * Implementation by: Sharon Sorin (sharonsorin, 322756156) and Maya Iwanir (mayaiwanir, 322885203)
 */
public class FibonacciHeap
{
    public int size; // Number of elements in the heap
    public HeapNode min; // The node with the smallest key
    public HeapNode first; // The first root
    private static int numOfLinks = 0; //Should be increased for every link operation
    private static int numOfCuts = 0; //Should be increased for every cut operation
    private int numOfTrees = 0; // Counts the number of trees in the heap
    private int numOfMarked = 0; // Counts the number of marked nodes in the heap

    /** public HeapNode getFirst()
     *
     * Returns the leftmost tree, which was inserted last to the heap
     * Complexity: O(1)
     *
     */
    public HeapNode getFirst(){
        return this.first;
    }

    /** public HeapNode getLast()
     *
     * Returns the rightmost tree, which was inserted first to the heap
     * Complexity: O(1)
     *
     */
    public HeapNode getLast(){
        return this.first.getPrev();
    }

   /**
    * public boolean isEmpty()
    *
    * Returns true if and only if the heap is empty.
    * Complexity: O(1)
    *
    */
    public boolean isEmpty()
    {
        return this.size()==0;
    }

   /**
    * public HeapNode insert(int key)
    *
    * Creates a node (of type HeapNode) which contains the given key, and inserts it into the heap.
    * The added key is assumed not to already belong to the heap.
    * Complexity: O(1)
    *
    * Returns the newly created node.
    */
    public HeapNode insert(int key)
    {
        HeapNode node = new HeapNode(key);
        if (size == 0) {
            first = node;
            min = node;
            node.setNext(node);
            node.setPrev(node);
        } else {
            node.setPrev(getLast());
            getLast().setNext(node);
            node.setNext(first);
            first.setPrev(node);
            first = node;
            if (node.getKey() < min.getKey()) {
                min = node;
            }
        }
        numOfTrees++;
        size++;
        return node;
    }

   /**
    * public void deleteMin()
    *
    * Deletes the node containing the minimum key.
    * Complexity: O(n)
    *
    */
    public void deleteMin()
    {
        if (min.getRank() == 0) {  // if min has no children, connect its prev to its next
            // if min is the only node, we will fix that after detecting that numOfTrees became 0
            min.getPrev().setNext(min.getNext());
            min.getNext().setPrev(min.getPrev());
        } else {  // otherwise, put its children between its prev and ites next
            min.getChild().getPrev().setNext(min.getNext());
            min.getNext().setPrev(min.getChild().getPrev());
            min.getChild().setPrev(min.getPrev());
            min.getPrev().setNext(min.getChild());
        }

        if (first.getKey() == min.getKey()) { // if we delete the first node we need to update first to point somewhere
            first = min.getPrev().getNext();  // if min had no children this will be min.next, otherwise it's the first child
        }

        numOfTrees += min.getRank() - 1;
        size--;

        if (numOfTrees == 0) {
            first = null;
            min = null;
            numOfMarked = 0;
        } else {
            consolidate();
        }
    }

    /**
     * private void consolidate()
     *
     * Go over all trees and preform successive linking.
     * Update numOfTrees, min, numOfMarked (unmark roots), numOfLinks, nullify parents of roots.
     *
     * Assumes that the heap is not empty.
     * Complexity: O(n)
     *
     */
    private void consolidate() {
        // We create an array of buckets which will contain all possible degrees
        HeapNode[] buckets = new HeapNode[(int) (2.5 * Math.log(size + 1) + 1)];

        // successive linking:
        // for every tree, try to put it in the right bucket, if there's a tree there, link and propogate forward
        // also fix number of trees, minimum, and root markings in this process
        HeapNode curr = first;
        HeapNode nextNode;
        int totalNumOfTrees = numOfTrees;
        int newMin = Integer.MAX_VALUE;

        for (int i = 0; i < totalNumOfTrees; i++) {
            nextNode = curr.getNext(); // we keep it now because we change curr in the loop
            if (curr.getMarked()) { // we were asked to unmark all the roots including those created before consolidate
                curr.unmarkNode();
                numOfMarked--;
            }

            // linking loop
            while (buckets[curr.getRank()] != null) {
                curr = HeapNode.link(curr, buckets[curr.getRank()]);
                buckets[curr.getRank() - 1] = null;
                numOfTrees--; // the actual num of trees decreased after linking
            }
            buckets[curr.getRank()] = curr;
            if (curr.getKey() < newMin) {
                min = curr;
                newMin = curr.getKey();
            }
            curr = nextNode;
        }

        // Redefine the "list of trees".
        first = null;

        curr = null;
        for (HeapNode root : buckets) {
            if (root == null) {
                continue;
            }
            if (first == null) {
                first = root;
                curr = first;
            } else {
                curr.setNext(root);
                root.setPrev(curr);
                curr = root;
            }

            curr.setParent(null);
        }
        curr.setNext(first); // last tree;
        first.setPrev(curr);
    }

   /**
    * public HeapNode findMin()
    *
    * Returns the node of the heap whose key is minimal, or null if the heap is empty.
    * Complexity: O(1)
    *
    */
    public HeapNode findMin()
    {
        if (this.isEmpty()) {
            return null;
        }
        return min;
    }

   /**
    * public void meld (FibonacciHeap heap2)
    *
    * Melds heap2 with the current heap.
    * Complexity: O(1)
    *
    */
    public void meld (FibonacciHeap heap2)
    {
        if (heap2.size() == 0) {
            return;
        }
        if (size == 0) {
            first = heap2.first;
            min = heap2.min;
        } else {
            HeapNode last1 = getLast();
            HeapNode last2 = heap2.getLast();
            last1.setNext(heap2.first);
            heap2.first.setPrev(last1);
            last2.setNext(first);
            first.setPrev(last2);
        }
        if (heap2.min.getKey() < min.getKey()) {
            min = heap2.min;
        }
        size += heap2.size();
        numOfTrees += heap2.numOfTrees;
        numOfMarked += heap2.numOfMarked;
    }

   /**
    * public int size()
    *
    * Returns the number of elements in the heap.
    * Complexity: O(1)
    *
    */
    public int size()
    {
    	return this.size;
    }

    /**
     * public int[] countersRep()
     *
     * Return an array of counters. The i-th entry contains the number of trees of order i in the heap.
     * (Note: The size of of the array depends on the maximum order of a tree.)
     * Complexity: O(n)
     *
     */
    public int[] countersRep()
    {
        if (this.isEmpty()) {
            return new int[0];
        }
        int[] counters = new int[getMaxRank() + 1];
        HeapNode current = first;
        for (int i = 0; i < numOfTrees; i++) {
            counters[current.getRank()] += 1;
            current = current.getNext();
        }
        return counters;
    }

    /**
     * private int getMaxRank()
     *
     * Returns the maximal rank of a tree in the heap.
     * Complexity: O(n)
     *
     */
    private int getMaxRank() {
        int maxRank = 0;
        HeapNode current = first;
        for (int i = 0; i < numOfTrees; i++) {
            maxRank = Math.max(current.getRank(), maxRank);
            current = current.getNext();
        }
        return maxRank;
    }

   /**
    * public void delete(HeapNode x)
    *
    * Deletes the node x from the heap.
	* It is assumed that x indeed belongs to the heap.
    * Complexity: O(n)
    *
    */
    public void delete(HeapNode x)
    {
        decreaseKey(x, x.getKey() - Integer.MIN_VALUE);
        deleteMin();
    }

   /**
    * public void decreaseKey(HeapNode x, int delta)
    *
    * Decreases the key of the node x by a non-negative value delta. The structure of the heap should be updated
    * to reflect this change (for example, the cascading cuts procedure should be applied if needed).
    * Complexity: O(n)
    *
    */
    public void decreaseKey(HeapNode x, int delta)
    {
        x.setKey(x.getKey() - delta);
        if (x.getKey() < min.getKey()) {
            min = x;
        }
        if (x.getParent() == null) {
            return;
        }
        if (x.getKey() > x.getParent().getKey()) {
            return;
        }

        cut(x);
    }


    /**
     * public void cut(HeapNode x)
     *
     * Cut a node x from its parent. Continue cascading cuts if needed. Assumes x is marked.
     * Complexity: O(n)
     *
     */
    public void cut(HeapNode x) {
        HeapNode parent;
        do { // process x, then move to its parent
            numOfCuts++;
            parent = x.getParent();
            // remove x from its parents children
            if (parent.getRank() == 1) {
                parent.setChild(null);
                parent.setRank(0);
            } else {
                if (parent.getChild() == x) {
                    parent.setChild(x.getNext());
                }

                x.getPrev().setNext(x.getNext());
                x.getNext().setPrev(x.getPrev());

                parent.setRank(parent.getRank() - 1);
            }
            // insert x to tree list
            x.setParent(null);
            if (x.getMarked()) {
                x.unmarkNode();
                numOfMarked--;
            }
            x.setPrev(getLast());
            getLast().setNext(x);
            x.setNext(first);
            first.setPrev(x);
            first = x;
            numOfTrees++;

            x = parent;
        } while (x.getMarked()); // continue as long as the parent is marked

        if (!(x.getParent() == null)) { // if current node is not a root, set it to be marked
            x.markNode();
            numOfMarked++;
        }
    }

   /**
    * public int nonMarked()
    *
    * This function returns the current number of non-marked items in the heap
    * Complexity: O(1)
    */
    public int nonMarked()
    {
        return this.size-this.numOfMarked;
    }

   /**
    * public int potential()
    *
    * This function returns the current potential of the heap, which is:
    * Potential = #trees + 2*#marked
    *
    * In words: The potential equals to the number of trees in the heap
    * plus twice the number of marked nodes in the heap.
    *
    * Complexity: O(1)
    *
    */
    public int potential()
    {
        return numOfTrees + 2*numOfMarked;
    }

   /**
    * public static int totalLinks()
    *
    * This static function returns the total number of link operations made during the
    * run-time of the program. A link operation is the operation which gets as input two
    * trees of the same rank, and generates a tree of rank bigger by one, by hanging the
    * tree which has larger value in its root under the other tree.
    * Complexity: O(1)
    */
    public static int totalLinks()
    {
    	return numOfLinks; // should be replaced by student code
    }

   /**
    * public static int totalCuts()
    *
    * This static function returns the total number of cut operations made during the
    * run-time of the program. A cut operation is the operation which disconnects a subtree
    * from its parent (during decreaseKey/delete methods).
    * Complexity: O(1)
    */
    public static int totalCuts()
    {
    	return numOfCuts;
    }

     /**
    * public static int[] kMin(FibonacciHeap H, int k)
    *
    * This static function returns the k smallest elements in a Fibonacci heap that contains a single tree.
    * The function should run in O(k*deg(H)). (deg(H) is the degree of the only tree in H.)
    *
    * ###CRITICAL### : you are NOT allowed to change H.
    * Complexity: O(k*deg(H))
    */
    public static int[] kMin(FibonacciHeap H, int k)
    {
        if (H.isEmpty() || (k == 0)) {
            return new int[0];
        }
        int[] minValues = new int[k];
        HeapNode curr = H.findMin();
        FibonacciHeap helperHeap = new FibonacciHeap();
        HeapNode cloneCurr = helperHeap.insert(curr.getKey());
        cloneCurr.setPointer(curr);
        for (int i=0 ; i<k; i++){
            HeapNode minimum = helperHeap.findMin().getPointer();
            minValues[i] = minimum.getKey();
            helperHeap.deleteMin();
            curr = minimum;

            HeapNode child = curr.getChild();
            if (child != null) {
                HeapNode cloneChild = helperHeap.insert(child.getKey());
                cloneChild.setPointer(child);
                while (child.getNext() != curr.getChild()) {
                    child = child.getNext();
                    cloneChild = helperHeap.insert(child.getKey());
                    cloneChild.setPointer(child);
                }
            }
        }
        return minValues;
    }

   /**
    * public class HeapNode
    *
    * If you wish to implement classes other than FibonacciHeap
    * (for example HeapNode), do it in this file, not in another file.
    *
    */
    public static class HeapNode{

    	public int key;
        public int rank;
        public boolean mark;
        public HeapNode child;  // represents the leftmost child among the heapNode's children
        public HeapNode next; // represents the next node, i.e. the right neighbor of the current node, or the leftmost node amongst its siblings if it's the rightmost one
        public HeapNode prev; // represents the previous node, i.e. the left neighbor of the current node, or the rightmost node amongst its siblings if it's the leftmost one
        public HeapNode parent;
        public HeapNode pointer;

    	public HeapNode(int key) {
    		this.key = key;
            this.child = null;
            this.next = null;
            this.prev = null;
            this.parent = null;
            this.mark = false;
            this.rank = 0;
            this.pointer = null;
    	}

       /** public int getKey()
        * returns the key
        *
        * Complexity: O(1))
        */
        public int getKey() {
    		return this.key;
    	}

       /** public void setKey()
        * sets the key
        * Complexity: O(1))
        */
        public void setKey(int key) {
            this.key = key;
        }

       /** public void setRank()
        * sets the rank (number of children)
        * Complexity: O(1))
        */
        public void setRank(int rank){
            this.rank = rank;
        }

       /** public int getRank()
        * returns the rank (number of children)
        * Complexity: O(1))
        */
        public int getRank(){
            return this.rank;
        }

       /** public void setChild()
        * sets the leftmost child
        * Complexity: O(1))
        */
        public void setChild(HeapNode child){
            this.child = child;
        }

       /** public HeapNode getChild()
        * returns the child attribute of the node (i.e. leftmost child amongst its children), null if the node's rank is 0
        * Complexity: O(1))
        */
        public HeapNode getChild(){
            return this.child;
        }

       /** public void setNext()
        * sets the next attribute
        * Complexity: O(1))
        */
        public void setNext(HeapNode next){
            this.next = next;
        }

       /** public HeapNode getNext()
        * returns the next attribute
        * Complexity: O(1))
        */
        public HeapNode getNext(){
            return this.next;
        }

       /** public void setPrev()
        * sets the prev attribute
        * Complexity: O(1))
        */
        public void setPrev(HeapNode prev){
            this.prev = prev;
        }

       /** public HeapNode getPrev()
        * returns the prev attribute
        * Complexity: O(1))
        */
        public HeapNode getPrev(){
            return this.prev;
        }

       /** public void unmarkNode()
        * marks node
        * Complexity: O(1))
        */
        public void markNode(){
            this.mark = true;
        }

       /** public void unmarkNode()
        * unmarks node
        * Complexity: O(1))
        */
        public void unmarkNode(){
            this.mark = false;
        }

       /** public boolean getMarked()
        * returns true iff this node is marked
        * Complexity: O(1))
        */
        public boolean getMarked(){
            return this.mark;
        }

       /** public HeapNode setParent()
        * sets the parent
        * Complexity: O(1))
        */
        public void setParent(HeapNode parent){
            this.parent = parent;
        }

       /** public HeapNode getParent()
        * returns the parent, null if there is no parent
        * Complexity: O(1))
        */
        public HeapNode getParent() {
           return this.parent;
       }

       /** public HeapNode setPointer()
        * sets the pointer (to another node)
        * Complexity: O(1))
        */
       public void setPointer(HeapNode pointer){
            this.pointer = pointer;
       }

       /** public HeapNode getPointer()
        * returns the pointer (to another node), null if no pointer was attached
        * Complexity: O(1))
        */
       public HeapNode getPointer(){
            return this.pointer;
       }

       /** public static HeapNode link(HeapNode root1, HeapNode root2)
        *
        * Link two roots. The root with the smaller key is returned, after the other root has been set to be
        * its leftmost child.
        * Complexity: O(1))
        */
       public static HeapNode link(HeapNode root1, HeapNode root2) {
           if (root1.getKey() > root2.getKey()) {
               return link(root2, root1);
           }
           numOfLinks++;

           if (root1.getRank() == 0) {
               root2.setNext(root2);
               root2.setPrev(root2);
           } else {
               HeapNode currFirst = root1.getChild();
               HeapNode currLast = currFirst.getPrev();
               currLast.setNext(root2);
               root2.setPrev(currLast);
               currFirst.setPrev(root2);
               root2.setNext(currFirst);

           }
           root1.setChild(root2);
           root2.setParent(root1);
           root1.rank++;

           return root1;
       }
   }
}
