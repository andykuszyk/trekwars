package trekwars.screens;

import com.jme3.asset.AssetManager;
import com.jme3.math.Vector2f;

public final class Button extends GuiElement {
    private final String _inactivePath;
    private final String _activePath;
    
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
        _inactivePath = inactivePath;
        _activePath = activePath;
    }
    
    
    public void activate() {
        _picture.setImage(_assetManager, _activePath, true);
    }
    
    public void deactivate() {
        _picture.setImage(_assetManager, _inactivePath, true);
    }
}
