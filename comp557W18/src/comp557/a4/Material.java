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
	private static Map<String,Material> materialMap = new HashMap<String,Material>();
	
	/** Material name */
    private String name;// = "";
    
    /** Diffuse colour, defaults to white */
    private Color4f diffuse;// = new Color4f(1,1,1,1);
    
    /** Specular colour, default to black (no specular highlight) */
    private Color4f specular;// = new Color4f(0,0,0,0);
    
    /** Specular hardness, or exponent, default to a reasonable value */ 
    private float shinyness;// = 64;
 
	public Material(String name, Color4f diffuse, Color4f specular, float shinyness) {
		// FIXME: premultiply
		//premultiply(diffuse);
		//premultiply(specular);
		this.name = name;
		this.diffuse = diffuse;
		this.specular = specular;
		this.shinyness = shinyness;
	}
    
    // https://en.wikipedia.org/wiki/Alpha_compositing
    /*private static void premultiply(Color4f c) {
		if(c.w < 0.001) {
			c.set(0.0f, 0.0f, 0.0f, 0.0f);
			return;
		}
		c.x /= c.w;
		c.y /= c.w;
		c.z /= c.w;
    }*/

    public String toString() {
    	return name;
    }
        
    public static Map<String,Material> getMaterialMap() {
    	return materialMap;
    }

	public String getName() {
		return name;
	}

	public Color4f getDiffuse() {
		return diffuse;
	}

	public Color4f getSpecular() {
		return specular;
	}

	public float getShinyness() {
		return shinyness;
	}

    
}
