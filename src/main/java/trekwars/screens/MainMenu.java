package trekwars.screens;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.Matrix3f;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Quad;
import com.jme3.texture.Texture;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import trekwars.core.InputMappings;
import trekwars.players.IPlayer;
import trekwars.players.PlayerFactory;
import trekwars.players.PlayerFactoryType;
import trekwars.players.PlayerType;

public class MainMenu extends AbstractStarfield {
    private final Node playersNode;
    private final Quaternion theta;
    private final float radiusSize;
    private Camera _camera;
    private ArrayList<IPlayer> _ships = new ArrayList<IPlayer>();
    private LocalDateTime lastKeyPress;

    public MainMenu(
            AssetManager assetManager,
            IPlayer player,
            Camera camera,
            PlayerFactory playerFactory,
            Vector2f screenSize) {
        super(assetManager, player, camera);
        _camera = camera;

        _ships.add(playerFactory.create(PlayerFactoryType.Voyager, PlayerType.Enemy));
        _ships.add(playerFactory.create(PlayerFactoryType.Brel, PlayerType.Enemy));
        _ships.add(playerFactory.create(PlayerFactoryType.Voyager, PlayerType.Enemy));
        _ships.add(playerFactory.create(PlayerFactoryType.Brel, PlayerType.Enemy));
        _ships.add(playerFactory.create(PlayerFactoryType.Voyager, PlayerType.Enemy));
        _ships.add(playerFactory.create(PlayerFactoryType.Brel, PlayerType.Enemy));

        playersNode = new Node();
        _rootNode.attachChild(playersNode);

        radiusSize = 20f;
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

        _guiNode.attachChild(new GuiElement(
                "main-menu",
                assetManager,
                new Vector2f(0f, 0f),
                new Vector2f(1f, 1f),
                screenSize,
                "Interface/main-menu.png").getPicture()
        );
    }
    
    @Override
    public void onAnalog(String name, float keyPressed, float tpf) {
        if(lastKeyPress != null && LocalDateTime.now().isBefore(lastKeyPress.plus(500, ChronoUnit.MILLIS))) {

            return;
        }
        lastKeyPress = LocalDateTime.now();
        Vector3f cameraLocation = _camera.getLocation();
        float stepRotate = (float)(Math.PI * 2 / _ships.size());
        if(name.equals(InputMappings.left)) {
            playersNode.rotate(0, stepRotate, 0);
        } else if (name.equals(InputMappings.right)) {
            playersNode.rotate(0, -stepRotate, 0);
        }
    }
    
    @Override
    public void onUpdate(float tpf) {
        for(IPlayer ship : _ships) {
            if(ship.getRootNode().getWorldTranslation().getZ() > radiusSize * 0.9f) {
                ship.getRootNode().rotate(0, tpf * 0.2f, 0);
            } else {
                ship.getRootNode().setLocalRotation(new Quaternion());
            }
        }
        Vector3f cameraLocation = _camera.getLocation();
    }
}
