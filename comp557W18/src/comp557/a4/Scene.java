package comp557.a4;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.vecmath.Color3f;
import javax.vecmath.Color4f;
import javax.vecmath.Point3d;
import javax.vecmath.Tuple3d;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

/**
 * Simple scene loader based on XML file format.
 * Initialize with Parser.
 */
public abstract class Scene {
    
    /** List of surfaces in the scene */
    protected List<Intersectable> surfaceList = new ArrayList<Intersectable>();
	
    /** Contains information about how to render the scene */
    protected Render render;
    
    /** The ambient light colour. */
    protected Color3f ambient = new Color3f();
    
	// https://en.wikipedia.org/wiki/Alpha_compositing
    private void alphaBlend(Color4f colour, Color4f add) {
    	colour.x = colour.x + add.x * (1.0f - colour.w);
    	colour.y = colour.y + add.y * (1.0f - colour.w);
    	colour.z = colour.z + add.z * (1.0f - colour.w);
    	colour.w = colour.w + add.w * (1.0f - colour.w);
    }
    
	static DecimalFormat df = new DecimalFormat("#.00");
	static private String tup(Tuple3d a) {
		return "(" + df.format(a.x) + "," + df.format(a.y) + "," + df.format(a.z) + ")";
	}
    static private String tup(Color3f a) {
    	return tup(new Vector3d(a));
	}
	Random ran = new Random(0);
    /**
     * @param irs Given a list of intersection results.
     * @return The color of the given pixel.
     */
    private Color4f colour(List<IntersectResult> irs) {
    	final boolean isPrint = ran.nextInt(100000) == 0;
    	// sort the results based on t. I go forward and add.
    	//irs.sort(Comparator.comparingDouble(IntersectResult::getT));
    	// Now they are already sorted.
    	// get all the lights -- already done!
    	//List<Light> lights = Light.getAllLights();
    	// the colour starts off at 0
    	Color4f c = new Color4f();
    	// each intersection result adds to the alpha until it get's full
    	if(isPrint) System.err.println("IRs total " + irs.size());;
        for(IntersectResult ir : irs) {
        	
    		if(isPrint) System.err.println("for " + ir);
        	Material m = ir.getMaterial();
        	Color4f kd = m.getDiffuse();
        	Color4f ks = m.getSpecular();
    		Point3d p0 = ir.getPoint();
        	// add contributions from lights.
        	Vector3f diffuse = new Vector3f();
        	Vector3f specular = new Vector3f();
        	List<Light> lights = Light.getLights();
        	for(Light light : lights) {

        		if(isPrint) System.err.println("  for " + light);		
        		Vector3d l = new Vector3d(light.getFrom());
        		l.sub(p0);
        		l.normalize();
        		
        		// if it's in shadow, the lighting doesn't affect it
        		if(inShadow(ir, light)) continue;
        		
        		// 07Lighting p5 diffuse: Ld = kd I max(0, n*l)
        		Vector3d n = ir.getNormal();
        		float nl = (float)n.dot(l);
        		Color4f lightColour = light.getColour();
        		Color3f I = new Color3f(lightColour.x, lightColour.y, lightColour.z);
        		I.scale((float)light.getPower());
        		if(nl > 0.0f) {
	        		Color3f Ld = new Color3f(kd.x * I.x, kd.y * I.y, kd.z * I.z);
	        		// fixed light to default to w=1 instead of w=0 */
	        		Ld.scale(nl * lightColour.w);
	        		diffuse.add(Ld);
	        		if(isPrint)
            			System.err.println("    l = "+tup(l)+"; n = "+tup(n)+"; l*n = "+df.format(nl));
        		}
        		
        		// 07Lighting p8 specular: Ls = ks I max(0, n*h)^p
            	Vector3d h = new Vector3d(render.getCamera().getFrom());
            	h.sub(p0);
            	h.normalize();
            	h.add(l);
    			h.normalize();
    			float nh = (float)n.dot(h);
    			if(nh > 0.0f) {
            		float p = m.getShinyness();
	        		Color3f Ls = new Color3f(ks.x * I.x, ks.y * I.y, ks.z * I.z);
	        		Ls.scale((float)light.getPower());
            		if(isPrint)
            			System.err.println("    Ls(inte)="+Ls);
	        		Ls.scale((float)Math.pow(nh, p));
	        		Ls.scale(nh /* light.color.w <- breaks? */);
	        		specular.add(Ls);
            		if(isPrint)
            			System.err.println("    h="+tup(h)+" p="+p+" spec n*h = "+df.format(nl)+" Ls = "+tup(Ls));
        		}

        	}
        	// use a probable heuristic. diffuse.length() * <- no, the coefficient is always 1 since with shadows the shapes do not become transparent
        	float alpha = kd.w + specular.length() * ks.w;
    		if(isPrint)
    			System.err.println("    alpha = "+df.format(alpha));
        	Color4f beautiful = new Color4f(diffuse.x + specular.x + ambient.x, diffuse.y + specular.y + ambient.y, diffuse.z + specular.z + ambient.z, alpha);
        	beautiful.clamp(0.0f, 1.0f);
    		alphaBlend(c, beautiful);
        	// if the alpha is 1, just stop
        	if(c.w >= 1.0f) break;
        }
        // turn on background.
        alphaBlend(c, render.getBgcolour());
        return c;
    }
    
	/**
     * renders the scene
     */
    public void render() {
 
        Camera cam = render.getCamera(); 
        int w = cam.getImageSize().width;
        int h = cam.getImageSize().height;
        
        List<Light> lights = Light.getLights();
        for(Light l : lights) {
        	System.err.println("Light " + l);
        }
        
        for ( int y = 0; y < h && !render.isDone(); y++ ) { // bottom to top
        	for ( int x = 0; x < w && !render.isDone(); x++ ) { // left to right
        		int samples = render.getSamples();
        		Color4f colour = new Color4f();
            	for (int s = 0; s < samples; s++) {

            		// Objective 1: generate a ray (use the generateRay method)
                    // Objective 2: test for intersection with scene surfaces
                	IntersectResult result = getClosestResult(new Ray(x, y, cam, s != 0));
    				
                	// FIXME: instead of [0..1] make it [0..n] and have reflected/refracted/transparent ray transport
                	List<IntersectResult> irs = new ArrayList<>();
                	if(result != null) irs.add(result);
                	
                	colour.add(colour(irs));
            	}
            	colour.scale(1.0f / samples);
            	
                // Objective 3: compute the shaded result for the intersection point (perhaps requiring shadow rays)
                // update the render image
                render.setPixel(x, y, colour);
            }
        }
        Light.getLights().clear();
        
        // save the final render image
        render.save();

        // wait for render viewer to close
        render.waitDone();
        
    }
    
	private IntersectResult getClosestResult(Ray ray) {
    	IntersectResult result = null;
    	for(Intersectable il : surfaceList) {
    		IntersectResult r = il.intersect(ray);
            if(r == null) continue;
            // Gets the closest t and ignore the rest.
            if(result == null || result.getT() > r.getT()) result = r;
    	}
    	return result;
	}

	// Objective 1: generate rays given the provided parmeters
	// I moved this to constructor in Ray.java.

	/**
	 * Shoot a shadow ray in the scene and get the result.
	 * 
	 * @param result Intersection result from raytracing. 
	 * @param light The light to check for visibility.
	 * @param root The scene node.
	 * @param shadowResult Contains the result of a shadow ray test.
	 * @param shadowRay Contains the shadow ray used to test for visibility.
	 * 
	 * @return True if a point is in shadow, false otherwise. 
	 */
	private boolean inShadow(IntersectResult ir, final Light light) {
		
		// Objective 5: check for shdows and use it in your lighting computation
		// TODO: boolean is not a good thing for transparent scenes
		
		// epsilon
		Point3d p0 = new Point3d(ir.getNormal());
		p0.scale(0.01);
		p0.add(ir.getPoint());
		
		Vector3d toLight = new Vector3d(light.getFrom());
		toLight.sub(p0);
		double mag = toLight.length();
		
		//System.err.println("light "+light+" is at "+toLight);
		Ray ray = new Ray(p0, toLight);
		//System.err.println("ray "+ray);
    	for(Intersectable il : surfaceList) {
    		IntersectResult shadow = il.intersect(ray);
    		if(shadow != null && shadow.getT() < mag) return true;
    	}
		return false;
	}

}

