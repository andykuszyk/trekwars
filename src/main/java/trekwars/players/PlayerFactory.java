package trekwars.players;

import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioNode;
import com.jme3.renderer.Camera;
import com.jme3.texture.Texture;
import java.util.ArrayList;

public class PlayerFactory {
    private final AssetManager _assetManager;
    private final Camera _camera;
    private final ArrayList<Texture> _explosionTextures;
    private final IPlayerController _playerController;
    private final AudioNode _explosionNode;
    
    public PlayerFactory(
            AssetManager assetManager,
            Camera camera,
            ArrayList<Texture> explosionTextures,
            IPlayerController playerController,
            AudioNode explosionNode) {
        _assetManager = assetManager;
        _camera = camera;
        _explosionTextures = explosionTextures;
        _playerController = playerController;
        _explosionNode = explosionNode;
    }

    public AbstractPlayer create(PlayerFactoryType type, PlayerType playerType) {
        if(type == PlayerFactoryType.Brel) {
            return new Brel(
                    _assetManager, 
                    playerType, 
                    _playerController, 
                    _explosionTextures, 
                    _camera,
                    _explosionNode);
        } else if (type == PlayerFactoryType.Voyager) {
            return new Voyager(
                    _assetManager, 
                    playerType, 
                    _playerController, 
                    _explosionTextures, 
                    _camera,
                    _explosionNode);
        } else {
            return null;
        }
    }
}
