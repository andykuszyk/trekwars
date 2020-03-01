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
    protected final Node _rootNode;
    private final Random _random = new Random();
    private final float _starRadius = 100;
    private final float _minStarDistance = 75;
    private final float _maxStarDistance = 150;
    private final int _numberOfStars = 200;
    protected final AssetManager _assetManager;
    private ArrayList<Spatial> _stars = new ArrayList<Spatial>();
    private final IPlayer _player;
    protected final Node _guiNode;
    protected final Camera _camera;
    
    public AbstractStarfield(
            AssetManager assetManager,
            IPlayer player,
            Camera camera
            ) {
        _player = player;
        _rootNode = new Node();
        _guiNode = new Node();
        _camera = camera;
        _assetManager = assetManager;
        createStarfield(assetManager);
        createLighting();
    }
    
    private void createStarfield(AssetManager assetManager) {
        _rootNode.attachChild(
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
        for(int i = 0; i < _numberOfStars; i++) {
            Quad quad = new Quad(2f,2f);
            Geometry geom = new Geometry("star", quad);
            geom.setMaterial(material);
            geom.setQueueBucket(RenderQueue.Bucket.Transparent);
            _rootNode.attachChild(geom);
            positionStar(geom);
            _stars.add(geom);
        }
    }
        
    private void createLighting() {
        AmbientLight al = new AmbientLight();
        al.setColor(ColorRGBA.White.mult(1.3f));
        _rootNode.addLight(al);
    }   
    
    private void positionStar(Spatial star) {
        Vector3f newCoords = new Vector3f(generateStarCoordinate(), generateStarCoordinate(), generateStarCoordinate());
        if(_player != null) {
            star.setLocalTranslation(_player.getRootNode().getLocalTranslation().add(newCoords));
        } else {
            star.setLocalTranslation(newCoords);
        }
    }
    
    private float generateStarCoordinate() {
        return
                (_random.nextFloat() - 0.5f) * 
                ((_starRadius + _minStarDistance) / _minStarDistance) * 
                _starRadius;
    }
    
    public void update(float tpf) {
        for(Spatial star : _stars) {
            star.lookAt(_camera.getLocation(), Vector3f.UNIT_Y);
            double distance;
            if(_player == null) {
                distance = star.getWorldTranslation().distance(_camera.getLocation());
            } else {
                distance = new Distance(star, _player.getRootNode()).getLocal();
            }
            if(distance < _minStarDistance || distance > _maxStarDistance) {
                positionStar(star);
            }
        }
        
        onUpdate(tpf);
    }
    
    protected void onUpdate(float tpf) {
    
    }

    public Node getRootNode() {
        return _rootNode;
    }

    public Node getGuiNode() {
        return _guiNode;
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
