package trekwars.races;

import java.util.ArrayList;
import com.jme3.asset.AssetManager;
import com.jme3.renderer.Camera;
import com.jme3.input.InputManager;
import com.jme3.math.Vector2f;
import trekwars.players.PlayerMetadata;
import trekwars.players.PlayerType;
import trekwars.players.PlayerFactoryType;
import trekwars.players.IPlayer;
import trekwars.players.PlayerFactory;
import trekwars.screens.IScreen;
import trekwars.screens.BasicStarfield;

public class Federation extends AbstractRace {
    public Federation(AssetManager assetManager) {
        super(assetManager);
    }

    public RaceType getRaceType() {
        return RaceType.Federation;
    }

    protected String getLogo() {
        return "federation-logo.jpg";
    }

    protected String getName() {
        return "Federation";
    }

    protected String getUniverse() {
        return PlayerMetadata.STARTREK;
    }

    @Override
    public IScreen buildEnvironment(
        IPlayer player,
        PlayerFactory playerFactory,
        Camera camera,
        InputManager inputManager,
        Vector2f screenSize) {

        ArrayList<IPlayer> waveOne = new ArrayList<IPlayer>();
        waveOne.add(playerFactory.create(PlayerFactoryType.Defiant, PlayerType.Enemy));
        waveOne.add(playerFactory.create(PlayerFactoryType.Defiant, PlayerType.Enemy));
        waveOne.add(playerFactory.create(PlayerFactoryType.Defiant, PlayerType.Enemy));

        ArrayList<IPlayer> waveTwo = new ArrayList<IPlayer>();
        waveTwo.add(playerFactory.create(PlayerFactoryType.Prometheus, PlayerType.Enemy));
        waveTwo.add(playerFactory.create(PlayerFactoryType.Prometheus, PlayerType.Enemy));

        ArrayList<IPlayer> waveThree = new ArrayList<IPlayer>();
        waveThree.add(playerFactory.create(PlayerFactoryType.EnterpriseE, PlayerType.Enemy));

        return new BasicStarfield(player, waveOne, waveTwo, waveThree, assetManager, camera, inputManager, screenSize);
    }
}
