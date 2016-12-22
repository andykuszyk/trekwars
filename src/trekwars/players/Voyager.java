package trekwars.players;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.Quaternion;
import com.jme3.scene.Spatial;

public class Voyager extends AbstractPlayer {
    
    private final Spatial _voyager;
    private final float _rollRightLimit = -0.6f;
    private final float _rollLeftLimit = 0.6f;
    private final float _rollMultiplier = 1f;
    
    public Voyager(AssetManager assetManager){
        _voyager = assetManager.loadModel("Models/Voyager.obj");
        Material mat_default = new Material(assetManager, "Common/MatDefs/Misc/ShowNormals.j3md");
        _voyager.setMaterial(mat_default);
        _voyager.setLocalScale(1);
        
        attachChild(_voyager);
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

    @Override
    protected void onTurn(TurnDirection turnDirection, float tpf) {
        Quaternion localRotation = _voyager.getLocalRotation();
        System.out.println(String.format(
                "Local rotation is XYZ (%1$f, %2$f, %3$f)", 
                localRotation.getX(), 
                localRotation.getY(), 
                localRotation.getZ()));
        
        switch(turnDirection){
            case Right:
                if(localRotation.getZ() > _rollRightLimit) {
                    _voyager.rotate(0,0,-getRotationalSpeed() * tpf * _rollMultiplier);
                } 
                break;
            case Left:
                if(localRotation.getZ() < _rollLeftLimit){
                    _voyager.rotate(0,0,getRotationalSpeed() * tpf * _rollMultiplier);
                } 
                break;
            default:
                if(localRotation.getZ() > 0){
                    _voyager.rotate(0,0,-getRotationalSpeed() * tpf * _rollMultiplier);
                } else if (localRotation.getZ() < 0) {
                    _voyager.rotate(0,0,getRotationalSpeed() * tpf * _rollMultiplier);
                }
                break;
        }
    }
}
