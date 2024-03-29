package trekwars.screens;

import com.jme3.asset.AssetManager;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.input.InputManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
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
import trekwars.races.RaceType;
import trekwars.races.RaceFactory;
import trekwars.races.IRace;

public class MainMenu extends AbstractStarfield {
    private final Node playersNode = new Node();
    private final Node enemiesNode = new Node();
    private final float verticalOffset = 40f;
    private final float horizontalOffset = 15f;
    private final Vector2f screenSize;
    private final PlayerFactory playerFactory;
    private final RaceFactory raceFactory;
    private final InputManager inputManager;
    private final BitmapText metadataText;
    private final BitmapText menuText;
    private Camera camera;
    private ArrayList<AbstractPlayer> ships = new ArrayList<AbstractPlayer>();
    private ArrayList<IRace> enemyRaces = new ArrayList<IRace>();
    private LocalDateTime lastKeyPress;
    private IScreen nextScreen;
    private Logger log = Logger.getGlobal();
    private AbstractPlayer currentPlayer;
    private IRace currentRace;

    public MainMenu(
            AssetManager assetManager,
            IPlayer player,
            Camera camera,
            PlayerFactory playerFactory,
            Vector2f screenSize,
            InputManager inputManager,
            RaceFactory raceFactory) {
        super(assetManager, player, camera);
        this.camera = camera;
        this.screenSize = screenSize;
        this.playerFactory = playerFactory;
        this.inputManager = inputManager;
        this.raceFactory = raceFactory;

        rootNode.attachChild(playersNode);
        rootNode.attachChild(enemiesNode);
        playersNode.setLocalTranslation(0f, 0f, 0f);
        enemiesNode.setLocalTranslation(0f, -verticalOffset, 0f);

        camera.setLocation(new Vector3f(0, 2.5f, 10f));
        camera.lookAt(new Vector3f(0, 0, 0), Vector3f.UNIT_Y);

        initialisePlayers(playerFactory);
        initialiseEnemies();

        guiNode.attachChild(new GuiElement(
            "main-menu",
            assetManager,
            new Vector2f(0f, 0f),
            new Vector2f(1f, 1f),
            screenSize,
            "Interface/main-menu.png").getPicture());

        BitmapFont okudaFont = assetManager.loadFont("Interface/Fonts/Okuda.fnt");
        ColorRGBA lcarsColour = new ColorRGBA(180f/255f, 120f/255f, 50f/255f, 1f);
        metadataText = new BitmapText(okudaFont, false);
        metadataText.setSize(okudaFont.getCharSet().getRenderedSize() * 1);
        metadataText.setText("");
        metadataText.setLocalTranslation(screenSize.getX() * 0.67f, screenSize.getY() * 0.205f, 1);
        metadataText.setColor(lcarsColour);
        menuText = new BitmapText(okudaFont, false);
        menuText.setSize(okudaFont.getCharSet().getRenderedSize() * 2);
        menuText.setText("CHOOSE YOUR SHIP");
        menuText.setColor(lcarsColour);
        menuText.setLocalTranslation(screenSize.getX() * 0.35f, screenSize.getY(), 1);
        guiNode.attachChild(metadataText);
        guiNode.attachChild(menuText);
    }

    private void initialiseEnemies() {
        int enemyCount = 0;
        for(RaceType raceType : RaceType.values()) {
            IRace race = raceFactory.create(raceType);
            enemyRaces.add(race);
            Spatial raceLogo = race.buildLogo();
            enemiesNode.attachChild(raceLogo);
            raceLogo.setLocalTranslation(enemyCount * horizontalOffset, 1.4f, 0f);
            enemyCount++;
        }
    }

    private void initialisePlayers(PlayerFactory playerFactory) {
        for(PlayerFactoryType type : PlayerFactoryType.values()) {
            ships.add(playerFactory.create(type, PlayerType.Enemy));
        }
        currentPlayer = ships.get(0);

        int shipCount = 0;
        for(IPlayer ship : ships) {
            playersNode.attachChild(ship.getRootNode());
            ship.getRootNode().setLocalTranslation(shipCount * horizontalOffset, 0f, 0f);
            shipCount++;
        }
    } 

    private enum MenuTrack {
        Players, EnemyRaces
    }

    @Override
    public void onAnalog(String name, float keyPressed, float tpf) {
        if(lastKeyPress != null && LocalDateTime.now().isBefore(lastKeyPress.plus(200, ChronoUnit.MILLIS))) {
            return;
        }
        lastKeyPress = LocalDateTime.now();

        Vector3f cameraLocation = camera.getLocation();
        log.info(String.format("camera location (xyz): %f, %f, %f", cameraLocation.x, cameraLocation.y, cameraLocation.z));
        MenuTrack currentMenuTrack = getMenuTrack(cameraLocation);
        float maxX = ((currentMenuTrack == MenuTrack.Players ? ships.size() : enemyRaces.size()) - 1) * horizontalOffset;

        if(name.equals(InputMappings.left) && cameraLocation.getX() > 0) {
            // Left - move the camera left
            camera.setLocation(new Vector3f(cameraLocation.getX() - horizontalOffset, cameraLocation.getY(), cameraLocation.getZ()));
        } else if (name.equals(InputMappings.right) && cameraLocation.getX() < maxX) {
            // Right - move the camera left
            camera.setLocation(new Vector3f(cameraLocation.getX() + horizontalOffset, cameraLocation.getY(), cameraLocation.getZ()));
        } else if(name.equals(InputMappings.cancel) && currentMenuTrack == MenuTrack.EnemyRaces) {
            // Esc/cancel - if on enemy track, return to player track
            camera.setLocation(new Vector3f(0f, cameraLocation.getY() + verticalOffset, cameraLocation.getZ()));
            menuText.setText("CHOOSE YOUR SHIP");
            camera.lookAt(new Vector3f(0, 0, 0), Vector3f.UNIT_Y);
        } else if(name.equals(InputMappings.select) && currentMenuTrack == MenuTrack.Players) {
            // Enter/select - if on player track move to enemy track
            setCurrentPlayer(cameraLocation);
            camera.setLocation(new Vector3f(0f, cameraLocation.getY() - verticalOffset, cameraLocation.getZ()));
            menuText.setText("CHOOSE YOUR ENEMY");
            camera.lookAt(new Vector3f(0, camera.getLocation().getY(), 0), Vector3f.UNIT_Y);
        } else if(name.equals(InputMappings.select) && currentMenuTrack == MenuTrack.EnemyRaces) {
            // Enter/select - if on enemy track then launch game
            GameOptions gameOptions = new GameOptions(currentPlayer.getPlayerFactoryType(), RaceType.Federation, currentRace.getRaceType());
            nextScreen = new Splash(
                    assetManager,
                    screenSize,
                    playerFactory,
                    inputManager,
                    camera,
                    Splash.NextScreen.BasicStarfield,
                    gameOptions,
                    raceFactory);
        }
    }

    private void setMetadata(MenuTrack currentMenuTrack) {
        if(currentMenuTrack == MenuTrack.Players) {
            setCurrentPlayer(camera.getLocation());
            PlayerMetadata metadata = PlayerMetadata.fromPlayerFactoryType(currentPlayer.getPlayerFactoryType());
            metadataText.setText(metadata.getFormattedMetadata());
        } else if (currentMenuTrack == MenuTrack.EnemyRaces) {
            setCurrentRace();
            metadataText.setText(currentRace.getFormattedMetadata());
        }
    }

    private void setCurrentRace() {
        float raceIndex = camera.getLocation().getX() / horizontalOffset;
        if (raceIndex >= 0 && raceIndex < this.enemyRaces.size()) {
            currentRace = this.enemyRaces.get((int)raceIndex);
        }
    }

    private void setCurrentPlayer(Vector3f cameraLocation) {
        float playerIndex = cameraLocation.getX() / horizontalOffset;
        if (playerIndex >=0 && playerIndex < this.ships.size()) {
            log.info(String.format("current player index is: %s", playerIndex));
            currentPlayer = this.ships.get((int)playerIndex);
        }
    }

    private MenuTrack getMenuTrack(Vector3f cameraLocation) {
        if(cameraLocation.y > 0f) {
            return MenuTrack.Players;
        } else {
            return MenuTrack.EnemyRaces;
        }
    }

    @Override
    public IScreen getNextScreen() {
        return nextScreen;
    }
    
    @Override
    public void onUpdate(float tpf) {
        for(IPlayer ship : ships) {
            ship.getRootNode().rotate(0, tpf * 0.2f, 0);
        }

        setMetadata(getMenuTrack(camera.getLocation()));
    }
}
