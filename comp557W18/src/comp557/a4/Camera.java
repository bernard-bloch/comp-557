package comp557.a4;

import java.awt.Dimension;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

/**
 * Simple camera object, which could be extended to handle a variety of 
 * different camera settings (e.g., aperature size, lens, shutter)
 */
public class Camera {
	
	/** Camera name */
    public String name = "camera";

    /** The eye position */
    public Point3d from = new Point3d(0,0,10);
    
    /** The "look at" position */
    public Point3d to = new Point3d(0,0,0);
    
    /** Up direction, default is y up */
    public Vector3d up = new Vector3d(0,1,0);
    
    /** Vertical field of view (in degrees), default is 45 degrees */
    public double fovy = 45.0;
    
    /** The rendered image size */
    public Dimension imageSize = new Dimension(640,480);

    // cache the axis of the camera given 
    private Vector3d x, y, z;

    /**
     * Default constructor
     */
    public Camera() {
    	// do nothing
        z = new Vector3d(from);
        z.sub(to);
        z.normalize();

        y = new Vector3d(up);
        y.normalize();

        x = new Vector3d();
        x.cross(y,z);

        // orthonormalize
        y.cross(z,x);
    }
    
    public Vector3d getXAxis() {
        return x;
    }

    public Vector3d getYAxis() {
        return y;
    }

    public Vector3d getZAxis(){
        return z;
    }
}

