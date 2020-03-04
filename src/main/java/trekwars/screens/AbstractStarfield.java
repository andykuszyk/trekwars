package trekwars.screens;

import com.jme3.asset.AssetManager;
import com.jme3.input.event.TouchEvent;
import com.jme3.light.AmbientLight;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Quad;
import com.jme3.texture.Texture;
import com.jme3.util.SkyFactory;
import java.util.ArrayList;
import java.util.Random;
import trekwars.core.Distance;
import trekwars.players.IPlayer;

public abstract class AbstractStarfield implements IScreen {
    protected final Node rootNode;
    private final Random random = new Random();
    private final float starRadius = 100;
    private final float minStarDistance = 75;
    private final float maxStarDistance = 150;
    private final int numberOfStars = 200;
    protected final AssetManager assetManager;
    private ArrayList<Spatial> stars = new ArrayList<Spatial>();
    private final IPlayer player;
    protected final Node guiNode;
    protected final Camera camera;
    
    public AbstractStarfield(
            AssetManager assetManager,
            IPlayer player,
            Camera camera
            ) {
        this.player = player;
        rootNode = new Node();
        guiNode = new Node();
        this.camera = camera;
        this.assetManager = assetManager;
        createStarfield(assetManager);
        createLighting();
    }
    
    private void createStarfield(AssetManager assetManager) {
        rootNode.attachChild(
            SkyFactory.createSky(
                assetManager, 
                assetManager.loadTexture("Textures/stars.png"), 
                assetManager.loadTexture("Textures/stars.png"), 
                assetManager.loadTexture("Textures/stars.png"), 
                assetManager.loadTexture("Textures/stars.png"), 
                assetManager.loadTexture("Textures/stars.png"), 
                assetManager.loadTexture("Textures/stars.png"), 
                Vector3f.UNIT_XYZ,
                200)
            );
        
        Material material = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        Texture star = assetManager.loadTexture("Textures/star.png");
        star.setWrap(Texture.WrapMode.Repeat);
        material.setTexture("ColorMap", star);
        material.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        for(int i = 0; i < numberOfStars; i++) {
            Quad quad = new Quad(2f,2f);
            Geometry geom = new Geometry("star", quad);
            geom.setMaterial(material);
            geom.setQueueBucket(RenderQueue.Bucket.Transparent);
            rootNode.attachChild(geom);
            positionStar(geom);
            stars.add(geom);
        }
    }
        
    private void createLighting() {
        AmbientLight al = new AmbientLight();
        al.setColor(ColorRGBA.White.mult(1.3f));
        rootNode.addLight(al);
    }   
    
    private void positionStar(Spatial star) {
        Vector3f newCoords = new Vector3f(generateStarCoordinate(), generateStarCoordinate(), generateStarCoordinate());
        if(player != null) {
            star.setLocalTranslation(player.getRootNode().getLocalTranslation().add(newCoords));
        } else {
            star.setLocalTranslation(newCoords);
        }
    }
    
    private float generateStarCoordinate() {
        return
                (random.nextFloat() - 0.5f) * 
                ((starRadius + minStarDistance) / minStarDistance) * 
                starRadius;
    }
    
    public void update(float tpf) {
        for(Spatial star : stars) {
            star.lookAt(camera.getLocation(), Vector3f.UNIT_Y);
            double distance;
            if(player == null) {
                distance = star.getWorldTranslation().distance(camera.getLocation());
            } else {
                distance = new Distance(star, player.getRootNode()).getLocal();
            }
            if(distance < minStarDistance || distance > maxStarDistance) {
                positionStar(star);
            }
        }
        
        onUpdate(tpf);
    }
    
    protected void onUpdate(float tpf) {
    
    }

    public Node getRootNode() {
        return rootNode;
    }

    public Node getGuiNode() {
        return guiNode;
    }

    public void onAnalog(String name, float keyPressed, float tpf) {
        
    }

    public void onAction(String name, boolean keyPressed, float tpf) {
        
    }

    public IScreen getNextScreen() {
        return null;
    }

    public void start() {

    }

    public void onTouch(TouchEvent evt, float tpf, float screenWidth, float screenHeight) {
        
    }
}
