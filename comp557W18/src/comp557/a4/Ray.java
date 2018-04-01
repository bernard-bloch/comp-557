package comp557.a4;

import javax.vecmath.Matrix4d;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

public class Ray {
	
	/** Originating point for the ray */
	private final Point3d eyePoint;
	
	/** The direction of the ray */
	private final Vector3d viewDirection;

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
	 */
	public Ray( Point3d eyePoint, Vector3d viewDirection ) {
		this.eyePoint = new Point3d(eyePoint);
		this.viewDirection = new Vector3d(viewDirection);
	}
	
    /**
     * Generate a ray through pixel (i,j).
     * 
     * @param i The pixel row.
     * @param j The pixel column.
     * @param offset The offset from the center of the pixel, in the range [-0.5,+0.5] for each coordinate. 
     * I have no idea what this is, I'm going to leave it out.
     * @param cam The camera.
     * @param ray Contains the generated ray.
     */
	public Ray(final int i, final int j, final Camera cam) {
		
		// TODO: Objective 1: generate rays given the provided parmeters
		
		// Raytracing p. 8
		// vector e = eyePoint
		// vector d = viewDirection
		// scalar d = 1

		// p10
		double fovMult = Math.tan(cam.fovy * Math.PI / 180.0) / cam.imageSize.height;
		double u = (0.5 - cam.imageSize.width * 0.5 + i) * fovMult;
		double v = (0.5 - cam.imageSize.height * 0.5 + j) * fovMult;

		// p9
		eyePoint = cam.from; // p = e
		viewDirection = new Vector3d(cam.getZAxis()); // d =
		viewDirection.negate(); // -(scalar d = 1)w
		Vector3d uVec = new Vector3d(cam.getXAxis());
		uVec.scale(u);
		viewDirection.add(uVec); // + u
		Vector3d vVec = new Vector3d(cam.getYAxis());
		vVec.scale(v);
		viewDirection.add(vVec); // + v
		// normalize, it makes testing shapes easier
		viewDirection.normalize();
	}
	
	/**
	 * Computes the location of a point along the ray using parameter t.
	 * @param t
	 * @param p
	 * That's so confusing. What does this do???
	 */
	/*public void getPoint( double t, Point3d p ) {
		p.scale( t, viewDirection );
		p.add( eyePoint );
	}*/
	
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
	
	public String toString() {
		return "Ray" + eyePoint + "->" + viewDirection;
	}

}

