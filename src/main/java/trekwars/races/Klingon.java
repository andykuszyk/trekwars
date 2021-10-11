package trekwars.races;

import com.jme3.asset.AssetManager;

public class Klingon extends AbstractRace {
    public Klingon(AssetManager assetManager) {
        super(assetManager);
    }

    protected String getLogo() {
        return "klingon-logo.png";
    }
}
