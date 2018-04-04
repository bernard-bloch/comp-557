package comp557.a4;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.vecmath.Color3f;
import javax.vecmath.Color4f;
import javax.vecmath.Tuple3d;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

/**
 * Simple scene loader based on XML file format.
 */
public class Scene {
    
    /** List of surfaces in the scene */
    public List<Intersectable> surfaceList = new ArrayList<Intersectable>();
	
	/** All scene lights */
	public Map<String,Light> lights = new HashMap<String,Light>();

    /** Contains information about how to render the scene */
    public Render render;
    
    /** The ambient light colour */
    public Color3f ambient = new Color3f();

    /** 
     * Default constructor.
     */
    public Scene() {
    	this.render = new Render();
    }
    
	// https://en.wikipedia.org/wiki/Alpha_compositing
    private void alphaBlend(Color4f colour, Color4f add) {
    	colour.x = colour.x + add.x * (1.0f - colour.w);
    	colour.y = colour.y + add.y * (1.0f - colour.w);
    	colour.z = colour.z + add.z * (1.0f - colour.w);
    	colour.w = colour.w + add.w * (1.0f - colour.w);
    }
    
    private void alphaBlend(Color4f colour, Color3f add) {
    	colour.x = colour.x + add.x * (1.0f - colour.w);
    	colour.y = colour.y + add.y * (1.0f - colour.w);
    	colour.z = colour.z + add.z * (1.0f - colour.w);
    	colour.w = 1;
    }    
    
	static private String tup(Tuple3d a) {
		DecimalFormat df = new DecimalFormat("#.0");
		return "(" + df.format(a.x) + ", " + df.format(a.y) + ", " + df.format(a.z) + ")";
	}
	Random ran = new Random(0);
    /**
     * @param irs Given a list of intersection results.
     * @return The color of the given pixel.
     */
    private Color4f colour(List<IntersectResult> irs) {
    	final boolean isPrint = ran.nextInt(4000) == 0;
    	// sort the results based on t. I go forward and add.
    	irs.sort(Comparator.comparingDouble(IntersectResult::getT));
    	// get all the lights
    	List<Light> lights = Light.getAllLights();
    	// the colour starts off at 0
    	Color4f c = new Color4f();
    	// each intersection result adds to the alpha until it get's full
        for(IntersectResult ir : irs) {
        	
    		if(isPrint) System.err.println("for " + ir);
        	Material m = ir.getMaterial();
        	assert(m != null);
        	// add contributions from lights.
        	Color3f diffuse = new Color3f();
        	Color3f specular = new Color3f();
        	for(Light light : lights) {

        		if(isPrint) System.err.println("  for " + light);
        		// 07Lighting p5 diffuse: Ld = kd I max(0, n*l)
        		Vector3d l = new Vector3d(light.from);
        		l.sub(ir.getPoint());
        		l.normalize();
        		Vector3d n = ir.getNormal();
        		float nl = (float)n.dot(l);
        		Color3f I = new Color3f(light.color.x, light.color.y, light.color.z);
        		if(nl > 0.0f) {
	        		Color3f kd = new Color3f(m.diffuse.x, m.diffuse.y, m.diffuse.z);
	        		Color3f Ld = new Color3f(kd.x * I.x, kd.y * I.y, kd.z * I.z);
	        		Ld.scale(nl /* * light.color.w <- does not illumiate sample scenes*/);
	        		diffuse.add(Ld);
	        		if(isPrint) {
	        			DecimalFormat df = new DecimalFormat("#.00");
            			System.err.println("    l = "+tup(l)+"; n = "+tup(n)+"; l*n = "+df.format(nl));
	        		}
        		}
        		
        		// 07Lighting p8 specular: Ls = ks I max(0, n*h)^p
            	Vector3d h = new Vector3d();
            	h.add(n, l);
    			h.normalize(); // doesn't say anything about error at singular point?
    			float nh = (float)n.dot(h);
    			if(nh > 0.0f) {
            		float p = m.shinyness;
                	Color4f ks = m.specular;
	        		Color3f Ls = new Color3f(ks.x * I.x, ks.y * I.y, ks.z * I.z);
	        		Ls.scale((float)Math.pow(nh, p));
	        		Ls.scale(nh /* * light.color.w*/);
	        		specular.add(Ls);
        		}

        	}
        	// use a probable heuristic
        	Vector3f diffuseVec = new Vector3f(diffuse);
        	Vector3f specularVec = new Vector3f(diffuse);
        	float alpha = diffuseVec.length() * m.diffuse.w + specularVec.length() * m.specular.w;
        	Color4f beautiful = new Color4f(diffuse.x + specular.x + ambient.x, diffuse.y + specular.y + ambient.y, diffuse.z + specular.z + ambient.z, alpha);
        	beautiful.clamp(0.0f, 1.0f);
    		alphaBlend(c, beautiful);
        	// if the alpha is 1, just stop
        	if(c.w >= 1.0f) break;
        }
        // turn on background. This will render an all alpha = 1
        alphaBlend(c, render.bgcolor);
        return c;
    }
    
    /**
     * renders the scene
     */
    public void render(boolean showPanel) {
 
        Camera cam = render.camera; 
        int w = cam.getImageSize().width;
        int h = cam.getImageSize().height;
        
        render.init(w, h, showPanel);
        
        // Material has no constructor arguments and all public data
        // I don't want to fix this. However, the alpha blending is much simpler when you pre-multiply
        Material.premultiplyAll();
        
        for ( int y = 0; y < h && !render.isDone(); y++ ) { // bottom to top
        	for ( int x = 0; x < w && !render.isDone(); x++ ) { // left to right
            	
                // Objective 1: generate a ray (use the generateRay method)
            	Ray ray = new Ray(x, y, cam);

                // Objective 2: test for intersection with scene surfaces
            	IntersectResult result = null;
            	for(Intersectable il : surfaceList) {
            		IntersectResult r = il.intersect(ray);
                    if(r == null) continue;
                    // Gets the closest t and ignore the rest. FIXME: this is inefficient
                    if(result == null || result.getT() > r.getT()) result = r;
            	}
				
            	// FIXME: instead of [0..1] make it [0..n] and have reflected/refracted/transparent scenes
            	List<IntersectResult> irs = new ArrayList<>();
            	if(result != null) irs.add(result);
            	
                // Objective 3: compute the shaded result for the intersection point (perhaps requiring shadow rays)
                // update the render image
                render.setPixel(x, y, colour(irs));
            }
        }
        
        // save the final render image
        render.save();
        
        // wait for render viewer to close
        render.waitDone();
        
    }
    
	// TODO: Objective 1: generate rays given the provided parmeters
	// What do you have against OOP? This is moved to constructor in Ray.java.

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
	public static boolean inShadow(final IntersectResult result, final Light light, final SceneNode root, IntersectResult shadowResult, Ray shadowRay) {
		
		// TODO: Objective 5: check for shdows and use it in your lighting computation
		
		return false;
	}    
}

