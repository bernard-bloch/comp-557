package comp557lw.a3;

import java.util.ArrayList;

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
        
        for(Face face : heds.faces) evenVertices(face);
        	
        //for(Face face : heds.faces) addChildVerticesToFace(face);
        //for(Face face : heds.faces) addEven(face);
        
        return heds2;        
    }
    
    // an even vertex is a vertex that is already in the mesh
    private static void evenVertices(Face face) {
        // question 2: even vertices of degree 2
    	HalfEdge he = face.he;
    	do {
        	Vertex neigbors[] = getHeadNeighbors(he);
    	} while((he = he.next) != face.he);
    }
    
    // count the degree of the head vertex
    private static int degreeHead(HalfEdge he) {
    	if(he == null) return 0;
    	int degree = 1;
    	HalfEdge cw = he;
    	while(true) {
    		cw = cw.next;
    		if(cw == null) break;
    		assert(cw != he);
    		if(cw.twin == he) break;
    		degree++;
    		cw = cw.twin;
    		if(cw == null) break;
    	}
    	if(cw != null) return degree;
    	// edge case
    	HalfEdge ccw = he;
    	while(true) {
    		ccw = ccw.twin;
    		if(ccw == null) break;
    		assert(ccw != he);
    		ccw = ccw.prev();
    		if(ccw == null) break;
    		assert(ccw != he);
    		degree++;
    	}
    	return degree;
    }
    
    // get all the vertices of distance 1 from the head. The size will be the degree.
    private static Vertex[] getHeadNeighbors(HalfEdge he) {
    	ArrayList<Vertex> vs = new ArrayList<>();
    	HalfEdge cw = he;
    	while(cw != null) {
    		cw = cw.next;
    		if(cw == null) break;
    		assert(cw != he);
    		vs.add(cw.head);
    		if(cw.twin == he) break;
    		cw = cw.twin;
    	}
    	if(cw == null) {
	    	// edge case
	    	HalfEdge ccw = he;
	    	while(ccw != null) {
	    		ccw = ccw.prev();
	    		assert(ccw != null);
	    		vs.add(ccw.head);
	    		ccw = ccw.next.twin;
	    		if(ccw == null) break;
	    		assert(ccw != he);
	    		ccw = ccw.prev();
	    		assert(ccw != null && ccw != he);
	    	}
    	}
    	Vertex list[] = new Vertex[vs.size()];
    	return vs.toArray(list);
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
