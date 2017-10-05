package trekwars.players;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

public class Brel extends AbstractPlayer {
    
    private final Spatial _brel;
    private final Node _spatialNode;
    
    public Brel(AssetManager assetManager, PlayerType playerType){
        super(playerType);
       
        _brel = assetManager.loadModel("Models/brel.j3o");
        Material brel_material = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        _brel.setMaterial(brel_material);
        _brel.setLocalScale(0.3f);
        
        _spatialNode = new Node();
        _spatialNode.attachChild(_brel);
        
        attachChild(_spatialNode);
    }

    @Override
    protected float getRotationalSpeed() {
        return (float)(Math.PI / 4);
    }

    @Override
    protected float getTranslationalSpeed() {
        return (float) 1.0;
    }
    
    @Override
    protected float getBoostMultiplier() {
        return (float)3.0f;
    }

    @Override
    protected float getBoostCapacity() {
        return 1;
    }

    @Override
    protected void autopilot(float tpf) {
    }

    @Override
    protected Node getSpatialNode() {
        return _spatialNode;
    }

    @Override
    protected void onFireStart(float tpf) {
        
    }

    @Override
    protected void onFireStop(float tpf) {
        
    }
}

