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

public class Prometheus extends AbstractPlayer {

    private final Spatial prometheus;
    private final Spatial phaser;
    private final AudioNode audioNode;
    private boolean isFiring = false;

    public Prometheus(
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
                new Vector3f(3f, 2f, 5f),
                new Vector3f(0, 0f, 0),
                assetManager,
                explosionTextures,
                camera,
                explosionNode,
                PlayerFactoryType.Prometheus
                );
        prometheus = assetManager.loadModel("Models/prometheus.obj");
        Material prometheusMaterial = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        prometheusMaterial.setTexture("ColorMap", assetManager.loadTexture("Models/prometheus.jpg"));
        prometheus.setMaterial(prometheusMaterial);
        prometheus.setLocalScale(0.5f);

        Quad quad = new Quad(0.5f,50);
        Material material = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        material.setColor("Color", ColorRGBA.White);
        phaser = new Geometry("star", quad);
        phaser.rotate(-(float)Math.PI / 2, 0f, 0f);
        phaser.setLocalTranslation(-0.25f, -1f, -1f);
        phaser.setMaterial(material);
        
        spatialNode.attachChild(prometheus);
        
        audioNode = new AudioNode(assetManager, "Sounds/federation-phaser.wav", false);
        audioNode.setPositional(true);
        audioNode.setLooping(false);
        audioNode.setVolume(0.1f);
        spatialNode.attachChild(audioNode);
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
        spatialNode.attachChild(phaser);
        isFiring = true;
    }
    
    @Override
    protected void onFireStop(float tpf) {
        spatialNode.detachChild(phaser);
        isFiring = false;
    }
    
    @Override
    protected void onUpdate(float tpf) {
        if(isFiring) {
            if(audioNode.getStatus() != AudioSource.Status.Playing) {
                audioNode.play();
            }
        } else {
            audioNode.stop();
        }
    }
    
    @Override
    public Iterable<Spatial> getWeaponSpatials() {
       ArrayList weapons = new ArrayList<Spatial>();
       weapons.add(phaser);
       return weapons;
    }
}
