package trekwars.screens;

import com.jme3.scene.Node;

public interface IScreen {
    void update(float tpf);
    Node getRootNode();
    Node getGuiNode();
    void onAnalog(String name, float keyPressed, float tpf);
    void onAction(String name, boolean keyPressed, float tpf);
}
