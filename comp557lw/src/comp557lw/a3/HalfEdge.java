package comp557lw.a3;

import static org.lwjgl.opengl.GL11.*;

import java.util.HashSet;
import java.util.Set;

import javax.vecmath.Point3d;

/**
 * Class containing the half edge pointers, and a method for drawing
 * the half edge for debugging and evaluation.
 */
public class HalfEdge {
    
    public HalfEdge twin;
    public HalfEdge next;
    public Vertex head;
    public HalfEdge child1;
    public HalfEdge child2;
    public HalfEdge parent;
    public Face leftFace;
    public Vertex half; // added this temporary to separate adding vertices from connecting edges

    /** @return the previous half edge (could just be stored) */
    public HalfEdge prev() {
        HalfEdge prev = this;
        while ( prev.next != this ) prev = prev.next;        
        return prev;
    }
    
    /**
     * Displays the half edge as a half arrow pointing to the head vertex.
     * @param drawable
     */
    public void display() {
        Point3d p0 = prev().head.p;
        Point3d p1 = head.p;
        Point3d p2 = next.head.p;
        double x,y,z;
        
        glLineWidth(3);
        glDisable( GL_LIGHTING );
        glBegin( GL_LINE_STRIP );
        glColor4f(1,1,1,0.8f);
        x = p0.x * 0.8 + (p1.x + p2.x) * 0.1;
        y = p0.y * 0.8 + (p1.y + p2.y) * 0.1;
        z = p0.z * 0.8 + (p1.z + p2.z) * 0.1;
        glVertex3d( x, y, z );
        x = p1.x * 0.8 + (p0.x + p2.x) * 0.1;
        y = p1.y * 0.8 + (p0.y + p2.y) * 0.1;
        z = p1.z * 0.8 + (p0.z + p2.z) * 0.1;
        glVertex3d( x, y, z );
        x = p1.x * 0.7 + p0.x * 0.1 + p2.x * 0.2;
        y = p1.y * 0.7 + p0.y * 0.1 + p2.y * 0.2;
        z = p1.z * 0.7 + p0.z * 0.1 + p2.z * 0.2;
        glVertex3d( x, y, z );        
        glEnd();
        glLineWidth(1);
        glEnable( GL_LIGHTING );
    }

    // Jonathan Bernard Bloch 260632216:

    // get all the HalfEdges going out he.head. It will have two halfedges in the same face if it is on the boundary
    public Set<HalfEdge> headOut() {
    	Set<HalfEdge> vs = new HashSet<>();
    	HalfEdge cw = this;
    	do {
    		// interior
    		cw = cw.next;
    		if(cw == null) break;
    		assert(cw != this);
    		vs.add(cw);
    		cw = cw.twin;
    	} while(cw != this && cw != null);
    	if(cw == null) {
	    	// edge case
    		HalfEdge ccw = this;
    		while(ccw.twin != null) {
	    		ccw = ccw.twin;
	    		vs.add(ccw);
	    		ccw = ccw.prev();
	    		assert(ccw != this);
    		}
    		// this is the edge that doesn't have an outgoing he
    		ccw = ccw.prev();
    		vs.add(ccw);
    	}
    	assert(vs.size() >= 3);
    	return vs;
    }
    
    // this gets the forward edge on a crease and boundary going away from the vertex
    // it returns null if the he is in the interior
    public HalfEdge headGetForward() {
    	HalfEdge cw = this;
    	do {
    		cw = cw.next;
    		if(cw.twin == null) return cw;
    		cw = cw.twin;
    	} while(cw != this);
    	return null;
    }
    
    // this gets the backwards edge on a crease and boundary going towards the vertex
    // it returns null if the he is in the interior
    // it is not as fast as headGetForward
    public HalfEdge headGetBackward() {
    	HalfEdge ccw = this;
    	while(ccw.twin != null) {
    		ccw = ccw.twin.prev();
    		if(ccw == this) return null;
    	}
    	return ccw;
    }
    
    // Edge vertex. Must be called after face vertices have computed the child.
    // I added a temporary variable he.half that stores a halfedge child. The connection is in the next step.
    public void divideEdge() {
    	// already has a it from the twin
    	if(half != null) {
    		assert(twin != null && half == twin.half);
    		return;
    	}
		half = new Vertex();
    	if(twin == null) {
    		// boundary odd
    		Point3d p = half.p;
    		p.add(head.p);
    		p.add(prev().head.p);
    		p.scale(0.5);
    	} else {
    		assert(twin.half == null);
    		twin.half = half;
    		// internal odd -- requires face vertices
    		Point3d p = half.p;
    		p.add(head.p);
    		p.add(leftFace.child.p);
    		p.add(twin.head.p);
    		p.add(twin.leftFace.child.p);
    		p.scale(0.25);
    	}
    }
    
}
