package trekwars.screens;

import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioNode;
import com.jme3.audio.AudioSource;
import com.jme3.input.InputManager;
import com.jme3.input.event.TouchEvent;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Logger;

import trekwars.core.InputMappings;
import trekwars.players.IPlayer;

public class BasicStarfield extends AbstractStarfield {
    private final IPlayer player;
    private final ArrayList<IPlayer> enemyWaveOne;
    private final ArrayList<IPlayer> enemyWaveTwo;
    private final ArrayList<IPlayer> enemyWaveThree;
    private final float cameraZDistance = 10f;
    private final float cameraYDistance = 3f;
    private final float enemyWaveOneZ = -50f;
    private final float enemyWaveTwoZ = -75f;
    private final float enemyWaveThreeZ = -100f;
    private final InputManager inputManager;
    private final Vector2f screenSize;
    private final AssetManager assetManager;
    private Button leftButton;
    private Button rightButton;
    private Button fireButton;
    private final AudioNode audioNode;
    private final ArrayList<TouchEvent> touchEvents = new ArrayList<TouchEvent>();
    private boolean isTouchLeft = false;
    private boolean isTouchRight = false;
    private boolean isTouchFire = false;
    private Logger log = Logger.getGlobal();

    public BasicStarfield(
            IPlayer player, 
            Iterable<IPlayer> enemyWaveOne,
            Iterable<IPlayer> enemyWaveTwo,
            Iterable<IPlayer> enemyWaveThree,
            AssetManager assetManager,
            Camera camera,
            InputManager inputManager,
            Vector2f screenSize){
        super(assetManager, player, camera);
        log.info("Starting basic starfield");

        this.player = player;
        this.enemyWaveOne = new ArrayList<IPlayer>((Collection<? extends IPlayer>) enemyWaveOne);
        this.enemyWaveTwo = new ArrayList<IPlayer>((Collection<? extends IPlayer>) enemyWaveTwo);
        this.enemyWaveThree = new ArrayList<IPlayer>((Collection<? extends IPlayer>) enemyWaveThree);
        this.inputManager = inputManager;
        this.screenSize = screenSize;
        this.assetManager = assetManager;

        log.info("Attaching and arranging children");
        attachChildren(player, enemyWaveOne, enemyWaveTwo, enemyWaveThree);
        arrangeEnemyWave(this.enemyWaveOne, enemyWaveOneZ);
        arrangeEnemyWave(this.enemyWaveTwo, enemyWaveTwoZ);
        arrangeEnemyWave(this.enemyWaveThree, enemyWaveThreeZ);
        
        audioNode = new AudioNode(assetManager, "Sounds/federation-theme.ogg", true);
        audioNode.setPositional(false);
        audioNode.setVolume(0.5f);
        rootNode.attachChild(audioNode);
        

        log.info("Initialising hud");
        initialiseHud();

        log.info("Basic starfield loaded");
    }
    
    private boolean isTouchDown(TouchEvent.Type evt) {
        return 
                evt == TouchEvent.Type.MOVE ||
                evt == TouchEvent.Type.SCROLL ||
                evt == TouchEvent.Type.SCALE_START ||
                evt == TouchEvent.Type.SCALE_MOVE ||
                evt == TouchEvent.Type.TAP ||
                evt == TouchEvent.Type.SHOWPRESS ||
                evt == TouchEvent.Type.DOWN;
    }
    
    public void onTouch(TouchEvent evt, float tpf, float screenWidth, float screenHeight) {
        touchEvents.add(evt);
    }
    
    private void handleTouch() {
        boolean leftIsHandled = false;
        boolean rightIsHandled = false;
        boolean fireIsHandled = false;
        
        for(TouchEvent evt : touchEvents) {
             System.out.println(String.format("SZYK: %s", evt.getType()));

             if(touchIsLeft(evt)) {
                 if(!leftIsHandled) {
                    isTouchLeft = isTouchDown(evt.getType());
                    leftIsHandled = !isTouchLeft;
                 }
             } else if(touchIsFire(evt)) {
                 if (!fireIsHandled) {
                     isTouchFire = isTouchDown(evt.getType());
                     fireIsHandled = !isTouchFire;
                 }
             } else if(!rightIsHandled) {
                 isTouchRight = isTouchDown(evt.getType());
                 rightIsHandled = !isTouchRight;
             }
         }
        
        if(isTouchLeft) player.turnLeft();
        if(isTouchRight) player.turnRight();
        if(isTouchFire) player.fire();
        
        touchEvents.clear();
    }
    
    public void start() {
        audioNode.play();
    }
    
    public IScreen getNextScreen() {
        return null;
    }
    
    private void initialiseHud() {
        guiNode.attachChild(new GuiElement(
                "top-left", 
                assetManager, 
                new Vector2f(0f, 0.9f), 
                new Vector2f(0.25f, 1f), 
                screenSize, 
                "Interface/lcars-top-left.png").getPicture());
        
        guiNode.attachChild(new GuiElement(
                "top-right", 
                assetManager, 
                new Vector2f(0.75f, 0.9f), 
                new Vector2f(1f, 1f), 
                screenSize, 
                "Interface/lcars-top-right.png").getPicture());
        
        guiNode.attachChild(new GuiElement(
                "top-middle", 
                assetManager, 
                new Vector2f(0.26f, 0.98f), 
                new Vector2f(0.74f, 1f),
                screenSize, 
                "Interface/lcars-box-inactive.png").getPicture());
        
        guiNode.attachChild(new GuiElement(
                "bottom-left", 
                assetManager, 
                new Vector2f(0f, 0f), 
                new Vector2f(0.25f, 0.1f),
                screenSize, 
                "Interface/lcars-bottom-left.png").getPicture());
        
        guiNode.attachChild(new GuiElement(
                "bottom-right", 
                assetManager, 
                new Vector2f(0.75f, 0f), 
                new Vector2f(1f, 0.1f),
                screenSize, 
                "Interface/lcars-bottom-right.png").getPicture());
        
        leftButton = new Button(
                "left-button",
                "Interface/lcars-box-inactive.png",
                "Interface/lcars-box-active.png",
                new Vector2f(0f, 0.11f), 
                new Vector2f(0.11f, 0.89f),
                assetManager,
                screenSize);
        guiNode.attachChild(leftButton.getPicture());
        
        rightButton = new Button(
                "right-button",
                "Interface/lcars-box-inactive.png",
                "Interface/lcars-box-active.png",
                new Vector2f(0.89f, 0.11f), 
                new Vector2f(1f, 0.89f),
                assetManager,
                screenSize);
        guiNode.attachChild(rightButton.getPicture());
        
        fireButton = new Button(
                "fire-button",
                "Interface/lcars-fire-inactive.png",
                "Interface/lcars-fire-active.png",
                new Vector2f(0.26f, 0f), 
                new Vector2f(0.74f, 0.1f),
                assetManager,
                screenSize);
        guiNode.attachChild(fireButton.getPicture());
    }
    
    private void arrangeEnemyWave(ArrayList<IPlayer> enemyWave, float z) {
        if(enemyWave == null) return;
        
        int shipCount = enemyWave.size();
        float averageWidth = getAverageWidth(enemyWaveOne);
        int shipIndex = 0;
        for(IPlayer ship : enemyWave) {
            float x = -(shipCount - 1) * averageWidth + shipIndex * averageWidth * 2;
            ship.getRootNode().setLocalTranslation(x,0,z);
            Vector3f target = player.getRootNode().getWorldTranslation();
            ship.getRootNode().rotate(0f, (float) Math.PI, 0f);
            shipIndex++;
        }
    }
    
    private float getAverageWidth(Iterable<IPlayer> players) {
        float sumOfWidths = 0f;
        float numberOfWidths = 0;
        sumOfWidths = 10;
        numberOfWidths = 1;
        
        return sumOfWidths / numberOfWidths;
    }
    
    private void attachRootNodes(Iterable<IPlayer> players){
        if(players == null) return;
        for(IPlayer player : players){
            for(Node node : player.getRootNodes()){
                rootNode.attachChild(node);
            }
        }
    }

    @Override
    protected void onUpdate(float tpf) {
        handleTouch();
        player.update(tpf);
        updatePlayers(enemyWaveOne, tpf);
        updatePlayers(enemyWaveTwo, tpf);
        updatePlayers(enemyWaveThree, tpf);
        positionCamera();     
        
        if(audioNode.getStatus() != AudioSource.Status.Playing) {
            audioNode.play();
        }
    }
    
    private void positionCamera(){
        float playerYRadians = player.getRootNode().getWorldRotation().toAngles(null)[1];
        Vector3f playerWorldTranslation = player.getRootNode().getWorldTranslation();
        double cameraZ = playerWorldTranslation.getZ() + (cameraZDistance * Math.cos(playerYRadians));
        double cameraX = playerWorldTranslation.getX() + (cameraZDistance * Math.sin(playerYRadians));
        
        camera.setLocation(new Vector3f((float)cameraX, (float)cameraYDistance, (float)cameraZ));
        camera.lookAt(playerWorldTranslation, Vector3f.UNIT_Y);
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
            player.turnLeft();
        }
        else if (name.equals(InputMappings.right)) {
            player.turnRight();
        }
        else if (name.equals(InputMappings.boost)) {
            player.boost();
        }
        else if (name.equals(InputMappings.fire)){
            player.fire();
        }
        else if(name.equals(InputMappings.stop)) {
            player.stop();
        }
        else if(name.equals(InputMappings.left_click)) {
            if(mouseIsLeft()) {
                player.turnLeft();
            } else if (mouseIsFire()) {
                player.fire();
            } else {
                player.turnRight();
            }
        }
    }
    
    private boolean touchIsLeft(TouchEvent evt) {
        return 
                evt.getX() < screenSize.getX() / 2 && 
                evt.getY() > screenSize.getY() / 5;
    }
    
    private boolean touchIsFire(TouchEvent evt) {
        return evt.getY() <= screenSize.getY() / 5;
    }
    
    private boolean mouseIsLeft() {
        return 
                inputManager.getCursorPosition().getX() < screenSize.getX() / 2 && 
                inputManager.getCursorPosition().getY() > screenSize.getY() / 5;
    }
    
    private boolean mouseIsFire() {
        return inputManager.getCursorPosition().getY() <= screenSize.getY() / 5;
    }
    
    public void onAction(String name, boolean keyPressed, float tpf) {
        if(name == null) return;
        if(name.equals(InputMappings.left_click)) {
            if(keyPressed) {
                if(mouseIsLeft()) {
                    leftButton.activate();
                    rightButton.deactivate();
                    fireButton.deactivate();
                } else if(mouseIsFire()) {
                    fireButton.activate();
                    leftButton.deactivate();
                    rightButton.deactivate();
                } else {
                    rightButton.activate();
                    leftButton.deactivate();
                    fireButton.deactivate();
                }
            } else {
                if(mouseIsLeft()) {
                    leftButton.deactivate();
                } else if(mouseIsFire()) {
                    fireButton.deactivate();
                } else {
                    rightButton.deactivate();
                }
            }
        }
    }

    private void attachChildren(
            IPlayer player, 
            Iterable<IPlayer> enemyWaveOne, 
            Iterable<IPlayer> enemyWaveTwo, 
            Iterable<IPlayer> enemyWaveThree) {
        for(Node node : player.getRootNodes()) {
            rootNode.attachChild(node);
        }
        attachRootNodes(enemyWaveOne);
        attachRootNodes(enemyWaveTwo);
        attachRootNodes(enemyWaveThree);
    }
}
