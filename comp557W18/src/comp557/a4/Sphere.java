package comp557.a4;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

/**
 * A simple sphere class.
 */
public class Sphere extends Intersectable {
    
	/** Radius of the sphere. */
	public double radius = 1;
    
	/** Location of the sphere center. */
	public Point3d center = new Point3d( 0, 0, 0 );
    
    /**
     * Default constructor
     */
    public Sphere() {
    	super();
    }
    
    /**
     * Creates a sphere with the request radius and center. 
     * 
     * @param radius
     * @param center
     * @param material
     */
    public Sphere( double radius, Point3d center, Material material ) {
    	super();
    	this.radius = radius;
    	this.center = center;
    	this.material = material;
    }
    
    @Override
    public IntersectResult intersect( Ray ray ) {
    
        // TODO: Objective 2: intersection of ray with sphere
    	
    	//Vector3d look = ray.getViewDirection();
    	//Point3d eye = ray.getEyePoint();
    	//boolean isP = look.epsilonEquals(new Vector3d(0, 0, -1), 0.001);
    	//if(isP) System.err.println("Camera " + eye+"->" + look);
    	
    	//System.out.println(this + "; " + ray);
    	// ray: x = p + td
    	// circle: |x - c|^2 = r^2
    	Vector3d p_min_c = new Vector3d(ray.getEyePoint());
    	p_min_c.sub(center);
    	//if(isP) System.err.println("(p-c) " + p_min_c);
    	double d_dot_p_min_c = ray.getViewDirection().dot(p_min_c);
    	//if(isP) System.err.println("d*(p-c) " + d_dot_p_min_c);
    	double disc = d_dot_p_min_c * d_dot_p_min_c - p_min_c.lengthSquared() + radius*radius;    	
    	// no intersection
    	if(disc < 0) return null;
    	// calculate parameters, want the first intersection
    	double t = -d_dot_p_min_c - Math.sqrt(disc);
    	Point3d intersect = ray.getPoint(t);
    	Vector3d normal = new Vector3d(intersect);
    	normal.sub(center);
    	normal.normalize();
    	//if(isP) System.err.println("t " + t + "; int " + intersect + "; n " + normal);    	
    	return new IntersectResult(normal, intersect, material, t);
    }
    
	public String toString() {
		return "Sphere" + center + "r" + radius;
	}

    
}
