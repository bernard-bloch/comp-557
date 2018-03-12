package comp557lw.a3;

import static org.lwjgl.opengl.GL11.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

// https://alvinalexander.com/java/java-tuple-classes
final class Tuple<T> {
	private final T a, b;
	public Tuple(T a, T b) { this.a = a; this.b = b; }
	// equals is wierd because it's going one way and the other
	public final boolean equals(Object o) {
		if(this == o) return true;
		if(this == null || getClass() != o.getClass()) return false;
		Tuple<?> x = (Tuple<?>) o;
		return this.a.equals(x.b) && this.b.equals(x.a);
	}
	// must be symmetric!
	public int hashCode() {
        return a.hashCode() + b.hashCode();
    }
}

/**
 * Half edge data structure.
 * Maintains a list of faces (i.e., one half edge of each) to allow for easy display of geometry.
 * 
 * @author TODO: Jonathan Bernard Bloch
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
     * Builds a half edge data structure from the polygon soup   
     * @param soup
     */
    public HEDS( PolygonSoup soup ) {
        
        
        // TODO: Objective 1: create the half edge data structure from a polygon soup

        Map<Tuple<int>, HalfEdge> twins = new HashMap<>();
        
        for(int[] face : soup.faceList) {
        	if(face.length < 3) {
        		System.err.println("The face " + face + " is degenarate skipping.");
        		continue;
        	}
			int vertexIndexPrev = face[face.length - 1];
			HalfEdge hePrev = null, heFirst = null, he;
        	for(int vertexIndex : face) {
				he = new HalfEdge(soup.vertexList.get(vertexIndex));
				if(hePrev != null) {
					hePrev.next = he;
				}
				else {
					// heFirst doesn't change, used to connect the face out of the loop
					heFirst = he;
				}
				
				// hashmap twins contains inverse, otherwise add it
				final Tuple<int> hash = new Tuple<>(vertexIndexPrev, vertexIndex);
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

        /*
        // brute force O(n^2)
        HalfEdge bogus = new HalfEdge();
        for(Face face1 : faces) {
        	for(HalfEdge he1 = face1.he; assert(he1),he1.next != face1.he; he1 = he1.next) {
        		if(he1.twin != null) continue;
        		he1.twin = bogus; // to not get this one
        		boolean doneInner = false;
        		for(Face face2 : faces) {
        			for(HalfEdge he2 = face2.he; assert(he2),he2.next != face2.he; he2 = he2.next) {
                		if(he2.twin != null || he1.next.head != he2.head || he2.next.head != he1.head) continue;
                		System.err.println("Pairing " + he1 + " and " + he2);
    					he1.twin = he2;
    					he2.twin = he1;
    					doneInner = true;
    					break;
        			}
        			if(doneInner) break;
        		}
        		// looked through all half edges and didn't find it's twin -- maybe it is open
        		if(!doneInner) {
        			he1.twin = null;
        			System.err.println("Vertex " + he1.head + " on face " + face1 + " doesn't have a correspoding twin.");
        		}
        	}
        }
        */
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
}
