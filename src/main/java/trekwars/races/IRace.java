package trekwars.races;

import com.jme3.scene.Spatial;

public interface IRace {
    public Spatial buildLogo();
    public String getFormattedMetadata();
    public RaceType getRaceType();
}
