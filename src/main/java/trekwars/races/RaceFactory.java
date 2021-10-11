package trekwars.races;

import com.jme3.asset.AssetManager;

public class RaceFactory {
    private final AssetManager assetManager;

    public RaceFactory(AssetManager assetManager) {
        this.assetManager = assetManager;
    }

    public IRace create(RaceType race) {
        switch(race) {
            case Klingon:
                return new Klingon(assetManager);
            case Federation:
                return new Federation(assetManager);
            case Empire:
                return new Empire(assetManager);
            case Republic:
                return new Republic(assetManager);
            default:
                return null;
        }
    }
}
