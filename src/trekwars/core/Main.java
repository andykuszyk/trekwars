package trekwars.core;

import com.jme3.app.SimpleApplication;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.renderer.RenderManager;
import java.lang.reflect.Array;
import java.util.ArrayList;
import trekwars.players.IPlayer;
import trekwars.players.Voyager;
import trekwars.screens.BasicStarfield;
import trekwars.screens.IScreen;

public class Main extends SimpleApplication {

    private IScreen _screen;
    
    public static void main(String[] args) {
        Main app = new Main();
        app.setShowSettings(false);
        app.start();
    }

    @Override
    public void simpleInitApp() {
        flyCam.setEnabled(false);
        
        inputManager.addMapping(InputMappings.left, new KeyTrigger(KeyInput.KEY_LEFT));
        inputManager.addMapping(InputMappings.right, new KeyTrigger(KeyInput.KEY_RIGHT));
        inputManager.addMapping(InputMappings.boost, new KeyTrigger(KeyInput.KEY_UP));
        inputManager.addMapping(InputMappings.select, new KeyTrigger(KeyInput.KEY_RETURN));
        inputManager.addMapping(InputMappings.fire, new KeyTrigger(KeyInput.KEY_SPACE));
        
        inputManager.addListener(
                actionListener, 
                InputMappings.left,
                InputMappings.right,
                InputMappings.boost,
                InputMappings.select,
                InputMappings.fire);

        ArrayList<IPlayer> waveOne = new ArrayList<IPlayer>();
        waveOne.add(new Voyager(assetManager));
        waveOne.add(new Voyager(assetManager));
        waveOne.add(new Voyager(assetManager));
        ArrayList<IPlayer> waveTwo = new ArrayList<IPlayer>();
        waveTwo.add(new Voyager(assetManager));
        waveTwo.add(new Voyager(assetManager));
        ArrayList<IPlayer> waveThree = new ArrayList<IPlayer>();
        waveThree.add(new Voyager(assetManager));
        setScreen(new BasicStarfield(
                new Voyager(assetManager), 
                waveOne, 
                waveTwo, 
                waveThree, 
                assetManager, 
                cam));
    }
    
    private AnalogListener actionListener = new AnalogListener() {
        public void onAnalog(String name, float keyPressed, float tpf) {
            _screen.onAnalog(name, keyPressed, tpf);
        }
    };

    @Override
    public void simpleUpdate(float tpf) {
        if(_screen == null) {
            throw new IllegalStateException("_screen cannot be null");
        }
        
        _screen.update(tpf);
    }
    
    private void setScreen(IScreen screen) {
        _screen = screen;
        rootNode.detachAllChildren();
        rootNode.attachChild(_screen.getRootNode());
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }
}
