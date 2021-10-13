package trekwars.races;

import com.jme3.asset.AssetManager;
import trekwars.players.PlayerMetadata;

public class Klingon extends AbstractRace {
    public Klingon(AssetManager assetManager) {
        super(assetManager);
    }

    protected String getLogo() {
        return "klingon-logo.png";
    }

    protected String getName() {
        return "Klingon";
    }

    protected String getUniverse() {
        return PlayerMetadata.STARTREK;
    }
}
