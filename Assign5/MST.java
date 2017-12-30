package apps;

import structures.*;
import java.util.ArrayList;

public class MST {
  
   /**
   * Initializes the algorithm by building single-vertex partial trees
   *
   * @param graph Graph for which the MST is to be found
   * @return The initial partial tree list
   */
  

	public static PartialTreeList initialize(Graph graph)
	{/* COMPLETE THIS METHOD */
	  
	   PartialTreeList l = new PartialTreeList();
	   Vertex[] v= graph.vertices;
	   PartialTree T;
	   boolean[] visited = new boolean[v.length];
	   PartialTree.Arc a = null;
	   int counter = 0;
	   for(int i =0 ; i < v.length;i++)
	   {
	       Vertex.Neighbor x = v[i].neighbors;
	       T = new PartialTree(v[i]);
	       visited[i] = true;
	       System.out.println(v[i]);
	       MinHeap<PartialTree.Arc> P = T.getArcs();
	      
	       while(x!= null)
	       {
	       a = new PartialTree.Arc(v[i], x.vertex,x.weight);
	
	       P.insert(a);
	       P.siftDown(counter);
	       x = x.next;
	
	       }
	       if(visited[i] == true)
	       {
	           l.append(T);
	       }
	       counter++;
	
	      
	   }
	  
	       return l;
	  
	}
   /**
   * Executes the algorithm on a graph, starting with the initial partial tree list
   *
   * @param ptlist Initial partial tree list
   * @return Array list of all arcs that are in the MST - sequence of arcs is irrelevant
   */
   public static ArrayList<PartialTree.Arc> execute(PartialTreeList ptlist)
   {/* COMPLETE THIS METHOD */
       ArrayList<PartialTree.Arc> list = new ArrayList();  
      
       while(ptlist.size()>1)
       {
          
           PartialTree PTX = ptlist.remove(); 
           
           PartialTree.Arc PQX = null;
          
           PartialTree.Arc storeArc = PTX.getArcs().getMin();
          
           Vertex v1 = storeArc.v1;
          
           Vertex v2 = storeArc.v2;
      
           checkingV2(PTX,v1,v2,PQX,storeArc);
         
            PQX = PTX.getArcs().deleteMin(); 
          
         
           System.out.println(PQX +" "+"is a component of the MST");
      
           PartialTree PTY = ptlist.removeTreeContaining(PQX.v2); 
           
            PTX.merge(PTY);
          
            list.add(PQX);
          
            ptlist.append(PTX);
          

       }
          
       return list;
   }
   private static void checkingV2(PartialTree PTX, Vertex v1,Vertex v2, PartialTree.Arc PQX, PartialTree.Arc storeArc)
   {
    while(compareMethod(v2, PTX) == true)
       {
       PQX = PTX.getArcs().deleteMin();    
      
       storeArc = PTX.getArcs().getMin();
     
       v1 = storeArc.v1;
     
       v2 = storeArc.v2;
       }
  
   }
   private static boolean compareMethod(Vertex v2, PartialTree PTX)
   {
        while(v2 != null)
        {
            if(PTX.getRoot() == v2)
            {
                return true;
            }
            if(v2.equals(v2.parent))
            {
                return false;
            }
            v2 = v2.parent;
        }
        return false;
    }
}