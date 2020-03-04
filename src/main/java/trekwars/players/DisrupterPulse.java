package trekwars.players;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Quad;

public class DisrupterPulse {
    private final float speedPerSecond;
    private final float range;
    private final Spatial spatial;
    private float distance = 0;
    private final Quaternion direction;
    private final Vector3f start;
    private final Vector3f offset;
    
    public DisrupterPulse(
            AssetManager assetManager, 
            float speedPerSecond, 
            float range, 
            float width, 
            float depth,
            Vector3f start,
            Quaternion direction,
            Vector3f offset) {
        this.speedPerSecond = speedPerSecond;
        this.range = range;
        this.direction = new Quaternion(direction);
        this.start = new Vector3f(start);
        this.offset = new Vector3f(offset);
        
        Quad quad = new Quad(width, depth);
        Material material = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        material.setColor("Color", ColorRGBA.Red);
        spatial = new Geometry("disrupter", quad);
        placeAtNode();
        spatial.setMaterial(material);
    }
    
    private void placeAtNode() {
        spatial.setLocalTranslation(start);
        spatial.setLocalRotation(direction);
        spatial.rotate((float)-Math.PI/2, 0f, 0f);
        spatial.move(direction.mult(offset));
    }
    
    public boolean getIsAlive() {
        return distance <= range;
    }
    
    public void update(float tpf) {
        distance += tpf * speedPerSecond;
        placeAtNode();
        spatial.move(direction.mult(new Vector3f(0, 0, -distance)));
    }
    
    public Spatial getSpatial() {
        return spatial;
    }
}
