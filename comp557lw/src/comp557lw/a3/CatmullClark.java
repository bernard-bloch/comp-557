package comp557lw.a3;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

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
    
    // get all the vertices of distance 1 from the head and add a halfedge that has head to vs
    static private void headNeighborsToVs(HalfEdge he, Set<HalfEdge> vs) {
    	HalfEdge cw = he;
    	while(cw != null) {
    		cw = cw.next;
    		if(cw == null) break;
    		assert(cw != he);
    		vs.add(cw);
    		if(cw.twin == he) break;
    		cw = cw.twin;
    	}
    	if(cw == null) {
	    	// edge case
	    	HalfEdge ccw = he;
	    	while(ccw != null) {
	    		ccw = ccw.prev();
	    		assert(ccw != null);
	    		vs.add(ccw);
	    		ccw = ccw.next.twin;
	    		if(ccw == null) break;
	    		assert(ccw != he);
	    		ccw = ccw.prev();
	    		assert(ccw != null && ccw != he);
	    	}
    	}
    }
    
	// get distance 0, 1 and 2
    static private void getHeadDistance(HalfEdge he, Set<Vertex> v0, Set<Vertex> v1, Set<Vertex> v2) {
    	Set<HalfEdge> he0 = new HashSet<>(), he1 = new HashSet<>(), he2 = new HashSet<>();
    	assert(he.head != null);
    	v0.clear();
    	v1.clear();
    	v2.clear();
    	he0.add(he);
    	for(HalfEdge h : he0) headNeighborsToVs(h, he1);
    	for(HalfEdge h : he1) headNeighborsToVs(h, he2);
    	he2.removeAll(he0);
    	he2.removeAll(he1);
    	//he0.forEach(h -> v0.add(h.head));
    	// https://stackoverflow.com/questions/30082555/collectors-toset-and-hashset
    	//v0 = he0.stream().map(h -> h.head).collect(Collectors.toSet());
    	//v1 = he1.stream().map(h -> h.head).collect(Collectors.toSet());
    	//v2 = he2.stream().map(h -> h.head).collect(Collectors.toSet());
    	// https://stackoverflow.com/questions/39326658/java-8-foreach-add-subobject-to-new-list
    	//v0 = he0.stream().map(prev()).collect(Collectors.toSet());
    	//for(HalfEdge h : he0) v0.add(h.head);
    	he0.forEach(h -> v0.add(h.head));
    	he1.forEach(h -> v1.add(h.head));
    	he2.forEach(h -> v2.add(h.head));
    	// there could be vertices that have many edges that have it as head, make sure
    	v2.removeAll(v0);
    	v2.removeAll(v1);
    }
    
    // an even vertex is a vertex that is already in the mesh
    private static void evenVertices(Face face) {

    	HalfEdge he = face.he;
    	do {
    		Set<Vertex> v0 = new HashSet<>(), v1 = new HashSet<>(), v2 = new HashSet<>();

        	getHeadDistance(he, v0, v1, v2);
        	System.err.println(he + ":\n\t("+v0.size()+")" + v0 + "\n\t("+v1.size()+")" + v1 + "\n\t("+v2.size()+")" + v2 + "\n");

        	/*assert(v1.length > 1);
        	if(v1.length == 2) {
                // question 2: even vertices of degree 2
        		Vertex child = new Vertex();
        		Point3d temp = new Point3d();
        		temp.set(v0.p);
        		temp.scale(0.75);
        		child.p.add(temp);
        		temp.set(v1[0].p);
        		temp.scale(0.125);
        		child.p.add(temp);
        		temp.set(v1[1].p);
        		temp.scale(0.125);
        		child.p.add(temp);
        		v0.child = child;
        	}
        	else {
        		// 
        	}*/
    	} while((he = he.next) != face.he);
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
