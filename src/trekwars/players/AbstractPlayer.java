package trekwars.players;

import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

public abstract class AbstractPlayer implements IPlayer {
    
    protected int _turnLeftCount = 0;
    protected int _turnRightCount = 0;
    protected int _boostCount = 0;
    protected int _fireCount = 0;
    private Node _rootNode;   
    
    protected void attachChild(Spatial child) {
        if(_rootNode == null) _rootNode = new Node();
        _rootNode.attachChild(child);
    }
    
    public void turnRight(float tpf) {
        _turnRightCount++;
    }
    
    public void turnLeft(float tpf){
        _turnLeftCount++;
    }
    
    public void boost(float tpf){
        _boostCount++;
    }
    
    public void fire(float tpf) {
        _fireCount++;
    }
    
    public abstract void update(float tpf);
    
    public Node getRootNode() {
        return _rootNode;
    }
}
