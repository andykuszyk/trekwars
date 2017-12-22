package trekwars.players;
import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioNode;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.texture.Texture;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

public class Brel extends AbstractPlayer {
    
    private final Spatial _brel;
    private final DisrupterPulseList _pulses;
    private Date _lastPulseTime = new Date();
    private final float _secondsBetweenPulses = 1;
    private final Node _disrupterNode;
    private final AudioNode _audioNode;
    private final float _disrupterRange = 30f;
    
    public Brel(
            AssetManager assetManager, 
            PlayerType playerType, 
            IPlayerController playerController,
            ArrayList<Texture> explosionTextures,
            Camera camera,
            AudioNode explosionNode){
        super(
                playerType, 
                playerController, 
                new ColorRGBA(0, 1, 0, 0.5f), 
                new Vector3f(4f, 2f, 3f),
                new Vector3f(0, -1f, 0),
                assetManager,
                explosionTextures,
                camera,
                explosionNode
                );
       
        _brel = assetManager.loadModel("Models/brel.j3o");
        Material brel_material = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        _brel.setMaterial(brel_material);
        _brel.setLocalScale(0.3f);
        
        _spatialNode.attachChild(_brel);
        _disrupterNode = new Node();
        _pulses = new DisrupterPulseList(
                assetManager,
                _disrupterNode,
                50,
                _disrupterRange,
                0.5f,
                2,
                new Vector3f()
        );
        
        _audioNode = new AudioNode(assetManager, "Sounds/disrupter.wav", false);
        _audioNode.setPositional(true);
        _audioNode.setLooping(false);
        _audioNode.setVolume(0.1f);
        _spatialNode.attachChild(_audioNode);
    }
    
    @Override
    public Iterable<Spatial> getWeaponSpatials() {
        ArrayList<Spatial> weapons = new ArrayList<Spatial>();
        for(DisrupterPulse pulse : _pulses.getPulses()) {
            weapons.add(pulse.getSpatial());
        }
        return weapons;
    }
    
    @Override
    protected Collection<Node> getOtherNodes() {
        ArrayList<Node> nodes = new ArrayList<Node>();
        nodes.add(_disrupterNode);
        return nodes;
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
        if(_life <= 0) return;
        float distance = getDistanceToPlayer();
        if(distance <= _disrupterRange / 4) {
            turnLeft();
            boost();
            move(tpf);
            fire();
        } else if(distance <= _disrupterRange / 2) {
            turnLeft();
            move(tpf);
        } else if(distance <= _disrupterRange) {
            fire();
        }
    }

    @Override
    protected void onFireStart(float tpf) {
         if(((new Date().getTime() - _lastPulseTime.getTime()) / 1000f) > _secondsBetweenPulses){
            _lastPulseTime = new Date();
            _pulses.addPulse(
                _spatialNode.getWorldTranslation(),
                _spatialNode.getWorldRotation(),
                new Vector3f(-2f, -0.5f, 0f)
            );
            _pulses.addPulse(
                _spatialNode.getWorldTranslation(),
                _spatialNode.getWorldRotation(),
                new Vector3f(2f, -0.5f, 0f)
            );
            _audioNode.play();
        }
    }

    @Override
    protected void onFireStop(float tpf) {
        
    }
    
    @Override
    protected void onUpdate(float tpf) {
        _pulses.update(tpf);
    }
}

