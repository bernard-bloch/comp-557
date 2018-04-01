package comp557.a4;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.vecmath.Color3f;
import javax.vecmath.Color4f;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector4f;

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
    
	static boolean isOnce = true;
    /**
     * @param irs Given a list of intersection results.
     * @return The color of the given pixel.
     */
    private Color4f colour(List<IntersectResult> irs) {
    	// sort the results based on t. I go forward and add.
    	irs.sort(Comparator.comparingDouble(IntersectResult::getT));
    	// get all the lights
    	List<Light> lights = Light.getAllLights();
    	// the colour starts off at 0
    	Color4f c = new Color4f();
    	// each intersection result adds to the alpha until it get's full
        for(IntersectResult ir : irs) {
        	Material m = ir.getMaterial();
        	assert(m != null);
        	// add contributions from lights. Ignore alpha component. What's an alpha on a light?
        	Color3f colour3 = new Color3f();
        	for(Light light : lights) {
        		// 07Lighting p5 diffuse: Ld = kd I max(0, n*l)
        		Vector3d l = new Vector3d(light.from);
        		l.sub(ir.getPoint());
        		l.normalize();
        		float nl = (float)ir.getNormal().dot(l);
        		if(nl <= 0.0) continue;
        		Color3f kd = new Color3f(m.diffuse.x, m.diffuse.y, m.diffuse.z);
        		Color3f I = new Color3f(light.color.x, light.color.y, light.color.z);
        		Color3f Ld = new Color3f(kd.x * I.x, kd.y * I.y, kd.z * I.z);
        		Ld.scale(nl);
        		// add to colour3
        		colour3.add(Ld);
        		if(isOnce) {
        			System.out.println("Scene " + ir.getNormal() + ": " + light + " to " + ir.getPoint() + " = " + l + " n*l " + nl);
        			isOnce = false;
        		}
        	}
        	// the contribution of the colour and the materials alpha
        	Color4f colour4 = new Color4f(colour3.x, colour3.y, colour3.z, m.diffuse.w);
        	//Color4f colour4 = new Color4f();
        	//colour4.x = (float)(ir.getNormal().x > 0.0 ? ir.getNormal().x : 0.0);
        	//colour4.y = (float)(ir.getNormal().y > 0.0 ? ir.getNormal().y : 0.0);
        	//colour4.z = (float)(ir.getNormal().z > 0.0 ? ir.getNormal().z : 0.0);
        	//colour4.w = 1.0f;
    		alphaBlend(c, colour4);
    		//c.add(colour4);
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
        int w = cam.imageSize.width;
        int h = cam.imageSize.height;
        
        render.init(w, h, showPanel);
        
        // Material has no constructor arguments and all public data
        // I don't want to fix this. However, the alpha blending is much simpler when you pre-multiply
        //Material.premultiplyAll();
        
        for ( int i = 0; i < h && !render.isDone(); i++ ) { // left to right
            for ( int j = 0; j < w && !render.isDone(); j++ ) { // bottom to top
            	
                // TODO: Objective 1: generate a ray (use the generateRay method)
            	Ray ray = new Ray(i, j, cam);
            	//System.out.println("Ray"+i+","+j+" "+ray.eyePoint+" going "+ray.viewDirection+"."); 

                // TODO: Objective 2: test for intersection with scene surfaces
				List<IntersectResult> irs = new ArrayList<>();
            	for(Intersectable il : surfaceList) il.intersect(ray, irs);
				
                // TODO: Objective 3: compute the shaded result for the intersection point (perhaps requiring shadow rays)
                
            	// Here is an example of how to calculate the pixel value.                
                // update the render image
                render.setPixel(j, i, colour(irs));
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

