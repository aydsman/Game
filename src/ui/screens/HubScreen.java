package ui.screens;

import combat.charms.Charm;
import entity.EnemyManager;
import entity.Player;
import entity.npc.NPC;
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

    // NPCs
    private ArrayList<NPC> npcs = new ArrayList<>();
    private NPC activeNPC = null;
    private boolean inDialog = false;
    private boolean spacePressedLastFrame = false;
    
    // Auto-save tracking
    private long lastAutoSaveTime = 0;
    private static final long AUTO_SAVE_INTERVAL = 10000; // Auto-save every 10 seconds
    private int lastSavedKills = 0;

    public HubScreen() {
        // No waves in hub
    }

    public HubScreen(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
        initializeNPCs();
    }

    private void initializeNPCs() {
        // Create test NPC near center of hub
        java.util.List<String> messages = new java.util.ArrayList<>();
        messages.add("Welcome to the Hub! This is a safe area where you can prepare for battle.");
        messages.add("Make sure to stock up on supplies before heading out.");
        messages.add("Press 'E' anytime you see this indicator above an NPC to talk to them.");
        npcs.add(new NPC(arena.getWidth() / 2 - 200, arena.getHeight() / 2 - 100, "Guide", messages));
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

        // Check for dialog advance with Space or E
        boolean eJustPressed = key.ePressed && !ePressedLastFrame;
        boolean spaceJustPressed = key.spacePressed && !spacePressedLastFrame;

        // If currently in dialog, handle dialog progression
        if (inDialog && activeNPC != null) {
            if (eJustPressed || spaceJustPressed) {
                activeNPC.advanceDialog();
                if (!activeNPC.isShowingDialog()) {
                    // Dialog finished
                    inDialog = false;
                    activeNPC = null;
                }
            }

            // Update key state tracking
            ePressedLastFrame = key.ePressed;
            spacePressedLastFrame = key.spacePressed;
            return;
        }

        // Check for NPC interaction (E key only to start)
        if (eJustPressed) {
            for (NPC npc : npcs) {
                if (npc.isInRange(player.getCenterX(), player.getCenterY())) {
                    activeNPC = npc;
                    activeNPC.startDialog();
                    inDialog = true;
                    break;
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

        // Store mouse position for draw method
        lastMouseX = mouse.mouseX;
        lastMouseY = mouse.mouseY;

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

        // Handle player movement
        int newX = player.getX();
        int newY = player.getY();
        int speed = (int) player.getSpeed();

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

        player.draw(g, camera.x, camera.y);

        // Draw NPC dialogs (after player so dialog appears on top)
        for (NPC npc : npcs) {
            if (npc.isShowingDialog()) {
                npc.drawDialog(g, camera.x, camera.y);
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
