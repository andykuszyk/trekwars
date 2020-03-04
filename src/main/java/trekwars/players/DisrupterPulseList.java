package trekwars.players;

import com.jme3.asset.AssetManager;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import java.util.ArrayList;

public class DisrupterPulseList {
    private final ArrayList<DisrupterPulse> pulses = new ArrayList<DisrupterPulse>();
    private final Node rootNode;
    private final AssetManager assetManager;
    private final float speedPerSecond;
    private final float range;
    private final float width;
    private final float depth;
    private final Vector3f gunOffset;
    
    public DisrupterPulseList(
            AssetManager assetManager, 
            Node rootNode,
            float speedPerSecond, 
            float range, 
            float width, 
            float depth,
            Vector3f gunOffset) {
        this.rootNode = rootNode;
        this.assetManager = assetManager;
        this.speedPerSecond = speedPerSecond;
        this.range = range;
        this.width = width;
        this.depth = depth;
        this.gunOffset = gunOffset;
    }
    
    public void addPulse(Vector3f start, Quaternion direction, Vector3f offset){
        DisrupterPulse pulse = new DisrupterPulse(
                assetManager, 
                speedPerSecond,
                range,
                width,
                depth,
                start,
                direction,
                offset
        );
        pulses.add(pulse);
        rootNode.attachChild(pulse.getSpatial());
    }
    
    public void update(float tpf) {
        ArrayList<DisrupterPulse> deadPulses = new ArrayList<DisrupterPulse>();
        
        for(DisrupterPulse pulse : pulses) {
            pulse.update(tpf);
            if(!pulse.getIsAlive()){
                deadPulses.add(pulse);
            }
        }
        
        pulses.removeAll(deadPulses);
        for(DisrupterPulse pulse : deadPulses) {
            rootNode.detachChild(pulse.getSpatial());
        }
    }
    
    public Iterable<DisrupterPulse> getPulses() {
        return pulses;
    }
}
