package trekwars.screens;

import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioNode;
import com.jme3.audio.AudioSource;
import com.jme3.input.InputManager;
import com.jme3.input.event.TouchEvent;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import java.util.ArrayList;
import java.util.Collection;
import trekwars.core.InputMappings;
import trekwars.players.IPlayer;

public class BasicStarfield extends AbstractStarfield {
    private final IPlayer _player;
    private final ArrayList<IPlayer> _enemyWaveOne;
    private final ArrayList<IPlayer> _enemyWaveTwo;
    private final ArrayList<IPlayer> _enemyWaveThree;
    private final float _cameraZDistance = 10f;
    private final float _cameraYDistance = 3f;
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
        super(assetManager, player, camera);
        _player = player;
        _enemyWaveOne = new ArrayList<IPlayer>((Collection<? extends IPlayer>) enemyWaveOne);
        _enemyWaveTwo = new ArrayList<IPlayer>((Collection<? extends IPlayer>) enemyWaveTwo);
        _enemyWaveThree = new ArrayList<IPlayer>((Collection<? extends IPlayer>) enemyWaveThree);
        _inputManager = inputManager;
        _screenSize = screenSize;
        _assetManager = assetManager;
        
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
        sumOfWidths = 10;
        numberOfWidths = 1;
        
        return sumOfWidths / numberOfWidths;
    }
    
    private void attachRootNodes(Iterable<IPlayer> players){
        if(players == null) return;
        for(IPlayer player : players){
            for(Node node : player.getRootNodes()){
                _rootNode.attachChild(node);
            }
        }
    }

    @Override
    protected void onUpdate(float tpf) {
        handleTouch();
        _player.update(tpf);
        updatePlayers(_enemyWaveOne, tpf);
        updatePlayers(_enemyWaveTwo, tpf);
        updatePlayers(_enemyWaveThree, tpf);
        positionCamera();     
        
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
}
