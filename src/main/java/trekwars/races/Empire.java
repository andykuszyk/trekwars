package trekwars.races;

import com.jme3.asset.AssetManager;

public class Empire extends AbstractRace {
    public Empire(AssetManager assetManager) {
        super(assetManager);
    }

    protected String getLogo() {
        return "empire-logo.png";
    }
}
