package trekwars.players;

import com.jme3.asset.AssetManager;
import com.jme3.renderer.Camera;
import com.jme3.texture.Texture;
import java.util.ArrayList;

public class PlayerFactory {
    private final AssetManager _assetManager;
    private final Camera _camera;
    private final ArrayList<Texture> _explosionTextures;
    private final IPlayerController _playerController;
    
    public PlayerFactory(
            AssetManager assetManager,
            Camera camera,
            ArrayList<Texture> explosionTextures,
            IPlayerController playerController) {
        _assetManager = assetManager;
        _camera = camera;
        _explosionTextures = explosionTextures;
        _playerController = playerController;
    }

    public AbstractPlayer create(PlayerFactoryType type, PlayerType playerType) {
        if(type == PlayerFactoryType.Brel) {
            return new Brel(
                    _assetManager, 
                    playerType, 
                    _playerController, 
                    _explosionTextures, 
                    _camera);
        } else if (type == PlayerFactoryType.Voyager) {
            return new Voyager(
                    _assetManager, 
                    playerType, 
                    _playerController, 
                    _explosionTextures, 
                    _camera);
        } else {
            return null;
        }
    }
}
