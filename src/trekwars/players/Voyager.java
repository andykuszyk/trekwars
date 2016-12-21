package trekwars.players;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.scene.Spatial;

public class Voyager extends AbstractPlayer {
    
    public Voyager(AssetManager assetManager){
        Spatial voyager = assetManager.loadModel("Models/Voyager.obj");
        Material mat_default = new Material(assetManager, "Common/MatDefs/Misc/ShowNormals.j3md");
        voyager.setMaterial(mat_default);
        voyager.setLocalTranslation(0, 0, -10);
        voyager.setLocalScale(1);
        
        attachChild(voyager);
    }

    public void update(float tpf) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
