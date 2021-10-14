package trekwars.races;

import com.jme3.scene.Spatial;
import trekwars.players.IPlayer;
import trekwars.players.PlayerFactory;
import com.jme3.input.InputManager;
import com.jme3.renderer.Camera;
import com.jme3.math.Vector2f;
import trekwars.screens.IScreen;

public interface IRace {
    Spatial buildLogo();
    String getFormattedMetadata();
    RaceType getRaceType();
    IScreen buildEnvironment(IPlayer player, PlayerFactory playerFactory, Camera camera, InputManager inputManager, Vector2f screenSize);
}
