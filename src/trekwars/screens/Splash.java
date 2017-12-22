package trekwars.screens;

import com.jme3.asset.AssetManager;
import com.jme3.input.InputManager;
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
import trekwars.players.AbstractPlayer;
import trekwars.players.IPlayer;
import trekwars.players.PlayerFactory;
import trekwars.players.PlayerFactoryType;
import trekwars.players.PlayerType;

public class Splash implements Callable<IScreen>, IScreen {
    private final Node _guiNode;
    private final Node _rootNode;
    private final AssetManager _assetManager;
    private final Vector2f _screenSize;
    private final PlayerFactory _playerFactory;
    private final InputManager _inputManager;
    private final Camera _camera;
    private Future<IScreen> _nextScreen;
    private final ExecutorService _executor;
    
    public Splash(
            AssetManager assetManager, 
            Vector2f screenSize,
            PlayerFactory playerFactory,
            InputManager inputManager,
            Camera camera) {
        _assetManager = assetManager;
        _screenSize = screenSize;
        _playerFactory = playerFactory;
        _camera = camera;
        _inputManager = inputManager;
        
        _guiNode = new Node();
        _rootNode = new Node();
        _guiNode.attachChild(new GuiElement(
                "splash", 
                assetManager, 
                new Vector2f(0f, 0f), 
                new Vector2f(1f, 1f), 
                screenSize, 
                "Interface/splash.jpg").getPicture());
        
        AmbientLight al = new AmbientLight();
        al.setColor(ColorRGBA.White.mult(1.3f));
        _rootNode.addLight(al);
        
        _executor = Executors.newSingleThreadExecutor();
        _nextScreen = _executor.submit(this);
    }

    public Node getGuiNode() {
        return _guiNode;
    }
    
    public void update(float tpf) { }

    public Node getRootNode() {
        return _rootNode;
    }

    public void onAnalog(String name, float keyPressed, float tpf) { }

    public void onAction(String name, boolean keyPressed, float tpf) { }
    
    public IScreen getNextScreen() {
        if(_nextScreen == null) {
            return null;
        } else if(_nextScreen.isDone()) {
            try {
                return _nextScreen.get();
            } catch (InterruptedException ex) { 
                _nextScreen = null;
            } catch (ExecutionException ex) {
                _nextScreen = null;
            }
            _executor.shutdown();
            return null;
        } else {
            return null;
        }
    }
    
    public void start() { }
    
    @Override
    public IScreen call() throws InterruptedException {
        AbstractPlayer player = _playerFactory.create(PlayerFactoryType.Voyager, PlayerType.Player);
        ArrayList<IPlayer> waveOne = new ArrayList<IPlayer>();
        waveOne.add(_playerFactory.create(PlayerFactoryType.Brel, PlayerType.Enemy));
        waveOne.add(_playerFactory.create(PlayerFactoryType.Brel, PlayerType.Enemy));
        waveOne.add(_playerFactory.create(PlayerFactoryType.Brel, PlayerType.Enemy));
        ArrayList<IPlayer> waveTwo = new ArrayList<IPlayer>();
        waveTwo.add(_playerFactory.create(PlayerFactoryType.Brel, PlayerType.Enemy));
        waveTwo.add(_playerFactory.create(PlayerFactoryType.Brel, PlayerType.Enemy));
        ArrayList<IPlayer> waveThree = new ArrayList<IPlayer>();
        waveThree.add(_playerFactory.create(PlayerFactoryType.Brel, PlayerType.Enemy));
        Thread.sleep(2000);
        return new BasicStarfield(
                player, 
                waveOne, 
                waveTwo, 
                waveThree, 
                _assetManager, 
                _camera,
                _inputManager,
                _screenSize);
    }   
}
