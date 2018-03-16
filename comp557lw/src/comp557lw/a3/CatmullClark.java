package comp557lw.a3;

import java.util.HashSet;
import java.util.Set;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

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
        
        for(Face face : heds.faces) {
            // question 2:
        	evenVertices(face);
        	// question 3a:
        	faceVertex(face);
        }
        	
        for(Face face : heds.faces) {
        	// question 3b
        	HalfEdge he = face.he;
        	do {
        		assert(he != null);
        		divideEdge(he);
        	} while((he = he.next) != face.he);
        }
        
        // question 4
        for(Face face : heds.faces) {
        	subdivideFace(face, heds2);
        }
        
        // question 5
        // "Refer to Equation 4.1 on Page 70 of the siggraph 2000 course notes on subdividion surfaces"
        // there is no such thing. Just guess. Add cross products, not normalized. The greater the distance, the greater the contribution.
        // This is independent of Catmull-Clark, should be called in another place
        for(Face face : heds2.faces) {
        	HalfEdge he = face.he;
        	do {
        		assert(he != null);
        		if(he.head.n == null) he.head.n = new Vector3d();
                Vector3d v1 = new Vector3d();
                Vector3d v2 = new Vector3d();
                Vector3d n = new Vector3d();
                v1.sub(he.head.p, he.prev().head.p);
                v2.sub(he.head.p, he.next.head.p);
                n.cross( v2,v1 );
                he.head.n.add(n);
        	} while((he = he.next) != face.he);
        }
        for(Face face : heds2.faces) {
        	HalfEdge he = face.he;
        	do {
                he.head.n.normalize();
        	} while((he = he.next) != face.he);
        }
        
        
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
    
    // this will store the vertices length 0, 1, 2, from the specified vertex in headDistance
    private static Point3d v0[], v1[], v2[];
    		
	// get distance 0, 1 and 2
    static private void headDistance(HalfEdge he) {
    	Set<HalfEdge> he0 = new HashSet<>(), he1 = new HashSet<>(), he2 = new HashSet<>();
    	assert(he.head != null);
    	he0.add(he);
    	for(HalfEdge h : he0) headNeighborsToVs(h, he1);
    	for(HalfEdge h : he1) headNeighborsToVs(h, he2);
    	he2.removeAll(he0);
    	he2.removeAll(he1);
    	Set<Point3d> temp0 = new HashSet<>(he0.size()), temp1 = new HashSet<>(he0.size()), temp2 = new HashSet<>(he2.size());
    	he0.forEach(h -> temp0.add(h.head.p));
    	he1.forEach(h -> temp1.add(h.head.p));
    	he2.forEach(h -> temp2.add(h.head.p));
    	// there could be vertices that have many edges that have it as head, make sure
    	temp2.removeAll(temp0);
    	temp2.removeAll(temp1);
    	//System.err.println(he + ":\n\t("+temp0.size()+")" + temp0 + "\n\t("+temp1.size()+")" + temp1 + "\n\t("+temp2.size()+")" + temp2 + "\n");
    	v0 = temp0.toArray(new Point3d[temp0.size()]);
    	v1 = temp1.toArray(new Point3d[temp1.size()]);
    	v2 = temp2.toArray(new Point3d[temp2.size()]);
    }
    
    // an even vertex is a vertex that is already in the mesh
    private static void evenVertices(Face face) {
    	HalfEdge he = face.he;
    	do {
    		// set up distances in temp arrays v0, v1, v2
        	headDistance(he);
    		Vertex child = new Vertex();
    		int k = v1.length /*+ v2.length <- oohhh k is the degree, didn't see it in orange */;
        	if(k < 2) {
        		System.err.println("Vertex " + he.head + " has degree < 2, skipping.");
        	}
        	// boundary
        	else if(k == 2) {
        		Point3d temp = new Point3d();
        		temp.set(v0[0]);
        		temp.scale(0.75);
        		child.p.add(temp);
        		temp.set(v1[0]);
        		temp.scale(0.125);
        		child.p.add(temp);
        		temp.set(v1[1]);
        		temp.scale(0.125);
        		child.p.add(temp);
        	}
        	// interior
        	else {
        		double beta = 3.0 / (2.0 * k);
        		double gamma = 1.0 / (4.0 * k);
        		
        		Point3d temp = new Point3d();
        		temp.add(v0[0]);
        		temp.scale(1.0 - beta - gamma);
        		child.p.add(temp);
        		
        		temp = new Point3d();
        		for(Point3d v : v1) temp.add(v);
        		temp.scale(beta / k);
        		child.p.add(temp);
        		
        		temp = new Point3d();
        		for(Point3d v : v2) temp.add(v);
        		temp.scale(gamma / k);
        		child.p.add(temp);
        	}

        	he.head.child = child;

    	} while((he = he.next) != face.he);
    }

    // face
    static void faceVertex(Face face) {
    	assert(face.child == null);
    	HalfEdge he = face.he;
    	face.child = new Vertex();
    	Point3d sum = face.child.p;
    	int count = 0;
    	do {
    		assert(he != null);
    		sum.add(he.head.p);
    		count++;
    	} while((he = he.next) != face.he);
    	assert(count >= 3);
    	sum.scale(1.0 / count);
    }
        
/*    private static Vertex even(HalfEdge he) {
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
    }*/
    
    // edge vertex. I added a he vertices adding from the connection step. Must be called after face.
    static void divideEdge(HalfEdge he) {
    	// already has a it from the twin
    	if(he.half != null) {
    		assert(he.twin != null && he.half == he.twin.half);
    		return;
    	}
		he.half = new Vertex();
    	if(he.twin == null) {
    		System.err.println("Got here.");
    		// boundary odd
    		Point3d p = he.half.p;
    		p.add(he.head.p);
    		p.add(he.prev().head.p);
    		p.scale(0.5);
    	} else {
    		assert(he.twin.half == null);
    		he.twin.half = he.half;
    		// internal odd -- requires face vertices
    		Point3d p = he.half.p;
    		p.add(he.head.p);
    		p.add(he.leftFace.child.p);
    		p.add(he.twin.head.p);
    		p.add(he.twin.leftFace.child.p);
    		p.scale(0.25);
    	}
    }
        
    private static void subdivideFace(Face face, HEDS heds) {
    	HalfEdge he = face.he, prevFromFace = null;
    	assert(face.he != null);
    	do {
    		assert(he != null && he.child1 == null && he.child2 == null && face.child != null);
    		// add child1. Next is the future toFace.
    		he.child1 = new HalfEdge();
    		he.child1.head = he.half;
    		he.child1.parent = he;
    		if(he.twin != null && he.twin.child2 != null) he.child1.twin = he.twin.child2;
    		// add child2. Next is the next iteration's child1.
    		he.child2 = new HalfEdge();
    		he.child2.head = he.head.child;
    		he.child2.parent = he;
    		if(he.twin != null && he.twin.child1 != null) he.child2.twin = he.twin.child1;
    		// add toFace and fromFace.
			HalfEdge toFace = new HalfEdge(), fromFace = new HalfEdge();
			toFace.head = face.child;
			fromFace.head = he.half;
			toFace.twin = fromFace;
			fromFace.twin = toFace;
			// link
			he.child1.next = toFace;
			fromFace.next = he.child2;
			// get previous
    		if(prevFromFace != null) {
    			// link prev.child2 to child1
    			assert(prevFromFace.next.next == null);
    			prevFromFace.next.next = he.child1;
    			// link toFace with prev.fromFace
    			assert(toFace.next == null);
    			toFace.next = prevFromFace;
    			Face sub = new Face(he.child1);
    			// fixme: calculate normal
    			// add it to the model
    			heds.faces.add(sub);
    		}
    		prevFromFace = fromFace;
    	} while((he = he.next) != face.he);
		// link prev.child2 to child1
		assert(prevFromFace != null && prevFromFace.next.next == null);
		prevFromFace.next.next = he.child1;
		// link toFace with prev.fromFace
		assert(he.child1.next.next == null);
		he.child1.next.next = prevFromFace;
		Face sub = new Face(he.child1);
		// fixme: calculate normal
		// add it to the model
		heds.faces.add(sub);
    }
    
    
/*    private static void addChildToEdges(Face face) {
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
     */   
}

