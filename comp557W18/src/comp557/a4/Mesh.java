package comp557.a4;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.vecmath.Point2d;
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
	 */
	private PolygonSoup soup;

	// raytracing does not do vertex normals. All faces must be triangles.
	private class Tri {
		Point2d v1, v2, v3;
		Plane p;
		Tri(Point3d v1, Point3d v2, Point3d v3) {
			Vector3d y = new Vector3d();
			Vector3d z = new Vector3d(v2);
			z.sub(v1);
			Vector3d x = new Vector3d(v3);
			x.sub(v1);
			y.cross(z, x);
			y.normalize();
			p = new Plane(v1, y, null, null);
			this.v1 = new Point2d(0, 0);
			/*this.v2 = new Point2d(z.dot());*/
		}
	}

	private List<Tri> tris = new ArrayList<>();
	
	public Mesh(String name, PolygonSoup soup, Material m) {
		super(m);
		this.name = name;
		this.soup = soup;
    	if ( !meshMap.containsKey(name) )
    		meshMap.put(name, this);
    	List<Vertex> vs = soup.vertexList;
    	for(int face[] : soup.faceList) {
    		// triangulate
    		for(int i = 2; i < face.length; i++) {
    			tris.add(new Tri(vs.get(face[0]).p, vs.get(face[i-1]).p, vs.get(face[i]).p));
    		}
    	}
	}			
		
	@Override
	public IntersectResult intersect(Ray ray) {
		
		// Objective 7: ray triangle intersection for meshes
		
		
		return null;
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
