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
    private final float _speedPerSecond;
    private final float _range;
    private final Spatial _spatial;
    private float _distance = 0;
    private final Quaternion _direction;
    private final Vector3f _start;
    private final Vector3f _offset;
    
    public DisrupterPulse(
            AssetManager assetManager, 
            float speedPerSecond, 
            float range, 
            float width, 
            float depth,
            Vector3f start,
            Quaternion direction,
            Vector3f offset) {
        _speedPerSecond = speedPerSecond;
        _range = range;
        _direction = new Quaternion(direction);
        _start = new Vector3f(start);
        _offset = new Vector3f(offset);
        
        Quad quad = new Quad(width, depth);
        Material material = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        material.setColor("Color", ColorRGBA.Red);
        _spatial = new Geometry("disrupter", quad);
        placeAtNode();
        _spatial.setMaterial(material);
    }
    
    private void placeAtNode() {
        _spatial.setLocalTranslation(_start);
        _spatial.setLocalRotation(_direction);
        _spatial.rotate((float)-Math.PI/2, 0f, 0f);
        _spatial.move(_direction.mult(_offset));
    }
    
    public boolean getIsAlive() {
        return _distance <= _range;
    }
    
    public void update(float tpf) {
        _distance += tpf * _speedPerSecond;
        placeAtNode();
        _spatial.move(_direction.mult(new Vector3f(0, 0, -_distance)));
    }
    
    public Spatial getSpatial() {
        return _spatial;
    }
}
