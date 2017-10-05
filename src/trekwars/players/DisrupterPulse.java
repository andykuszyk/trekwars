package trekwars.players;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Quad;

public class DisrupterPulse {
    private final float _speedPerSecond;
    private final float _range;
    private final Spatial _spatial;
    private float _distance = 0;
    
    public DisrupterPulse(
            AssetManager assetManager, 
            float speedPerSecond, 
            float range, 
            float width, 
            float depth) {
        _speedPerSecond = speedPerSecond;
        _range = range;
        
        Quad quad = new Quad(width, depth);
        Material material = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        material.setColor("Color", ColorRGBA.Red);
        _spatial = new Geometry("disrupter", quad);
        _spatial.rotate(-(float)Math.PI / 2, 0f, 0f);
        _spatial.setLocalTranslation(-0.25f, -1f, -1f);
        _spatial.setMaterial(material);
    }
    
    public boolean getIsAlive() {
        return _distance > _range;
    }
    
    public void update(float tpf) {
        _distance += tpf * _speedPerSecond;
        
    }
    
    public Spatial getSpatial() {
        return _spatial;
    }
}
