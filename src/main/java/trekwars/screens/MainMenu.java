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
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Quad;
import com.jme3.texture.Texture;
import java.util.ArrayList;
import trekwars.core.InputMappings;
import trekwars.players.IPlayer;
import trekwars.players.PlayerFactory;
import trekwars.players.PlayerFactoryType;
import trekwars.players.PlayerType;

public class MainMenu extends AbstractStarfield {
    private Camera _camera;
    private ArrayList<IPlayer> _ships = new ArrayList<IPlayer>();
    private Spatial _chooseYourShip;
    
    public MainMenu(
            AssetManager assetManager,
            IPlayer player,
            Camera camera,
            PlayerFactory playerFactory) {
        super(assetManager, player, camera);
        _camera = camera;
        _camera.setLocation(new Vector3f(0, 5, -15));
        _camera.lookAt(new Vector3f(0, 0, 5), Vector3f.UNIT_Y);
        _ships.add(playerFactory.create(PlayerFactoryType.Voyager, PlayerType.Enemy));
        _ships.add(playerFactory.create(PlayerFactoryType.Brel, PlayerType.Enemy));
        float xOffset = 0f;
        for(IPlayer ship : _ships) {
            _rootNode.attachChild(ship.getRootNode());
            ship.getRootNode().setLocalTranslation(xOffset, 0, 0);
            xOffset -= 10;
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
        Vector3f cameraLocation = _camera.getLocation();
        if(name.equals(InputMappings.left)) {
            _camera.setLocation(new Vector3f(cameraLocation.x + 0.1f, cameraLocation.y, cameraLocation.z));
        } else if (name.equals(InputMappings.right)) {
            _camera.setLocation(new Vector3f(cameraLocation.x - 0.1f, cameraLocation.y, cameraLocation.z));
        }
    }
    
    @Override
    public void onUpdate(float tpf) {
        for(IPlayer ship : _ships) {
            ship.getRootNode().rotate(0, tpf * 0.5f, 0);
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
