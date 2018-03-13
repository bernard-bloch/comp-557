package comp557lw.a3;

import javax.vecmath.Point3d;

/**
 * Class implementing the Catmull-Clark subdivision scheme
 * 
 * @author Jonathan Bernard Bloch
 */
public class CatmullClark {

    /**
     * Subdivides the provided half edge data structure
     * @param heds
     * @return the subdivided mesh
     */
    public static HEDS subdivide( HEDS heds ) {
        HEDS heds2 = new HEDS();
        
        
        // TODO: Objectives 2,3,4: finish this method!!
        // you will certainly want to write lots of helper methods!
        for(Face face : heds.faces) addChildVerticesToFace(face);
        for(Face face : heds.faces) addEven(face);
        
        return heds2;        
    }
    
    // Odd vertices are created at each edge and in the center of each face.
    private static void addChildVerticesToFace(Face face) 
    {
    	Point3d p = new Point3d();
    	int count = 0;
    	HalfEdge he = face.he;
    	do
    	{
    		assert(he != null);
    		p.add(he.head.p);
    		count++;
    		he = he.next;
    	} while(he != face.he);
    	assert(count > 2);
    	p.scale(1/(double)count);
    	face.child = new Vertex();
    	face.child.p = p;
    }
    
    private static void addEven(Face face) {
    	HalfEdge he = face.he;
    	do
    	{
    		assert(he != null);
    		even(he);
    		he = he.next;
    	} while(he != face.he);
    }
    
    private static Vertex even(HalfEdge he) {
    	// If there is a child, return it
    	if(he.head.child != null) return he.head.child;
    	// build the child
    	Point3d nextBorder = nextBorder(he);
    	Point3d p = he.head.p;
    	if(nextBorder != null)
    	{
    		// masks for even vertices
    		Point3d border = border(he);
    		assert(border != null);
    		p.scale(6);
    		p.add(nextBorder);
    		p.add(border);
    		p.scale(1/8);
    	}
    	else {
    		// interior point
    		assert(false);
    	}
    	assert(p != null);
    	he.head.child = new Vertex();
    	he.head.child.p = p;
   
    	// return the child
    	return he.head.child;
    }
        
    private static void addChildToEdges(Face face) {
    	HalfEdge prev = face.he.prev();
    	HalfEdge he = face.he;
    	int k = 0;
    	do
    	{
    		k++;
    		assert(k < 50);
    		assert(he != null);
    		if(he.child1 != null)
    		{
    			prev = he;
    			continue;
    		}
    		Point3d temp = new Point3d();
    		temp.add(prev.head.p);
    		temp.add(he.head.p);
    		if(he.twin == null)
    		{
    			// border, do not have twin, just make do
    			temp.scale(1/(double)2);
    		}
    		else {
    			temp.add(he.leftFace.child.p);
    			temp.add(he.twin.leftFace.child.p);
    			temp.scale(1/(double)4);
    		}
    		Vertex edge = new Vertex();
    		edge.p = temp;
    		Vertex head = even(he);
    		Vertex tail = even(prev);
    		assert(he.child1 == null && he.child2 == null);
    		he.child1 = new HalfEdge();
    		he.child1.head = edge;
    		he.child1.parent = he;
    		he.child2 = new HalfEdge();
    		he.child2.head = head;
    		he.child2.parent = he;
    		if(he.twin != null)
    		{
        		assert(he.twin.child1 == null && he.twin.child2 == null);
    			he.twin.child1 = new HalfEdge();
    			he.twin.child1.head = edge;
    			he.twin.child1.parent = he.twin;
    			he.twin.child2 = new HalfEdge();
    			he.twin.child2.head = tail;
    			he.twin.child2.parent = he.twin;
    		}
    		prev = he;
    		he = he.next;
    		break;
    	} while(he != face.he);

    }
    
    private static Vertex midpoint(Vertex a, Vertex b)
    {
    	Vertex c = new Vertex();
    	c.p.x = (a.p.x+ b.p.x)/2.0;
    	c.p.y = (a.p.y + b.p.y)/2.0;
    	c.p.z = (a.p.z + b.p.z)/2.0;
    	return c;
    }
    
    private static Point3d nextBorder(HalfEdge he)
    {
    	HalfEdge next = he.next;
    	while(next.twin != null)
    	{
    		next = next.twin.next;
    		// nope
    		if(next.twin == he) return null;
    	}
    	return next.prev().head.p;
    }
    
    private static Point3d border(HalfEdge he)
    {
    	HalfEdge next = he;
    	while(next.twin != null)
    	{
    		next = next.twin.prev();
    		// nope
    		if(next == he) return null;
    	}
    	return next.prev().head.p;
    }
        
}
