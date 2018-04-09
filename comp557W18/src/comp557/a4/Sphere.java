package comp557.a4;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

/**
 * A simple sphere class.
 */
public class Sphere extends Intersectable {
    
	/** Radius of the sphere. */
	public double radius;// = 1;
    
	/** Location of the sphere center. */
	public Point3d center;// = new Point3d( 0, 0, 0 );
        
    /**
     * Creates a sphere with the request radius and center. 
     * 
     * @param radius
     * @param center
     * @param material
     */
    public Sphere( Point3d center, double radius, Material material ) {
    	super(material);
    	this.radius = radius;
    	this.center = center;
    }
    
    public boolean inSphere(final Point3d x) {
    	double l2 = this.center.distanceSquared(x);
    	return radius * radius < l2;
    }
    
    @Override
    public IntersectResult intersect( Ray ray ) {
    
        // Objective 2: intersection of ray with sphere
    	
    	// ray: x = p + td
    	// circle: |x - c|^2 = r^2
    	Vector3d p_min_c = new Vector3d(ray.getEyePoint());
    	p_min_c.sub(center);
    	double d_dot_p_min_c = ray.getViewDirection().dot(p_min_c);
    	double disc = d_dot_p_min_c * d_dot_p_min_c - p_min_c.lengthSquared() + radius*radius;    	
    	// no intersection
    	if(disc < 0) return null;
    	// calculate parameters, want the first intersection
    	double t = -d_dot_p_min_c - Math.sqrt(disc);
    	if(t < 0) return null;
    	Point3d intersect = ray.getPoint(t);
    	Vector3d normal = new Vector3d(intersect);
    	normal.sub(center);
    	normal.normalize();
    	return new IntersectResult(this, t, intersect, normal, material);
    }
    
	public String toString() {
		return "Sphere" + center + "r" + radius;
	}

    
}
