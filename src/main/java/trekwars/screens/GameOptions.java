package trekwars.screens;

import trekwars.players.PlayerFactoryType;
import trekwars.races.RaceType;

public class GameOptions {
    private final PlayerFactoryType playerFactoryType;
    private final RaceType playerRace;
    private final RaceType enemyRace;

    public GameOptions(PlayerFactoryType playerType, RaceType playerRace, RaceType enemyRace) {
        this.playerFactoryType = playerType;
        this.playerRace = playerRace;
        this.enemyRace = enemyRace;
    }

    public PlayerFactoryType getPlayerFactoryType() {
        return this.playerFactoryType;
    }

    public RaceType getPlayerRace() {
        return this.playerRace;
    }

    public RaceType getEnemyRace() {
        return this.enemyRace;
    }
}
