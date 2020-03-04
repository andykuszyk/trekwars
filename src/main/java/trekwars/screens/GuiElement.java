package trekwars.screens;

import com.jme3.asset.AssetManager;
import com.jme3.math.Vector2f;
import com.jme3.ui.Picture;

public class GuiElement {
    protected final Picture picture;
    protected final AssetManager assetManager;
    
    public GuiElement(
            String name,
            AssetManager assetManager,
            Vector2f bottomLeft,
            Vector2f topRight,
            Vector2f screenSize,
            String imagePath
            ) {
        picture = new Picture(name);
        this.assetManager = assetManager;
        float width = ((topRight.getX() - bottomLeft.getX()) / 1) * screenSize.getX();
        float height = ((topRight.getY() - bottomLeft.getY()) / 1) * screenSize.getY();
        float x = Math.max((bottomLeft.getX() * screenSize.getX()), 0);
        float y = Math.max((bottomLeft.getY() * screenSize.getY()), 0);
        picture.setWidth(width);
        picture.setHeight(height);
        picture.setPosition(x, y);
        picture.setImage(assetManager, imagePath, true);
    }
    
    public Picture getPicture() {
        return picture;
    }
}
