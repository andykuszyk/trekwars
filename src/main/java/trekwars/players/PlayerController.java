package trekwars.players;

import java.util.ArrayList;

public class PlayerController implements IPlayerController {

    private final ArrayList<AbstractPlayer> _players = new ArrayList<AbstractPlayer>();
    private AbstractPlayer _player = null;
    
    public void registerPlayer(AbstractPlayer player) {
        _players.add(player);
        if(player.getPlayerType() == PlayerType.Player) {
            _player = player;
        }
    }

    public Iterable<AbstractPlayer> getPlayers() {
        return _players;
    }
    
    public AbstractPlayer getPlayer() {
        return _player;
    }
}
