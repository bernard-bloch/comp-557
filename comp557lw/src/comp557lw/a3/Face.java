package comp557lw.a3;

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
    
    int no;
    static int debug_no;
    
    /** 
     * Constructs a face from a half edge, and computes the flat normal.
     * This constructor also sets all of the leftFace members of the 
     * half edges that make up this face.
     * @param he
     * @fixme Bernard: should be the average of all the normals, normalized, I guess it doesn't matter much
     */
    public Face( HalfEdge he ) {
    	this.no = ++debug_no;
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
    
    public String toString() {
    	return "Face"+no;
    }
}
