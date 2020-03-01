package trekwars.screens;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;

import java.io.Console;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Quad;
import com.jme3.texture.Texture;
import trekwars.core.InputMappings;
import trekwars.players.IPlayer;
import trekwars.players.PlayerFactory;
import trekwars.players.PlayerFactoryType;
import trekwars.players.PlayerType;

public class MainMenu extends AbstractStarfield {
    private final Node playersNode;
    private final Quaternion playersAngle;
    private final float radiusSize = 20f;
    private final Node enemiesNode;
    private final float verticalOffset = 40f;
    private Camera _camera;
    private ArrayList<IPlayer> _ships = new ArrayList<IPlayer>();
    private ArrayList<Spatial> enemies = new ArrayList<Spatial>();
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
        enemiesNode = new Node();
        _rootNode.attachChild(playersNode);
        _rootNode.attachChild(enemiesNode);

        playersAngle = new Quaternion().fromAngleAxis((float)(Math.PI * 2 / _ships.size()), Vector3f.UNIT_Y);
        Vector3f playersRadius = new Vector3f(0, 0, radiusSize);
        _camera.setLocation(new Vector3f(0, (float)(radiusSize * 0.25), radiusSize * 2));
        _camera.lookAt(new Vector3f(0, 0, radiusSize), Vector3f.UNIT_Y);

        for(IPlayer ship : _ships) {
            playersNode.attachChild(ship.getRootNode());
            ship.getRootNode().setLocalTranslation(playersRadius);
            playersRadius = playersAngle.mult(playersRadius);
        }

        List<String> logos = Arrays.asList("empire-logo.png", "federation-logo.jpg", "klingon-logo.png", "rebels-logo.gif");
        Quaternion logosAngle = new Quaternion().fromAngleAxis((float)(Math.PI * 2 / logos.size()), Vector3f.UNIT_Y);
        Vector3f logosRadius = new Vector3f(0, 0, radiusSize);
        float logoSize = 5f;
        enemiesNode.setLocalTranslation(0f, -verticalOffset, 0f);
        for(String logo : logos) {
            Spatial logoSpatial = makeLogoSpatial(logo, logoSize);
            enemies.add(logoSpatial);
            enemiesNode.attachChild(logoSpatial);
            logoSpatial.setLocalTranslation(logosRadius);
            logosRadius = logosAngle.mult(logosRadius);
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

    private Spatial makeLogoSpatial(String logo, float logoSize) {
        Material material = new Material(_assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        Texture texture = _assetManager.loadTexture("Interface/" + logo);
        texture.setWrap(Texture.WrapMode.Repeat);
        material.setTexture("ColorMap", texture);
        Geometry spatial = new Geometry(logo, new Quad(logoSize, logoSize));
        spatial.setMaterial(material);
        Node node = new Node();
        node.attachChild(spatial);
        spatial.setLocalTranslation(-logoSize / 2, -logoSize / 2, 0f);
        return node;
    }

    @Override
    public void onAnalog(String name, float keyPressed, float tpf) {
        if(lastKeyPress != null && LocalDateTime.now().isBefore(lastKeyPress.plus(500, ChronoUnit.MILLIS))) {

            return;
        }
        Vector3f cameraLocation = _camera.getLocation();
        boolean atPlayers = cameraLocation.y > 0f;
        lastKeyPress = LocalDateTime.now();
        float playersStepRotate = (float)(Math.PI * 2 / _ships.size());
        float enemiesStepRotate = (float)(Math.PI * 2 / enemies.size());
        if(name.equals(InputMappings.left)) {
            if(atPlayers) {
                playersNode.rotate(0, playersStepRotate, 0);
            } else {
                enemiesNode.rotate(0, enemiesStepRotate, 0);
            }
        } else if (name.equals(InputMappings.right)) {
            if(atPlayers) {
                playersNode.rotate(0, -playersStepRotate, 0);
            } else {
                enemiesNode.rotate(0, -enemiesStepRotate, 0);
            }
        } else if(name.equals(InputMappings.select)) {
            if(atPlayers) {
                _camera.setLocation(cameraLocation.add(new Vector3f(0f, -verticalOffset, 0f)));
            } else {
                _camera.setLocation(cameraLocation.add(new Vector3f(0f, verticalOffset, 0f)));
            }
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
    }
}
