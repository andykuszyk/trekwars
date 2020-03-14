package trekwars.players;

import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioNode;
import com.jme3.collision.CollisionResults;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Quad;
import com.jme3.scene.shape.Sphere;
import com.jme3.texture.Texture;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Logger;

public abstract class AbstractPlayer implements IPlayer {
    
    protected int turnLeftCount = 0;
    protected int turnRightCount = 0;
    protected int boostCount = 0;
    protected int fireCount = 0;
    protected int stopCount = 0;
    private Node rootNode;   
    private final float rollRightLimit = -0.6f;
    private final float rollLeftLimit = 0.6f;
    private final float rollMultiplier = 1f;
    private float previousRotationAmount = 0f;
    private final PlayerType playerType;
    private final IPlayerController playerController;
    protected final Node spatialNode;
    private final Spatial shields;
    private float shieldAlpha = 0f;
    private final ColorRGBA shieldColor;
    private final Material shieldsMaterial;
    private final float shieldUpAlphaRate = 5f;
    private final float shieldDownAlphaRate = 0.1f;
    protected float life;
    private final float lifeCapacity;
    private final Geometry explosion;
    private int explosionFrame = 0;
    private final ArrayList<Texture> explosionTextures;
    private final Camera camera;
    private final float explosionSize = 30f;
    private final AudioNode explosionNode;
    protected final Logger log = Logger.getGlobal();
    private final PlayerFactoryType playerFactoryType;
    
    protected AbstractPlayer(
            PlayerType playerType, 
            IPlayerController playerController,
            ColorRGBA shieldColor,
            Vector3f shieldScale,
            Vector3f shieldTranslation,
            AssetManager assetManager,
            ArrayList<Texture> explosionTextures,
            Camera camera,
            AudioNode explosionNode,
            PlayerFactoryType playerFactoryType) {
        this.playerType = playerType;
        this.playerController = playerController;
        spatialNode = new Node();
        this.shieldColor = shieldColor;
        attachChild(spatialNode);
        this.explosionTextures = explosionTextures;
        this.camera = camera;
        this.explosionNode = explosionNode;
        this.playerFactoryType = playerFactoryType;

        if(playerType == PlayerType.Player) {
            lifeCapacity = life = 1f;
        } else {
            lifeCapacity = life = 0.25f;
        }
        
        // Setup explosion 
        explosion = new Geometry("explosion", new Quad(explosionSize, explosionSize));
        Material explosionMaterial = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        explosionMaterial.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        explosion.setQueueBucket(RenderQueue.Bucket.Transparent);
        explosion.setMaterial(explosionMaterial);
        explosion.setLocalTranslation(explosionSize / 2, -explosionSize / 2, 0);
        
        // Setup shields 
        Sphere sphere = new Sphere(10, 10, 1);
        shieldsMaterial = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        shieldsMaterial.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        setShieldColor();
        shields = new Geometry("shields", sphere);
        shields.setMaterial(shieldsMaterial);
        shields.setQueueBucket(RenderQueue.Bucket.Transparent);
        shields.scale(shieldScale.x, shieldScale.y, shieldScale.z);
        shields.setLocalTranslation(shieldTranslation);
        spatialNode.attachChild(shields);
        
        playerController.registerPlayer(this);
    }

    public PlayerFactoryType getPlayerFactoryType() {
        return this.playerFactoryType;
    }
    
    private void setShieldColor() {
        float lifeRatio = life / lifeCapacity;
        shieldsMaterial.setColor(
            "Color", 
            new ColorRGBA(
                1 - lifeRatio, 
                lifeRatio * shieldColor.g, 
                lifeRatio * shieldColor.b, 
                shieldAlpha
            ));
    }
    
    public PlayerType getPlayerType() {
        return playerType;
    }
    
    protected enum TurnDirection {
        Right, Left, None
    }
    
    protected final void attachChild(Spatial child) {
        if(rootNode == null) rootNode = new Node();
        rootNode.attachChild(child);
    }
    
    public void turnRight() {
        turnRightCount++;
    }
    
    public void turnLeft(){
        turnLeftCount++;
    }
    
    public void boost(){
        boostCount++;
    }
    
    public void fire() {
        fireCount++;
    }
    
    public void stop() {
        stopCount++;
    }
    
    public void update(float tpf) {
        if(life > 0) {
            turn(tpf);
            fire(tpf);
            
            switch(playerType){
                default:
                case Player:
                    move(tpf);
                    break;
                case Enemy:
                    autopilot(tpf);
                    break;
            } 
        }
        
        updateShields(tpf);
        handleExplosion(tpf);
        onUpdate(tpf);
    }
    
    private void handleExplosion(float tpf) {
        if(life <= 0) {
            if(explosionFrame == 0) {
                rootNode.detachAllChildren();
                rootNode.attachChild(explosion);
                rootNode.attachChild(explosionNode);
                explosionNode.play();
            } 
            
            if(explosionFrame >= explosionTextures.size()) {
                return;
            }
            
            explosion.lookAt(camera.getLocation(), Vector3f.UNIT_Y);
            explosion.getMaterial().setTexture("ColorMap", explosionTextures.get(explosionFrame));
            explosionFrame++;
        }
    }
    
    private void updateShields(float tpf) {
        for(AbstractPlayer player : playerController.getPlayers()) {
            if(player == this) continue;
            boolean isHit = false;
            for(Spatial weapon : player.getWeaponSpatials()) {
                if(weapon.getParent() == null) continue;
                CollisionResults results = new CollisionResults();
                weapon.collideWith(spatialNode.getWorldBound(), results);
                if(results.size() > 0) {
                    isHit = true;
                    break;
                }
            }
            if(isHit) {
                life -= 0.05f * tpf;
                increaseShieldAlpha(tpf);
            } else {
                if(playerType == PlayerType.Player) {
                    if(life >= 0) {
                        life += 0.0001f * tpf;
                    }
                }
                decreaseShieldAlpha(tpf);
            }
        }
    }
    
    private void increaseShieldAlpha(float tpf) {
        shieldAlpha = Math.min(0.5f, shieldAlpha + (shieldUpAlphaRate * tpf));
        setShieldColor();
    }
    
    private void decreaseShieldAlpha(float tpf) {
        shieldAlpha = Math.max(0.01f, shieldAlpha - (shieldDownAlphaRate * tpf));
        setShieldColor();
    }
    
    protected void onUpdate(float tpf) { }
    
    protected abstract void autopilot(float tpf);
    
    private void fire(float tpf) {
        if(fireCount > 0){
            onFireStart(tpf);
        } else {
            onFireStop(tpf);
        }
        fireCount = 0;
    }
    
    protected abstract void onFireStart(float tpf);
    
    protected abstract void onFireStop(float tpf);
    
    private void turn(float tpf) {
        TurnDirection currentTurnDirection = getTurnDirection();
        float rotationalMultiplier = getRotationalSpeed() * tpf;
        float rollAmount = rotationalMultiplier * rollMultiplier;
        boolean shouldTurn = false;
        
        Quaternion localRotation = spatialNode.getLocalRotation();

        if(shouldTurn(currentTurnDirection, localRotation)) {
            roll(spatialNode, localRotation, rollAmount, currentTurnDirection);
            shouldTurn = true;
        } else {
            int turningMultiplier = currentTurnDirection == TurnDirection.None ? 1 : 2;
            restoreRotation(spatialNode, rotationalMultiplier * turningMultiplier, localRotation);
        }
        
        float rotationAmount;
        if(shouldTurn){
            float targetRotationAmount = getRotationalSpeed() * tpf * getRotationSign(currentTurnDirection);
            boolean achievedTargetRotation = 
                    targetRotationAmount > 0 ?
                    previousRotationAmount >= targetRotationAmount :
                    previousRotationAmount <= targetRotationAmount;
            if(achievedTargetRotation) {
                rotationAmount = targetRotationAmount;
            } else {
                if(getRotationSign(currentTurnDirection) != Math.round(previousRotationAmount / Math.abs(previousRotationAmount))) {
                    previousRotationAmount = previousRotationAmount * -1;
                }
                rotationAmount = 
                        Math.abs(previousRotationAmount) < Math.abs(targetRotationAmount * tpf) ?
                        targetRotationAmount * tpf :
                        previousRotationAmount * (1 + 2 * tpf / getRotationalSpeed());
                if(Math.abs(rotationAmount) > Math.abs(targetRotationAmount)) {
                    rotationAmount = targetRotationAmount;
                }
            }
        } else {
            rotationAmount = previousRotationAmount * (1 - tpf / getRotationalSpeed());
        }
        rootNode.rotate(0,rotationAmount,0);
        previousRotationAmount = rotationAmount;
        
        turnLeftCount = 0;
        turnRightCount = 0;
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
                if(localRotation.getZ() > rollRightLimit) {
                    spatial.rotate(rollAmount/5,-rollAmount/2,-rollAmount);
                } 
                break;
            case Left:
                if(localRotation.getZ() < rollLeftLimit){
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
        if( turnRightCount + turnLeftCount == 0){
            return TurnDirection.None;
        } else if(turnRightCount - turnLeftCount > 0){
            return TurnDirection.Right;
        } else {
            return TurnDirection.Left;
        }
    }
    
    protected void move(float tpf) {
        if(life <= 0) return;
        
        float boostMultiplier = 1;
        if(boostCount > 0) {
            boostMultiplier = getBoostMultiplier();
        }
        boostCount = 0;
        if(stopCount > 0) {
            boostMultiplier = 0f;
        }
        stopCount = 0;
        
        float playerBonus = playerType == PlayerType.Enemy ? 1f : 3f;
        Vector3f m = new Vector3f(0, 0, -(boostMultiplier * (getTranslationalSpeed() * tpf * playerBonus)));
        Vector3f worldMove = rootNode.localToWorld(m, m);
        rootNode.setLocalTranslation(worldMove);
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
        if(rootNode == null){
            rootNode = new Node();
        }
        return rootNode;
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
        if(playerController.getPlayer() == null) return 0f;
        return this.getRootNode().getWorldTranslation().distance(
                playerController.getPlayer().getRootNode().getWorldTranslation());
    }
}
