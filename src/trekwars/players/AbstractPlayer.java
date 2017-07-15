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
    private float _previousRotationAmount = 0f;
    private final PlayerType _playerType;
    
    protected AbstractPlayer(PlayerType playerType) {
        _playerType = playerType;
    }
    
    public PlayerType getPlayerType() {
        return _playerType;
    }
    
    protected enum TurnDirection {
        Right, Left, None
    }
    
    protected void attachChild(Spatial child) {
        if(_rootNode == null) _rootNode = new Node();
        _rootNode.attachChild(child);
    }
    
    public void turnRight() {
        _turnRightCount++;
    }
    
    public void turnLeft(){
        _turnLeftCount++;
    }
    
    public void boost(){
        if(_lastBoostTime != null) return;
        _lastBoostTime = new Date();
    }
    
    public void fire() {
        _fireCount++;
    }
    
    public void update(float tpf) {
        turn(tpf);
        fire(tpf);
        
        switch(_playerType){
            default:
            case Player:
                move(tpf);
                break;
            case Enemy:
                autopilot(tpf);
                break;
        } 
    }
    
    protected abstract void autopilot(float tpf);
    
    protected abstract Node getSpatialNode();
    
    private void fire(float tpf) {
        //TODO
        _fireCount = 0;
    }
    
    protected abstract void onFire(float tpf);
    
    private void turn(float tpf) {
        TurnDirection currentTurnDirection = getTurnDirection();
        float rotationalMultiplier = getRotationalSpeed() * tpf;
        float rollAmount = rotationalMultiplier * _rollMultiplier;
        boolean shouldTurn = false;
        
        Node child = getSpatialNode();
        Quaternion localRotation = child.getLocalRotation();

        if(shouldTurn(currentTurnDirection, localRotation)) {
            roll(child, localRotation, rollAmount, currentTurnDirection);
            shouldTurn = true;
        } else {
            int turningMultiplier = currentTurnDirection == TurnDirection.None ? 1 : 2;
            restoreRotation(child, rotationalMultiplier * turningMultiplier, localRotation);
        }
        
        float rotationAmount;
        if(shouldTurn){
            float targetRotationAmount = getRotationalSpeed() * tpf * getRotationSign(currentTurnDirection);
            boolean achievedTargetRotation = 
                    targetRotationAmount > 0 ?
                    _previousRotationAmount >= targetRotationAmount :
                    _previousRotationAmount <= targetRotationAmount;
            if(achievedTargetRotation) {
                rotationAmount = targetRotationAmount;
            } else {
                if(getRotationSign(currentTurnDirection) != Math.round(_previousRotationAmount / Math.abs(_previousRotationAmount))) {
                    _previousRotationAmount = _previousRotationAmount * -1;
                }
                rotationAmount = 
                        Math.abs(_previousRotationAmount) < Math.abs(targetRotationAmount * tpf) ?
                        targetRotationAmount * tpf :
                        _previousRotationAmount * (1 + 2 * tpf / getRotationalSpeed());
                if(Math.abs(rotationAmount) > Math.abs(targetRotationAmount)) {
                    rotationAmount = targetRotationAmount;
                }
            }
        } else {
            rotationAmount = _previousRotationAmount * (1 - tpf / getRotationalSpeed());
        }
        _rootNode.rotate(0,rotationAmount,0);
        _previousRotationAmount = rotationAmount;
        
        _turnLeftCount = 0;
        _turnRightCount = 0;
    }
    
    private boolean shouldTurn(TurnDirection currentTurnDirection, Quaternion localRotation){
        return 
                isTurning(currentTurnDirection) && 
                (currentRollDirection(localRotation) == currentTurnDirection || 
                currentRollDirection(localRotation) == TurnDirection.None);
    }
    
    private boolean isTurning(TurnDirection turnDirection) {
        return turnDirection != TurnDirection.None;
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
    
    private TurnDirection currentRollDirection(Quaternion localRotation){
        if(Math.abs(localRotation.getZ() - 0) < 0.05) {
            return TurnDirection.None;
        } else if(localRotation.getZ() < 0) {
            return TurnDirection.Right;
        } else {
            return TurnDirection.Left;
        }
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
    
    /**
     * Gets the rotational speed of the player in units per second.
     * This represents the rate at which a player can turn.
     * e.g. a value of 1 would indicate that the player can turn all
     * the way round in one second.
     * @return A float that indicates the number of units the player can turn through in a second.
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
