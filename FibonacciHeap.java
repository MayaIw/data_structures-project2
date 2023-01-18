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
    	return new HeapNode(key); // should be replaced by student code
    }

   /**
    * public void deleteMin()
    *
    * Deletes the node containing the minimum key.
    *
    */
    public void deleteMin()
    {
     	return; // should be replaced by student code

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
    	  return; // should be replaced by student code
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
        int maxOrder = (int) Math.floor(Math.log(n+1));
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
    	return; // should be replaced by student code
    }

   /**
    * public void decreaseKey(HeapNode x, int delta)
    *
    * Decreases the key of the node x by a non-negative value delta. The structure of the heap should be updated
    * to reflect this change (for example, the cascading cuts procedure should be applied if needed).
    */
    public void decreaseKey(HeapNode x, int delta)
    {
    	return; // should be replaced by student code
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
        return -234; // should be replaced by student code
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
    	return numofLinks; // should be replaced by student code
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
    	return -456; // should be replaced by student code
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
   }
}
