package comp557.a4;

import javax.vecmath.Point3d;
import javax.vecmath.Vector2d;
import javax.vecmath.Vector3d;

/**
 * A simple box class. A box is defined by it's lower (@see min) and upper (@see max) corner. 
 */
public class Box extends Intersectable {

	private Point3d min;
	private Point3d max;
	
	// https://docs.oracle.com/javase/tutorial/java/javaOO/enum.html
	enum Axes {
		X(new Vector3d(1,0,0), new Vector2d(min.y, min.z), new Vector2d(max.y, max.z)),
		Y(new Vector3d(0,1,0)),
		Z(new Vector3d(0,0,1));
		private final Vector3d axis;
		private final Vector2d min, max;
		Axes(Vector3d axis, Vector2d min, Vector2d max) {
			this.axis = axis;
			this.min = min;
			this.max = max;
		};
		Vector3d getAxis() { return this.axis; };
		Vector2d getMin() { return this.min; }
		Vector2d getMax() { return this.max; }
	}

	class Axis {
		Axes axis;
		Plane plane;
		Axis(Point3d x, Axes axis) {
			this.axis = axis;
			
		}
	}
	
	private Axis planes[];
	
    /**
     * Default constructor.
     */
    public Box(Point3d min, Point3d max, Material m) {
    	super(m);
    	this.min = min;
    	this.max = max;
    	planes = new Plane[6];
    	planes[0] = new Plane(min, new Vector3d(1,0,0), m, null);
    	planes[1] = new Plane(min, new Vector3d(0,1,0), m, null);
    	planes[2] = new Plane(min, new Vector3d(0,0,1), m, null);
    	planes[3] = new Plane(max, new Vector3d(1,0,0), m, null);
    	planes[4] = new Plane(max, new Vector3d(0,1,0), m, null);
    	planes[5] = new Plane(max, new Vector3d(0,0,1), m, null);
    }

	@Override
	public IntersectResult intersect(Ray ray) {
		// Objective 6: intersection of Ray with axis aligned box
		IntersectResult ir[] = new IntersectResult[6];
		for(int i = 0; i < 6; i++) ir[i] = planes[i].intersect(ray);
		IntersectResult closest = null;
		for(IntersectResult i : ir) {
			if(i == null) continue;
			if(closest == null || i.getT() < closest.getT()) closest = i;
		}
		return closest;
	}	
    
}
