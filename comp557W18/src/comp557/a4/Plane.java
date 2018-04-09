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
	private Material material2;
	
	/** The plane. */
	private Vector3d n;// = new Vector3d( 0, 1, 0 );
	private Vector3d p0;
    
    /**
     * @param material2 Can be null.
     */
    public Plane(Point3d p0, Vector3d n, Material material, Material material2) {
    	super(material);
    	n.normalize();
    	this.p0 = new Vector3d(p0);
    	this.n = n;
    	this.material2 = material2;
    }

        
    public String toString() {
    	return "Plane(p0=" + p0 + ";n=" + n + ")";
    }
    @Override
    public IntersectResult intersect( Ray ray ) {
    
        // Objective 4: intersection of ray with plane
    	// https://en.wikipedia.org/wiki/Line%E2%80%93plane_intersection
    	// plane: (p - p0)*n = 0;
    	// line: p = tl + l0;
    	// l*n != 0 -> t = ((p0 - l0)*n)/(l*n);
    	Vector3d l = ray.getViewDirection();
    	double ln = l.dot(n);
    	if(ln == 0.0) return null; // perpendicular
    	Vector3d p0_sub_l0 = new Vector3d(p0);
    	p0_sub_l0.sub(ray.getEyePoint());
    	double t = p0_sub_l0.dot(n) / ln;
    	if(t <= 0.0) return null;
    	Point3d intersect = ray.getPoint(t);
    	// decide which colour to give the material. Project the ray onto the plane.
    	Material m = material;
    	if(material2 != null) {
	    	Vector3d i = new Vector3d(intersect);
	    	int x = (int)Math.floor(i.dot(new Vector3d(n.y, n.z, n.x)));
	    	int y = (int)Math.floor(i.dot(new Vector3d(n.z, n.x, n.y)));
	    	if(((x ^ y) & 1) != 0) m = material2;
    	}
    	return new IntersectResult(this, t, intersect, n, m);
    }
    public Vector3d getP0() {
    	return p0;
    }
    
}
