package comp557.a4;

import java.util.function.Function;

import javax.vecmath.Point2d;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

/**
 * A simple box class. A box is defined by it's lower (@see min) and upper (@see max) corner. 
 */
public class Box extends Intersectable {

	private MinMax axes[];

	// https://docs.oracle.com/javase/tutorial/java/javaOO/enum.html
	private enum Axes {
		X(new Vector3d(1,0,0), x -> new Point2d(x.y, x.z)),
		Y(new Vector3d(0,1,0), y -> new Point2d(y.x, y.z)),
		Z(new Vector3d(0,0,1), z -> new Point2d(z.x, z.y));
		private final Vector3d axis;
		private final Function<Point3d,Point2d> proj;
		Axes(Vector3d axis, Function<Point3d,Point2d> proj) {
			this.axis = axis;
			this.proj = proj;
		};
		Vector3d getAxis() { return this.axis; };
		Function<Point3d,Point2d> getProj() { return this.proj; }
	}
		
	private final class MinMax extends Intersectable {
		private Axes axis;
		private Plane minPlane, maxPlane;
		private Point2d min, max;
		MinMax(final Axes axis, final Point3d min, final Point3d max, final Material m) {
			super(m);
			this.axis = axis;
			this.minPlane = new Plane(min, axis.getAxis(), m, null);
			this.maxPlane = new Plane(max, axis.getAxis(), m, null);
			this.min = axis.getProj().apply(min);
			this.max = axis.getProj().apply(max);
		}
		public IntersectResult intersect(Ray ray) {
			IntersectResult irMin, irMax;
			irMin = minPlane.intersect(ray);
			if(irMin != null) {
				Point2d proj = axis.getProj().apply(irMin.getPoint());
				if(min.x < proj.x || min.y < proj.x) irMin = null;
			}
			irMax = maxPlane.intersect(ray);
			if(irMax != null) {
				Point2d proj = axis.getProj().apply(irMax.getPoint());
				if(max.x > proj.x || max.y > proj.x) irMax = null;
			}
			if(irMin == null) return irMax;
			if(irMax == null) return irMin;
			return irMin.getT() < irMax.getT() ? irMin : irMax;
		}
	}
	
    /**
     * Default constructor.
     */
    public Box(Point3d min, Point3d max, Material m) {
    	super(m);
    	axes = new MinMax[3];
    	axes[0] = new MinMax(Axes.X, min, max, m);
    	axes[1] = new MinMax(Axes.Y, min, max, m);
    	axes[2] = new MinMax(Axes.Z, min, max, m);
    }

	@Override
	public IntersectResult intersect(Ray ray) {
		// Objective 6: intersection of Ray with axis aligned box
		IntersectResult ir[] = new IntersectResult[axes.length];
		for(int i = 0; i < axes.length; i++) ir[i] = axes[i].intersect(ray);
		if(ir[0] != null && (ir[1] == null || ir[0].getT() < ir[1].getT())) ir[1] = ir[0];
		if(ir[1] != null && (ir[2] == null || ir[1].getT() < ir[2].getT())) ir[2] = ir[1];
		return ir[2];
	}	
    
}
