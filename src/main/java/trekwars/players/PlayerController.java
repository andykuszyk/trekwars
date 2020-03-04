package trekwars.players;

import java.util.ArrayList;

public class PlayerController implements IPlayerController {

    private final ArrayList<AbstractPlayer> players = new ArrayList<AbstractPlayer>();
    private AbstractPlayer player = null;
    
    public void registerPlayer(AbstractPlayer player) {
        players.add(player);
        if(player.getPlayerType() == PlayerType.Player) {
            player = player;
        }
    }

    public Iterable<AbstractPlayer> getPlayers() {
        return players;
    }
    
    public AbstractPlayer getPlayer() {
        return player;
    }
}
