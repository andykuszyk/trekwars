package trekwars.screens;

import com.jme3.asset.AssetManager;
import com.jme3.input.InputManager;
import com.jme3.input.event.TouchEvent;
import com.jme3.light.AmbientLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Logger;

import trekwars.players.AbstractPlayer;
import trekwars.players.IPlayer;
import trekwars.players.PlayerFactory;
import trekwars.players.PlayerFactoryType;
import trekwars.players.PlayerType;
import trekwars.races.RaceFactory;
import trekwars.races.IRace;

public class Splash implements Callable<IScreen>, IScreen {
    private final Node guiNode;
    private final Node rootNode;
    private final AssetManager assetManager;
    private final Vector2f screenSize;
    private final PlayerFactory playerFactory;
    private final RaceFactory raceFactory;
    private final InputManager inputManager;
    private final Camera camera;
    private final GameOptions gameOptions;
    private Future<IScreen> nextScreen;
    private final ExecutorService executor;
    private final NextScreen nextScreenSelection;
    private Logger log = Logger.getGlobal();

    public enum NextScreen {
        BasicStarfield, MainMenu
    }
    
    public Splash(
            AssetManager assetManager, 
            Vector2f screenSize,
            PlayerFactory playerFactory,
            InputManager inputManager,
            Camera camera,
            NextScreen nextScreen,
            GameOptions gameOptions,
            RaceFactory raceFactory) {
        this.assetManager = assetManager;
        this.screenSize = screenSize;
        this.playerFactory = playerFactory;
        this.camera = camera;
        this.inputManager = inputManager;
        this.gameOptions = gameOptions;
        this.raceFactory = raceFactory;
        nextScreenSelection = nextScreen;
        guiNode = new Node();
        rootNode = new Node();
        guiNode.attachChild(new GuiElement(
                "splash", 
                assetManager, 
                new Vector2f(0f, 0f), 
                new Vector2f(1f, 1f), 
                screenSize, 
                "Interface/splash.jpg").getPicture());
        
        AmbientLight al = new AmbientLight();
        al.setColor(ColorRGBA.White.mult(1.3f));
        rootNode.addLight(al);
        
        executor = Executors.newSingleThreadExecutor();
        this.nextScreen = executor.submit(this);
    }
    
    public void onTouch(TouchEvent evt, float tpf, float screenWidth, float screenHeight) {
    
    }

    public Node getGuiNode() {
        return guiNode;
    }
    
    public void update(float tpf) { }

    public Node getRootNode() {
        return rootNode;
    }

    public void onAnalog(String name, float keyPressed, float tpf) { }

    public void onAction(String name, boolean keyPressed, float tpf) { }
    
    public IScreen getNextScreen() {
        if(nextScreen == null) {
            return null;
        } else if(nextScreen.isDone()) {
            try {
                return nextScreen.get();
            } catch (InterruptedException ex) { 
                nextScreen = null;
            } catch (ExecutionException ex) {
                nextScreen = null;
            }
            executor.shutdown();
            return null;
        } else {
            return null;
        }
    }
    
    public void start() { }
    
    @Override
    public IScreen call() throws InterruptedException {
        try {
            log.info("About to load next screen: " + nextScreenSelection.toString());
            if (nextScreenSelection == NextScreen.BasicStarfield) {
                AbstractPlayer player = playerFactory.create(gameOptions.getPlayerFactoryType(), PlayerType.Player);
                IRace enemyRace = raceFactory.create(gameOptions.getEnemyRace());
                log.info("Loaded the screen, waiting for 1 seconds");
                Thread.sleep(1000);
                return enemyRace.buildEnvironment(player, playerFactory, camera, inputManager, screenSize);
            } else if (nextScreenSelection == NextScreen.MainMenu) {
                return new MainMenu(assetManager, null, camera, playerFactory, screenSize, inputManager, raceFactory);
            } else {
                return null;
            }
        } finally {
            executor.shutdown();
        }
    }
}
