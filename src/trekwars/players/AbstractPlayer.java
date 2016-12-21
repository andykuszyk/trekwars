package trekwars.players;

import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import java.util.Date;

public abstract class AbstractPlayer implements IPlayer {
    
    protected int _turnLeftCount = 0;
    protected int _turnRightCount = 0;
    protected Date _lastBoostTime = null;
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
        if(_lastBoostTime != null) return;
        _lastBoostTime = new Date();
    }
    
    public void fire(float tpf) {
        _fireCount++;
    }
    
    public void update(float tpf) {
        turn(tpf);
        move(tpf);
        
        onUpdate(tpf);
        
        _fireCount = 0;
    }
    
    private void turn(float tpf) {
        int rightOrLeft = _turnRightCount + _turnLeftCount == 0 ? 0 : _turnRightCount - _turnLeftCount > 0 ? -1 : 1;
        _rootNode.rotate(0, getRotationalSpeed() * tpf * rightOrLeft, 0);
        
        _turnLeftCount = 0;
        _turnRightCount = 0;
    }
    
    private void move(float tpf) {
        int boostMultiplier = 1;
        if(_lastBoostTime != null){
            float timeSinceLastBoost = new Date().getTime() - _lastBoostTime.getTime();
            if(timeSinceLastBoost > getBoostCapacity()){
                _lastBoostTime = null;
            } else {
                boostMultiplier = 2;
            }
        }
        
        Vector3f m = new Vector3f(0, 0, -(boostMultiplier * (getTranslationalSpeed() * tpf)));
        Vector3f worldMove = _rootNode.localToWorld(m, m);
        _rootNode.setLocalTranslation(worldMove);
    }
    
    protected abstract void onUpdate(float tpf);
    
    /**
     * Gets the rotational speed of the player in radians per second.
     * This represents the rate at which a player can turn.
     * e.g. a value of 2 Pi would indicate that the player can turn all
     * the way round in one second.
     * @return A float that indicates the number of radians the player can turn through in a second.
     */
    protected abstract float getRotationalSpeed();
    
    /**
     * Gets the number of units the player moves each second.
     * @return 
     */
    protected abstract float getTranslationalSpeed();
    
    /**
     * Gets the number of seconds the player can boost for.
     * @return 
     */
    protected abstract float getBoostCapacity();
    
    public Node getRootNode() {
        if(_rootNode == null){
            _rootNode = new Node();
        }
        return _rootNode;
    }
}
