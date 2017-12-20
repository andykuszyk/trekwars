package trekwars.players;

import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioNode;
import com.jme3.audio.AudioSource;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Quad;

public class Voyager extends AbstractPlayer {
    
    private final Spatial _voyager;
    private final Node _spatialNode;
    private final Spatial _phaser;
    private final AudioNode _audioNode;
    private boolean _isFiring = false;
    
    public Voyager(AssetManager assetManager, PlayerType playerType){
        super(playerType);
        
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
        
        _spatialNode = new Node();
        _spatialNode.attachChild(_voyager);
        _spatialNode.attachChild(_phaser);
        
        _audioNode = new AudioNode(assetManager, "Sounds/federation-phaser.wav", false);
        _audioNode.setPositional(true);
        _audioNode.setLooping(false);
        _audioNode.setVolume(0.1f);
        _spatialNode.attachChild(_audioNode);
        
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
        return;
    }

    @Override
    protected Node getSpatialNode() {
        return _spatialNode;
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
}
