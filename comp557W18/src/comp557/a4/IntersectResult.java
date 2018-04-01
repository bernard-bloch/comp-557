package comp557.a4;

import javax.vecmath.Matrix4d;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

/**
 * Use this class to store the result of an intersection, or modify it to suit your needs!
 */
public class IntersectResult {
	
	/** The normal at the intersection */ 
	private Vector3d n;// = new Vector3d();
	
	/** Intersection position */
	private Point3d p;// = new Point3d();
	
	/** The material of the intersection */
	private Material material;// = null;
		
	/** Parameter on the ray giving the position of the intersection */
	private double t;// = Double.POSITIVE_INFINITY; 
	
	/**
	 * Default constructor.
	 */
	IntersectResult(Vector3d n, Point3d p, Material m, double t) {
		this.n = n;
		this.p = p;
		this.material = m;
		this.t = t;
	}
	
	/**
	 * Copy constructor.
	 */
	IntersectResult( IntersectResult other ) {
		n.set( other.n );
		p.set( other.p );
		t = other.t;
		material = other.material;
	}
	
	/**
	 * Transforms this IntersectionResult by mat
	 */
	public void transform(Matrix4d mat) {
		mat.transform(n);
		mat.transform(p);
	}
	
	public double getT() {
		return t;
	}
	
	public Vector3d getNormal() {
		return n;
	}
	
	public Point3d getPoint() {
		return p;
	}
	
	public Material getMaterial() {
		return material;
	}
	
	public String toString() {
		return "IR(" + t + ") = " + p + ", " + material;
	}
}

