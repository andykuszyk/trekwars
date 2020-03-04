package trekwars.players;

import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioNode;
import com.jme3.renderer.Camera;
import com.jme3.texture.Texture;
import java.util.ArrayList;

public class PlayerFactory {
    private final AssetManager assetManager;
    private final Camera camera;
    private final ArrayList<Texture> explosionTextures;
    private final IPlayerController playerController;
    private final AudioNode explosionNode;
    
    public PlayerFactory(
            AssetManager assetManager,
            Camera camera,
            ArrayList<Texture> explosionTextures,
            IPlayerController playerController,
            AudioNode explosionNode) {
        this.assetManager = assetManager;
        this.camera = camera;
        this.explosionTextures = explosionTextures;
        this.playerController = playerController;
        this.explosionNode = explosionNode;
    }

    public AbstractPlayer create(PlayerFactoryType type, PlayerType playerType) {
        if(type == PlayerFactoryType.Brel) {
            return new Brel(
                    assetManager, 
                    playerType, 
                    playerController, 
                    explosionTextures, 
                    camera,
                    explosionNode);
        } else if (type == PlayerFactoryType.Voyager) {
            return new Voyager(
                    assetManager, 
                    playerType, 
                    playerController, 
                    explosionTextures, 
                    camera,
                    explosionNode);
        } else {
            return null;
        }
    }
}
