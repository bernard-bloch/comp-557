package comp557.a4;

/**
 * Abstract class for an intersectable surface 
 */
public abstract class Intersectable {
	
	/** Material for this intersectable surface
	 * COULD BE NULL */
	protected Material material;
	
	/** 
	 * Default constructor, creates the default material for the surface
	 */
	public Intersectable(Material m) {
		this.material = m;
	}
	
	/**
	 * Test for intersection between a ray and this surface. This is an abstract
	 *   method and must be overridden for each surface type.
	 * @param ray
	 * @return Result or null if it didn't intersect.
	 */
    public abstract IntersectResult intersect(Ray ray);

	public Material getMaterial() {
		return material;
	}
    
}
