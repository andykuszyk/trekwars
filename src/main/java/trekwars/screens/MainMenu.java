package trekwars.screens;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.Matrix3f;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Quad;
import com.jme3.texture.Texture;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Vector;

import trekwars.core.InputMappings;
import trekwars.players.IPlayer;
import trekwars.players.PlayerFactory;
import trekwars.players.PlayerFactoryType;
import trekwars.players.PlayerType;

public class MainMenu extends AbstractStarfield {
    private final Node playersNode;
    private final Quaternion theta;
    private Camera _camera;
    private ArrayList<IPlayer> _ships = new ArrayList<IPlayer>();
    private Spatial _chooseYourShip;
    private LocalDateTime lastKeyPress;

    public MainMenu(
            AssetManager assetManager,
            IPlayer player,
            Camera camera,
            PlayerFactory playerFactory) {
        super(assetManager, player, camera);
        _camera = camera;

        _ships.add(playerFactory.create(PlayerFactoryType.Voyager, PlayerType.Enemy));
        _ships.add(playerFactory.create(PlayerFactoryType.Brel, PlayerType.Enemy));
        _ships.add(playerFactory.create(PlayerFactoryType.Voyager, PlayerType.Enemy));
        _ships.add(playerFactory.create(PlayerFactoryType.Brel, PlayerType.Enemy));
        _ships.add(playerFactory.create(PlayerFactoryType.Voyager, PlayerType.Enemy));
        _ships.add(playerFactory.create(PlayerFactoryType.Brel, PlayerType.Enemy));
        _ships.add(playerFactory.create(PlayerFactoryType.Voyager, PlayerType.Enemy));

        playersNode = new Node();
        _rootNode.attachChild(playersNode);

        float radiusSize = 10f;
        theta = new Quaternion().fromAngleAxis((float)(Math.PI * 2 / _ships.size()), Vector3f.UNIT_Y);
        Vector3f radius = new Vector3f(0, 0, radiusSize);
        _camera.setLocation(new Vector3f(0, (float)(radiusSize * 0.25), radiusSize * 2));
        _camera.lookAt(new Vector3f(0, 0, radiusSize), Vector3f.UNIT_Y);

        int index = 0;
        for(IPlayer ship : _ships) {
            playersNode.attachChild(ship.getRootNode());
            ship.getRootNode().setLocalTranslation(radius);
            radius = theta.mult(radius);
        }
        
        _chooseYourShip = makeSign(loadSignMaterial("Interface/choose-your-ship.png", assetManager));
        _rootNode.attachChild(_chooseYourShip);
    }
    
    private Spatial makeSign(Material material) {
        Quad quad = new Quad(10f,2f);
        Geometry geom = new Geometry("sign", quad);
        geom.setMaterial(material);
        geom.setQueueBucket(RenderQueue.Bucket.Transparent);
        return geom;
    }
    
    private Material loadSignMaterial(String filePath, AssetManager assetManager) {
        Material material = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        Texture texture = assetManager.loadTexture(filePath);
        texture.setWrap(Texture.WrapMode.Repeat);
        material.setTexture("ColorMap", texture);
        material.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        return material;
    }
    
    @Override
    public void onAnalog(String name, float keyPressed, float tpf) {
        if(lastKeyPress != null && LocalDateTime.now().isBefore(lastKeyPress.plusSeconds(1))) {
            return;
        }
        lastKeyPress = LocalDateTime.now();
        Vector3f cameraLocation = _camera.getLocation();
        if(name.equals(InputMappings.left)) {
            playersNode.rotate(theta);
        } else if (name.equals(InputMappings.right)) {
            playersNode.rotate(theta.mult(-1));
        }
    }
    
    @Override
    public void onUpdate(float tpf) {
        for(IPlayer ship : _ships) {
//            ship.getRootNode().rotate(0, tpf * 0.5f, 0);
        }
        Vector3f cameraLocation = _camera.getLocation();
        _chooseYourShip.setLocalTranslation(
                cameraLocation.x + 5,
                3,
                0
                );
        _chooseYourShip.lookAt(cameraLocation, Vector3f.UNIT_Y);
    }
}
