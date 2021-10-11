package trekwars.races;

import com.jme3.asset.AssetManager;

public class Republic extends AbstractRace {
    public Republic(AssetManager assetManager) {
        super(assetManager);
    }

    protected String getLogo() {
        return "republic-logo.png";
    }
}
