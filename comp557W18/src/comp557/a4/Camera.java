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
    //private String name;
	// never used
    
    /** FOV multiplier */
    private double fov;
    
    /** The rendered image size */
    private Dimension imageSize;// = new Dimension(640,480);

    private Point3d from;
    private Vector3d x, y, z;

    /**
     * constructor. for real.
     *      */
    public Camera(Point3d from, Point3d to, Vector3d up, double fovy, Dimension image) {
    	//this.name = name;

    	// from the from, to, and up, get the axis
    	this.from = new Point3d(from);
        this.z = new Vector3d(from);
        this.z.sub(to);
        this.z.normalize();
        this.y = new Vector3d(up);
        this.y.normalize();
        this.x = new Vector3d();
        this.x.cross(this.y,z);
        // orthonormalize
        this.y.cross(z,this.x);
		
        // from the fovy, calcuate the fov multiplier
        this.fov = -image.getHeight() * 1.0 / Math.tan(fovy * Math.PI / 180.0);
        
        // x, y -> imageSize
        this.imageSize = image;

        System.out.println("Camera x:"+x+"; y:"+y+"; z:"+z+".");
    }
        
    public Point3d getFrom() {
    	return from;
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
    
    public double getFOVMultiplier() {
    	return fov;
    }
    
    public Dimension getImageSize() {
    	return imageSize;
    }
    
}

