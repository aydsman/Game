package entity.npc;

import java.util.List;

/**
 * Represents a block of NPC dialog text with optional choices.
 */
public class DialogBlock {
    private String text;
    private List<DialogChoice> choices; // null or empty means no choices, advance to next block

    public DialogBlock(String text, List<DialogChoice> choices) {
        this.text = text;
        this.choices = choices;
    }

    public String getMessage() { return text; }
    public List<DialogChoice> getChoices() { return choices; }
    public boolean hasChoices() { return choices != null && !choices.isEmpty(); }
}
