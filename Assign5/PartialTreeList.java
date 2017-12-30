package apps;

import java.util.Iterator;
import java.util.NoSuchElementException;

import structures.Vertex;


public class PartialTreeList implements Iterable<PartialTree> {
  
   /**
   * Inner class - to build the partial tree circular linked list
   *
   */
   public static class Node {
       /**
       * Partial tree
       */
       public PartialTree tree;
      
       /**
       * Next node in linked list
       */
       public Node next;
      
       /**
       * Initializes this node by setting the tree part to the given tree,
       * and setting next part to null
       *
       * @param tree Partial tree
       */
       public Node(PartialTree tree) {
           this.tree = tree;
           next = null;
       }
   }

   /**
   * Pointer to last node of the circular linked list
   */
   private Node rear;
  
   /**
   * Number of nodes in the CLL
   */
   private int size;
  
   /**
   * Initializes this list to empty
   */
    public PartialTreeList() {
       rear = null;
       size = 0;
    }

    /**
     * Adds a new tree to the end of the list
     *
     * @param tree Tree to be added to the end of the list
     */
    public void append(PartialTree tree) {
       Node ptr = new Node(tree);
       if (rear == null) {
           ptr.next = ptr;
       } else {
           ptr.next = rear.next;
           rear.next = ptr;
       }
       rear = ptr;
       size++;
    }

  
    public PartialTree remove() throws NoSuchElementException
    {/* COMPLETE THIS METHOD */
       Node n = null;
       if(rear == null) 
         {
            throw new NoSuchElementException();
         }
         else
         {
            size--;
            if(rear.next == rear)
            {
                n = rear;
                rear = null;
                return n.tree;
            }
            else
            {
                n = rear.next;
                rear.next = rear.next.next;
                return n.tree;
            }
        }
    }
  

    public PartialTree removeTreeContaining(Vertex vertex) throws NoSuchElementException
    {/* COMPLETE THIS METHOD */
               PartialTree output = null;
                boolean found = false;
                Node storeTree = null;
             
      
                if(rear == null)
                {
                    throw new NoSuchElementException();
                }
              
                Node ptr = rear.next;
              
                Node prev = rear;
              
                int i = 0;
              
                output = removeTree(i, vertex, ptr, found, output, prev);
      
                return output;

    }
    private PartialTree removeTree(int i,Vertex vertex,Node ptr, boolean found,PartialTree output,Node prev)
   {
           do
            {
                if(removeTreeTraverse(vertex, ptr.tree,found) == true)
                {
                    if(ptr == rear)
                    {
                        output = ptr.tree;
                      
                        prev.next = rear.next;
                      
                        rear = prev;
                      
                        size--;
                      
                        return output;
                    }
                      else
                    {
                        output = ptr.tree;
                      
                        prev.next = ptr.next;
                      
                        size--;
                      
                        return output;
                    }
        
                }
                   prev = ptr;
           
                   ptr = ptr.next;
        
                   i++;
                  
            }while(i < size);
         
           return output;
       }
    private boolean removeTreeTraverse(Vertex vertex, PartialTree Tree, boolean found)
    {
        while(vertex != null)
        {
            if(vertex == Tree.getRoot())
            {
                found = true;
                return true;
            }
            if(vertex.equals(vertex.parent))
            {
                return false;
            }
      
            vertex = vertex.parent;
        }
        return false;
    }
          

   
  
  
    public int size() {
       return size;
    }
  

    public Iterator<PartialTree> iterator() {
       return new PartialTreeListIterator(this);
    }
  
    private class PartialTreeListIterator implements Iterator<PartialTree> {
      
       private PartialTreeList.Node ptr;
       private int rest;
      
       public PartialTreeListIterator(PartialTreeList target) {
           rest = target.size;
           ptr = rest > 0 ? target.rear.next : null;
       }
      
       public PartialTree next()
       throws NoSuchElementException {
           if (rest <= 0) {
               throw new NoSuchElementException();
           }
           PartialTree ret = ptr.tree;
           ptr = ptr.next;
           rest--;
           return ret;
       }
      
       public boolean hasNext() {
           return rest != 0;
       }
      
       public void remove()
       throws UnsupportedOperationException {
           throw new UnsupportedOperationException();
       }
      
    }
}

