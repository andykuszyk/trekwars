package trekwars.screens;

import com.jme3.asset.AssetManager;
import com.jme3.math.Vector2f;

public final class Button extends GuiElement {
    private final String inactivePath;
    private final String activePath;
    
    public Button(
            String name, 
            String inactivePath, 
            String activePath, 
            Vector2f bottomLeft,
            Vector2f topRight,
            AssetManager assetManager,
            Vector2f screenSize
            ) {
        super(name, assetManager, bottomLeft, topRight, screenSize, inactivePath);
        this.inactivePath = inactivePath;
        this.activePath = activePath;
    }
    
    
    public void activate() {
        picture.setImage(assetManager, activePath, true);
    }
    
    public void deactivate() {
        picture.setImage(assetManager, inactivePath, true);
    }
}
