package trekwars.races;

import com.jme3.asset.AssetManager;
import trekwars.players.PlayerMetadata;

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
}
