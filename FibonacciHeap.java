import java.lang.Math;
/**
 * FibonacciHeap
 *
 * An implementation of a Fibonacci Heap over integers.
 */
public class FibonacciHeap
{
    public int size; // Number of elements in the heap
    public HeapNode min;
    private int markedNodes = 0; //Should be increased for every new marked node
    private int numofTrees = 0;
    public HeapNode first;
    private static int numofLinks = 0; //Should be increased for every link operation
    private static int numOfLinks = 0; //should be increased for every link operation
    private static int numOfCuts = 0; //should be increased for every cut operation
    private int numOfTrees = 0;
    private int numOfMarked = 0;

    public HeapNode getFirst(){
        return this.first;
    }
   /**
    * public boolean isEmpty()
    *
    * Returns true if and only if the heap is empty.
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
            node.setPrev(first.prev());
            node.setNext(first.next());
            first.prev().setNext(node);
            first.setPrev(node);
            if (node.getKey() < min.getKey()) {min = node;}
        }
        numOfTrees++;
        size++;
        return node;
    }

   /**
    * public void deleteMin()
    *
    * Deletes the node containing the minimum key.
    *
    */
    public void deleteMin()
    {
        if (first.getKey() == min.getKey()) { // if we delete the first node we need to update first to point somewhere
            first = min.getChild();
        }
        if (numOfTrees == 1) { // only one tree
            first = min.getChild();
        } else {
            min.getChild().prev().setNext(min.next());
            min.next().setPrev(min.getChild().prev());
            min.getChild().setPrev(min.prev());
            min.prev().setNext(min.getChild());
        }

        numOfTrees += min.getRank() - 1;
        numOfCuts += min.getRank(); //todo: check in forum if this counts
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
     * Assume heap is not empty.
     */
    private void consolidate() {
        // We create an array of buckets which will containg all possible degrees
        // todo: if ArrayList is allowed, switch to dynamic array
        HeapNode[] buckets = new HeapNode[(int)(1.5 * Math.log(size + 1) + 1)];

        // successive linking:
        // for every tree, try to put it in the right bucket, if there's a tree there, link and propogate forward
        // also fix number of trees, minimum, and root markings in this process
        HeapNode curr = first;
        int totalNumofTrees = numOfTrees;
        int newMin = Integer.MAX_VALUE;

        for (int i = 0; i < totalNumofTrees; i ++) {
            if (curr.isMarked()) { // we were asked to unmark all the roots including those created before consolidate
                curr.unmarkNode();
                numOfMarked--;
            }

            // linking loop
            while (buckets[curr.getRank()] != null) {
                curr = HeapNode.link(curr, buckets[curr.getRank()]);
                numOfTrees--; // the actual num of trees decreased after linking
            }
            buckets[curr.getRank()] = curr;
            if (curr.getKey() < newMin) {
                min = curr;
                newMin = curr.getKey();
            }
            curr = curr.next();
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
    *
    */
    public HeapNode findMin()
    {
    	if(this.isEmpty()){
            return null;
        }
        return this.min;
    }

   /**
    * public void meld (FibonacciHeap heap2)
    *
    * Melds heap2 with the current heap.
    *
    */
    public void meld (FibonacciHeap heap2)
    {
    	  HeapNode last1 = first.prev();
          HeapNode last2 = heap2.first.prev();
          last1.setNext(heap2.first);
          heap2.first.setPrev(last1);
          last2.setNext(first);
          first.setPrev(last2);

          if (heap2.min.getKey() < min.getKey()) {
              min = heap2.min;
          }
          size += heap2.size();
          numOfTrees += heap2.numOfTrees;

//          if (maximalRank < heap2.maximalRank) {
//              maximalRank = heap2.maximalRank;
//          }
    }

   /**
    * public int size()
    *
    * Returns the number of elements in the heap.
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
    *
    */
    public int[] countersRep()
    {
        if(this.isEmpty()){
            return new int[0];
        }
        int n = this.size();
        int maxOrder = (int)Math.floor(Math.log(n+1)); //todo: need to find the real maximum
    	int[] counters = new int[maxOrder];
        HeapNode current = this.getFirst();
        counters[current.getRank()] += 1;
        while(current.getNext() != this.getFirst()){
            current = current.getNext();
            counters[current.getRank()] += 1;
        }
        return counters;
    }

   /**
    * public void delete(HeapNode x)
    *
    * Deletes the node x from the heap.
	* It is assumed that x indeed belongs to the heap.
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
    */
    public void decreaseKey(HeapNode x, int delta)
    {
        x.setKey(x.getKey() - delta);
        if (x.getParent() == null) {return;}
        if (x.getKey() > x.getParent().getKey()) {return;}

        cut(x);
    }

    public void cut(HeapNode x) {
        numOfCuts++;
        HeapNode parent = x.getParent();
        // remove x from its parents children
        if (parent.getRank() == 1) {
            parent.setChild(null);
            parent.setRank(0);
        } else {
            if (parent.getChild() == x) {
                parent.setChild(x.next());
            } //todo: is this the expected behaviour? check in forum

            x.prev().setNext(x.next());
            x.next().setPrev(x.prev());

            parent.setRank(parent.getRank() - 1);
        }
        // insert x to tree list
        x.setParent(null);
        x.unmarkNode();
        x.setNext(first.next());
        x.setPrev(first.prev());
        first.prev().setNext(x);
        first.next().setPrev(x);
        first = x;
        if (x.getKey() < min.getKey()) {
            min = x;
        }

        // cascade
        if (parent.isMarked()) {
            cut(parent);
        } else {
            parent.markNode();
        }
    }

   /**
    * public int nonMarked()
    *
    * This function returns the current number of non-marked items in the heap
    */
    public int nonMarked()
    {
        return this.size-this.markedNodes;
    }

   /**
    * public int potential()
    *
    * This function returns the current potential of the heap, which is:
    * Potential = #trees + 2*#marked
    *
    * In words: The potential equals to the number of trees in the heap
    * plus twice the number of marked nodes in the heap.
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
    */
    public static int[] kMin(FibonacciHeap H, int k)
    {
        int[] minValues = new int[k];
        HeapNode curr = H.findMin();
        FibonacciHeap helperHeap = new FibonacciHeap();
        HeapNode cloneCurr = helperHeap.insert(curr.getKey());
        cloneCurr.setPointer(curr);
        for (int i=0 ; i<k; i++){
            HeapNode child = curr.getChild();
            HeapNode cloneChild = helperHeap.insert(child.getKey());
            cloneChild.setPointer(child);
            while(child.getNext()!=curr.getChild()){
                child = child.getNext();
                cloneChild = helperHeap.insert(child.getKey());
                cloneChild.setPointer(child);
            }
            HeapNode minimum = helperHeap.findMin().getPointer();
            minValues[i] = minimum.getKey();
            helperHeap.deleteMin();
            curr = minimum;
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
        public HeapNode child;
        public HeapNode next;
        public HeapNode prev;
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

    	public int getKey() {
    		return this.key;
    	}

        public void setKey(int key) {
            this.key = key;
        }

        public void setRank(int rank){
            this.rank = rank;
        }

        public int getRank(){
            return this.rank;
        }

        public void setChild(HeapNode child){
            this.child = child;
        }

        public HeapNode getChild(){
            return this.child;
        }

        public void setNext(HeapNode next){
            this.next = next;
        }

        public HeapNode getNext(){
            return this.next;
        }

        public void setPrev(HeapNode prev){
            this.prev = prev;
        }

        public HeapNode getPrev(){
            return this.prev;
        }

        public void markNode(){
            this.mark = true;
        }

        public void unmarkNode(){
            this.mark = false;
        }

        public boolean getMarked(){
            return this.mark;
        }

        public void setParent(HeapNode parent){
            this.parent = parent;
        }

       public HeapNode getParent() {
           return this.parent;
       }

       public void setPointer(HeapNode pointer){
            this.pointer = pointer;
       }

       public HeapNode getPointer(){
            return this.pointer;
       }

       /** public static HeapNode link(HeapNode root1, HeapNode root2)
        *
        * Link two roots. The root with the smaller key is returned, after the other root has been set to be
        * its leftmost child.
        */
       public static HeapNode link(HeapNode root1, HeapNode root2) {
            if (root1.getKey() > root2.getKey()) {
                return link(root2, root1);
            }
           numOfLinks++;

            HeapNode currFirst = root1.getChild();
            HeapNode currLast = currFirst.prev();
            currLast.setNext(root2);
            root2.setPrev(currLast);
            currFirst.setPrev(root2);
            root2.setNext(currFirst);
            root1.setChild(root2);
            root2.setParent(root1);
            root1.rank++;


            return root1;
       }
   }
}
