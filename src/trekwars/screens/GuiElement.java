package trekwars.screens;

import com.jme3.asset.AssetManager;
import com.jme3.math.Vector2f;
import com.jme3.ui.Picture;

public class GuiElement {
    protected final Picture _picture;
    protected final AssetManager _assetManager;
    
    public GuiElement(
            String name,
            AssetManager assetManager,
            Vector2f bottomLeft,
            Vector2f topRight,
            Vector2f screenSize,
            String imagePath
            ) {
        _picture = new Picture(name);
        _assetManager = assetManager;
        float width = ((topRight.getX() - bottomLeft.getX()) / 1) * screenSize.getX();
        float height = ((topRight.getY() - bottomLeft.getY()) / 1) * screenSize.getY();
        float x = Math.max((bottomLeft.getX() * screenSize.getX()), 0);
        float y = Math.max((bottomLeft.getY() * screenSize.getY()), 0);
        _picture.setWidth(width);
        _picture.setHeight(height);
        _picture.setPosition(x, y);
        _picture.setImage(_assetManager, imagePath, true);
    }
    
    public Picture getPicture() {
        return _picture;
    }
}
