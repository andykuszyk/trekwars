package trekwars.players;

public interface IPlayerController {
    void registerPlayer(AbstractPlayer player);
    Iterable<AbstractPlayer> getPlayers();
    AbstractPlayer getPlayer();
}
