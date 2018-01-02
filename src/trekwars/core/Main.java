package trekwars.core;

import com.jme3.app.SimpleApplication;
import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioNode;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.input.controls.TouchListener;
import com.jme3.input.controls.TouchTrigger;
import com.jme3.input.event.TouchEvent;
import com.jme3.math.Vector2f;
import com.jme3.renderer.RenderManager;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture.WrapMode;
import java.util.ArrayList;
import trekwars.players.IPlayerController;
import trekwars.players.PlayerController;
import trekwars.players.PlayerFactory;
import trekwars.screens.IScreen;
import trekwars.screens.Splash;

public class Main extends SimpleApplication implements TouchListener {

    private IScreen _screen;
    
    public static void main(String[] args) {
        Main app = new Main();
        app.setShowSettings(false);
        app.start();
        app.settings.setFullscreen(true);
    }

    @Override
    public void simpleInitApp() {
        flyCam.setEnabled(false);
        this.setDisplayStatView(false);
//        this.setDisplayFps(false);
        Vector2f screenSize = new Vector2f(this.settings.getWidth(), this.settings.getHeight());
        initialiseInput();
        
        setScreen(new Splash(assetManager, screenSize, getPlayerFactory(), inputManager, cam));
    }
    
    public PlayerFactory getPlayerFactory() {
        ArrayList<Texture> explosionTextures = loadExplosionTextures(assetManager);
        IPlayerController playerController = new PlayerController();
        AudioNode explosionNode = new AudioNode(assetManager, "Sounds/explosion.wav", false);
        return new PlayerFactory(
                assetManager, 
                cam, 
                explosionTextures, 
                playerController, 
                explosionNode);
    }
    
    private void initialiseInput() {
        inputManager.addMapping("Touch", new TouchTrigger(0));
        inputManager.addMapping(InputMappings.left, new KeyTrigger(KeyInput.KEY_LEFT));
        inputManager.addMapping(InputMappings.right, new KeyTrigger(KeyInput.KEY_RIGHT));
        inputManager.addMapping(InputMappings.boost, new KeyTrigger(KeyInput.KEY_UP));
        inputManager.addMapping(InputMappings.stop, new KeyTrigger(KeyInput.KEY_DOWN));
        inputManager.addMapping(InputMappings.select, new KeyTrigger(KeyInput.KEY_RETURN));
        inputManager.addMapping(InputMappings.fire, new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addMapping(InputMappings.left_click, new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        
        inputManager.addListener(
                actionListener, 
                InputMappings.left,
                InputMappings.right,
                InputMappings.boost,
                InputMappings.stop,
                InputMappings.select,
                InputMappings.fire,
                InputMappings.left_click);
        
        inputManager.addListener(
                analogListener, 
                InputMappings.left,
                InputMappings.right,
                InputMappings.boost,
                InputMappings.stop,
                InputMappings.select,
                InputMappings.fire,
                InputMappings.left_click);
        
        inputManager.addListener(this, new String[] {"Touch"});
    }
    
    @Override
     public void onTouch(String binding, TouchEvent evt, float tpf) {
         _screen.onTouch(evt, tpf, this.settings.getWidth(), this.settings.getHeight());
         evt.setConsumed();
     }
    
    private ArrayList<Texture> loadExplosionTextures(AssetManager assetManager) {
        ArrayList<Texture> textures = new ArrayList<Texture>();
        for(int i = 0; i <= 273; i++) {
            String number;
            if (i < 10) {
                number = String.format("000%d", i);
            } else if (i < 100) {
                number = String.format("00%d", i);
            } else {
                number = String.format("0%d", i);
            }
            Texture texture = assetManager.loadTexture(String.format(
                    "Textures/explosion/explosion%s.png",
                    number
                    ));
            texture.setWrap(WrapMode.Repeat);
            textures.add(texture);
        }
        return textures;
    }
    
    private AnalogListener analogListener = new AnalogListener() {
        public void onAnalog(String name, float keyPressed, float tpf) {
            _screen.onAnalog(name, keyPressed, tpf);
        }
    };
    
    private ActionListener actionListener = new ActionListener() {
        public void onAction(String name, boolean keyPressed, float tpf) {
            _screen.onAction(name, keyPressed, tpf);
        }
    };

    @Override
    public void simpleUpdate(float tpf) {
        if(_screen == null) {
            throw new IllegalStateException("_screen cannot be null");
        }
        
        _screen.update(tpf);
        IScreen nextScreen = _screen.getNextScreen();
        if(nextScreen != null) {
            setScreen(nextScreen);
        }
    }
    
    private void setScreen(IScreen screen) {
        _screen = screen;
        rootNode.detachAllChildren();
        guiNode.detachAllChildren();
        rootNode.attachChild(_screen.getRootNode());
        guiNode.attachChild(_screen.getGuiNode());
        _screen.start();
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }
}
