package ui.screens;

import combat.charms.Charm;
import entity.EnemyManager;
import entity.Player;
import entity.npc.NPC;
import entity.npc.InteractableObject;
import entity.npc.DialogBlock;
import entity.npc.DialogChoice;
import combat.powers.Power;
import combat.powers.Move;
import ui.GamePanel;
import ui.HUD;
import ui.ChestUI;
import ui.InventoryScreen;
import util.Camera;
import util.KeyHandler;
import util.MouseHandler;
import world.arena.arenas.HubArena;
import world.chests.Chest;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.ArrayList;

/**
 * Hub screen - a peaceful area with grey ground and no enemies.
 */
public class HubScreen {

    HubArena arena = new HubArena();
    Player player = new Player(arena.getWidth() / 2, arena.getHeight() / 2);
    Camera camera = new Camera();
    HUD hud = new HUD();
    ChestUI chestUI = new ChestUI();
    InventoryScreen inventoryScreen = new InventoryScreen();
    EnemyManager enemyManager = new EnemyManager();
    ArrayList<Chest> chests = new ArrayList<>();
    Chest activeChest = null;
    combat.Item draggedItem = null;
    int dragSource = -1;
    int dragSourceSlot = -1;
    boolean wasLeftPressedLastFrame = false;
    boolean ePressedLastFrame = false;
    boolean inventoryOpen = false;
    boolean tabPressedLastFrame = false;
    boolean onePressedLastFrame = false;
    boolean twoPressedLastFrame = false;
    boolean threePressedLastFrame = false;
    boolean fourPressedLastFrame = false;
    private int lastMouseX = 0;
    private int lastMouseY = 0;
    private boolean debugMode = false;
    private boolean paused = false;
    private boolean pPressedLastFrame = false;
    private static final int PAUSE_MENU_MARGIN = 150;
    private GamePanel gamePanel;

    /** Session admin toggle + reset save (bottom-right stack). */
    private final Rectangle hubAdminToggleBtn = new Rectangle(1450, 788, 140, 40);
    private final Rectangle hubResetSaveBtn = new Rectangle(1450, 838, 140, 42);
    private boolean hubAdminToggleHovered;
    private boolean hubResetSaveHovered;

    // NPCs
    private ArrayList<NPC> npcs = new ArrayList<>();
    private NPC activeNPC = null;
    private boolean inDialog = false;
    private boolean spacePressedLastFrame = false;

    // Interactable Objects
    private ArrayList<InteractableObject> interactables = new ArrayList<>();
    private InteractableObject activeInteractable = null;
    
    // Auto-save tracking
    private long lastAutoSaveTime = 0;
    private static final long AUTO_SAVE_INTERVAL = 10000; // Auto-save every 10 seconds
    private int lastSavedKills = 0;

    // Key tracking for W and S keys
    private boolean upPressedLastFrame = false;
    private boolean downPressedLastFrame = false;

    public HubScreen() {
        // No waves in hub
    }

    public HubScreen(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
        initializeNPCs();
    }

    private void initializeNPCs() {
        int centerX = arena.getWidth() / 2;
        int centerY = arena.getHeight() / 2;

        // Guide NPC
        java.util.List<DialogBlock> guideBlocks = new java.util.ArrayList<>();
        guideBlocks.add(new DialogBlock("Welcome to the Hub! This is a safe area where you can prepare for battle.", null));
        guideBlocks.add(new DialogBlock("Make sure to stock up on supplies before heading out.", null));
        guideBlocks.add(new DialogBlock("Press 'E' anytime you see this indicator above an NPC to talk to them.", null));
        npcs.add(new NPC(centerX - 200, centerY - 100, "Guide", guideBlocks));

        // Blacksmith NPC
        java.util.List<DialogBlock> blacksmithBlocks = new java.util.ArrayList<>();
        java.util.List<DialogChoice> blacksmithChoices = new java.util.ArrayList<>();
        blacksmithBlocks.add(new DialogBlock("Welcome to my shop! I have all sorts of items for sale.", blacksmithChoices));
        blacksmithChoices.add(new DialogChoice("Chose your gear", "screen:loadout"));
        blacksmithChoices.add(new DialogChoice("Upgrade your gear", "next"));
        blacksmithChoices.add(new DialogChoice("Help", "next"));
        blacksmithChoices.add(new DialogChoice("Leave", "next"));
        npcs.add(new NPC(centerX + 200, centerY - 100, "Blacksmith", blacksmithBlocks));

        // Quest Giver NPC
        java.util.List<DialogBlock> questBlocks = new java.util.ArrayList<>();
        java.util.List<DialogChoice> questChoices = new java.util.ArrayList<>();
        questBlocks.add(new DialogBlock("Come to me to manage your quests.", questChoices));
        questChoices.add(new DialogChoice("Daily Quests", "next"));
        questChoices.add(new DialogChoice("Weekly Quests", "next"));
        questChoices.add(new DialogChoice("Seasonal Quests", "next"));
        questChoices.add(new DialogChoice("Milestone Quests", "next"));
        questChoices.add(new DialogChoice("Help", "next"));
        questChoices.add(new DialogChoice("Leave", "next"));
        npcs.add(new NPC(centerX, centerY - 200, "Quest Giver", questBlocks));

        // Shopkeeper NPC
        java.util.List<DialogBlock> shopBlocks = new java.util.ArrayList<>();
        java.util.List<DialogChoice> shopChoices = new java.util.ArrayList<>();
        shopBlocks.add(new DialogBlock("Welcome to my shop! I have all sorts of items for sale.", shopChoices));
        shopChoices.add(new DialogChoice("Enter Shop", "screen:shop"));
        shopChoices.add(new DialogChoice("Help", "next"));
        shopChoices.add(new DialogChoice("Leave", "next"));
        npcs.add(new NPC(centerX, centerY + 200, "Shopkeeper", shopBlocks));

        // Crafting NPC
        java.util.List<DialogBlock> crafterBlocks = new java.util.ArrayList<>();
        java.util.List<DialogChoice> crafterChoices = new java.util.ArrayList<>();
        crafterBlocks.add(new DialogBlock("Need a fusion? I can turn old gear into new power.", crafterChoices));
        crafterChoices.add(new DialogChoice("Open Crafting", "screen:crafting"));
        crafterChoices.add(new DialogChoice("Leave", "next"));
        npcs.add(new NPC(centerX + 280, centerY + 110, "Artificer", crafterBlocks));

        // Interactable Objects
        // Skill Tree
        java.util.List<DialogBlock> skillTreeBlocks = new java.util.ArrayList<>();
        java.util.List<DialogChoice> skillTreeChoices = new java.util.ArrayList<>();
        skillTreeBlocks.add(new DialogBlock("Enter here to unlock upgrades for your character.", skillTreeChoices));
        skillTreeChoices.add(new DialogChoice("Enter", "next"));
        skillTreeChoices.add(new DialogChoice("Help", "next"));
        skillTreeChoices.add(new DialogChoice("Leave", "next"));
        interactables.add(new InteractableObject(centerX - 300, centerY, "Skill Tree", null, skillTreeBlocks));

        interactables.add(new InteractableObject(centerX + 300, centerY, "Your Home", null, java.util.List.of()));
        
        // Portal to Arena Selection
        java.util.List<DialogBlock> portalBlocks = new java.util.ArrayList<>();
        java.util.List<DialogChoice> portalChoices = new java.util.ArrayList<>();
        portalBlocks.add(new DialogBlock("The portal glows ominously. Step through to enter an arena.", portalChoices));
        portalChoices.add(new DialogChoice("Enter", "screen:arenaselection"));
        portalChoices.add(new DialogChoice("Leave", "next"));
        interactables.add(new InteractableObject(centerX, centerY + 300, "Portal", null, portalBlocks));

        // Locker
        java.util.List<DialogBlock> lockerBlocks = new java.util.ArrayList<>();
        java.util.List<DialogChoice> lockerChoices = new java.util.ArrayList<>();
        lockerBlocks.add(new DialogBlock("Enter here to change your appearance.", lockerChoices));
        lockerChoices.add(new DialogChoice("Enter", "next"));
        lockerChoices.add(new DialogChoice("Help", "next"));
        lockerChoices.add(new DialogChoice("Leave", "next"));
        interactables.add(new InteractableObject(centerX - 400, centerY, "Locker", null, lockerBlocks));

        // Crafting Station
        java.util.List<DialogBlock> craftingBlocks = new java.util.ArrayList<>();
        java.util.List<DialogChoice> craftingChoices = new java.util.ArrayList<>();
        craftingBlocks.add(new DialogBlock("Enter here to craft and merge items.", craftingChoices));
        craftingChoices.add(new DialogChoice("Enter", "screen:crafting"));
        craftingChoices.add(new DialogChoice("Help", "next"));
        craftingChoices.add(new DialogChoice("Leave", "next"));
        interactables.add(new InteractableObject(centerX + 400, centerY, "Crafting Station", null, craftingBlocks));
    }

    public void resetMouseClicks(MouseHandler mouse) {
        player.resetMouseClicks(mouse);
    }

    private void updateNPCs(KeyHandler key) {
        // Update NPC in-range status for all NPCs
        for (NPC npc : npcs) {
            boolean inRange = npc.isInRange(player.getCenterX(), player.getCenterY());
            npc.setInRange(inRange);
        }

        // Update InteractableObject in-range status
        for (InteractableObject obj : interactables) {
            boolean inRange = obj.isInRange(player.getCenterX(), player.getCenterY());
            obj.setInRange(inRange);
        }

        // Check for dialog advance with Space or E
        boolean eJustPressed = key.ePressed && !ePressedLastFrame;
        boolean spaceJustPressed = key.spacePressed && !spacePressedLastFrame;

        // If currently in dialog, handle dialog progression
        if (inDialog && activeNPC != null) {
            if (eJustPressed || spaceJustPressed) {
                String action = activeNPC.advanceDialog();
                if (action != null && action.startsWith("screen:")) {
                    gamePanel.switchScreen(action.substring(7));
                }
                if (!activeNPC.isShowingDialog()) {
                    // Dialog finished
                    inDialog = false;
                    activeNPC = null;
                }
            }

            // Handle choice cycling with W and S keys
            boolean wJustPressed = key.upPressed && !upPressedLastFrame;
            if (wJustPressed) {
                activeNPC.cycleChoiceUp();
            }
            boolean sJustPressed = key.downPressed && !downPressedLastFrame;
            if (sJustPressed) {
                activeNPC.cycleChoiceDown();
            }

            // Update key state tracking
            ePressedLastFrame = key.ePressed;
            spacePressedLastFrame = key.spacePressed;
            upPressedLastFrame = key.upPressed;
            downPressedLastFrame = key.downPressed;
            return;
        }

        // Handle dialog for interactable objects
        if (inDialog && activeInteractable != null) {
            if (eJustPressed || spaceJustPressed) {
                String action = activeInteractable.advanceDialog();
                if (action != null && action.startsWith("screen:")) {
                    gamePanel.switchScreen(action.substring(7));
                }
                if (!activeInteractable.isShowingDialog()) {
                    // Dialog finished
                    inDialog = false;
                    activeInteractable = null;
                }
            }

            // Handle choice cycling with W and S keys
            boolean wJustPressed = key.upPressed && !upPressedLastFrame;
            if (wJustPressed) {
                activeInteractable.cycleChoiceUp();
            }
            boolean sJustPressed = key.downPressed && !downPressedLastFrame;
            if (sJustPressed) {
                activeInteractable.cycleChoiceDown();
            }

            // Update key state tracking
            ePressedLastFrame = key.ePressed;
            spacePressedLastFrame = key.spacePressed;
            upPressedLastFrame = key.upPressed;
            downPressedLastFrame = key.downPressed;
            return;
        }

        // Check for NPC interaction (E key only to start)
        if (eJustPressed) {
            for (NPC npc : npcs) {
                if (npc.isInRange(player.getCenterX(), player.getCenterY())) {
                    if (npc.getScreenName() != null) {
                        // Switch to screen instead of dialog
                        gamePanel.switchScreen(npc.getScreenName());
                    } else {
                        activeNPC = npc;
                        activeNPC.startDialog();
                        inDialog = true;
                    }
                    break;
                }
            }

            // Check for InteractableObject interaction
            if (!inDialog) {
                for (InteractableObject obj : interactables) {
                    if (obj.isInRange(player.getCenterX(), player.getCenterY())) {
                        String screen = obj.getScreenName();
                        if (screen != null && gamePanel != null) {
                            gamePanel.switchScreen(screen);
                        } else {
                            activeInteractable = obj;
                            activeInteractable.startDialog();
                            inDialog = true;
                        }
                        break;
                    }
                }
            }
        }

        // Update key state tracking
        ePressedLastFrame = key.ePressed;
        spacePressedLastFrame = key.spacePressed;
    }

    public void update(KeyHandler key, MouseHandler mouse, int screenWidth, int screenHeight) {
        int arenaWidth  = arena.getWidth();
        int arenaHeight = arena.getHeight();

        lastMouseX = mouse.mouseX;
        lastMouseY = mouse.mouseY;
        hubAdminToggleHovered = hubAdminToggleBtn.contains(lastMouseX, lastMouseY);
        hubResetSaveHovered = hubResetSaveBtn.contains(lastMouseX, lastMouseY);

        // Handle pause with P key
        if (key.pPressed && !pPressedLastFrame) {
            paused = !paused;
        }
        pPressedLastFrame = key.pPressed;

        // If paused, skip all update logic
        if (paused) {
            return;
        }

        // Handle NPC dialog
        updateNPCs(key);

        // Toggle debug mode with O key
        if (key.oPressed) {
            debugMode = !debugMode;
            key.oPressed = false;
        }


        // Handle hotbar scrolling
        if (mouse.scrollDirection != 0) {
            int currentSlot = hud.getInventoryUI().getSelectedSlot();
            int newSlot = currentSlot + mouse.scrollDirection;
            if (newSlot >= 5) newSlot = 0;
            if (newSlot < 0) newSlot = 4;
            hud.getInventoryUI().setSelectedSlot(newSlot);
            player.equipHotbarSlot(newSlot);
            mouse.leftClicked = false;
            mouse.resetScroll();
        }

        // Handle inventory toggle with Tab key
        if (key.tabPressed && !tabPressedLastFrame) {
            inventoryOpen = !inventoryOpen;
        }
        tabPressedLastFrame = key.tabPressed;

        // Update power key states
        onePressedLastFrame = key.onePressed;
        twoPressedLastFrame = key.twoPressed;
        threePressedLastFrame = key.threePressed;
        fourPressedLastFrame = key.fourPressed;

        // Update inventory hover
        if (inventoryOpen) {
            int playerScreenX = player.getCenterX() - camera.x;
            int playerScreenY = player.getCenterY() - camera.y;
            inventoryScreen.updatePositions(playerScreenX, playerScreenY, screenWidth, screenHeight, 0, 0, false);
            int invSlot = inventoryScreen.getSlotAtPosition(mouse.mouseX, mouse.mouseY);
            inventoryScreen.setHoveredSlot(invSlot);
        } else {
            inventoryScreen.setHoveredSlot(-1);
        }

        // Handle mouse drag and drop
        handleDragAndDrop(mouse.leftPressed, mouse.leftClicked, mouse.mouseX, mouse.mouseY, screenWidth, screenHeight);

        if (!inDialog) {
            // Handle player movement
            // Calculate intended movement
            int newX = player.getX();
            int newY = player.getY();
            int speed = (int) (player.getSpeed() * 0.5); // Reduce speed in hub to match arena feel

            if (key.upPressed)    newY -= speed;
            if (key.downPressed)  newY += speed;
            if (key.leftPressed)  newX -= speed;
            if (key.rightPressed) newX += speed;

            // Clamp to arena bounds
            newX = Math.max(0, Math.min(newX, arenaWidth - player.getW()));
            newY = Math.max(0, Math.min(newY, arenaHeight - player.getL()));

            player.setX(newX);
            player.setY(newY);

            // Use the shared shooting mechanics method
            player.handleShootingMechanics(mouse, key, inventoryOpen || draggedItem != null, null, arenaWidth, arenaHeight);
        }
        
        camera.follow(player.getX(), player.getY(), player.getW(), player.getL(), screenWidth, screenHeight, arenaWidth, arenaHeight);
        player.setCameraOffset(camera.x, camera.y);
        player.aimBarrel(mouse.mouseX + camera.x, mouse.mouseY + camera.y);
        
        // Auto-save stats periodically
        performAutoSave();
    }
    
    /**
     * Periodically auto-save player stats to prevent data loss
     */
    private void performAutoSave() {
        long currentTime = System.currentTimeMillis();
        if (lastAutoSaveTime == 0) {
            lastAutoSaveTime = currentTime;
        }
        
        // Auto-save every interval or when kills change
        int currentKills = player.getStats().getKills();
        if (currentTime - lastAutoSaveTime >= AUTO_SAVE_INTERVAL || currentKills > lastSavedKills) {
            save.SaveManager.quickSaveStats(currentKills, (int) player.getStats().getDamageDealt());
            lastAutoSaveTime = currentTime;
            lastSavedKills = currentKills;
            if (lastSavedKills % 5 == 0 && lastSavedKills > 0) {
                System.out.println("Auto-saved: " + lastSavedKills + " kills");
            }
        }
    }

    public void draw(Graphics2D g, int screenWidth, int screenHeight) {
        arena.draw(g, screenWidth, screenHeight, camera.x, camera.y);

        // Draw NPCs
        for (NPC npc : npcs) {
            npc.draw(g, camera.x, camera.y);
        }

        // Draw InteractableObjects
        for (InteractableObject obj : interactables) {
            obj.draw(g, camera.x, camera.y);
        }

        player.draw(g, camera.x, camera.y);

        // Draw NPC dialogs (after player so dialog appears on top)
        for (NPC npc : npcs) {
            if (npc.isShowingDialog()) {
                npc.drawDialog(g, camera.x, camera.y);
            }
        }

        // Draw InteractableObject dialogs
        for (InteractableObject obj : interactables) {
            if (obj.isShowingDialog()) {
                obj.drawDialog(g, camera.x, camera.y);
            }
        }

        // Draw inventory UI if open
        if (inventoryOpen) {
            int playerScreenX = player.getCenterX() - camera.x;
            int playerScreenY = player.getCenterY() - camera.y;
            inventoryScreen.draw(g, player.getInventory(), playerScreenX, playerScreenY, screenWidth, screenHeight, 0, 0, false);
        }

        hud.draw(g, player, screenWidth, screenHeight);

        // Draw power moves UI if power is equipped
        drawPowerMovesUI(g, screenWidth, screenHeight);

        // Draw player projectiles
        for (combat.Projectile p : player.getProjectiles()) {
            g.setColor(p.getColor());
            int radius = p.getRadius();
            g.fillOval(p.getX() - camera.x - radius, p.getY() - camera.y - radius, radius * 2, radius * 2);
        }

        // Draw dragged item following mouse
        if (draggedItem != null) {
            g.setColor(Color.YELLOW);
            int dragSize = 40;
            g.fillRect(lastMouseX - dragSize / 2, lastMouseY - dragSize / 2, dragSize, dragSize);
            g.setColor(Color.WHITE);
            g.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 10));
            String name = draggedItem.getName();
            if (name != null) {
                g.drawString(name, lastMouseX - dragSize / 2 + 2, lastMouseY - dragSize / 2 + 14);
            }
        }

        // Draw debug mode status
        g.setColor(Color.WHITE);
        g.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 14));
        String debugStatus = "debug mode: " + (debugMode ? "on" : "off");
        java.awt.FontMetrics fm = g.getFontMetrics();
        int textWidth = fm.stringWidth(debugStatus);
        g.drawString(debugStatus, screenWidth - textWidth - 10, 20);

        drawHubDevButtons(g);

        // Draw pause menu if paused
        if (paused) {
            drawPauseMenu(g, screenWidth, screenHeight);
        }
    }

    private void handleDragAndDrop(boolean leftPressed, boolean leftClicked, int mouseX, int mouseY, int screenWidth, int screenHeight) {
        // Reset drag if inventory is not open
        if (!inventoryOpen) {
            draggedItem = null;
            dragSource = -1;
            dragSourceSlot = -1;
            return;
        }

        // Mouse press - start drag
        if (leftPressed && draggedItem == null && !wasLeftPressedLastFrame) {
            int inventorySlot = getInventorySlotAtPositionSimple(mouseX, mouseY, screenWidth, screenHeight);
            if (inventorySlot >= 0 && inventorySlot < 5) {
                Object item = player.getHotbar().get(inventorySlot);
                if (item instanceof combat.Item) {
                    draggedItem = (combat.Item) item;
                    dragSource = -1;
                    dragSourceSlot = inventorySlot;
                }
            }

            // Check inventory slots for drag start
            if (draggedItem == null && inventoryOpen) {
                int playerScreenX = player.getCenterX() - camera.x;
                int playerScreenY = player.getCenterY() - camera.y;
                inventoryScreen.updatePositions(playerScreenX, playerScreenY, screenWidth, screenHeight, 0, 0, false);
                int invSlot = inventoryScreen.getSlotAtPosition(mouseX, mouseY);
                if (invSlot >= 0 && invSlot <= 4) {
                    if (invSlot >= 0 && invSlot <= 2) {
                        // Charm slot
                        Charm charm = player.getInventory().getCharm(invSlot);
                        if (charm != null) {
                            draggedItem = charm;
                            dragSource = 1;
                            dragSourceSlot = invSlot;
                        }
                    } else if (invSlot == 3) {
                        // Power slot
                        Power power = player.getInventory().getPower();
                        if (power != null) {
                            draggedItem = power;
                            dragSource = 2;
                            dragSourceSlot = invSlot;
                        }
                    } else if (invSlot == 4) {
                        // Summon slot
                        combat.summons.Summon summon = player.getInventory().getSummon();
                        if (summon != null) {
                            draggedItem = summon;
                            dragSource = 3;
                            dragSourceSlot = invSlot;
                        }
                    }
                }
            }
        }
        wasLeftPressedLastFrame = leftPressed;

        // Mouse release - end drag
        if (!leftPressed && draggedItem != null) {
            boolean placed = false;

            int inventorySlot = getInventorySlotAtPositionSimple(mouseX, mouseY, screenWidth, screenHeight);
            if (inventorySlot >= 0 && inventorySlot < 5) {
                if (dragSource == -1 && inventorySlot != dragSourceSlot) {
                    // From inventory to different inventory slot - swap
                    Object destItem = player.getHotbar().get(inventorySlot);
                    player.getHotbar().set(dragSourceSlot, destItem);
                    player.getHotbar().set(inventorySlot, draggedItem);
                    placed = true;
                } else if (dragSource >= 1 && dragSource <= 3) {
                    // From inventory slots to hotbar
                    Object destItem = player.getHotbar().get(inventorySlot);
                    if (destItem == null) {
                        player.getHotbar().set(inventorySlot, draggedItem);
                        removeFromInventorySlot(dragSource, dragSourceSlot);
                        placed = true;
                    }
                }
            }

            // Check inventory slots (charm/power/summon)
            if (!placed && inventoryOpen) {
                int playerScreenX = player.getCenterX() - camera.x;
                int playerScreenY = player.getCenterY() - camera.y;
                inventoryScreen.updatePositions(playerScreenX, playerScreenY, screenWidth, screenHeight, 0, 0, false);
                int invSlot = inventoryScreen.getSlotAtPosition(mouseX, mouseY);
                if (invSlot >= 0 && invSlot <= 4 && inventoryScreen.isValidDrop(invSlot, draggedItem)) {
                    if (dragSource == -1) {
                        // From hotbar to inventory
                        if (invSlot >= 0 && invSlot <= 2) {
                            if (player.getInventory().getCharm(invSlot) == null) {
                                player.getInventory().equipCharm(invSlot, (Charm) draggedItem);
                                player.getHotbar().set(dragSourceSlot, null);
                                placed = true;
                            }
                        } else if (invSlot == 3) {
                            if (player.getInventory().getPower() == null) {
                                player.getInventory().equipPower((Power) draggedItem);
                                player.getHotbar().set(dragSourceSlot, null);
                                placed = true;
                            }
                        } else if (invSlot == 4) {
                            if (player.getInventory().getSummon() == null) {
                                player.getInventory().equipSummon((combat.summons.Summon) draggedItem);
                                player.getHotbar().set(dragSourceSlot, null);
                                placed = true;
                            }
                        }
                    } else if (dragSource >= 1 && dragSource <= 3 && invSlot != dragSourceSlot) {
                        // Between inventory slots - only allow if destination is empty
                        if (invSlot >= 0 && invSlot <= 2 && dragSource == 1) {
                            if (player.getInventory().getCharm(invSlot) == null) {
                                player.getInventory().removeCharm(dragSourceSlot);
                                player.getInventory().equipCharm(invSlot, (Charm) draggedItem);
                                placed = true;
                            }
                        } else if (invSlot == 3 && dragSource == 2) {
                            if (player.getInventory().getPower() == null) {
                                player.getInventory().removePower();
                                player.getInventory().equipPower((Power) draggedItem);
                                placed = true;
                            }
                        } else if (invSlot == 4 && dragSource == 3) {
                            if (player.getInventory().getSummon() == null) {
                                player.getInventory().removeSummon();
                                player.getInventory().equipSummon((combat.summons.Summon) draggedItem);
                                placed = true;
                            }
                        }
                    }
                }
            }

            draggedItem = null;
            dragSource = -1;
            dragSourceSlot = -1;

            // Re-equip selected hotbar slot
            player.equipHotbarSlot(hud.getInventoryUI().getSelectedSlot());
        }
    }

    private int getInventorySlotAtPositionSimple(int mouseX, int mouseY, int screenWidth, int screenHeight) {
        int slotSize = 50;
        int slotSpacing = 5;
        int hotbarSize = 5;
        int totalWidth = (slotSize * hotbarSize) + (slotSpacing * (hotbarSize - 1));
        int startX = (screenWidth - totalWidth) / 2;
        int startY = screenHeight - slotSize - 20;

        for (int i = 0; i < 5; i++) {
            int slotX = startX + (i * (slotSize + slotSpacing));
            if (mouseX >= slotX && mouseX < slotX + slotSize && mouseY >= startY && mouseY < startY + slotSize) {
                return i;
            }
        }
        return -1;
    }

    private void removeFromInventorySlot(int dragSource, int dragSourceSlot) {
        if (dragSource == 1) {
            player.getInventory().removeCharm(dragSourceSlot);
        } else if (dragSource == 2) {
            player.getInventory().removePower();
        } else if (dragSource == 3) {
            player.getInventory().removeSummon();
        }
    }

    private void handlePowerMoves(util.KeyHandler key) {
        Power equippedPower = player.getInventory().getPower();
        if (equippedPower == null) return;

        if (key.onePressed && !onePressedLastFrame) {
            equippedPower.useMove(1);
        }
        if (key.twoPressed && !twoPressedLastFrame) {
            equippedPower.useMove(2);
        }
        if (key.threePressed && !threePressedLastFrame) {
            equippedPower.useMove(3);
        }
        if (key.fourPressed && !fourPressedLastFrame) {
            equippedPower.useMove(4);
        }
    }

    private void drawPowerMovesUI(Graphics2D g, int screenWidth, int screenHeight) {
        Power equippedPower = player.getInventory().getPower();
        if (equippedPower == null) return;

        int maxMoves = equippedPower.getMaxMoves();
        if (maxMoves <= 0) return;

        int boxWidth = 200;
        int boxHeight = 40;
        int boxSpacing = 5;
        int padding = 20;

        int startX = screenWidth - boxWidth - padding;

        for (int i = maxMoves; i >= 1; i--) {
            Move move = equippedPower.getMove(i);
            if (move == null) continue;

            int slotIndexFromBottom = maxMoves - i;
            int y = screenHeight - padding - boxHeight - (slotIndexFromBottom * (boxHeight + boxSpacing));

            g.setColor(new Color(50, 50, 70, 200));
            g.fillRect(startX, y, boxWidth, boxHeight);

            g.setColor(Color.WHITE);
            g.drawRect(startX, y, boxWidth, boxHeight);

            g.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 16));
            g.setColor(Color.WHITE);
            String moveName = move.getName();
            g.drawString(moveName, startX + 10, y + 26);

            g.setColor(new Color(100, 100, 150));
            g.fillRect(startX + boxWidth - 30, y, 30, boxHeight);
            g.setColor(Color.WHITE);
            g.drawRect(startX + boxWidth - 30, y, 30, boxHeight);

            String number = String.valueOf(i);
            java.awt.FontMetrics fm = g.getFontMetrics();
            int numberWidth = fm.stringWidth(number);
            int numberX = startX + boxWidth - 15 - (numberWidth / 2);
            g.drawString(number, numberX, y + 26);
        }
    }

    public Player getPlayer() {
        return player;
    }

    private void drawPauseMenu(Graphics2D g, int screenWidth, int screenHeight) {
        // Calculate menu box dimensions
        int menuWidth = screenWidth - (PAUSE_MENU_MARGIN * 2);
        int menuHeight = screenHeight - (PAUSE_MENU_MARGIN * 2);
        int menuX = PAUSE_MENU_MARGIN;
        int menuY = PAUSE_MENU_MARGIN;

        // Draw translucent background
        g.setColor(new Color(0, 0, 0, 180));
        g.fillRect(0, 0, screenWidth, screenHeight);

        // Draw menu box
        g.setColor(new Color(50, 50, 70, 220));
        g.fillRect(menuX, menuY, menuWidth, menuHeight);
        g.setColor(Color.WHITE);
        g.drawRect(menuX, menuY, menuWidth, menuHeight);

        // Draw title
        g.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 48));
        String title = "PAUSED";
        java.awt.FontMetrics fm = g.getFontMetrics();
        int titleWidth = fm.stringWidth(title);
        int titleX = menuX + (menuWidth - titleWidth) / 2;
        int titleY = menuY + 80;
        g.drawString(title, titleX, titleY);

        // Button dimensions
        int buttonWidth = 200;
        int buttonHeight = 50;
        int buttonSpacing = 20;
        int totalButtonHeight = (buttonHeight * 4) + (buttonSpacing * 3);
        int startButtonY = menuY + (menuHeight - totalButtonHeight) / 2;

        // Resume button
        int settingsX = menuX + (menuWidth - buttonWidth) / 2;
        int resumeY = startButtonY;
        drawPauseButton(g, "Resume", settingsX, resumeY, buttonWidth, buttonHeight);

        // Settings button
        int settingsY = resumeY + buttonHeight + buttonSpacing;
        drawPauseButton(g, "Settings", settingsX, settingsY, buttonWidth, buttonHeight);

        // Help button
        int helpY = settingsY + buttonHeight + buttonSpacing;
        drawPauseButton(g, "Help", settingsX, helpY, buttonWidth, buttonHeight);

        // Quit button
        int quitY = helpY + buttonHeight + buttonSpacing;
        drawPauseButton(g, "Quit", settingsX, quitY, buttonWidth, buttonHeight);

        // Instructions
        g.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 16));
        String instructions = "Press P to resume";
        int instructionsWidth = g.getFontMetrics().stringWidth(instructions);
        int instructionsX = menuX + (menuWidth - instructionsWidth) / 2;
        int instructionsY = menuY + menuHeight - 40;
        g.drawString(instructions, instructionsX, instructionsY);
    }

    private void drawHubDevButtons(Graphics2D g) {
        boolean adminOn = gamePanel != null && gamePanel.isSessionAdminMode();
        Color adminFill = hubAdminToggleHovered
                ? (adminOn ? new Color(90, 170, 120) : new Color(210, 130, 55))
                : (adminOn ? new Color(60, 130, 85) : new Color(145, 88, 32));
        g.setColor(adminFill);
        g.fillRoundRect(hubAdminToggleBtn.x, hubAdminToggleBtn.y, hubAdminToggleBtn.width, hubAdminToggleBtn.height, 8, 8);
        g.setColor(new Color(40, 38, 36));
        g.setStroke(new java.awt.BasicStroke(1.5f));
        g.drawRoundRect(hubAdminToggleBtn.x, hubAdminToggleBtn.y, hubAdminToggleBtn.width, hubAdminToggleBtn.height, 8, 8);
        g.setStroke(new java.awt.BasicStroke(1f));
        g.setColor(Color.WHITE);
        g.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 12));
        String adminLabel = adminOn ? "Admin: ON" : "Admin: OFF";
        g.drawString(adminLabel, hubAdminToggleBtn.x + 18, hubAdminToggleBtn.y + 26);

        Color resetFill = hubResetSaveHovered ? new Color(200, 70, 70) : new Color(150, 45, 45);
        g.setColor(resetFill);
        g.fillRoundRect(hubResetSaveBtn.x, hubResetSaveBtn.y, hubResetSaveBtn.width, hubResetSaveBtn.height, 8, 8);
        g.setColor(new Color(40, 38, 36));
        g.setStroke(new java.awt.BasicStroke(1.5f));
        g.drawRoundRect(hubResetSaveBtn.x, hubResetSaveBtn.y, hubResetSaveBtn.width, hubResetSaveBtn.height, 8, 8);
        g.setStroke(new java.awt.BasicStroke(1f));
        g.setColor(Color.WHITE);
        g.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 13));
        g.drawString("RESET SAVE", hubResetSaveBtn.x + 14, hubResetSaveBtn.y + 27);
    }

    public boolean isPaused() {
        return paused;
    }

    public void handleHubDevClick(int mouseX, int mouseY, int screenWidth, int screenHeight) {
        if (paused) return;
        if (gamePanel == null) return;
        if (hubAdminToggleBtn.contains(mouseX, mouseY)) {
            gamePanel.toggleSessionAdminMode();
            return;
        }
        if (hubResetSaveBtn.contains(mouseX, mouseY)) {
            gamePanel.resetSaveAndRelaunch();
        }
    }

    private void drawPauseButton(Graphics2D g, String text, int x, int y, int width, int height) {
        // Button background
        g.setColor(new Color(100, 100, 150, 200));
        g.fillRect(x, y, width, height);

        // Button border
        g.setColor(Color.WHITE);
        g.drawRect(x, y, width, height);

        // Button text
        g.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 20));
        java.awt.FontMetrics fm = g.getFontMetrics();
        int textWidth = fm.stringWidth(text);
        int textHeight = fm.getAscent();
        int textX = x + (width - textWidth) / 2;
        int textY = y + (height + textHeight) / 2 - 5;
        g.drawString(text, textX, textY);
    }

    public void handlePauseMenuClick(int mouseX, int mouseY, int screenWidth, int screenHeight) {
        if (!paused) return;

        // Calculate menu box dimensions
        int menuWidth = screenWidth - (PAUSE_MENU_MARGIN * 2);
        int menuHeight = screenHeight - (PAUSE_MENU_MARGIN * 2);
        int menuX = PAUSE_MENU_MARGIN;
        int menuY = PAUSE_MENU_MARGIN;

        // Button dimensions
        int buttonWidth = 200;
        int buttonHeight = 50;
        int buttonSpacing = 20;
        int totalButtonHeight = (buttonHeight * 4) + (buttonSpacing * 3);
        int startButtonY = menuY + (menuHeight - totalButtonHeight) / 2;

        int buttonX = menuX + (menuWidth - buttonWidth) / 2;

        // Resume button
        int resumeY = startButtonY;
        if (mouseX >= buttonX && mouseX <= buttonX + buttonWidth &&
            mouseY >= resumeY && mouseY <= resumeY + buttonHeight) {
            paused = false;
            return;
        }

        // Settings button
        int settingsY = resumeY + buttonHeight + buttonSpacing;
        if (mouseX >= buttonX && mouseX <= buttonX + buttonWidth &&
            mouseY >= settingsY && mouseY <= settingsY + buttonHeight) {
            if (gamePanel != null) {
                gamePanel.switchScreen("settings");
            }
            return;
        }

        // Help button
        int helpY = settingsY + buttonHeight + buttonSpacing;
        if (mouseX >= buttonX && mouseX <= buttonX + buttonWidth &&
            mouseY >= helpY && mouseY <= helpY + buttonHeight) {
            if (gamePanel != null) {
                gamePanel.switchScreen("help");
            }
            return;
        }

        // Quit button
        int quitY = helpY + buttonHeight + buttonSpacing;
        if (mouseX >= buttonX && mouseX <= buttonX + buttonWidth &&
            mouseY >= quitY && mouseY <= quitY + buttonHeight) {
            if (gamePanel != null) {
                gamePanel.switchScreen("menu");
            }
            return;
        }
    }
}
