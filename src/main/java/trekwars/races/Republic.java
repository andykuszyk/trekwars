package trekwars.races;

import com.jme3.asset.AssetManager;
import trekwars.players.PlayerMetadata;

public class Republic extends AbstractRace {
    public Republic(AssetManager assetManager) {
        super(assetManager);
    }

    public RaceType getRaceType() {
        return RaceType.Republic;
    }

    protected String getLogo() {
        return "republic-logo.png";
    }

    protected String getName() {
        return "Republic";
    }

    protected String getUniverse() {
        return PlayerMetadata.STARWARS;
    }
}
