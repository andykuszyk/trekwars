package trekwars.players;

import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioNode;
import com.jme3.renderer.Camera;
import com.jme3.texture.Texture;
import trekwars.players.borg.Cube;
import trekwars.players.borg.TacticalCube;
import trekwars.players.dominion.Galor;
import trekwars.players.federation.Defiant;
import trekwars.players.federation.EnterpriseE;
import trekwars.players.federation.Prometheus;
import trekwars.players.federation.Voyager;
import trekwars.players.klingon.Brel;

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
        switch(type){
            case Brel:
                return new Brel(
                        assetManager,
                        playerType,
                        playerController,
                        explosionTextures,
                        camera,
                        explosionNode);
            case Voyager:
                return new Voyager(
                        assetManager,
                        playerType,
                        playerController,
                        explosionTextures,
                        camera,
                        explosionNode);
            case Defiant:
                return new Defiant(
                        assetManager,
                        playerType,
                        playerController,
                        explosionTextures,
                        camera,
                        explosionNode);
            case EnterpriseE:
                return new EnterpriseE(
                        assetManager,
                        playerType,
                        playerController,
                        explosionTextures,
                        camera,
                        explosionNode);
            case Prometheus:
                return new Prometheus(
                        assetManager,
                        playerType,
                        playerController,
                        explosionTextures,
                        camera,
                        explosionNode);
            case BorgCube:
                return new Cube(
                        assetManager,
                        playerType,
                        playerController,
                        explosionTextures,
                        camera,
                        explosionNode);
            case BorgTacticalCube:
                return new TacticalCube(
                        assetManager,
                        playerType,
                        playerController,
                        explosionTextures,
                        camera,
                        explosionNode);
            case CardassianGalor:
                return new Galor(
                        assetManager,
                        playerType,
                        playerController,
                        explosionTextures,
                        camera,
                        explosionNode);
            default:
                return null;
        }
    }
}
