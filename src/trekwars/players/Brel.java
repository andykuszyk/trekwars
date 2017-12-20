package trekwars.players;
import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioNode;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Sphere;
import com.jme3.texture.Texture;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

public class Brel extends AbstractPlayer {
    
    private final Spatial _brel;
    private final Node _spatialNode;
    private final DisrupterPulseList _pulses;
    private Date _lastPulseTime = new Date();
    private final float _secondsBetweenPulses = 1;
    private final Node _disrupterNode;
    private final AudioNode _audioNode;
    private final float _disrupterRange = 30f;
    
    public Brel(AssetManager assetManager, PlayerType playerType, AbstractPlayer player){
        super(playerType, player);
       
        _brel = assetManager.loadModel("Models/brel.j3o");
        Material brel_material = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        _brel.setMaterial(brel_material);
        _brel.setLocalScale(0.3f);
        
        Sphere sphere = new Sphere(10, 10, 1);
        Material shieldsMaterial = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        shieldsMaterial.setColor("Color", new ColorRGBA(0, 1, 0, 0.5f));
        shieldsMaterial.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        Geometry shields = new Geometry("shields", sphere);
        shields.setMaterial(shieldsMaterial);
        shields.setQueueBucket(RenderQueue.Bucket.Transparent);
        shields.scale(4f, 2f, 3f);
        shields.setLocalTranslation(0, -1f, 0);
        
        _spatialNode = new Node();
        _spatialNode.attachChild(_brel);
        _spatialNode.attachChild(shields);
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
        
        attachChild(_spatialNode);
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
    protected Node getSpatialNode() {
        return _spatialNode;
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

