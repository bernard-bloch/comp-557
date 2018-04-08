package comp557.a4;

import java.text.DecimalFormat;
import java.util.Random;

import javax.vecmath.Matrix4d;
import javax.vecmath.Point3d;
import javax.vecmath.Tuple3d;
import javax.vecmath.Vector2f;
import javax.vecmath.Vector3d;

public class Ray {
	
	/** Originating point for the ray */
	private final Point3d eyePoint;
	
	/** The direction of the ray */
	private final Vector3d viewDirection;

	// more fuzziness creates a more impressionist style with samples > 1
	private static Random random = new Random();
	
	/**
	 * Copy constructor.
	 */
	public Ray(Ray copy) {
		eyePoint = new Point3d(copy.eyePoint);
		viewDirection = new Vector3d(copy.viewDirection);
	}
	
	/**
	 * Creates a new ray from the copy transformed by mat.
	 */
	public Ray(Ray copy, Matrix4d mat) {
		this(copy);
		mat.transform(eyePoint);
		mat.transform(viewDirection);
	}
	
	/** 
	 * Creates a new ray with the given eye point and view direction 
	 * @param eyePoint
	 * @param viewDirection
	 * Unused.
	 */
	public Ray( Point3d eyePoint, Vector3d viewDirection ) {
		this.eyePoint = new Point3d(eyePoint);
		this.viewDirection = new Vector3d(viewDirection);
		this.viewDirection.normalize();
	}
	
    /**
     * Generate a ray through pixel (x,y).
     * 
     * @param x Increasing from the left.
     * @param y Increasing from the top.
     * @param cam The camera.
     */
	public Ray(final int x, final int y, final Camera cam, boolean isJitter) {
		
		// Objective 1: generate rays given the provided parmeters
		
		// Raytracing p. 8
		// vector e = eyePoint
		// vector d = viewDirection
		// scalar d = 1
		
		// anti-alias
		Vector2f jitter = new Vector2f();
		if(isJitter) {
			jitter.x = (float)(random.nextGaussian() * cam.getFuzziness());
			jitter.y = (float)(random.nextGaussian() * cam.getFuzziness());
		}

		// p9
		eyePoint = cam.getFrom(); // p = e
		viewDirection = new Vector3d(cam.getZAxis()); // d =
		viewDirection.scale(cam.getFOVMultiplier());
		Vector3d uVec = new Vector3d(cam.getXAxis());
		uVec.scale(-cam.getImageSize().width * 0.5 + x + 0.5 + jitter.x);
		viewDirection.add(uVec); // + u
		Vector3d vVec = new Vector3d(cam.getYAxis());
		vVec.scale(cam.getImageSize().height * 0.5 - y + 0.5 + jitter.y);
		viewDirection.add(vVec); // + v
		// normalize, it makes it easier
		viewDirection.normalize();
	}
		
	/**
	 * Computes the location of a point along the ray using parameter t.
	 * @param t
	 * @return The point.
	 */
	public Point3d getPoint( double t ) {
		Point3d p = new Point3d(viewDirection);
		p.scaleAdd(t, eyePoint);
		return p;
	}
	
	public Point3d getEyePoint() {
		return eyePoint;
	}
	
	public Vector3d getViewDirection() {
		return viewDirection;
	}

	static private String tup(Tuple3d a) {
		DecimalFormat df = new DecimalFormat("#.0");
		return "(" + df.format(a.x) + "," + df.format(a.y) + "," + df.format(a.z) + ")";
	}

	public String toString() {
		return "Ray" + tup(eyePoint) + "->" + tup(viewDirection);
	}
	
}

