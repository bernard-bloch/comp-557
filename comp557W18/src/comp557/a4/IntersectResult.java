package comp557.a4;

import java.text.DecimalFormat;

import javax.vecmath.Matrix4d;
import javax.vecmath.Point3d;
import javax.vecmath.Tuple3d;
import javax.vecmath.Vector3d;

/**
 * Use this class to store the result of an intersection, or modify it to suit your needs!
 */
public class IntersectResult {
	
	// the shape
	private Intersectable shape;

	/** Parameter on the ray giving the position of the intersection */
	private double t;// = Double.POSITIVE_INFINITY; 

	/** Intersection position */
	private Point3d p;// = new Point3d();

	/** The normal at the intersection */ 
	private Vector3d n;// = new Vector3d();
	
	/** The material of the intersection */
	private Material material;// = null;
			
	/**
	 * Default constructor.
	 */
	IntersectResult(Intersectable shape, double t, Point3d p, Vector3d n, Material m) {
		this.shape = shape;
		this.t = t;
		this.p = p;
		this.n = n;
		this.material = m;
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
	
	public Intersectable getShape() {
		return shape;
	}
	
	public Material getMaterial() {
		return material;
	}
	
	static private String tup(Tuple3d a) {
		DecimalFormat df = new DecimalFormat("#.0");
		return "(" + df.format(a.x) + ", " + df.format(a.y) + ", " + df.format(a.z) + ")";
	}
	
	public String toString() {
		DecimalFormat df = new DecimalFormat("#.0");
		return "IR t=" + df.format(t) + ", p=" + tup(p) + ", n=" + tup(n) + ", m=" + shape.getMaterial();
	}
}

