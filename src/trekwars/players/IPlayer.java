package trekwars.players;

import com.jme3.scene.Node;

public interface IPlayer {
    void turnRight();
    void turnLeft();
    void boost();
    void fire();
    void update(float tpf);
    Node getRootNode();
    PlayerType getPlayerType();
}
