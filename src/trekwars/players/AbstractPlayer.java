package trekwars.players;

import com.jme3.math.Quaternion;
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
    private final float _rollRightLimit = -0.6f;
    private final float _rollLeftLimit = 0.6f;
    private final float _rollMultiplier = 1f;
    private TurnDirection _turningDirection = TurnDirection.None;
    
    protected enum TurnDirection {
        Right, Left, None
    }
    
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
        TurnDirection userTurnDirection = getTurnDirection();
        
        for(Spatial child : _rootNode.getChildren()){
            Quaternion localRotation = child.getLocalRotation();
            
            float rotationalMultiplier = getRotationalSpeed() * tpf;
            float rollAmount = rotationalMultiplier * _rollMultiplier;
            
            if(isNotTurning(localRotation) && _turningDirection == TurnDirection.None) {
                _turningDirection = userTurnDirection;
            }
            
            roll(child, localRotation, rollAmount, _turningDirection);
            
            if(userTurnDirection == TurnDirection.None)    {
                _turningDirection = TurnDirection.None;
            }
            
            if(_turningDirection == TurnDirection.None){
                restoreRotation(child, rotationalMultiplier, localRotation);
            }
        }
        
        if(_turningDirection != TurnDirection.None){
            _rootNode.rotate(0, getRotationalSpeed() * tpf * getRotationSign(userTurnDirection), 0);
        }
        
        _turnLeftCount = 0;
        _turnRightCount = 0;
    }
    
    private void roll(Spatial spatial, Quaternion localRotation, float rollAmount, TurnDirection turnDirection){
        switch(turnDirection){
            case Right:
                if(localRotation.getZ() > _rollRightLimit) {
                    spatial.rotate(rollAmount/5,-rollAmount/2,-rollAmount);
                } 
                break;
            case Left:
                if(localRotation.getZ() < _rollLeftLimit){
                    spatial.rotate(rollAmount/5,rollAmount/2,rollAmount);
                } 
                break;
            default: return;
        }
    }
    
    private boolean isNotTurning(Quaternion localRotation){
        return
                Math.abs(localRotation.getX() - 0) < 0.1 &&
                Math.abs(localRotation.getY() - 0) < 0.1 &&
                Math.abs(localRotation.getZ() - 0) < 0.1;
    }
    
    private void restoreRotation(Spatial spatial, float rotationalMultiplier, Quaternion localRotation) {
        spatial.rotate(
            -localRotation.getX() * rotationalMultiplier * 2,
            -localRotation.getY() * rotationalMultiplier * 2,
            -localRotation.getZ() * rotationalMultiplier * 2); 
    }
    
    private int getRotationSign(TurnDirection turnDirection){
        switch(turnDirection){
            case Left:
                return 1;
            case Right:
                return -1;
            default:
                return 0;
        }
    }
    
    private TurnDirection getTurnDirection() {
        if( _turnRightCount + _turnLeftCount == 0){
            return TurnDirection.None;
        } else if(_turnRightCount - _turnLeftCount > 0){
            return TurnDirection.Right;
        } else {
            return TurnDirection.Left;
        }
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
