package trekwars.players;

import com.jme3.asset.AssetManager;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import java.util.ArrayList;

public class DisrupterPulseList {
    private final ArrayList<DisrupterPulse> _pulses = new ArrayList<DisrupterPulse>();
    private final Node _rootNode;
    private final AssetManager _assetManager;
    private final float _speedPerSecond;
    private final float _range;
    private final float _width;
    private final float _depth;
    private final Vector3f _gunOffset;
    
    public DisrupterPulseList(
            AssetManager assetManager, 
            Node rootNode,
            float speedPerSecond, 
            float range, 
            float width, 
            float depth,
            Vector3f gunOffset) {
        _rootNode = rootNode;
        _assetManager = assetManager;
        _speedPerSecond = speedPerSecond;
        _range = range;
        _width = width;
        _depth = depth;
        _gunOffset = gunOffset;
    }
    
    public void addPulse(Vector3f start, Quaternion direction, Vector3f offset){
        DisrupterPulse pulse = new DisrupterPulse(
                _assetManager, 
                _speedPerSecond,
                _range,
                _width,
                _depth,
                start,
                direction,
                offset
        );
        _pulses.add(pulse);
        _rootNode.attachChild(pulse.getSpatial());
    }
    
    public void update(float tpf) {
        ArrayList<DisrupterPulse> deadPulses = new ArrayList<DisrupterPulse>();
        
        for(DisrupterPulse pulse : _pulses) {
            pulse.update(tpf);
            if(!pulse.getIsAlive()){
                deadPulses.add(pulse);
            }
        }
        
        _pulses.removeAll(deadPulses);
        for(DisrupterPulse pulse : deadPulses) {
            _rootNode.detachChild(pulse.getSpatial());
        }
    }
    
}
