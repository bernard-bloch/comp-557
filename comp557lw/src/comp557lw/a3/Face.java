package comp557lw.a3;

import java.util.Set;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

/**
 * Simple face class
 */
public class Face {    
    
    /** Face normal that can be used for flat shading */
    Vector3d n;
    
    /** Some half edge on the face */
    HalfEdge he;
    
    /** Child vertex in the middle of the face */
    Vertex child;
    
    /** 
     * Constructs a face from a half edge, and computes the flat normal.
     * This constructor also sets all of the leftFace members of the 
     * half edges that make up this face.
     * @param he
     */
    public Face( HalfEdge he ) {
    	this.no = ++debug_no; // added this unique id for debugging, used in toString
        this.he = he;
        Point3d p0 = he.head.p;
        Point3d p1 = he.next.head.p;
        Point3d p2 = he.next.next.head.p;
        Vector3d v1 = new Vector3d();
        Vector3d v2 = new Vector3d();
        n = new Vector3d();
        v1.sub(p1,p0);
        v2.sub(p2,p1);
        n.cross( v1,v2 );
        HalfEdge loop = he;
        do {
            loop.leftFace = this;
            loop = loop.next;
        } while ( loop != he );
    }    

    // Jonathan Bernard Bloch 260632216:
    
    // Loops through each vertex of each fsce and averages all the points and sets face.child to the new vertex
    // Only done once
    public void computeChild() {
    	assert(this.child == null);
    	HalfEdge he = this.he;
    	this.child = new Vertex();
    	Point3d sum = this.child.p;
    	int count = 0;
    	do {
    		assert(he != null);
    		sum.add(he.head.p);
    		count++;
    	} while((he = he.next) != this.he);
    	assert(count >= 3);
    	sum.scale(1.0 / count);
    }

    // Even vertices are already in the mesh, but is going to be shifted in the child
    // This adds all even vertices to the face
    public void evenVertices() {
    	Point3d temp;
    	HalfEdge he = this.he;
    	do {
    		// Multiple he.s going to the same vertex don't need recomputing
    		if(he.head.child != null) continue;
    		Vertex child = he.head.child = new Vertex();
    		HalfEdge forward = he.headGetForward();
        	// boundary
        	if(forward != null) {
        		HalfEdge backward = he.headGetBackward();
        		temp = new Point3d();
        		temp.set(he.head.p);
        		temp.scale(0.75);
        		child.p.add(temp);
        		temp = new Point3d();
        		temp.add(forward.head.p);
        		temp.add(backward.prev().head.p);
        		temp.scale(0.125);
        		child.p.add(temp);
        	}
        	// interior
        	else {
        		// get outward vertices
        		Set<HalfEdge> out = he.headOut();
        		int k = out.size();
        		double beta = 3.0 / (2.0 * k);
        		double gamma = 1.0 / (4.0 * k);
        		
        		temp = new Point3d();
        		temp.set(he.head.p);
        		temp.scale(1.0 - beta - gamma);
        		child.p.add(temp);
        		
        		temp = new Point3d();
        		for(HalfEdge o : out) temp.add(o.head.p);
        		temp.scale(beta / k);
        		child.p.add(temp);
        		
        		temp = new Point3d();
        		// Important there are exactly out.size vertices added
        		// Fine if they are not quadrilaterals. The mesh will be slightly unbalanced in the 2nd degree.
        		for(HalfEdge o : out) temp.add(o.next.head.p);
        		temp.scale(gamma / k);
        		child.p.add(temp);
        	}
    	} while((he = he.next) != this.he);
    }

    
    //Links the face child vertices with appropriate faces. The new faces are put in heds.
    // Must have children of face, halfedges, and vertices
    public void subdivide(HEDS heds) {
    	// Check that the preconditions have been met
    	assert(this.child != null);
    	HalfEdge he = this.he, prevFromFace = null;
    	do {
        	// Check that the preconditions have been met
    		assert(he != null && he.child1 == null && he.child2 == null && he.half != null && he.head.child != null);
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
			toFace.head = this.child;
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
    			// add it to the model
    			heds.faces.add(sub);
    		}
    		prevFromFace = fromFace;
    	} while((he = he.next) != this.he);
		// link prev.child2 to child1
		assert(prevFromFace != null && prevFromFace.next.next == null);
		prevFromFace.next.next = he.child1;
		// link toFace with prev.fromFace
		assert(he.child1.next.next == null);
		he.child1.next.next = prevFromFace;
		Face sub = new Face(he.child1);
		// add it to the model
		heds.faces.add(sub);
    }
    
    // Debugging unique id
    int no;
    static int debug_no;
        
    public String toString() {
    	return "Face"+no;
    }
}
