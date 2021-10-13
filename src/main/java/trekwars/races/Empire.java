package trekwars.races;

import com.jme3.asset.AssetManager;
import trekwars.players.PlayerMetadata;

public class Empire extends AbstractRace {
    public Empire(AssetManager assetManager) {
        super(assetManager);
    }

    protected String getLogo() {
        return "empire-logo.png";
    }

    protected String getName() {
        return "Empire";
    }

    protected String getUniverse() {
        return PlayerMetadata.STARWARS;
    }
}
