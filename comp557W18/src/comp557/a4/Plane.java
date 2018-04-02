package comp557.a4;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

/**
 * Class for a plane at y=0.
 * 
 * This surface can have two materials.  If both are defined, a 1x1 tile checker 
 * board pattern should be generated on the plane using the two materials.
 */
public class Plane extends Intersectable {
    
	/** The second material, if non-null is used to produce a checker board pattern. */
	Material material2;
	
	/** The plane normal is the y direction */
	public static final Vector3d n = new Vector3d( 0, 1, 0 );
    
    /**
     * Default constructor
     */
    public Plane() {
    	super();
    }

        
    @Override
    public IntersectResult intersect( Ray ray ) {
    
        // TODO: Objective 4: intersection of ray with plane
    	// https://en.wikipedia.org/wiki/Line%E2%80%93plane_intersection
    	// plane: (p - p0)*n = 0;
    	// line: p = tl + l0;
    	// l*n != 0 -> t = ((p0 - l0)*n)/(l*n);
    	// p0 = 0;
    	Vector3d l = ray.getViewDirection();
    	double ln = l.dot(n);
    	if(ln == 0.0) return null;
    	Vector3d l0 = new Vector3d(ray.getEyePoint());
    	double t = l0.dot(n) / ln;
    	if(t <= 0.0) return null;
    	Point3d intersect = ray.getPoint(t);
    	// decide which colour to give the material. Project the ray onto the plane.
    	Vector3d i = new Vector3d(intersect);
    	int x = (int)i.dot(new Vector3d(n.y, n.z, n.x));
    	int y = (int)i.dot(new Vector3d(n.z, n.x, n.y));
    	Material m = ((x ^ y) & 1) != 0 ? material : material2;
    	return new IntersectResult(n, intersect, m, t);
    }
    
}
