package trekwars.players;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.Quaternion;
import com.jme3.scene.Spatial;

public class Voyager extends AbstractPlayer {
    
    private final Spatial _voyager;
    
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
                if(localRotation.getZ() > -0.5) {
                    _voyager.rotate(0,0,-getRotationalSpeed() * tpf);
                    //decreaseZRotation(localRotation, tpf);
                } 
//                else {
//                    setZRotation(localRotation, (float)(0.9 * Math.PI));
//                }
                break;
            case Left:
                if(localRotation.getZ() < 0.5){
                    _voyager.rotate(0,0,getRotationalSpeed() * tpf);
                    //increaseZRotation(localRotation, tpf);
                } 
//                else {
//                    setZRotation(localRotation, (float)(Math.PI * 3 / 4));
//                }
                break;
            default:
//                if(localRotation.getZ() > 0 && localRotation.getZ() < Math.PI){
//                    decreaseZRotation(localRotation, tpf);
//                } else if(localRotation.getZ() > Math.PI) {
//                    increaseZRotation(localRotation, tpf);
//                }
//                setZRotation(localRotation, 0);
                break;
        }
    }
    
    private void setZRotation(Quaternion localRotation, float z) {
        System.out.println(String.format("Setting Z rotation to %1$f", z));
        _voyager.rotate(localRotation.getX(), localRotation.getY(), z);
//        _voyager.setLocalRotation(new Quaternion(
//                localRotation.getX(),
//                localRotation.getY(),
//                z,
//                localRotation.getW()));
    }
    
    private void increaseZRotation(Quaternion localRotation, float tpf){
        _voyager.rotate(
        //_voyager.setLocalRotation(new Quaternion(
                localRotation.getX(),
                localRotation.getY(),
                wrapRadians(localRotation.getZ() + getRotationalSpeed() * tpf));
          //      localRotation.getW()));
    }
    
    private void decreaseZRotation(Quaternion localRotation, float tpf){
        _voyager.rotate(
        //_voyager.setLocalRotation(new Quaternion(
                localRotation.getX(),
                localRotation.getY(),
                wrapRadians(localRotation.getZ() - getRotationalSpeed() * tpf));
          //      localRotation.getW()));
    }
    
    private float wrapRadians(float radians) {
        return radians;
//        if(radians > Math.PI * 2){
//            return (float)(radians - (Math.PI * 2));
//        } else if (radians < 0){
//            return (float)(radians + (Math.PI * 2));
//        } else {
//            return radians;
//        }
    }
}
