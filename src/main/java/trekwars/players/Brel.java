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
    
    private final Spatial brel;
    private final DisrupterPulseList pulses;
    private Date lastPulseTime = new Date();
    private final float secondsBetweenPulses = 1;
    private final Node disrupterNode;
    private final AudioNode audioNode;
    private final float disrupterRange = 30f;
    
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
       
        brel = assetManager.loadModel("Models/brel.j3o");
        Material brel_material = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        brel.setMaterial(brel_material);
        brel.setLocalScale(0.3f);
        
        spatialNode.attachChild(brel);
        disrupterNode = new Node();
        pulses = new DisrupterPulseList(
                assetManager,
                disrupterNode,
                50,
                disrupterRange,
                0.5f,
                2,
                new Vector3f()
        );
        
        audioNode = new AudioNode(assetManager, "Sounds/disrupter.wav", false);
        audioNode.setPositional(true);
        audioNode.setLooping(false);
        audioNode.setVolume(0.1f);
        spatialNode.attachChild(audioNode);
    }
    
    @Override
    public Iterable<Spatial> getWeaponSpatials() {
        ArrayList<Spatial> weapons = new ArrayList<Spatial>();
        for(DisrupterPulse pulse : pulses.getPulses()) {
            weapons.add(pulse.getSpatial());
        }
        return weapons;
    }
    
    @Override
    protected Collection<Node> getOtherNodes() {
        ArrayList<Node> nodes = new ArrayList<Node>();
        nodes.add(disrupterNode);
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
        if(life <= 0) return;
        float distance = getDistanceToPlayer();
        if(distance <= disrupterRange / 4) {
            turnLeft();
            boost();
            move(tpf);
            fire();
        } else if(distance <= disrupterRange / 2) {
            turnLeft();
            move(tpf);
        } else if(distance <= disrupterRange) {
            fire();
        }
    }

    @Override
    protected void onFireStart(float tpf) {
         if(((new Date().getTime() - lastPulseTime.getTime()) / 1000f) > secondsBetweenPulses){
            lastPulseTime = new Date();
            pulses.addPulse(
                spatialNode.getWorldTranslation(),
                spatialNode.getWorldRotation(),
                new Vector3f(-2f, -0.5f, 0f)
            );
            pulses.addPulse(
                spatialNode.getWorldTranslation(),
                spatialNode.getWorldRotation(),
                new Vector3f(2f, -0.5f, 0f)
            );
            audioNode.play();
        }
    }

    @Override
    protected void onFireStop(float tpf) {
        
    }
    
    @Override
    protected void onUpdate(float tpf) {
        pulses.update(tpf);
    }
}

