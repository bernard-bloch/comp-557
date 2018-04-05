package comp557.a4;

import javax.vecmath.Point3d;

/**
 * A simple box class. A box is defined by it's lower (@see min) and upper (@see max) corner. 
 */
public class Box extends Intersectable {

	private Point3d min;
	private Point3d max;
	
    /**
     * Default constructor. Creates a 2x2x2 box centered at (0,0,0)
     */
    public Box(Point3d min, Point3d max, Material m) {
    	super(m);
    	this.min = min;
    	this.max = max;
    }	

	@Override
	public IntersectResult intersect(Ray ray) {
		// TODO: Objective 6: intersection of Ray with axis aligned box
		return null;
	}	

}
