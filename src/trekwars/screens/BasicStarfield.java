package trekwars.screens;

import com.jme3.asset.AssetManager;
import com.jme3.input.InputManager;
import com.jme3.input.event.TouchEvent;
import com.jme3.light.AmbientLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Quad;
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
    private final float _minStarDistance = 50;
    private final int _numberOfStars = 1000;
    private List<Spatial> _stars = new ArrayList<Spatial>();
    private final float _enemyWaveOneZ = -25f;
    private final float _enemyWaveTwoZ = -50f;
    private final float _enemyWaveThreeZ = -75f;
    private final InputManager _inputManager;
    private final Vector2f _screenSize;

    public BasicStarfield(
            IPlayer player, 
            Iterable<IPlayer> enemyWaveOne,
            Iterable<IPlayer> enemyWaveTwo,
            Iterable<IPlayer> enemyWaveThree,
            AssetManager assetManager,
            Camera camera,
            InputManager inputManager,
            Vector2f screenSize){
        if(player == null || camera == null) {
            //TODO: throw new ArgumentNullException();
        }
        
        _camera = camera;
        _rootNode = new Node();
        _player = player;
        _enemyWaveOne = new ArrayList<IPlayer>((Collection<? extends IPlayer>) enemyWaveOne);
        _enemyWaveTwo = new ArrayList<IPlayer>((Collection<? extends IPlayer>) enemyWaveTwo);;
        _enemyWaveThree = new ArrayList<IPlayer>((Collection<? extends IPlayer>) enemyWaveThree);;
        _inputManager = inputManager;
        _screenSize = screenSize;
        
        createStarfield(assetManager);
        createLighting();
        attachChildren(player, enemyWaveOne, enemyWaveTwo, enemyWaveThree);
        arrangeEnemyWave(_enemyWaveOne, _enemyWaveOneZ);
        arrangeEnemyWave(_enemyWaveTwo, _enemyWaveTwoZ);
        arrangeEnemyWave(_enemyWaveThree, _enemyWaveThreeZ);
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
        star.setLocalTranslation(generateStarCoordinate(), generateStarCoordinate(), generateStarCoordinate());
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
        _player.update(tpf);
        updatePlayers(_enemyWaveOne, tpf);
        updatePlayers(_enemyWaveTwo, tpf);
        updatePlayers(_enemyWaveThree, tpf);
        
        positionCamera();
        
        for(Spatial star : _stars) {
            star.lookAt(_camera.getLocation(), Vector3f.UNIT_Y);
            if(new Distance(star, _player.getRootNode()).getLocal() < _minStarDistance) {
                positionStar(star);
            }
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
            float x = _inputManager.getCursorPosition().getX();
            if(x < _screenSize.getX() / 2) {
                _player.turnLeft();
            } else {
                _player.turnRight();
            }
        }
    }

    private void createStarfield(AssetManager assetManager) {
        Material material = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        material.setColor("Color", ColorRGBA.White);
        for(int i = 0; i < _numberOfStars; i++) {
            Quad quad = new Quad(1,1);
            Geometry geom = new Geometry("star", quad);
            geom.setMaterial(material);
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
