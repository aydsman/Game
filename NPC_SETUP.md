# NPC Setup Guide

## Creating NPCs

To add an NPC to the game, follow this pattern:

```java
List<String> messages = new ArrayList<>();
messages.add("Hello traveler!");
messages.add("Care to buy some potions?");
npcs.add(new NPC(x, y, "Shopkeeper", messages));
```


## Adding NPCs to HubScreen

Add your NPC initialization code in the `initializeNPCs()` method in `HubScreen.java`:

```java
private void initializeNPCs() {
    // Example NPC
    List<String> messages = new ArrayList<>();
    messages.add("Hello traveler!");
    messages.add("Care to buy some potions?");
    npcs.add(new NPC(800, 600, "Shopkeeper", messages));
}
```

## NPC Parameters

- `x` - X position in the arena
- `y` - Y position in the arena
- `name` - NPC name (shown in dialog box)
- `messages` - List of dialog messages the NPC will say

## Interaction

- Walk near an NPC to see the "E" indicator
- Press `E` to start talking
- Press `E` or `Space` to advance through messages
- Press `E` or `Space` on the last message to close the dialog
