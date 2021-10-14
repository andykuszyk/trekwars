package trekwars.races;

import java.util.ArrayList;
import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Quad;
import com.jme3.texture.Texture;
import com.jme3.scene.Spatial;
import com.jme3.input.InputManager;
import com.jme3.renderer.Camera;
import com.jme3.math.Vector2f;
import trekwars.players.IPlayer;
import trekwars.players.PlayerFactoryType;
import trekwars.players.PlayerFactory;
import trekwars.players.PlayerType;
import trekwars.screens.BasicStarfield;
import trekwars.screens.IScreen;

public abstract class AbstractRace implements IRace {
    private final AssetManager assetManager;
    private final float logoSize = 3.5f;

    public AbstractRace(AssetManager assetManager) {
        this.assetManager = assetManager;
    } 

    protected abstract String getLogo();
    protected abstract String getName();
    protected abstract String getUniverse();

    public String getFormattedMetadata() {
        return String.format("Race      : %s\nUniverse : %s", getName(), getUniverse());
    }

    public abstract RaceType getRaceType();

    public Spatial buildLogo() {
        Material material = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        Texture texture = assetManager.loadTexture("Interface/" + getLogo());
        texture.setWrap(Texture.WrapMode.Repeat);
        material.setTexture("ColorMap", texture);
        Geometry front = new Geometry(getLogo(), new Quad(logoSize, logoSize * 1.4f));
        front.setMaterial(material);
        Geometry back = front.clone();
        Node node = new Node();
        node.attachChild(front);
        node.attachChild(back);
        front.setLocalTranslation(-logoSize / 2, -logoSize / 2, 0f);
        back.rotate(0, (float)Math.PI, 0);
        back.setLocalTranslation(logoSize / 2, -logoSize / 2, 0f);
        return node;
    }

    public IScreen buildEnvironment(
        IPlayer player,
        PlayerFactory playerFactory,
        Camera camera,
        InputManager inputManager,
        Vector2f screenSize) {
        ArrayList<IPlayer> waveOne = new ArrayList<IPlayer>();
        waveOne.add(playerFactory.create(PlayerFactoryType.Brel, PlayerType.Enemy));
        waveOne.add(playerFactory.create(PlayerFactoryType.Brel, PlayerType.Enemy));
        waveOne.add(playerFactory.create(PlayerFactoryType.Brel, PlayerType.Enemy));

        ArrayList<IPlayer> waveTwo = new ArrayList<IPlayer>();
        waveTwo.add(playerFactory.create(PlayerFactoryType.Brel, PlayerType.Enemy));
        waveTwo.add(playerFactory.create(PlayerFactoryType.Brel, PlayerType.Enemy));

        ArrayList<IPlayer> waveThree = new ArrayList<IPlayer>();
        waveThree.add(playerFactory.create(PlayerFactoryType.Brel, PlayerType.Enemy));

        return new BasicStarfield(player, waveOne, waveTwo, waveThree, assetManager, camera, inputManager, screenSize);
    }
}
