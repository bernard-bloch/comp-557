package comp557.a4;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.vecmath.Matrix4d;
import comp557.a4.IntersectResult;
import comp557.a4.Intersectable;
import comp557.a4.Ray;

/**
 * The scene is constructed from a hierarchy of nodes, where each node
 * contains a transform, a material definition, some amount of geometry, 
 * and some number of children nodes.  Each node has a unique name so that
 * it can be instanced elsewhere in the hierarchy (provided it does not 
 * make loops. 
 * 
 * Note that if the material (inherited from Intersectable) for a scene 
 * node is non-null, it should override the material of any child.
 * No /\ That is a terrible idea. material is always non-null.
 */
public class SceneNode extends Intersectable {
	
	/** Static map for accessing scene nodes by name, to perform instancing */
	static public Map<String,SceneNode> nodeMap = new HashMap<String,SceneNode>();
	
    public String name;
   
    /** Matrix transform for this node */
    public Matrix4d M;
    
    /** Inverse matrix transform for this node */
    public Matrix4d Minv;
    
    /** Child nodes */
    public List<Intersectable> children;
    
    /**
     * Default constructor.
     * Note that all nodes must have a unique name, so that they can used as an instance later on.
     */
    public SceneNode() {
    	super(null);
    	this.name = "";
    	this.M = new Matrix4d();
    	this.Minv = new Matrix4d();
    	this.children = new LinkedList<Intersectable>();
    }
    
    /**
     * This is much more understandable.
     */
    @Override
    public IntersectResult intersect( Ray ray ) {
    	Ray tmpRay = new Ray(ray, Minv);
    	IntersectResult ir = null;
        for ( Intersectable s : children ) {
            IntersectResult irTmp = s.intersect( tmpRay );
            if(irTmp != null && (ir == null || ir.getT() > irTmp.getT())) ir = irTmp;
        }
        if(ir == null) return null;
        ir.transform(M);
        if(material != null) ir.setMaterial(material);
        return ir;
    }
    
    public String toString() {
    	return "SceneNode"+name;
    }
}

