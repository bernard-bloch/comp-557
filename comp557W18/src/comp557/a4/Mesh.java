package comp557.a4;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import comp557.a4.PolygonSoup.Vertex;

public class Mesh extends Intersectable {
	
	/** Static map storing all meshes by name */
	private static Map<String,Mesh> meshMap = new HashMap<String,Mesh>();
	
	/**  Name for this mesh, to allow re-use of a polygon soup across Mesh objects */
	private String name;// = "";
	
	/**
	 * The polygon soup.
	 * Only for cloning objects.
	 */
	private final PolygonSoup soup;
	
	private final Sphere bounding;

	// tris is processed, triangulated polygon soup
	private final List<Tri> tris = new ArrayList<>();

	// All faces must be triangles.
	private class Tri {
		private final Vector3d a, b, c, ab, bc, ca, n;
		private final double /*d,*/ area;
		private final Plane p;
		Tri(final Point3d a, final Point3d b, final Point3d c) {
			this.a = new Vector3d(a);
			this.b = new Vector3d(b);
			this.c = new Vector3d(c);
			ab = new Vector3d(b);
			ab.sub(a);
			bc = new Vector3d(c);
			bc.sub(b);
			ca = new Vector3d(a);
			ca.sub(c);
			n = new Vector3d();
			n.cross(ab, bc); // normal
			n.normalize();
			//d = -n.dot(new Vector3d(a)); // ax + by + cz + d = 0
			// we must calculate the area
			Vector3d crossProd = new Vector3d();
			crossProd.cross(ca, ab);
			area = 1.0 / n.dot(crossProd);
			p = new Plane(a, n, material, null);
		}
	}
	
	public Mesh(final String name, final PolygonSoup soup, final Material m) {
		super(m);
    	assert(name != null && soup != null && m != null && soup.vertexList != null && soup.vertexList.size() > 0);
		this.name = name;
		this.soup = soup;
    	if ( !meshMap.containsKey(name) )
    		meshMap.put(name, this);
    	List<Vertex> vs = soup.vertexList;
    	// figure out the bounding sphere
    	Vector3d toCentre = new Vector3d();
    	double toCentreLen, radius = 0.0;
		Point3d centre = new Point3d(vs.get(0).p);
    	for(Vertex v : vs) {
   			toCentre.sub(v.p, centre);
   			toCentreLen = toCentre.length();
			if(radius < toCentreLen) {
				double scale = 0.5*(toCentreLen-radius);
				toCentre.scale(scale);
    			toCentreLen *= scale;
				centre.add(toCentre);
				radius += toCentreLen;
			}
    	}
    	this.bounding = new Sphere(centre, radius, m);
    	// triangulate and process
    	for(int face[] : soup.faceList) {
    		for(int i = 2; i < face.length; i++) {
    			tris.add(new Tri(vs.get(face[0]).p, vs.get(face[i-1]).p, vs.get(face[i]).p));
    		}
    	}
	}			
		
	@Override
	public IntersectResult intersect(Ray ray) {
		
		// Objective 7: ray triangle intersection for meshes
		
		// quick bounding sphere test
		if( bounding.intersect(ray) == null ) return null;
		
		IntersectResult ir = null, irtemp;
		Vector3d v = new Vector3d(); // temporary vector to store cross-products
		double a = 0, b = 0, c = 0, atemp, btemp, ctemp;
		for(Tri t : tris) {
			irtemp = t.p.intersect(ray);
			if(irtemp == null || (ir != null && irtemp.getT() > ir.getT())) continue;

			// see if the intersection is in the triangle using barycentric coordinates
			Point3d p = irtemp.getPoint();

			Vector3d ap = new Vector3d(p);
			ap.sub(t.a);
			v.cross(t.ab, ap);
			if((ctemp = v.dot(t.n)) < 0) continue;
			
			Vector3d bp = new Vector3d(p);
			bp.sub(t.b);
			v.cross(t.bc, bp);
			if((atemp = v.dot(t.n)) < 0) continue;

			Vector3d cp = new Vector3d(p);
			cp.sub(t.c);
			v.cross(t.ca, cp);
			if((btemp = v.dot(t.n)) < 0) continue;

			c = ctemp * t.area;
			a = atemp * t.area;
			b = btemp * t.area;
			ir = irtemp;
		}
		// FIXME: interpolate normals for smooth surfaces
		return ir;
	}

	public static Map<String, Mesh> getMeshMap() {
		return meshMap;
	}

	public String getName() {
		return name;
	}

	public PolygonSoup getSoup() {
		return soup;
	}

}
