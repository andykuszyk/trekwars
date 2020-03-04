package trekwars.core;

import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;

public class Distance {
    private final double local;
    
    public Distance(Spatial a, Spatial b) {
        Vector3f aLocal = a.getLocalTranslation();
        Vector3f bLocal = b.getLocalTranslation();
        
        local = Math.sqrt(
                Math.pow(aLocal.getX() - bLocal.getX(), 2) + 
                Math.pow(aLocal.getY() - bLocal.getY(), 2) + 
                Math.pow(aLocal.getZ() - bLocal.getZ(), 2));
    }
    
    public double getLocal() {
        return local;
    }
}
