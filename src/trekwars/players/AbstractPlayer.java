package trekwars.players;

import com.jme3.asset.AssetManager;
import com.jme3.collision.CollisionResults;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Sphere;
import java.util.ArrayList;
import java.util.Collection;

public abstract class AbstractPlayer implements IPlayer {
    
    protected int _turnLeftCount = 0;
    protected int _turnRightCount = 0;
    protected int _boostCount = 0;
    protected int _fireCount = 0;
    protected int _stopCount = 0;
    private Node _rootNode;   
    private final float _rollRightLimit = -0.6f;
    private final float _rollLeftLimit = 0.6f;
    private final float _rollMultiplier = 1f;
    private float _previousRotationAmount = 0f;
    private final PlayerType _playerType;
    private final IPlayerController _playerController;
    protected final Node _spatialNode;
    private final Spatial _shields;
    private float _shieldAlpha = 0f;
    private final ColorRGBA _shieldColor;
    private final Material _shieldsMaterial;
    private final float _shieldUpAlphaRate = 5f;
    private final float _shieldDownAlphaRate = 0.1f;
    private float _life;
    private final float _lifeCapacity;
    
    protected AbstractPlayer(
            PlayerType playerType, 
            IPlayerController playerController,
            ColorRGBA shieldColor,
            Vector3f shieldScale,
            Vector3f shieldTranslation,
            AssetManager assetManager
            ) {
        _playerType = playerType;
        _playerController = playerController;
        _spatialNode = new Node();
        _shieldColor = shieldColor;
        attachChild(_spatialNode);
        
        if(playerType == PlayerType.Player) {
            _lifeCapacity = _life = 1f;
        } else {
            _lifeCapacity = _life = 0.25f;
        }
        
        Sphere sphere = new Sphere(10, 10, 1);
        _shieldsMaterial = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        _shieldsMaterial.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        setShieldColor();
        _shields = new Geometry("shields", sphere);
        _shields.setMaterial(_shieldsMaterial);
        _shields.setQueueBucket(RenderQueue.Bucket.Transparent);
        _shields.scale(shieldScale.x, shieldScale.y, shieldScale.z);
        _shields.setLocalTranslation(shieldTranslation);
        _spatialNode.attachChild(_shields);
        playerController.registerPlayer(this);
    }
    
    private void setShieldColor() {
        float lifeRatio = _life / _lifeCapacity;
        _shieldsMaterial.setColor(
            "Color", 
            new ColorRGBA(
                1 - lifeRatio, 
                lifeRatio * _shieldColor.g, 
                lifeRatio * _shieldColor.b, 
                _shieldAlpha
            ));
    }
    
    public PlayerType getPlayerType() {
        return _playerType;
    }
    
    protected enum TurnDirection {
        Right, Left, None
    }
    
    protected final void attachChild(Spatial child) {
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
        _boostCount++;
    }
    
    public void fire() {
        _fireCount++;
    }
    
    public void stop() {
        _stopCount++;
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
        
        updateShields(tpf);
        
        onUpdate(tpf);
    }
    
    private void updateShields(float tpf) {
        for(AbstractPlayer player : _playerController.getPlayers()) {
            if(player == this) continue;
            boolean isHit = false;
            for(Spatial weapon : player.getWeaponSpatials()) {
                if(weapon.getParent() == null) continue;
                CollisionResults results = new CollisionResults();
                weapon.collideWith(_spatialNode.getWorldBound(), results);
                if(results.size() > 0) {
                    isHit = true;
                    break;
                }
            }
            if(isHit) {
                _life -= 0.05f * tpf;
                increaseShieldAlpha(tpf);
            } else {
                if(_playerType == PlayerType.Player) {
                    _life += 0.001f * tpf;
                }
                decreaseShieldAlpha(tpf);
            }
        }
    }
    
    private void increaseShieldAlpha(float tpf) {
        _shieldAlpha = Math.min(0.5f, _shieldAlpha + (_shieldUpAlphaRate * tpf));
        setShieldColor();
    }
    
    private void decreaseShieldAlpha(float tpf) {
        _shieldAlpha = Math.max(0.01f, _shieldAlpha - (_shieldDownAlphaRate * tpf));
        setShieldColor();
    }
    
    protected void onUpdate(float tpf) { }
    
    protected abstract void autopilot(float tpf);
    
    private void fire(float tpf) {
        if(_fireCount > 0){
            onFireStart(tpf);
        } else {
            onFireStop(tpf);
        }
        _fireCount = 0;
    }
    
    protected abstract void onFireStart(float tpf);
    
    protected abstract void onFireStop(float tpf);
    
    private void turn(float tpf) {
        TurnDirection currentTurnDirection = getTurnDirection();
        float rotationalMultiplier = getRotationalSpeed() * tpf;
        float rollAmount = rotationalMultiplier * _rollMultiplier;
        boolean shouldTurn = false;
        
        Quaternion localRotation = _spatialNode.getLocalRotation();

        if(shouldTurn(currentTurnDirection, localRotation)) {
            roll(_spatialNode, localRotation, rollAmount, currentTurnDirection);
            shouldTurn = true;
        } else {
            int turningMultiplier = currentTurnDirection == TurnDirection.None ? 1 : 2;
            restoreRotation(_spatialNode, rotationalMultiplier * turningMultiplier, localRotation);
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
    
    protected void move(float tpf) {
        float boostMultiplier = 1;
        if(_boostCount > 0) {
            boostMultiplier = getBoostMultiplier();
        }
        _boostCount = 0;
        if(_stopCount > 0) {
            boostMultiplier = 0f;
        }
        _stopCount = 0;
        
        float playerBonus = _playerType == PlayerType.Enemy ? 1f : 3f;
        Vector3f m = new Vector3f(0, 0, -(boostMultiplier * (getTranslationalSpeed() * tpf * playerBonus)));
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
    
    /**
     * Gets the amount by which the translational speed should be multiplied
     * when boosting.
     * @return 
     */
    protected abstract float getBoostMultiplier();
    
    public Node getRootNode() {
        if(_rootNode == null){
            _rootNode = new Node();
        }
        return _rootNode;
    }
    
    public Iterable<Node> getRootNodes() {
        ArrayList<Node> nodes = new ArrayList<Node>();
        nodes.add(getRootNode());
        nodes.addAll(getOtherNodes());
        return nodes;
    }
    
    protected Collection<Node> getOtherNodes() {
        return new ArrayList<Node>();
    }
    
    public abstract Iterable<Spatial> getWeaponSpatials();
    
    protected float getDistanceToPlayer() {
        if(_playerController.getPlayer() == null) return 0f;
        return this.getRootNode().getWorldTranslation().distance(
                _playerController.getPlayer().getRootNode().getWorldTranslation());
    }
}
