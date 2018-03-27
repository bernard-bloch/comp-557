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
    public void intersect( Ray ray, IntersectResult result ) {
    
        // TODO: Objective 2: intersection of ray with sphere
    	// (l * (o - c))^2 - |o - c|^2 + r^2 > 0
    	// o = Ray.eyePoint
    	// l = Ray.viewDirection
    	// c = Sphere.center
    	// r = Sphere.radius
    	
    	Vector3d oc = new Vector3d(ray.eyePoint); oc.sub(center);
    	double loc = ray.viewDirection.dot(oc);
    	double disc = (loc * loc) - oc.dot(oc) + radius * radius;
    	if(disc > 0) {
    		// how do you set it to white?
    		result.material = material;
    	}
    }
    
}
