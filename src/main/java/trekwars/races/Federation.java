package trekwars.races;

import com.jme3.asset.AssetManager;

public class Federation extends AbstractRace {
    public Federation(AssetManager assetManager) {
        super(assetManager);
    }

    protected String getLogo() {
        return "federation-logo.jpg";
    }
}
