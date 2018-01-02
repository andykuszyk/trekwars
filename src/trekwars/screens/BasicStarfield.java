package trekwars.screens;

import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioNode;
import com.jme3.audio.AudioSource;
import com.jme3.input.InputManager;
import com.jme3.input.event.TouchEvent;
import com.jme3.light.AmbientLight;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Quad;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture.WrapMode;
import com.jme3.util.SkyFactory;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import trekwars.core.Distance;
import trekwars.core.InputMappings;
import trekwars.players.IPlayer;

public class BasicStarfield implements IScreen {
    
    private final Node _rootNode;
    private final IPlayer _player;
    private final ArrayList<IPlayer> _enemyWaveOne;
    private final ArrayList<IPlayer> _enemyWaveTwo;
    private final ArrayList<IPlayer> _enemyWaveThree;
    private final Camera _camera;
    private final float _cameraZDistance = 10f;
    private final float _cameraYDistance = 3f;
    private final Random _random = new Random();
    private final float _starRadius = 100;
    private final float _minStarDistance = 75;
    private final float _maxStarDistance = 150;
    private final int _numberOfStars = 200;
    private List<Spatial> _stars = new ArrayList<Spatial>();
    private final float _enemyWaveOneZ = -50f;
    private final float _enemyWaveTwoZ = -75f;
    private final float _enemyWaveThreeZ = -100f;
    private final InputManager _inputManager;
    private final Vector2f _screenSize;
    private final AssetManager _assetManager;
    private Button _leftButton;
    private Button _rightButton;
    private Button _fireButton;
    private final AudioNode _audioNode;
    private final Node _guiNode;
    private final ArrayList<TouchEvent> _touchEvents = new ArrayList<TouchEvent>();
    private boolean _isTouchLeft = false;
    private boolean _isTouchRight = false;
    private boolean _isTouchFire = false;

    public BasicStarfield(
            IPlayer player, 
            Iterable<IPlayer> enemyWaveOne,
            Iterable<IPlayer> enemyWaveTwo,
            Iterable<IPlayer> enemyWaveThree,
            AssetManager assetManager,
            Camera camera,
            InputManager inputManager,
            Vector2f screenSize){
        _camera = camera;
        _rootNode = new Node();
        _player = player;
        _enemyWaveOne = new ArrayList<IPlayer>((Collection<? extends IPlayer>) enemyWaveOne);
        _enemyWaveTwo = new ArrayList<IPlayer>((Collection<? extends IPlayer>) enemyWaveTwo);
        _enemyWaveThree = new ArrayList<IPlayer>((Collection<? extends IPlayer>) enemyWaveThree);
        _inputManager = inputManager;
        _screenSize = screenSize;
        _assetManager = assetManager;
        _guiNode = new Node();
        
        createStarfield(assetManager);
        createLighting();
        attachChildren(player, enemyWaveOne, enemyWaveTwo, enemyWaveThree);
        arrangeEnemyWave(_enemyWaveOne, _enemyWaveOneZ);
        arrangeEnemyWave(_enemyWaveTwo, _enemyWaveTwoZ);
        arrangeEnemyWave(_enemyWaveThree, _enemyWaveThreeZ);
        
        _audioNode = new AudioNode(assetManager, "Sounds/federation-theme.ogg", true);
        _audioNode.setPositional(false);
        _audioNode.setVolume(0.5f);
        _rootNode.attachChild(_audioNode);
        
        initialiseHud();
    }
    
    private boolean isTouchDown(TouchEvent.Type evt) {
        return 
                evt == TouchEvent.Type.MOVE ||
                evt == TouchEvent.Type.SCROLL ||
                evt == TouchEvent.Type.SCALE_START ||
                evt == TouchEvent.Type.SCALE_MOVE ||
                evt == TouchEvent.Type.TAP ||
                evt == TouchEvent.Type.SHOWPRESS ||
                evt == TouchEvent.Type.DOWN;
    }
    
    public void onTouch(TouchEvent evt, float tpf, float screenWidth, float screenHeight) {
        _touchEvents.add(evt);
    }
    
    private void handleTouch() {
        boolean leftIsHandled = false;
        boolean rightIsHandled = false;
        boolean fireIsHandled = false;
        
        for(TouchEvent evt : _touchEvents) {
             System.out.println(String.format("SZYK: %s", evt.getType()));

             if(touchIsLeft(evt)) {
                 if(!leftIsHandled) {
                    _isTouchLeft = isTouchDown(evt.getType());
                    leftIsHandled = !_isTouchLeft;
                 }
             } else if(touchIsFire(evt)) {
                 if (!fireIsHandled) {
                     _isTouchFire = isTouchDown(evt.getType());
                     fireIsHandled = !_isTouchFire;
                 }
             } else if(!rightIsHandled) {
                 _isTouchRight = isTouchDown(evt.getType());
                 rightIsHandled = !_isTouchRight;
             }
         }
        
        if(_isTouchLeft) _player.turnLeft();
        if(_isTouchRight) _player.turnRight();
        if(_isTouchFire) _player.fire();
        
        _touchEvents.clear();
    }
    
    public void start() {
        _audioNode.play();
    }
    
    public IScreen getNextScreen() {
        return null;
    }
    
    public Node getGuiNode() {
        return _guiNode;
    }
    
    private void initialiseHud() {
        _guiNode.attachChild(new GuiElement(
                "top-left", 
                _assetManager, 
                new Vector2f(0f, 0.9f), 
                new Vector2f(0.25f, 1f), 
                _screenSize, 
                "Interface/lcars-top-left.png").getPicture());
        
        _guiNode.attachChild(new GuiElement(
                "top-right", 
                _assetManager, 
                new Vector2f(0.75f, 0.9f), 
                new Vector2f(1f, 1f), 
                _screenSize, 
                "Interface/lcars-top-right.png").getPicture());
        
        _guiNode.attachChild(new GuiElement(
                "top-middle", 
                _assetManager, 
                new Vector2f(0.26f, 0.98f), 
                new Vector2f(0.74f, 1f),
                _screenSize, 
                "Interface/lcars-box-inactive.png").getPicture());
        
        _guiNode.attachChild(new GuiElement(
                "bottom-left", 
                _assetManager, 
                new Vector2f(0f, 0f), 
                new Vector2f(0.25f, 0.1f),
                _screenSize, 
                "Interface/lcars-bottom-left.png").getPicture());
        
        _guiNode.attachChild(new GuiElement(
                "bottom-right", 
                _assetManager, 
                new Vector2f(0.75f, 0f), 
                new Vector2f(1f, 0.1f),
                _screenSize, 
                "Interface/lcars-bottom-right.png").getPicture());
        
        _leftButton = new Button(
                "left-button",
                "Interface/lcars-box-inactive.png",
                "Interface/lcars-box-active.png",
                new Vector2f(0f, 0.11f), 
                new Vector2f(0.11f, 0.89f),
                _assetManager,
                _screenSize);
        _guiNode.attachChild(_leftButton.getPicture());
        
        _rightButton = new Button(
                "right-button",
                "Interface/lcars-box-inactive.png",
                "Interface/lcars-box-active.png",
                new Vector2f(0.89f, 0.11f), 
                new Vector2f(1f, 0.89f),
                _assetManager,
                _screenSize);
        _guiNode.attachChild(_rightButton.getPicture());
        
        _fireButton = new Button(
                "fire-button",
                "Interface/lcars-fire-inactive.png",
                "Interface/lcars-fire-active.png",
                new Vector2f(0.26f, 0f), 
                new Vector2f(0.74f, 0.1f),
                _assetManager,
                _screenSize);
        _guiNode.attachChild(_fireButton.getPicture());
    }
    
    private void arrangeEnemyWave(ArrayList<IPlayer> enemyWave, float z) {
        if(enemyWave == null) return;
        
        int shipCount = enemyWave.size();
        float averageWidth = getAverageWidth(_enemyWaveOne);
        int shipIndex = 0;
        for(IPlayer ship : enemyWave) {
            float x = -(shipCount - 1) * averageWidth + shipIndex * averageWidth * 2;
            ship.getRootNode().setLocalTranslation(x,0,z);
            Vector3f target = _player.getRootNode().getWorldTranslation();
            ship.getRootNode().rotate(0f, (float) Math.PI, 0f);
            shipIndex++;
        }
    }
    
    private float getAverageWidth(Iterable<IPlayer> players) {
        float sumOfWidths = 0f;
        float numberOfWidths = 0;
  
        //TODO
        sumOfWidths = 10;
        numberOfWidths = 1;
//        for (IPlayer player : players) {
//            for(Spatial         for (IPlayer player : players) {
//            for(Spatial child : player.getRootNode().getChildren()) {
//                child.getWorldBound().
//            }
//        }child : player.getRootNode().getChildren()) {
//                child.getWorldBound().
//            }
//        }
        
        return sumOfWidths / numberOfWidths;
    }
    
    private void positionStar(Spatial star) {
        Vector3f newCoords = new Vector3f(generateStarCoordinate(), generateStarCoordinate(), generateStarCoordinate());
        star.setLocalTranslation(_player.getRootNode().getLocalTranslation().add(newCoords));
    }
    
    private float generateStarCoordinate() {
        return
                (_random.nextFloat() - 0.5f) * 
                ((_starRadius + _minStarDistance) / _minStarDistance) * 
                _starRadius;
    }
    
    private void attachRootNodes(Iterable<IPlayer> players){
        if(players == null) return;
        for(IPlayer player : players){
            for(Node node : player.getRootNodes()){
                _rootNode.attachChild(node);
            }
        }
    }
    
    public Node getRootNode() {
        return _rootNode;
    }

    public void update(float tpf) {
        handleTouch();
        _player.update(tpf);
        updatePlayers(_enemyWaveOne, tpf);
        updatePlayers(_enemyWaveTwo, tpf);
        updatePlayers(_enemyWaveThree, tpf);
        positionCamera();
        
        for(Spatial star : _stars) {
            star.lookAt(_camera.getLocation(), Vector3f.UNIT_Y);
            double distance = new Distance(star, _player.getRootNode()).getLocal();
            if(distance < _minStarDistance || distance > _maxStarDistance) {
                positionStar(star);
            }
        }
        
        if(_audioNode.getStatus() != AudioSource.Status.Playing) {
            _audioNode.play();
        }
    }
    
    private void positionCamera(){
        float playerYRadians = _player.getRootNode().getWorldRotation().toAngles(null)[1];
        Vector3f playerWorldTranslation = _player.getRootNode().getWorldTranslation();
        double cameraZ = playerWorldTranslation.getZ() + (_cameraZDistance * Math.cos(playerYRadians));
        double cameraX = playerWorldTranslation.getX() + (_cameraZDistance * Math.sin(playerYRadians));
        
        _camera.setLocation(new Vector3f((float)cameraX, (float)_cameraYDistance, (float)cameraZ));
        _camera.lookAt(playerWorldTranslation, Vector3f.UNIT_Y);
    }
    
    private void updatePlayers(Iterable<IPlayer> players, float tpf){
        if (players == null) return;
        for(IPlayer player : players){
            player.update(tpf);
        }
    }

    public void onAnalog(String name, float keyPressed, float tpf) {
        if(name == null) return;
        if (name.equals(InputMappings.left)) {
            _player.turnLeft();
        }
        else if (name.equals(InputMappings.right)) {
            _player.turnRight();
        }
        else if (name.equals(InputMappings.boost)) {
            _player.boost();
        }
        else if (name.equals(InputMappings.fire)){
            _player.fire();
        }
        else if(name.equals(InputMappings.stop)) {
            _player.stop();
        }
        else if(name.equals(InputMappings.left_click)) {
            if(mouseIsLeft()) {
                _player.turnLeft();
            } else if (mouseIsFire()) {
                _player.fire();
            } else {
                _player.turnRight();
            }
        }
    }
    
    private boolean touchIsLeft(TouchEvent evt) {
        return 
                evt.getX() < _screenSize.getX() / 2 && 
                evt.getY() > _screenSize.getY() / 5;
    }
    
    private boolean touchIsFire(TouchEvent evt) {
        return evt.getY() <= _screenSize.getY() / 5;
    }
    
    private boolean mouseIsLeft() {
        return 
                _inputManager.getCursorPosition().getX() < _screenSize.getX() / 2 && 
                _inputManager.getCursorPosition().getY() > _screenSize.getY() / 5;
    }
    
    private boolean mouseIsFire() {
        return _inputManager.getCursorPosition().getY() <= _screenSize.getY() / 5;
    }
    
    public void onAction(String name, boolean keyPressed, float tpf) {
        if(name == null) return;
        if(name.equals(InputMappings.left_click)) {
            if(keyPressed) {
                if(mouseIsLeft()) {
                    _leftButton.activate();
                    _rightButton.deactivate();
                    _fireButton.deactivate();
                } else if(mouseIsFire()) {
                    _fireButton.activate();
                    _leftButton.deactivate();
                    _rightButton.deactivate();
                } else {
                    _rightButton.activate();
                    _leftButton.deactivate();
                    _fireButton.deactivate();
                }
            } else {
                if(mouseIsLeft()) {
                    _leftButton.deactivate();
                } else if(mouseIsFire()) {
                    _fireButton.deactivate();
                } else {
                    _rightButton.deactivate();
                }
            }
        }
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
        star.setWrap(WrapMode.Repeat);
        material.setTexture("ColorMap", star);
        material.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
        for(int i = 0; i < _numberOfStars; i++) {
            Quad quad = new Quad(2f,2f);
            Geometry geom = new Geometry("star", quad);
            geom.setMaterial(material);
            geom.setQueueBucket(Bucket.Transparent);
            _rootNode.attachChild(geom);
            positionStar(geom);
            _stars.add(geom);
        }
    }

    private void attachChildren(
            IPlayer player, 
            Iterable<IPlayer> enemyWaveOne, 
            Iterable<IPlayer> enemyWaveTwo, 
            Iterable<IPlayer> enemyWaveThree) {
        for(Node node : player.getRootNodes()) {
            _rootNode.attachChild(node);
        }
        attachRootNodes(enemyWaveOne);
        attachRootNodes(enemyWaveTwo);
        attachRootNodes(enemyWaveThree);
    }

    private void createLighting() {
        AmbientLight al = new AmbientLight();
        al.setColor(ColorRGBA.White.mult(1.3f));
        _rootNode.addLight(al);
    }
}
