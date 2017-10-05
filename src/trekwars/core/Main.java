package trekwars.core;

import com.jme3.app.SimpleApplication;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.TouchListener;
import com.jme3.input.controls.TouchTrigger;
import com.jme3.input.event.TouchEvent;
import com.jme3.renderer.RenderManager;
import java.lang.reflect.Array;
import java.util.ArrayList;
import trekwars.players.Brel;
import trekwars.players.IPlayer;
import trekwars.players.PlayerType;
import trekwars.players.Voyager;
import trekwars.screens.BasicStarfield;
import trekwars.screens.IScreen;

public class Main extends SimpleApplication implements TouchListener {

    private IScreen _screen;
    
    public static void main(String[] args) {
        Main app = new Main();
        app.setShowSettings(false);
        app.start();
    }

    @Override
    public void simpleInitApp() {
        flyCam.setEnabled(false);
        
        inputManager.addMapping("Touch", new TouchTrigger(0));
        inputManager.addMapping(InputMappings.left, new KeyTrigger(KeyInput.KEY_LEFT));
        inputManager.addMapping(InputMappings.right, new KeyTrigger(KeyInput.KEY_RIGHT));
        inputManager.addMapping(InputMappings.boost, new KeyTrigger(KeyInput.KEY_UP));
        inputManager.addMapping(InputMappings.stop, new KeyTrigger(KeyInput.KEY_DOWN));
        inputManager.addMapping(InputMappings.select, new KeyTrigger(KeyInput.KEY_RETURN));
        inputManager.addMapping(InputMappings.fire, new KeyTrigger(KeyInput.KEY_SPACE));
        
        inputManager.addListener(this, new String[] {"Touch"});
        inputManager.addListener(
                actionListener, 
                InputMappings.left,
                InputMappings.right,
                InputMappings.boost,
                InputMappings.stop,
                InputMappings.select,
                InputMappings.fire);

        ArrayList<IPlayer> waveOne = new ArrayList<IPlayer>();
        waveOne.add(new Brel(assetManager, PlayerType.Enemy));
        waveOne.add(new Brel(assetManager, PlayerType.Enemy));
        waveOne.add(new Brel(assetManager, PlayerType.Enemy));
        ArrayList<IPlayer> waveTwo = new ArrayList<IPlayer>();
        waveTwo.add(new Brel(assetManager, PlayerType.Enemy));
        waveTwo.add(new Brel(assetManager, PlayerType.Enemy));
        ArrayList<IPlayer> waveThree = new ArrayList<IPlayer>();
        waveThree.add(new Brel(assetManager, PlayerType.Enemy));
        setScreen(new BasicStarfield(
                new Brel(assetManager, PlayerType.Player), 
                waveOne, 
                waveTwo, 
                waveThree, 
                assetManager, 
                cam));
    }
    
    @Override
    public void onTouch(String binding, TouchEvent evt, float tpf) {
        _screen.onTouch(evt, tpf, this.settings.getWidth(), this.settings.getHeight());
        evt.setConsumed();
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
