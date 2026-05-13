package entity.npc;

import java.util.List;

/**
 * Represents a choice in NPC dialog.
 */
public class DialogChoice {
    private String text;
    private String action; // e.g., "next", "screen:shop", "block:2"

    public DialogChoice(String text, String action) {
        this.text = text;
        this.action = action;
    }

    public String getText() { return text; }
    public String getAction() { return action; }
}
