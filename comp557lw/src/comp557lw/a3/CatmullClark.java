package comp557lw.a3;

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
        
        // Jonathan Bernard Bloch 260632216:
        // The helper methods are in appropriate classes, HalfEdge and Face
        
    	for(Face face : heds.faces) {
            // question 2:
        	face.evenVertices();
        	// question 3a:
        	face.computeChild();
        }
        	
        for(Face face : heds.faces) {
        	// question 3b
        	HalfEdge he = face.he;
        	do {
        		assert(he != null);
        		he.divideEdge();
        	} while((he = he.next) != face.he);
        }
        
        // question 4
        heds.faces.forEach((Face f) -> f.subdivide(heds2));
        
        // question 5
        // "Refer to Equation 4.1 on Page 70 of the siggraph 2000 course notes on subdividion surfaces" I don't see it
        for(Face face : heds2.faces) {
        	HalfEdge he = face.he;
        	// The greater the distance, the greater the contribution.
        	do {
        		assert(he != null);
        		if(he.head.n == null) he.head.n = new Vector3d();
                Vector3d v1 = new Vector3d();
                Vector3d v2 = new Vector3d();
                Vector3d n = new Vector3d();
                v1.sub(he.head.p, he.prev().head.p);
                v2.sub(he.head.p, he.next.head.p);
                n.cross( v2,v1 ); // it's inverted? okay.
                he.head.n.add(n);
        	} while((he = he.next) != face.he);
        }
        // normalization step
        for(Face face : heds2.faces) {
        	HalfEdge he = face.he;
        	do {
                he.head.n.normalize();
        	} while((he = he.next) != face.he);
        }
        
        
        return heds2;        
    }
        
    
}

