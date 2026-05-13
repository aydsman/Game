package entity.npc;

import java.awt.Color;
import java.util.List;

/**
 * An interactable object that can trigger screen transitions when interacted with.
 * Similar to NPC but without dialog - pressing E near it switches to a specified screen.
 */
public class InteractableObject extends NPC {
    private String screenName; // The screen to switch to when interacted with

    public InteractableObject(int x, int y, String name, String screenName, List<DialogBlock> dialogBlocks) {
        super(x, y, name, dialogBlocks);
        this.screenName = screenName;
        // Change color to distinguish from NPCs
        setColor(Color.GREEN);
    }

    public String getScreenName() {
        return screenName;
    }

    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }
}
