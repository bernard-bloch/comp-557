package comp557.a4;

import java.awt.Dimension;
import java.util.Scanner;

import javax.vecmath.Color3f;
import javax.vecmath.Color4f;
import javax.vecmath.Matrix4d;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * A factory class to generate raytracer objects from XML definition. 
 */
public class Parser extends Scene {

	/**
	 * Create a scene.
	 */
	public Parser(Node dataNode) {
		Node ambientAttr = dataNode.getAttributes().getNamedItem("ambient");
        if( ambientAttr != null ) {
        	Scanner s = new Scanner( ambientAttr.getNodeValue());
            float x = s.nextFloat();
            float y = s.nextFloat();
            float z = s.nextFloat();
            this.ambient.set(x, y, z);   
            s.close();
        }
        NodeList nodeList = dataNode.getChildNodes();
        for ( int i = 0; i < nodeList.getLength(); i++ ) {
            Node n = nodeList.item(i);
            // skip all text, just process the ELEMENT_NODEs
            if ( n.getNodeType() != Node.ELEMENT_NODE ) continue;
            String nodeName = n.getNodeName();
            if ( nodeName.equalsIgnoreCase( "material" ) ) {                
                Material material = Parser.createMaterial(n);
                Material.getMaterialMap().put( material.getName(), material );
            } else if ( nodeName.equalsIgnoreCase( "light" ) ) {                
                Light light = Parser.createLight(n);
                this.lights.put( light.name, light);
            } else if ( nodeName.equalsIgnoreCase( "render" ) ) {                
                this.render = Parser.createRender(n);
            } else if ( nodeName.equalsIgnoreCase( "node" ) ) {
            	this.surfaceList.add( Parser.createSceneNode(n) );
            } else if ( nodeName.equalsIgnoreCase( "plane" ) ) {
        		Plane plane = Parser.createPlane(n);
        		this.surfaceList.add( plane );
            } else if ( nodeName.equalsIgnoreCase( "box" ) ) {
        		Box box = Parser.createBox(n);
        		this.surfaceList.add( box );
            } else if ( nodeName.equalsIgnoreCase( "sphere" ) ) {
        		Sphere sphere = Parser.createSphere(n);
        		this.surfaceList.add( sphere );
            } else if ( nodeName.equalsIgnoreCase( "mesh" ) ) {
            	Mesh mesh = Parser.createMesh(n);
            	if(mesh != null) this.surfaceList.add( mesh );
            }
        }
	}
	
	/**
	 * Create a scenegraph node.
	 */
	public static SceneNode createSceneNode(Node dataNode) {
		SceneNode sceneNode = new SceneNode();
        sceneNode.name = dataNode.getAttributes().getNamedItem("name").getNodeValue();		
		Node refAttr = dataNode.getAttributes().getNamedItem("ref");
		if ( refAttr != null ) {
			// add references to all child nodes and geometries
			//
			SceneNode other = SceneNode.nodeMap.get( refAttr.getNodeValue() );
			if ( other != null ) {
				for (Intersectable s : other.children) {
					sceneNode.children.add(s);
				}
			}
		} else {
	        // create geometries for this node.
			//
	        NodeList nodeList = dataNode.getChildNodes();
	        for ( int i = 0; i < nodeList.getLength(); i++ ) {
	            Node n = nodeList.item(i);
	            // skip all text, just process the ELEMENT_NODEs
	            if ( n.getNodeType() != Node.ELEMENT_NODE ) continue;
	            String nodeName = n.getNodeName();
	        	if ( nodeName.compareToIgnoreCase( "node") == 0 ) {
	                SceneNode childNode = Parser.createSceneNode(n) ;
	                sceneNode.children.add( childNode );
	            } else if ( nodeName.equalsIgnoreCase( "plane" ) ) {
	        		Plane plane = Parser.createPlane(n);
	        		sceneNode.children.add( plane );
	            } else if ( nodeName.equalsIgnoreCase( "box" ) ) {
	        		Box box = Parser.createBox(n);
	        		sceneNode.children.add( box );
	            } else if ( nodeName.equalsIgnoreCase( "sphere" ) ) {
	        		Sphere sphere = Parser.createSphere(n);
	        		sceneNode.children.add( sphere );
	            } else if ( nodeName.equalsIgnoreCase( "mesh" ) ) {
	            	Mesh mesh = Parser.createMesh(n);
	            	sceneNode.children.add( mesh );
	            }
	        }	        
            if ( !SceneNode.nodeMap.containsKey(sceneNode.name) ) {
            	SceneNode.nodeMap.put( sceneNode.name, sceneNode );
            } else {
            	System.err.println("Parser.createSceneNode(): node with name " + sceneNode.name + " already exists!");
            }	        
		}
		
		// Build the scene node transform.
		//
		sceneNode.M.setIdentity();        
		Node translationAttr = dataNode.getAttributes().getNamedItem("translation");
		if ( translationAttr != null ) {
        	Scanner s = new Scanner( translationAttr.getNodeValue() );
        	double x = s.nextDouble();
        	double y = s.nextDouble();
        	double z = s.nextDouble();
            s.close(); 
        	Vector3d t = new Vector3d(x,y,z);
        	Matrix4d T = new Matrix4d();
        	T.set(t);
        	sceneNode.M.mul(T);
		}		
		Node rotationAttr = dataNode.getAttributes().getNamedItem("rotation");
		if ( rotationAttr != null ) {
        	Scanner s = new Scanner( rotationAttr.getNodeValue() );
        	double degX = s.nextDouble();
        	double degY = s.nextDouble();
        	double degZ = s.nextDouble();
            s.close(); 
        	Matrix4d R = new Matrix4d();
        	R.rotX( Math.toRadians(degX) );
        	sceneNode.M.mul(R);
        	R.rotY( Math.toRadians(degY) );
        	sceneNode.M.mul(R);
        	R.rotZ( Math.toRadians(degZ) );
        	sceneNode.M.mul(R);
		}
		Node scaleAttr = dataNode.getAttributes().getNamedItem("scale");
		if ( scaleAttr != null ) {
            Scanner s = new Scanner( scaleAttr.getNodeValue() );   
            Matrix4d S = new Matrix4d();
            S.setIdentity();
            S.setElement(0,0,s.nextDouble());
            S.setElement(1,1,s.nextDouble());
            S.setElement(2,2,s.nextDouble());
            sceneNode.M.mul( S );
            s.close(); 
		}				
		// cache the inverse matrix, since we only need to compute it once!
		sceneNode.Minv.invert(sceneNode.M);		
		sceneNode.material = parseMaterial(dataNode, "material");					
		return sceneNode;	
	}
	
	/**
	 * Create a light.
	 */
	public static Light createLight(Node dataNode) {
		Light light = new Light();
        light.name = dataNode.getAttributes().getNamedItem("name").getNodeValue();        
        Node colorAttr = dataNode.getAttributes().getNamedItem("color");
        if ( colorAttr != null ) {
        	Scanner s = new Scanner( colorAttr.getNodeValue());
        	float r = s.nextFloat();
            float g = s.nextFloat();
            float b = s.nextFloat();
            float a = (s.hasNextFloat() ? s.nextFloat() : 1); // fixed 0 -> 1
            light.color.set(r,g,b,a);   
            s.close();    	
        }
        Node fromAttr = dataNode.getAttributes().getNamedItem("from");
        if ( fromAttr != null ) {
        	Scanner s = new Scanner( fromAttr.getNodeValue());
            double x = s.nextDouble();
            double y = s.nextDouble();
            double z = s.nextDouble();
            light.from.set(x,y,z); 
            s.close();
        }
        Node powerAttr = dataNode.getAttributes().getNamedItem("power");
        if ( powerAttr != null ) {
        	light.power = Double.parseDouble( powerAttr.getNodeValue() );
        }
        Node typeAttr = dataNode.getAttributes().getNamedItem("type");
        if ( typeAttr != null ) {
        	light.type = typeAttr.getNodeValue();
        }        
		return light;
	}
	
	/**
	 * Create a camera.
	 * Bernard: fixed. It was creating 3 cameras.
	 */
	public static Camera createCamera(Node dataNode) {
		/** Camera name */
	    //String name = "camera";

	    /** The eye position */
	    Point3d from = new Point3d(0,0,10);
	    
	    /** The "look at" position */
	    Point3d to = new Point3d(0,0,0);
	    
	    /** Up direction, default is y up */
	    Vector3d up = new Vector3d(0,1,0);
	    
	    /** Vertical field of view (in degrees), default is 45 degrees */
	    double fovy = 45.0;
	    
	    /** The rendered image size */
	    Dimension imageSize = new Dimension(640,480);

		//Node nameAttr = dataNode.getAttributes().getNamedItem("name");
		//if( nameAttr != null ) name = nameAttr.getNodeValue();
		Node fromAttr = dataNode.getAttributes().getNamedItem("from");
        if ( fromAttr != null ) {
        	Scanner s = new Scanner( fromAttr.getNodeValue());
            double x = s.nextDouble();
            double y = s.nextDouble();
            double z = s.nextDouble();
            from.set(x,y,z);     
            s.close();
        }
        Node toAttr = dataNode.getAttributes().getNamedItem("to");
        if ( toAttr != null ) {
        	Scanner s = new Scanner( toAttr.getNodeValue());
            double x = s.nextDouble();
            double y = s.nextDouble();
            double z = s.nextDouble();
            to.set(x,y,z);     
            s.close();
        }
        Node upAttr = dataNode.getAttributes().getNamedItem("up");
        if ( upAttr != null ) {
        	Scanner s = new Scanner( upAttr.getNodeValue());
            double x = s.nextDouble();
            double y = s.nextDouble();
            double z = s.nextDouble();
            up.set(x,y,z);
            s.close();
        }
        Node fovAttr = dataNode.getAttributes().getNamedItem("fovy");
        if ( fovAttr != null ) {
            fovy = Double.parseDouble( fovAttr.getNodeValue() );       	
        }
        Node widthAttr = dataNode.getAttributes().getNamedItem("width");
        if ( widthAttr != null ) {
            imageSize.width = Integer.parseInt( widthAttr.getNodeValue() );        	
        }
        Node heightAttr = dataNode.getAttributes().getNamedItem("height");
        if ( heightAttr != null ) {
            imageSize.height = Integer.parseInt( heightAttr.getNodeValue() );        	
        }
        
		return new Camera(from, to, up, fovy, imageSize);
	}
	
	/**
	 * Create a material.
	 */
	public static Material createMaterial(Node dataNode) {
		/** Material name */
	    String name = "material";
	    
	    /** Diffuse colour, defaults to white */
	    Color4f diffuse = new Color4f(1,1,1,1);
	    
	    /** Specular colour, default to black (no specular highlight) */
	    Color4f specular = new Color4f(0,0,0,0);
	    
	    /** Specular hardness, or exponent, default to a reasonable value */ 
	    float shinyness = 64;

		Node refAttr = dataNode.getAttributes().getNamedItem("ref");
		if( refAttr  != null ) {
			return Material.getMaterialMap().get( refAttr.getNodeValue() );
		} else {
	    	Node nameAttr = dataNode.getAttributes().getNamedItem("name");
	    	if ( nameAttr != null ) {
	    		name = nameAttr.getNodeValue();
	    	}
	    	Node diffuseAttr = dataNode.getAttributes().getNamedItem("diffuse");
	    	if ( diffuseAttr != null ) {
	        	Scanner s = new Scanner( diffuseAttr.getNodeValue() );
	            float r = s.nextFloat();
	            float g = s.nextFloat();
	            float b = s.nextFloat();
	            float a = (s.hasNextFloat() ? s.nextFloat() : 1);
	            diffuse.set(r,g,b,a);
	            s.close();
	    	}
	    	Node specularAttr = dataNode.getAttributes().getNamedItem("specular");
	    	if ( specularAttr != null ) {
	        	Scanner s = new Scanner( specularAttr.getNodeValue());
	            float r = s.nextFloat();
	            float g = s.nextFloat();
	            float b = s.nextFloat();
	            float a = (s.hasNextFloat() ? s.nextFloat() : 1);
	            specular.set(r,g,b,a);   
	            s.close();
	    	}
	    	Node hardnessAttr = dataNode.getAttributes().getNamedItem("hardness");
	    	if ( hardnessAttr != null ) {
	    		shinyness = Float.parseFloat( hardnessAttr.getNodeValue() );
	    	}
		}
		return new Material(name, diffuse, specular, shinyness);
	}

	/**
	 * Create a renderer.
	 * I fixed it.
	 */
	public static Render createRender(Node dataNode) {
		/** The render camera */
	    Camera camera = null;
	    
	    /** Samples per pixel */
	    int samples = 1;
	    
	    /** The output filename */
	    String output = "render.png";
	    
	    /** The background color */
	    Color3f bgcolor = new Color3f();
	    
		Node outputAttr = dataNode.getAttributes().getNamedItem("output");
		if ( outputAttr != null ) {
			output = outputAttr.getNodeValue();
		}
		Node bgcolorAttr = dataNode.getAttributes().getNamedItem("bgcolor");
		if ( bgcolorAttr != null ) {
        	Scanner s = new Scanner( bgcolorAttr.getNodeValue());
            float r = s.nextFloat();
            float g = s.nextFloat();
            float b = s.nextFloat();
			bgcolor.set(r,g,b);
			s.close();
		}		
		Node samplesAttr = dataNode.getAttributes().getNamedItem("samples");
		if ( samplesAttr != null ) {
        	Scanner s = new Scanner( samplesAttr.getNodeValue());
            samples = s.nextInt(); 
			s.close();
		}
    	NodeList nodeList = dataNode.getChildNodes();
    	for (int i = 0; i < nodeList.getLength(); i++) {
    		Node n = nodeList.item(i);
            // skip all text, just process the ELEMENT_NODEs
            if ( n.getNodeType() != Node.ELEMENT_NODE ) continue;
    		String name = n.getNodeName();
			if ( name.equalsIgnoreCase("camera") ) {
				camera = Parser.createCamera(n);
    		}
    	}
		return new Render(camera, samples, output, bgcolor, true);
	}
	
	/**
	 * Create a plane. material2 can be null.
	 */
	public static Plane createPlane( Node dataNode ) {
		Material material = parseMaterial(dataNode, "material");	
		Material material2 = parseMaterial(dataNode, "material2");
		return new Plane(new Point3d(0,0,0), new Vector3d(0,1,0), material, material2);	
		// TODO: expand this easily
	}
	
	/**
	 * Create a sphere object.
	 */
	public static Sphere createSphere(Node dataNode) {
		/** Radius of the sphere. */
		double radius = 1;
	    
		/** Location of the sphere center. */
		Point3d center = new Point3d( 0, 0, 0 );
		
		Node centerAttr = dataNode.getAttributes().getNamedItem("center");
		if ( centerAttr != null ) {
            Scanner s = new Scanner( centerAttr.getNodeValue() );
            double x = s.nextDouble();
            double y = s.nextDouble();
            double z = s.nextDouble();
            center = new Point3d(x, y, z);
            s.close();
		}
		Node radiusAttr = dataNode.getAttributes().getNamedItem("radius");
		if ( radiusAttr != null ) {
			radius = Double.parseDouble( radiusAttr.getNodeValue() );
		}
		Material material = parseMaterial(dataNode, "material");	
    	return new Sphere(center, radius, material);
	}

	/**
	 * Create a box object.
	 */
	public static Box createBox(Node dataNode) {
		Point3d min = new Point3d( -1, -1, -1 );
		Point3d max = new Point3d( 1, 1, 1 );

		Node minAttr = dataNode.getAttributes().getNamedItem("min");
		if ( minAttr != null ) {
            Scanner s = new Scanner( minAttr.getNodeValue() );
            double x = s.nextDouble();
            double y = s.nextDouble();
            double z = s.nextDouble();
            min = new Point3d(x, y, z);
            s.close();
		}
		Node maxAttr = dataNode.getAttributes().getNamedItem("max");
		if ( maxAttr != null ) {
            Scanner s = new Scanner( maxAttr.getNodeValue() );
            double x = s.nextDouble();
            double y = s.nextDouble();
            double z = s.nextDouble();
            max = new Point3d(x, y, z);
            s.close();
		}		
		Material material = parseMaterial(dataNode, "material");
    	return new Box(min, max, material);
	}	
	
	/**
	 * Create a mesh object.
	 */
	public static Mesh createMesh(Node dataNode) {
		PolygonSoup soup;
    	String name = dataNode.getAttributes().getNamedItem("name").getNodeValue();
        Node filenameAttr = dataNode.getAttributes().getNamedItem("filename");
        if ( filenameAttr != null ) {
        	soup = new PolygonSoup( filenameAttr.getNodeValue() );
        } else {
			String instance = dataNode.getAttributes().getNamedItem("ref").getNodeValue();
			Mesh other = Mesh.getMeshMap().get(instance);
			assert( other != null );
			if( other == null ) {
				System.err.println(instance + " is not existing.");
				return null;
			}
			soup = other.getSoup();
        }
        Material material = parseMaterial(dataNode, "material");
    	return new Mesh(name, soup, material);    	
	}

	/**
	 * Utility method to parse a material tag.
	 */
	private static Material parseMaterial(Node dataNode, String tagName) {
		Material material = null;
    	NodeList nodeList = dataNode.getChildNodes();
    	for (int i = 0; i < nodeList.getLength(); i++) {
    		Node n = nodeList.item(i);
            // skip all text, just process the ELEMENT_NODEs
            if ( n.getNodeType() != Node.ELEMENT_NODE ) continue;
    		String name = n.getNodeName();
			if ( name.equalsIgnoreCase(tagName) ) {
    			Node refNode = n.getAttributes().getNamedItem("ref");
    			if( refNode != null ) { 
					material = Material.getMaterialMap().get( refNode.getNodeValue() );
    			} else {
    				material = Parser.createMaterial(n);
    			}
    		}
    	}
    	return material;
	}		
}
