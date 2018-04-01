package comp557.a4;

import java.util.HashMap;
import java.util.Map;

import javax.vecmath.Color4f;

/**
 * A class defining the material properties of a surface, 
 * such as colour and specularity. 
 */
public class Material {
	
	/** Static member to access all the materials */
	public static Map<String,Material> materialMap = new HashMap<String,Material>();
	
	/** Material name */
    public String name = "";
    
    /** Diffuse colour, defaults to white */
    public Color4f diffuse = new Color4f(1,1,1,1);
    
    /** Specular colour, default to black (no specular highlight) */
    public Color4f specular = new Color4f(0,0,0,0);
    
    /** Specular hardness, or exponent, default to a reasonable value */ 
    public float shinyness = 64;
 
    private boolean isPremultiplied = false;
    
    /**
     * Default constructor
     */
    public Material() {
    	// do nothing
    }
    
    public String toString() {
    	return name;
    }
    
    // https://en.wikipedia.org/wiki/Alpha_compositing
    private static void premultiply(Color4f c) {
		if(c.w < 0.001) {
			c.set(0.0f, 0.0f, 0.0f, 0.0f);
			return;
		}
		c.x /= c.w;
		c.y /= c.w;
		c.z /= c.w;
    }
    
    public static void premultiplyAll() {
    	// https://stackoverflow.com/questions/46898/how-to-efficiently-iterate-over-each-entry-in-a-map
    	materialMap.forEach((k, v) -> {
    		if(v.isPremultiplied) return;
    		premultiply(v.diffuse);
    		premultiply(v.specular);
    		v.isPremultiplied = true;
    	} );
    }
    
}
