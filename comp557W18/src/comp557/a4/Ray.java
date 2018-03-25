package comp557.a4;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

public class Ray {
	
	/** Originating point for the ray */
	public Point3d eyePoint = new Point3d( 0, 0, 0 );
	
	/** The direction of the ray */
	public Vector3d viewDirection = new Vector3d( 0, 0, -1 );

	/**
	 * Default constructor.  Be careful not to use the ray before
	 * setting the eye point and view direction!
	 */
	public Ray() {
		// do nothing
	}
	
	/** 
	 * Creates a new ray with the given eye point and view direction 
	 * @param eyePoint
	 * @param viewDirection
	 */
	public Ray( Point3d eyePoint, Vector3d viewDirection ) {
		this.eyePoint.set(eyePoint);
		this.viewDirection.set(viewDirection);
	}

	/**
	 * Setup the ray.
	 * @param eyePoint
	 * @param viewDirection
	 */
	public void set( Point3d eyePoint, Vector3d viewDirection ) {
		this.eyePoint.set(eyePoint);
		this.viewDirection.set(viewDirection);
	}
	
	/**
	 * Computes the location of a point along the ray using parameter t.
	 * @param t
	 * @param p
	 */
	public void getPoint( double t, Point3d p ) {
		p.scale( t, viewDirection );
		p.add( eyePoint );
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
     * No.
     */
	public Ray(final int i, final int j, final Camera cam) {
		
		// TODO: Objective 1: generate rays given the provided parmeters
		int w = cam.imageSize.width;
		int h = cam.imageSize.height;

		// p. 10
		Vector3d u = new Vector3d(cam.getXAxis());
		u.scale(cam.imageSize.width / 2 + (eyePoint.x - w / 2 + 0.5) / (w / 2));
		Vector3d v = new Vector3d(cam.getYAxis());
		v.negate();
		v.scale(cam.imageSize.height / 2 + (eyePoint.y - h / 2 + 0.5) / (h / 2));

		// return
		/*Vector3d ray = new Vector3d(cam.getZAxis().negate());
		ray.add(u);
		ray.add(v);*/
		return new Ray(cam.from, cam.getZAxis().negate().add(u).add(v));
	}

}
