package trekwars.players;

import com.jme3.scene.Node;

public interface IPlayer {
    void turnRight(float tpf);
    void turnLeft(float tpf);
    void boost(float tpf);
    void fire(float tpf);
    void update(float tpf);
    Node getRootNode();
    PlayerType getPlayerType();
}
