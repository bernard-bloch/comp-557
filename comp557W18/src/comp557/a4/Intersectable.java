package comp557.a4;

/**
 * Abstract class for an intersectable surface 
 */
public abstract class Intersectable {
	
	/** Material for this intersectable surface */
	public Material material;
	
	/** 
	 * Default constructor, creates the default material for the surface
	 */
	public Intersectable(Material m) {
		this.material = m;
		//new Material("default", new Color4f(1,1,1,1), new Color4f(0,0,0,0), 64);
	}
	
	/**
	 * Test for intersection between a ray and this surface. This is an abstract
	 *   method and must be overridden for each surface type.
	 * @param ray
	 * @return Result or null if it didn't intersect.
	 */
    public abstract IntersectResult intersect(Ray ray);
    
}
