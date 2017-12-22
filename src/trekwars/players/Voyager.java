package trekwars.players;

import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioNode;
import com.jme3.audio.AudioSource;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Quad;
import com.jme3.texture.Texture;
import java.util.ArrayList;

public class Voyager extends AbstractPlayer {
    
    private final Spatial _voyager;
    private final Spatial _phaser;
    private final AudioNode _audioNode;
    private boolean _isFiring = false;
    
    public Voyager(
            AssetManager assetManager, 
            PlayerType playerType, 
            IPlayerController playerController,
            ArrayList<Texture> explosionTextures,
            Camera camera,
            AudioNode explosionNode){
        super(
                playerType, 
                playerController, 
                new ColorRGBA(0, 0, 1, 0.5f), 
                new Vector3f(3f, 2f, 4f),
                new Vector3f(0, 0f, 0),
                assetManager,
                explosionTextures,
                camera,
                explosionNode
                );
        
        // Voyager
        _voyager = assetManager.loadModel("Models/voyager.j3o");
        Material voyager_material = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        voyager_material.setTexture("ColorMap", assetManager.loadTexture("Textures/voyager.png"));
        _voyager.setMaterial(voyager_material);
        _voyager.setLocalScale(0.5f);
        
        
        // Phaser
        Quad quad = new Quad(0.5f,50);
        Material material = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        material.setColor("Color", ColorRGBA.White);
        _phaser = new Geometry("star", quad);
        _phaser.rotate(-(float)Math.PI / 2, 0f, 0f);
        _phaser.setLocalTranslation(-0.25f, -1f, -1f);
        _phaser.setMaterial(material);
        
        _spatialNode.attachChild(_voyager);
        _spatialNode.attachChild(_phaser);
        
        _audioNode = new AudioNode(assetManager, "Sounds/federation-phaser.wav", false);
        _audioNode.setPositional(true);
        _audioNode.setLooping(false);
        _audioNode.setVolume(0.1f);
        _spatialNode.attachChild(_audioNode);
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
        return;
    }

    @Override
    protected void onFireStart(float tpf) {
        _spatialNode.attachChild(_phaser);
        _isFiring = true;
    }
    
    @Override
    protected void onFireStop(float tpf) {
        _spatialNode.detachChild(_phaser);
        _isFiring = false;
    }
    
    @Override
    protected void onUpdate(float tpf) {
        if(_isFiring) {
            if(_audioNode.getStatus() != AudioSource.Status.Playing) {
                _audioNode.play();
            }
        } else {
            _audioNode.stop();
        }
    }
    
    @Override
    public Iterable<Spatial> getWeaponSpatials() {
       ArrayList weapons = new ArrayList<Spatial>();
       weapons.add(_phaser);
       return weapons;
    }
}
