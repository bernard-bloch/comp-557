package comp557.a4;

import java.util.function.Function;

import javax.vecmath.Point2d;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

/**
 * A simple box class. A box is defined by it's lower (@see min) and upper (@see max) corner. 
 */
public class Box extends Intersectable {

	private MinMax x, y, z;

	// https://docs.oracle.com/javase/tutorial/java/javaOO/enum.html
	private enum Axes {
		X(new Vector3d(1,0,0), v -> v.x, x -> new Point2d(x.y, x.z)),
		Y(new Vector3d(0,1,0), v -> v.y, y -> new Point2d(y.x, y.z)),
		Z(new Vector3d(0,0,1), v -> v.z, z -> new Point2d(z.x, z.y));
		private final Vector3d axisNeg, axisPos;
		private final Function<Point3d,Double> proj;
		private final Function<Point3d,Point2d> nullProj;
		Axes(Vector3d axis, Function<Point3d,Double> proj, Function<Point3d,Point2d> nullProj) {
			this.axisNeg = new Vector3d(axis);
			axisNeg.negate();
			this.axisPos = axis;
			this.proj = proj;
			this.nullProj = nullProj;
		};
		Vector3d getAxisNeg() { return this.axisNeg; };
		Vector3d getAxisPos() { return this.axisPos; };
		Function<Point3d,Double> getProj() { return this.proj; }
		Function<Point3d,Point2d> getNullProj() { return this.nullProj; }
	}
		
	private final class MinMax extends Intersectable {
		private Axes axis;
		private Plane minPlane, maxPlane;
		private double min, max;
		MinMax(final Axes axis, final Point3d min, final Point3d max, final Material m) {
			super(m);
			this.axis = axis;
			this.minPlane = new Plane(min, axis.getAxisNeg(), m, null);
			this.maxPlane = new Plane(max, axis.getAxisPos(), m, null);
			this.min = axis.getProj().apply(min);
			this.max = axis.getProj().apply(max);
		}
		public IntersectResult intersect(Ray ray) {
			
			// get the points that could intersect
			double eye = axis.getProj().apply(ray.getEyePoint());
			Point3d direction = new Point3d(ray.getViewDirection());
			double dir = axis.getProj().apply(direction);
			Plane could = null;
			if(dir < 0.0) {
				if(eye > max) could = maxPlane;
			} else if(dir > 0.0) {
				if(eye < min) could = minPlane;
			}
			if(could == null) return null;

			IntersectResult ir;
			/*irMin = minPlane.intersect(ray);
			if(irMin != null) {
				Point2d proj = axis.getNullProj().apply(irMin.getPoint());
				if(min.x < proj.x || min.y < proj.x) irMin = null;
			}*/
			ir = could.intersect(ray);
			/*if(irMax != null) {
				Point2d proj = axis.getNullProj().apply(irMax.getPoint());
				if(proj.x > max.x || proj.y > max.x) irMax = null;
			}*/
			/*if(irMin == null) return irMax;
			if(irMax == null) return irMin;
			return irMin.getT() < irMax.getT() ? irMin : irMax;*/
			return ir;
		}
		public String toString() {
			return "MinMax"+axis+" is "+minPlane+" and "+maxPlane+" with "+min+" and "+max;
		}
	}
	
    /**
     * Default constructor.
     */
    public Box(Point3d min, Point3d max, Material m) {
    	super(m);
    	x = new MinMax(Axes.X, min, max, m);
    	y = new MinMax(Axes.Y, min, max, m);
    	z = new MinMax(Axes.Z, min, max, m);
    }

	@Override
	public IntersectResult intersect(Ray ray) {
		// Objective 6: intersection of Ray with axis aligned box
		IntersectResult irx = x.intersect(ray);
		IntersectResult iry = y.intersect(ray);
		IntersectResult irz = z.intersect(ray);
		if(irx != null && (iry == null || irx.getT() < iry.getT())) iry = irx;
		if(iry != null && (irz == null || iry.getT() < irz.getT())) irz = iry;
		return irz;
	}	
    
}
