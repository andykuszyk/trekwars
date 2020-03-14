package trekwars.screens;

import com.jme3.asset.AssetManager;
import com.jme3.input.InputManager;
import com.jme3.material.Material;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;

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
import trekwars.players.*;

public class MainMenu extends AbstractStarfield {
    private final Node playersNode = new Node();
    private final Quaternion playersAngle;
    private final float radiusSize = 20f;
    private final Node enemyRacesNode = new Node();
    private final Node playerRacesNode = new Node();
    private final float verticalOffset = 40f;
    private final Vector2f screenSize;
    private final PlayerFactory playerFactory;
    private final InputManager inputManager;
    private Camera camera;
    private ArrayList<AbstractPlayer> ships = new ArrayList<AbstractPlayer>();
    private ArrayList<Spatial> playerRaces = new ArrayList<Spatial>();
    private ArrayList<Spatial> enemyRaces = new ArrayList<Spatial>();
    private LocalDateTime lastKeyPress;
    private IScreen nextScreen;
    private Logger log = Logger.getGlobal();

    public MainMenu(
            AssetManager assetManager,
            IPlayer player,
            Camera camera,
            PlayerFactory playerFactory,
            Vector2f screenSize, InputManager inputManager) {
        super(assetManager, player, camera);
        this.camera = camera;
        this.screenSize = screenSize;
        this.playerFactory = playerFactory;
        this.inputManager = inputManager;
        rootNode.attachChild(playersNode);
        rootNode.attachChild(enemyRacesNode);
        rootNode.attachChild(playerRacesNode);

        playerRacesNode.setLocalTranslation(0f, 0, 0f);
        playersNode.setLocalTranslation(0f, -verticalOffset, 0f);
        enemyRacesNode.setLocalTranslation(0f, -verticalOffset * 2, 0f);

        camera.setLocation(new Vector3f(0, (float)(radiusSize * 0.25), radiusSize * 2));
        camera.lookAt(new Vector3f(0, 0, radiusSize), Vector3f.UNIT_Y);

        for(PlayerFactoryType type : PlayerFactoryType.values()) {
            ships.add(playerFactory.create(type, PlayerType.Enemy));
        }

        playersAngle = new Quaternion().fromAngleAxis((float)(Math.PI * 2 / ships.size()), Vector3f.UNIT_Y);
        Vector3f playersRadius = new Vector3f(0, 0, radiusSize);
        for(IPlayer ship : ships) {
            playersNode.attachChild(ship.getRootNode());
            ship.getRootNode().setLocalTranslation(playersRadius);
            playersRadius = playersAngle.mult(playersRadius);
        }

        List<String> logos = Arrays.asList("empire-logo.png", "federation-logo.jpg", "klingon-logo.png", "rebels-logo.gif");
        Quaternion logosAngle = new Quaternion().fromAngleAxis((float)(Math.PI * 2 / logos.size()), Vector3f.UNIT_Y);
        Vector3f logosRadius = new Vector3f(0, 0, radiusSize);
        float logoSize = 5f;

        for(String logo : logos) {
            initialiseLogo(logosRadius, logoSize, logo);
            logosRadius = logosAngle.mult(logosRadius);
        }

        guiNode.attachChild(new GuiElement(
                "main-menu",
                assetManager,
                new Vector2f(0f, 0f),
                new Vector2f(1f, 1f),
                screenSize,
                "Interface/main-menu.png").getPicture()
        );
    }

    private void initialiseLogo(Vector3f logosRadius, float logoSize, String logo) {
        Spatial playerLogoSpatial = makeLogoSpatial(logo, logoSize);
        playerRaces.add(playerLogoSpatial);
        playerRacesNode.attachChild(playerLogoSpatial);
        playerLogoSpatial.setLocalTranslation(logosRadius);

        Spatial enemyLogoSpatial = makeLogoSpatial(logo, logoSize);
        enemyRaces.add(enemyLogoSpatial);
        enemyRacesNode.attachChild(enemyLogoSpatial);
        enemyLogoSpatial.setLocalTranslation(logosRadius);
    }

    private Spatial makeLogoSpatial(String logo, float logoSize) {
        Material material = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        Texture texture = assetManager.loadTexture("Interface/" + logo);
        texture.setWrap(Texture.WrapMode.Repeat);
        material.setTexture("ColorMap", texture);
        Geometry front = new Geometry(logo, new Quad(logoSize, logoSize));
        front.setMaterial(material);
        Geometry back = front.clone();
        Node node = new Node();
        node.attachChild(front);
        node.attachChild(back);
        front.setLocalTranslation(-logoSize / 2, -logoSize / 2, 0f);
        back.rotate(0, (float)Math.PI, 0);
        back.setLocalTranslation(logoSize / 2, -logoSize / 2, 0f);
        return node;
    }

    private enum MenuTrack {
        PlayerRaces, Players, EnemyRaces
    }

    @Override
    public void onAnalog(String name, float keyPressed, float tpf) {
        if(lastKeyPress != null && LocalDateTime.now().isBefore(lastKeyPress.plus(200, ChronoUnit.MILLIS))) {
            return;
        }
        lastKeyPress = LocalDateTime.now();

        Vector3f cameraLocation = camera.getLocation();
        MenuTrack currentMenuTrack = getMenuTrack(cameraLocation);

        float playersStepRotate = (float)(Math.PI * 2 / ships.size());
        float enemyRacesStepRotate = (float)(Math.PI * 2 / enemyRaces.size());
        float playerRacesStepRotate = (float)(Math.PI * 2 / playerRaces.size());

        if(name.equals(InputMappings.left)) {
            rotateMenuTrack(currentMenuTrack, playersStepRotate, enemyRacesStepRotate, playerRacesStepRotate);
        } else if (name.equals(InputMappings.right)) {
            rotateMenuTrack(currentMenuTrack, -playersStepRotate, -enemyRacesStepRotate, -playerRacesStepRotate);
        } else if(name.equals(InputMappings.up)) {
            switch (currentMenuTrack) {
                case PlayerRaces:
                    return;
                default:
                    camera.setLocation(cameraLocation.add(new Vector3f(0, verticalOffset, 0f)));
                    break;
            }
        } else if(name.equals(InputMappings.down)) {
            switch (currentMenuTrack) {
                case EnemyRaces:
                    return;
                default:
                    camera.setLocation(cameraLocation.add(new Vector3f(0, -verticalOffset, 0f)));
                    break;

            }
        } else if(name.equals(InputMappings.select)) {
            PlayerFactoryType playerFactoryType = PlayerFactoryType.Defiant;
            boolean playerSet = false;
            for(AbstractPlayer player : this.ships) {
                log.info(String.format("player %s at z %f", player.getPlayerFactoryType(), player.getRootNode().getWorldTranslation().z));
                if(player.getRootNode().getWorldTranslation().z >= radiusSize * 0.9f) {
                    playerFactoryType = player.getPlayerFactoryType();
                    playerSet = true;
                    break;
                }
            }
            if(!playerSet) {
                log.warning("Player not set!");
            }
            GameOptions gameOptions = new GameOptions(playerFactoryType, RaceType.Federation, RaceType.Klingon);
            nextScreen = new Splash(assetManager, screenSize, playerFactory, inputManager, camera, Splash.NextScreen.BasicStarfield, gameOptions);
        }
    }

    private void rotateMenuTrack(MenuTrack currentMenuTrack, float playersStepRotate, float enemyRacesStepRotate, float playerRacesStepRotate) {
        switch(currentMenuTrack) {
            case Players:
                playersNode.rotate(0, playersStepRotate, 0);
                break;
            case PlayerRaces:
                playerRacesNode.rotate(0, playerRacesStepRotate, 0);
                break;
            case EnemyRaces:
                enemyRacesNode.rotate(0, enemyRacesStepRotate, 0);
                break;
        }
    }

    private MenuTrack getMenuTrack(Vector3f cameraLocation) {
        if(cameraLocation.y > 0f) {
            return MenuTrack.PlayerRaces;
        } else if(cameraLocation.y < -verticalOffset){
            return MenuTrack.EnemyRaces;
        } else {
            return MenuTrack.Players;
        }
    }

    @Override
    public IScreen getNextScreen() {
        return nextScreen;
    }
    
    @Override
    public void onUpdate(float tpf) {
        for(IPlayer ship : ships) {
            if(ship.getRootNode().getWorldTranslation().getZ() > radiusSize * 0.9f) {
                ship.getRootNode().rotate(0, tpf * 0.2f, 0);
            } else {
                ship.getRootNode().setLocalRotation(new Quaternion());
            }
        }

        for(Spatial enemy : enemyRaces) {
            enemy.rotate(0, tpf * 0.5f, 0);
        }

        for(Spatial player : playerRaces) {
            player.rotate(0, tpf * 0.5f, 0);
        }
    }
}
