package comp557.a4;

import java.util.LinkedList;
import java.util.List;

import javax.vecmath.Color4f;
import javax.vecmath.Point3d;

public class Light /*implements Iterable<Light>*/ {
	
	/** Light name */
    private String name;// = "";
    
    /** Light colour, default is white */
    private Color4f color;// = new Color4f(1,1,1,1);
    
    /** Light position, default is the origin */
    private Point3d from;// = new Point3d(0,0,0);
    
    /** Light intensity, I, combined with colour is used in shading */
    private double power;// = 1.0;
    
    /** Type of light, default is a point light */
    private String type;// = "point";

    // store all the lights for lighting
    static private List<Light> lights = new LinkedList<>();
    
    /**
     * Default constructor 
     */
    public Light(String name, Color4f colour, Point3d from, double power, String type) {
    	// do nothing
    	//allLights.add(this);
    	this.name = name;
    	this.color = colour;
    	this.from = from;
    	this.power = power;
    	this.type = type; // does nothing
    	lights.add(this);
    }
    
    public String getName() {
    	return name;
    }
    
    public Point3d getFrom() {
    	return from;
    }
    
    public Color4f getColour() {
    	return color;
    }
    
    public double getPower() {
    	return power;
    }
    
    /**
     * Never used.
     * @return
     */
    public String getType() {
    	return type;
    }
    
    /*static public List<Light> getAllLights() {
    	return allLights;
    }*/
    
    public String toString() {
    	return name+" at "+from+" colour "+color+" power "+power;
    }
    
    public static List<Light> getLights() {
    	return lights;
    }
}
