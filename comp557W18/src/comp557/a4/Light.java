package comp557.a4;

import javax.vecmath.Color4f;
import javax.vecmath.Point3d;

public class Light /*implements Iterable<Light>*/ {
	
	/** Light name */
    public String name = "";
    
    /** Light colour, default is white */
    public Color4f color = new Color4f(1,1,1,1);
    
    /** Light position, default is the origin */
    public Point3d from = new Point3d(0,0,0);
    
    /** Light intensity, I, combined with colour is used in shading */
    public double power = 1.0;
    
    /** Type of light, default is a point light */
    public String type = "point";

    // store all the lights for lighting
    //static private List<Light> allLights = new ArrayList<>();
    
    /**
     * Default constructor 
     */
    public Light() {
    	// do nothing
    	//allLights.add(this);
    }
    
    /*static public List<Light> getAllLights() {
    	return allLights;
    }*/
    
    public String toString() {
    	return name+" at "+from;
    }
}
