package trekwars.players;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

public class Voyager extends AbstractPlayer {
    
    public Voyager(AssetManager assetManager){
        Spatial voyager = assetManager.loadModel("Models/Voyager.obj");
        Material mat_default = new Material(assetManager, "Common/MatDefs/Misc/ShowNormals.j3md");
        voyager.setMaterial(mat_default);
        voyager.setLocalScale(1);
        
        attachChild(voyager);
        getRootNode().setLocalTranslation(0, 0, -10);
    }

    protected void onUpdate(float tpf) {
        // voyager specific update logic here.
    }

    @Override
    protected float getRotationalSpeed() {
        return (float)(Math.PI / 4);
    }

    @Override
    protected float getTranslationalSpeed() {
        return (float) 0.5;
    }

    @Override
    protected float getBoostCapacity() {
        return 1;
    }
    
}
