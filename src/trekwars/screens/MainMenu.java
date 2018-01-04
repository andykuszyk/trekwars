package trekwars.screens;

import com.jme3.asset.AssetManager;
import com.jme3.math.Matrix3f;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import java.util.ArrayList;
import trekwars.core.InputMappings;
import trekwars.players.IPlayer;
import trekwars.players.PlayerFactory;
import trekwars.players.PlayerFactoryType;
import trekwars.players.PlayerType;

public class MainMenu extends AbstractStarfield {
    private Camera _camera;
    private ArrayList<IPlayer> _ships = new ArrayList<IPlayer>();
    
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
        _ships.add(playerFactory.create(PlayerFactoryType.Brel, PlayerType.Player));
        float xOffset = 0f;
        for(IPlayer ship : _ships) {
            _rootNode.attachChild(ship.getRootNode());
            ship.getRootNode().setLocalTranslation(xOffset, 0, 0);
            xOffset += 5;
        }
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
            ship.update(tpf);
        }
    }
}
