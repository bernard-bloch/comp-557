package comp557lw.a3;

import static org.lwjgl.opengl.GL11.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

/**
 * Half edge data structure.
 * Maintains a list of faces (i.e., one half edge of each) to allow for easy display of geometry.
 * 
 * @author Jonathan Bernard Bloch
 */
public class HEDS {

    /**
     * List of faces 
     */
    List<Face> faces = new ArrayList<Face>();
        
    /**
     * Constructs an empty mesh (used when building a mesh with subdivision)
     */
    public HEDS() {
        // do nothing
    }
        
    /**
     * Draws the half edge data structure by drawing each of its faces.
     * Per vertex normals are used to draw the smooth surface when available,
     * otherwise a face normal is computed. 
     * @param drawable
     */
    public void display() {
        // note that we do not assume triangular or quad faces, so this method is slow! :(     
        Point3d p;
        Vector3d n;        
        for ( Face face : faces ) {
            HalfEdge he = face.he;
            if ( he.head.n == null ) { // don't have per vertex normals? use the face
                glBegin( GL_POLYGON );
                n = he.leftFace.n;
                glNormal3d( n.x, n.y, n.z );
                HalfEdge e = he;
                do {
                    p = e.head.p;
                    glVertex3d( p.x, p.y, p.z );
                    e = e.next;
                } while ( e != he );
                glEnd();
            } else {
                glBegin( GL_POLYGON );                
                HalfEdge e = he;
                do {
                    p = e.head.p;
                    n = e.head.n;
                    glNormal3d( n.x, n.y, n.z );
                    glVertex3d( p.x, p.y, p.z );
                    e = e.next;
                } while ( e != he );
                glEnd();
            }
        }
    }
    
    /** 
     * Draws all child vertices to help with debugging and evaluation.
     * (this will draw each points multiple times)
     * @param drawable
     */
    public void drawChildVertices() {
    	glDisable( GL_LIGHTING );
        glPointSize(8);
        glBegin( GL_POINTS );
        for ( Face face : faces ) {
            if ( face.child != null ) {
                Point3d p = face.child.p;
                glColor3f(0,0,1);
                glVertex3d( p.x, p.y, p.z );
            }
            HalfEdge loop = face.he;
            do {
                if ( loop.head.child != null ) {
                    Point3d p = loop.head.child.p;
                    glColor3f(1,0,0);
                    glVertex3d( p.x, p.y, p.z );
                }
                if ( loop.child1 != null && loop.child1.head != null ) {
                    Point3d p = loop.child1.head.p;
                    glColor3f(0,1,0);
                    glVertex3d( p.x, p.y, p.z );
                }
                loop = loop.next;
            } while ( loop != face.he );
        }
        glEnd();
        glEnable( GL_LIGHTING );
    }

    /**
     * Builds a half edge data structure from the polygon soup   
     * @param soup
     */
    public HEDS( PolygonSoup soup ) {
        
        
        // TODO: Objective 1: create the half edge data structure from a polygon soup

        // Jonathan Bernard Bloch 260632216:

    	Map<Tuple, HalfEdge> twins = new HashMap<>();
        
        // iterate on faces
        for(int[] face : soup.faceList) {
        	if(face.length < 3) {
        		System.err.println("The face " + face + " is degenarate skipping.");
        		continue;
        	}
			int vertexIndexPrev = face[face.length - 1];
			HalfEdge hePrev = null, heFirst = null, he = null;
        	for(int vertexIndex : face) {
				he = new HalfEdge();
				he.head = soup.vertexList.get(vertexIndex);
				if(hePrev != null) {
					hePrev.next = he;
				}
				else {
					// heFirst doesn't change, used to connect the face out of the loop
					heFirst = he;
				}
				
				// hashmap twins contains inverse, otherwise add it
				final Tuple hash = new Tuple(vertexIndexPrev, vertexIndex);
				HalfEdge twin = twins.get(hash);
				if(twin == null) {
					twins.put(hash, he);
				}
				else {
        			assert(twin.twin == null);
        			twins.remove(hash);
					he.twin = twin;
					twin.twin = he;
				}

				// update for the next iteration
				vertexIndexPrev = vertexIndex;
				hePrev = he;
        	}
        	assert(heFirst != null && he != null && he.next == null);
        	he.next = heFirst;
        	faces.add(new Face(heFirst));
        }
        if(!twins.isEmpty()) {
        	System.err.println("There are still edges with unpaired twins. Hole in the mesh?");
        	twins.clear();
        }
        
    } 
    
}

//https://alvinalexander.com/java/java-tuple-classes
final class Tuple {
	private final int a, b;
	public Tuple(int a, int b) { this.a = a; this.b = b; }
	// equals is wierd because it's going one way and the other
	public final boolean equals(Object o) {
		if(this == o || this == null || getClass() != o.getClass()) return false;
		Tuple x = (Tuple) o;
		return this.a == x.b && this.b == x.a;
	}
	// must be symmetric!
	public int hashCode() {
		return a + b;
	}
}

