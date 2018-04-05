package comp557.a4;

import java.util.HashMap;
import java.util.Map;

public class Mesh extends Intersectable {
	
	/** Static map storing all meshes by name */
	private static Map<String,Mesh> meshMap = new HashMap<String,Mesh>();
	
	/**  Name for this mesh, to allow re-use of a polygon soup across Mesh objects */
	private String name;// = "";
	
	/**
	 * The polygon soup.
	 */
	private PolygonSoup soup;

	public Mesh(String name, PolygonSoup soup, Material m) {
		super(m);
		this.name = name;
		this.soup = soup;
    	if ( !meshMap.containsKey(name) )
    		meshMap.put(name, this);
	}			
		
	@Override
	public IntersectResult intersect(Ray ray) {
		
		// TODO: Objective 7: ray triangle intersection for meshes
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
