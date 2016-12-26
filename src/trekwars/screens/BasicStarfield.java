package trekwars.screens;

import com.jme3.asset.AssetManager;
import com.jme3.light.AmbientLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Quad;
import com.jme3.scene.shape.Sphere;
import com.jme3.texture.Texture;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import trekwars.core.Distance;
import trekwars.core.InputMappings;
import trekwars.players.IPlayer;

public class BasicStarfield implements IScreen {
    
    private final Node _rootNode;
    private final IPlayer _player;
    private final Iterable<IPlayer> _enemyWaveOne;
    private final Iterable<IPlayer> _enemyWaveTwo;
    private final Iterable<IPlayer> _enemyWaveThree;
    private final Camera _camera;
    private final float _cameraZDistance = 10f;
    private final float _cameraYDistance = 3f;
    private final Random _random = new Random();
    private final float _starRadius = 100;
    private final float _minStarDistance = 50;
    private final int _numberOfStars = 1000;
    private List<Spatial> _stars = new ArrayList<Spatial>();

    public BasicStarfield(
            IPlayer player, 
            Iterable<IPlayer> enemyWaveOne,
            Iterable<IPlayer> enemyWaveTwo,
            Iterable<IPlayer> enemyWaveThree,
            AssetManager assetManager,
            Camera camera){
        if(player == null || camera == null) {
            //TODO: throw new ArgumentNullException();
        }
        
        _camera = camera;
        _rootNode = new Node();
        _player = player;
        _enemyWaveOne = enemyWaveOne;
        _enemyWaveTwo = enemyWaveTwo;
        _enemyWaveThree = enemyWaveThree;
        
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
        
        AmbientLight al = new AmbientLight();
        al.setColor(ColorRGBA.White.mult(1.3f));
        _rootNode.addLight(al);
        
        _rootNode.attachChild(player.getRootNode());
        attachRootNodes(enemyWaveOne);
        attachRootNodes(enemyWaveTwo);
        attachRootNodes(enemyWaveThree);
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
            _rootNode.attachChild(player.getRootNode());
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
            _player.turnLeft(tpf);
        }
        else if (name.equals(InputMappings.right)) {
            _player.turnRight(tpf);
        }
        else if (name.equals(InputMappings.boost)) {
            _player.boost(tpf);
        }
        else if (name.equals(InputMappings.fire)){
            _player.fire(tpf);
        }
    }
}
