package ui.screens;

import combat.Item;
import combat.ItemRegistry;
import combat.Ranged;
import combat.Melee;
import combat.charms.Charm;
import combat.charms.Charm1;
import combat.charms.DamageCharm1;
import combat.charms.DamageCharm2;
import combat.charms.DamageCharm3;
import combat.summons.Summon;
import combat.summons.Summon1;
import combat.powers.Power;
import combat.powers.Move;
import combat.powers.Fire;
import combat.powers.Light;
import combat.powers.Earth;
import combat.powers.Lightning;
import combat.powers.Water;
import combat.powers.Magma;
import combat.powers.Overgrowth;
import combat.powers.Haki;
import combat.powers.Sharingan;
import combat.powers.Rinnegan;
import combat.powers.EarthV2;
import combat.powers.FireV2;
import combat.powers.LightningV2;
import combat.powers.MagmaV2;
import combat.powers.WaterV2;
import combat.powers.Infinity;
import combat.powers.KingOfCurses;
import combat.powers.RinneSharingan;
import combat.consumables.Consumable;
import combat.consumables.Consumable1;
import combat.consumables.SpeedPotion1;
import combat.consumables.SpeedPotion2;
import combat.consumables.SpeedPotion3;
import combat.ranged.pistols.Pistol1;
import combat.ranged.rifles.Rifle1;
import combat.ranged.shotguns.Shotgun1;
import combat.ranged.smgs.SMG1;
import combat.ranged.snipers.Sniper1;
import combat.melee.swords.Sword1;
import combat.melee.hammers.Hammer1;
import combat.melee.daggers.Dagger1;
import combat.melee.maces.Mace1;
import combat.melee.scythes.Scythe1;
import combat.clothing.ClothingItem;
import combat.clothing.ClothingType;
import combat.clothing.tops.BoxyTee;
import combat.clothing.tops.Hoodie;
import combat.clothing.tops.LongSleeve;
import combat.clothing.tops.SuitTop;
import combat.clothing.tops.TankTop;
import combat.clothing.tops.TShirt;
import combat.clothing.bottoms.BaggyJeans;
import combat.clothing.bottoms.BasicPants;
import combat.clothing.bottoms.Jorts;
import combat.clothing.bottoms.Shorts;
import combat.clothing.bottoms.SuitBottom;
import combat.clothing.bottoms.SwimShorts;
import combat.clothing.bottoms.Sweatpants;
import combat.clothing.accessories.AllSeeingEye;
import combat.clothing.accessories.Aviators;
import combat.clothing.accessories.Cape;
import combat.clothing.accessories.Crown;
import combat.clothing.accessories.Fedora;
import combat.clothing.accessories.Glasses;
import combat.clothing.accessories.Headband;
import combat.clothing.accessories.NinjaHeadband;
import combat.clothing.accessories.Shades;
import entity.Enemy;
import entity.enemies.Enemy1;
import entity.enemies.Enemy2;
import entity.enemies.Enemy3;
import entity.enemies.Enemy4;
import entity.enemies.Enemy5;
import entity.Boss;
import entity.boss.Boss1;
import entity.miniboss.Miniboss1;
import ui.GamePanel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemGalleryScreen {

    private GamePanel gamePanel;
    private ItemRegistry itemRegistry;

    private Rectangle backBtn = new Rectangle(30, 20, 100, 50);
    private String hoveredButton = null;
    private Item selectedItem = null;
    private Enemy selectedEnemy = null;
    private Boss selectedBoss = null;
    private ClothingItem selectedClothingItem = null;

    // Type selector buttons
    private String[] itemTypes = {"Weapons", "Charms", "Summons", "Powers", "Consumables", "Enemies", "Bosses", "Clothes"};
    private Rectangle[] typeButtons = new Rectangle[8];
    private int selectedType = 0;

    // Enemy and Boss lists
    private List<Enemy> enemies = new ArrayList<>();
    private List<Boss> bosses = new ArrayList<>();

    // Icon cache
    private Map<String, BufferedImage> iconCache = new HashMap<>();

    // Grid settings
    private int gridStartX = 50;
    private int gridStartY = 150;
    private int slotSize = 60;
    private int slotSpacing = 10;
    private int itemsPerRow = 10;

    public ItemGalleryScreen(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
        this.itemRegistry = new ItemRegistry();

        enemies.add(new Enemy1(0, 0));
        enemies.add(new Enemy2(0, 0));
        enemies.add(new Enemy3(0, 0));
        enemies.add(new Enemy4(0, 0));
        enemies.add(new Enemy5(0, 0));

        bosses.add(new Boss1(0, 0));
        bosses.add(new Miniboss1(0, 0));

        int btnX = 130;
        int btnY = 20;
        int btnWidth = 100;
        int btnHeight = 35;
        for (int i = 0; i < 8; i++) {
            typeButtons[i] = new Rectangle(btnX + (i * (btnWidth + 10)), btnY, btnWidth, btnHeight);
        }
    }

    public void handleMouseScroll(int scrollDirection) {
        if (selectedItem == null) return;
        if (selectedItem instanceof Ranged || selectedItem instanceof Melee) {
            if (scrollDirection > 0) cycleTier(1);
            else if (scrollDirection < 0) cycleTier(-1);
        }
    }

    private void cycleTier(int direction) {
        int currentTier = selectedItem.getTier();
        int newTier = currentTier + direction;
        if (newTier < 1) newTier = 1;
        if (newTier > 5) newTier = 5;
        if (newTier != currentTier) {
            selectedItem = createItemWithTier(selectedItem.getName(), newTier);
        }
    }

    private Item createItemWithTier(String itemName, int tier) {
        return switch (itemName) {
            case "Pistol1", "Glock" -> new Pistol1(tier);
            case "Rifle1", "AK-47" -> new Rifle1(tier);
            case "Shotgun1", "Pump - Action" -> new Shotgun1(tier);
            case "SMG1", "P90" -> new SMG1(tier);
            case "Sniper1" -> new Sniper1(tier);
            case "Sword1" -> new Sword1(tier);
            case "Hammer1" -> new Hammer1(tier);
            case "Dagger1" -> new Dagger1(tier);
            case "Mace1" -> new Mace1(tier);
            case "Scythe1" -> new Scythe1(tier);
            case "Charm1" -> new Charm1(tier);
            case "DamageCharm1" -> new DamageCharm1(tier);
            case "DamageCharm2" -> new DamageCharm2(tier);
            case "DamageCharm3" -> new DamageCharm3(tier);
            case "Summon1" -> new Summon1(tier);
            case "Fire" -> new Fire(tier);
            case "Light" -> new Light(tier);
            case "Earth" -> new Earth(tier);
            case "Lightning" -> new Lightning(tier);
            case "Water" -> new Water(tier);
            case "Magma" -> new Magma(tier);
            case "Overgrowth" -> new Overgrowth(tier);
            case "Haki" -> new Haki(tier);
            case "Sharingan" -> new Sharingan(tier);
            case "Rinnegan" -> new Rinnegan(tier);
            case "EarthV2" -> new EarthV2(tier);
            case "FireV2" -> new FireV2(tier);
            case "LightningV2" -> new LightningV2(tier);
            case "MagmaV2" -> new MagmaV2(tier);
            case "WaterV2" -> new WaterV2(tier);
            case "Infinity" -> new Infinity(tier);
            case "King of Curses" -> new KingOfCurses(tier);
            case "Rinne Sharingan" -> new RinneSharingan(tier);
            case "Consumable1" -> new Consumable1(tier);
            default -> {
                String normalized = itemName.replace(" ", "").replace("-", "");
                try {
                    Class<?> cls = Class.forName("combat.powers." + normalized);
                    yield (Item) cls.getConstructor(int.class).newInstance(tier);
                } catch (Exception e) {
                    try {
                        Class<?> cls = Class.forName("combat.powers.fruits." + normalized);
                        yield (Item) cls.getConstructor(int.class).newInstance(tier);
                    } catch (Exception ex) {
                        yield selectedItem;
                    }
                }
            }
        };
    }

    public void draw(Graphics2D g) {
        GradientPaint bgGradient = new GradientPaint(0, 0, new Color(30, 30, 40), 0, 900, new Color(20, 20, 30));
        g.setPaint(bgGradient);
        g.fillRect(0, 0, 1600, 900);
        drawBackgroundPattern(g, 1600, 900);

        drawStyledBackButton(g, backBtn, "Back", hoveredButton == "back");

        for (int i = 0; i < itemTypes.length; i++) {
            Color accentColor = getTypeAccentColor(i);
            boolean isSelected = selectedType == i;
            boolean isHovered = hoveredButton != null && hoveredButton.equals("type" + i);
            drawStyledTypeButton(g, typeButtons[i], itemTypes[i], accentColor, isSelected, isHovered);
        }

        if (selectedType == 7) {
            drawClothingGrid(g);
        } else {
            drawItemGrid(g);
        }

        if (selectedItem != null) {
            drawItemDetails(g);
        } else if (selectedEnemy != null) {
            drawEnemyDetails(g);
        } else if (selectedBoss != null) {
            drawBossDetails(g);
        } else if (selectedClothingItem != null) {
            drawClothingDetails(g);
        }
    }

    private Color getTypeAccentColor(int typeIndex) {
        return switch (typeIndex) {
            case 0 -> new Color(231, 76, 60);
            case 1 -> new Color(155, 89, 182);
            case 2 -> new Color(52, 152, 219);
            case 3 -> new Color(241, 196, 15);
            case 4 -> new Color(46, 204, 113);
            case 5 -> new Color(192, 57, 43);
            case 6 -> new Color(230, 126, 34);
            case 7 -> new Color(230, 180, 50);
            default -> new Color(149, 165, 166);
        };
    }

    private void drawStyledBackButton(Graphics2D g, Rectangle btn, String label, boolean isHovered) {
        if (isHovered) {
            GradientPaint gradient = new GradientPaint(btn.x, btn.y, new Color(100, 200, 255), btn.x, btn.y + btn.height, new Color(70, 150, 200));
            g.setPaint(gradient);
        } else {
            GradientPaint gradient = new GradientPaint(btn.x, btn.y, new Color(50, 50, 60), btn.x, btn.y + btn.height, new Color(40, 40, 50));
            g.setPaint(gradient);
        }
        g.fillRoundRect(btn.x, btn.y, btn.width, btn.height, 10, 10);
        g.setPaint(null);
        g.setColor(isHovered ? new Color(100, 200, 255) : new Color(80, 80, 90));
        g.setStroke(new BasicStroke(2));
        g.drawRoundRect(btn.x, btn.y, btn.width, btn.height, 10, 10);
        g.setStroke(new BasicStroke(1));
        g.setColor(Color.WHITE);
        g.setFont(new Font("Segoe UI", Font.BOLD, 16));
        FontMetrics fm = g.getFontMetrics();
        int textX = btn.x + (btn.width - fm.stringWidth(label)) / 2;
        int textY = btn.y + (btn.height + fm.getAscent() - fm.getDescent()) / 2;
        g.drawString(label, textX, textY);
    }

    private void drawStyledTypeButton(Graphics2D g, Rectangle btn, String label, Color accentColor, boolean isSelected, boolean isHovered) {
        if (isSelected) {
            GradientPaint gradient = new GradientPaint(btn.x, btn.y, accentColor, btn.x, btn.y + btn.height, accentColor.darker());
            g.setPaint(gradient);
        } else if (isHovered) {
            GradientPaint gradient = new GradientPaint(btn.x, btn.y, new Color(70, 70, 80), btn.x, btn.y + btn.height, new Color(50, 50, 60));
            g.setPaint(gradient);
        } else {
            GradientPaint gradient = new GradientPaint(btn.x, btn.y, new Color(50, 50, 60), btn.x, btn.y + btn.height, new Color(40, 40, 50));
            g.setPaint(gradient);
        }
        g.fillRoundRect(btn.x, btn.y, btn.width, btn.height, 8, 8);
        g.setPaint(null);
        g.setColor(isSelected ? accentColor : (isHovered ? new Color(100, 200, 255) : new Color(70, 70, 80)));
        g.setStroke(new BasicStroke(isSelected ? 2 : 1));
        g.drawRoundRect(btn.x, btn.y, btn.width, btn.height, 8, 8);
        g.setStroke(new BasicStroke(1));
        g.setColor(isSelected ? Color.WHITE : new Color(200, 200, 200));
        g.setFont(new Font("Segoe UI", Font.BOLD, 12));
        FontMetrics fm = g.getFontMetrics();
        int textX = btn.x + (btn.width - fm.stringWidth(label)) / 2;
        int textY = btn.y + (btn.height + fm.getAscent() - fm.getDescent()) / 2;
        g.drawString(label, textX, textY);
    }

    private void drawItemGrid(Graphics2D g) {
        g.setFont(new Font("Arial", Font.PLAIN, 12));
        List<?> itemsToShow = getItemsForSelectedType();

        for (int i = 0; i < itemsToShow.size(); i++) {
            Object obj = itemsToShow.get(i);
            int row = i / itemsPerRow;
            int col = i % itemsPerRow;
            int x = gridStartX + col * (slotSize + slotSpacing);
            int y = gridStartY + row * (slotSize + slotSpacing);

            boolean isSelected = obj == selectedItem || obj == selectedEnemy || obj == selectedBoss;
            if (isSelected) {
                GradientPaint gradient = new GradientPaint(x, y, new Color(80, 100, 140), x, y + slotSize, new Color(60, 80, 120));
                g.setPaint(gradient);
            } else {
                GradientPaint gradient = new GradientPaint(x, y, new Color(50, 50, 60), x, y + slotSize, new Color(40, 40, 50));
                g.setPaint(gradient);
            }
            g.fillRoundRect(x, y, slotSize, slotSize, 6, 6);
            g.setPaint(null);

            g.setColor(isSelected ? new Color(100, 200, 255) : new Color(70, 70, 80));
            g.setStroke(new BasicStroke(2));
            g.drawRoundRect(x, y, slotSize, slotSize, 6, 6);
            g.setStroke(new BasicStroke(1));

            if (selectedType != 0) {
                int tierForDot = 1;
                if (obj instanceof Item item) {
                    tierForDot = item.getTier();
                } else if (obj instanceof Enemy e) {
                    tierForDot = e.getLevel();
                } else if (obj instanceof Boss b) {
                    tierForDot = b.getLevel();
                }
                drawTierDotTopRight(g, x, y, tierForDot);
            }

            if (obj instanceof Item item) {
                BufferedImage icon = loadIcon(item.getIconPath());
                if (icon != null) {
                    int maxWidth = slotSize - 6;
                    int maxHeight = slotSize - 16;
                    double scaleX = (double) maxWidth / icon.getWidth();
                    double scaleY = (double) maxHeight / icon.getHeight();
                    double scale = Math.min(scaleX, scaleY);
                    int drawWidth = (int) (icon.getWidth() * scale);
                    int drawHeight = (int) (icon.getHeight() * scale);
                    int drawX = x + 1 + (slotSize - 2 - drawWidth) / 2;
                    int drawY = y + 1 + (slotSize - 14 - drawHeight) / 2;
                    g.drawImage(icon, drawX, drawY, drawWidth, drawHeight, null);
                } else {
                    if (item instanceof Ranged) g.setColor(new Color(200, 100, 100));
                    else if (item instanceof Melee) g.setColor(new Color(100, 200, 100));
                    else if (item instanceof Charm) g.setColor(new Color(200, 200, 100));
                    else if (item instanceof Summon) g.setColor(new Color(100, 100, 200));
                    else if (item instanceof Power) g.setColor(new Color(200, 100, 200));
                    else if (item instanceof Consumable) g.setColor(new Color(100, 200, 200));
                    else g.setColor(new Color(150, 150, 150));
                    g.fillRoundRect(x + 10, y + 10, slotSize - 20, slotSize - 20, 4, 4);
                }
                g.setColor(Color.WHITE);
                g.setFont(new Font("Segoe UI", Font.PLAIN, 10));
                String name = item.getName();
                if (name == null) name = "Unknown";
                else if (name.length() > 8) name = name.substring(0, 8) + ".";
                g.drawString(name, x + 4, y + slotSize - 4);

            } else if (obj instanceof Enemy enemy) {
                g.setColor(new Color(150, 50, 50));
                g.fillRoundRect(x + 10, y + 10, slotSize - 20, slotSize - 20, 4, 4);
                g.setColor(Color.WHITE);
                g.setFont(new Font("Segoe UI", Font.PLAIN, 10));
                String name = enemy.getClass().getSimpleName();
                if (name.length() > 8) name = name.substring(0, 8) + ".";
                g.drawString(name, x + 4, y + slotSize - 4);

            } else if (obj instanceof Boss boss) {
                g.setColor(new Color(255, 100, 0));
                g.fillRoundRect(x + 10, y + 10, slotSize - 20, slotSize - 20, 4, 4);
                g.setColor(Color.WHITE);
                g.setFont(new Font("Segoe UI", Font.PLAIN, 10));
                String name = boss.getClass().getSimpleName();
                if (name.length() > 8) name = name.substring(0, 8) + ".";
                g.drawString(name, x + 4, y + slotSize - 4);
            }
        }
    }

    private void drawClothingGrid(Graphics2D g) {
        List<ClothingItem> items = getClothingItems();
        ClothingType currentSection = null;
        int currentY = gridStartY;
        int col = 0;

        for (ClothingItem item : items) {
            // Draw section header when clothing type changes
            if (item.getType() != currentSection) {
                if (col != 0) {
                    currentY += slotSize + slotSpacing + 10;
                    col = 0;
                }
                currentSection = item.getType();
                String sectionLabel = switch (currentSection) {
                    case ACCESSORY -> "ACCESSORIES";
                    case TOP -> "TOPS";
                    case BOTTOM -> "BOTTOMS";
                    default -> "";
                };
                g.setColor(new Color(100, 200, 255));
                g.setFont(new Font("Segoe UI", Font.BOLD, 14));
                g.drawString(sectionLabel, gridStartX, currentY);
                currentY += 22;
                col = 0;
            }

            int x = gridStartX + col * (slotSize + slotSpacing);
            int y = currentY;

            boolean isSelected = item == selectedClothingItem;
            if (isSelected) {
                GradientPaint grad = new GradientPaint(x, y, new Color(80, 100, 140), x, y + slotSize, new Color(60, 80, 120));
                g.setPaint(grad);
            } else {
                GradientPaint grad = new GradientPaint(x, y, new Color(50, 50, 60), x, y + slotSize, new Color(40, 40, 50));
                g.setPaint(grad);
            }
            g.fillRoundRect(x, y, slotSize, slotSize, 6, 6);
            g.setPaint(null);

            g.setColor(isSelected ? new Color(100, 200, 255) : new Color(70, 70, 80));
            g.setStroke(new BasicStroke(2));
            g.drawRoundRect(x, y, slotSize, slotSize, 6, 6);
            g.setStroke(new BasicStroke(1));

            drawTierDotTopRight(g, x, y, item.getTier());

            // Icon
            BufferedImage icon = loadIcon(item.getAssetPath());
            if (icon != null) {
                int maxW = slotSize - 6;
                int maxH = slotSize - 16;
                double scale = Math.min((double) maxW / icon.getWidth(), (double) maxH / icon.getHeight());
                int drawW = (int) (icon.getWidth() * scale);
                int drawH = (int) (icon.getHeight() * scale);
                g.drawImage(icon, x + (slotSize - drawW) / 2, y + (slotSize - 14 - drawH) / 2, drawW, drawH, null);
            } else {
                Color fallback = switch (item.getType()) {
                    case ACCESSORY -> new Color(147, 112, 219);
                    case TOP -> new Color(52, 152, 219);
                    case BOTTOM -> new Color(46, 204, 113);
                    default -> new Color(100, 100, 100);
                };
                g.setColor(fallback);
                g.fillRoundRect(x + 10, y + 10, slotSize - 20, slotSize - 20, 4, 4);
            }

            // Name
            g.setColor(Color.WHITE);
            g.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            String name = item.getName();
            if (name.length() > 8) name = name.substring(0, 8) + ".";
            g.drawString(name, x + 4, y + slotSize - 4);

            col++;
            if (col >= itemsPerRow) {
                col = 0;
                currentY += slotSize + slotSpacing;
            }
        }
    }

    private List<ClothingItem> getClothingItems() {
        List<ClothingItem> all = new ArrayList<>();

        // Accessories
        all.add(new AllSeeingEye());
        all.add(new Aviators());
        all.add(new Cape());
        all.add(new Crown());
        all.add(new Fedora());
        all.add(new Glasses());
        all.add(new Headband());
        all.add(new NinjaHeadband());
        all.add(new Shades());

        // Tops
        all.add(new BoxyTee());
        all.add(new Hoodie());
        all.add(new LongSleeve());
        all.add(new SuitTop());
        all.add(new TankTop());
        all.add(new TShirt());

        // Bottoms
        all.add(new BaggyJeans());
        all.add(new BasicPants());
        all.add(new Jorts());
        all.add(new Shorts());
        all.add(new SuitBottom());
        all.add(new SwimShorts());
        all.add(new Sweatpants());

        // Sort: by type order, then tier, then name alphabetically
        all.sort(Comparator
                .comparingInt((ClothingItem c) -> typeOrder(c.getType()))
                .thenComparingInt(ClothingItem::getTier)
                .thenComparing(ClothingItem::getName));

        return all;
    }

    private int typeOrder(ClothingType type) {
        return switch (type) {
            case ACCESSORY -> 0;
            case TOP -> 1;
            case BOTTOM -> 2;
            default -> 3;
        };
    }

    private BufferedImage loadIcon(String path) {
        if (path == null || path.isEmpty()) return null;
        if (iconCache.containsKey(path)) return iconCache.get(path);
        try {
            BufferedImage img = ImageIO.read(new File(path));
            iconCache.put(path, img);
            return img;
        } catch (IOException e) {
            return null;
        }
    }

    private Color tierColorForGallery(int tier) {
        int t = Math.max(1, Math.min(5, tier));
        return switch (t) {
            case 1 -> new Color(200, 200, 200);
            case 2 -> new Color(100, 200, 100);
            case 3 -> new Color(100, 150, 255);
            case 4 -> new Color(200, 100, 255);
            case 5 -> new Color(255, 215, 0);
            default -> Color.WHITE;
        };
    }

    /** Tier marker at top-right of grid cell (weapons tab skips this). */
    private void drawTierDotTopRight(Graphics2D g, int slotX, int slotY, int tier) {
        g.setColor(tierColorForGallery(tier));
        g.fillOval(slotX + slotSize - 11, slotY + 3, 8, 8);
    }

    private List<?> getItemsForSelectedType() {
        return switch (selectedType) {
            case 0 -> itemRegistry.getWeapons();
            case 1 -> itemRegistry.getCharms();
            case 2 -> itemRegistry.getSummons();
            case 3 -> itemRegistry.getPowers();
            case 4 -> itemRegistry.getConsumables();
            case 5 -> enemies;
            case 6 -> bosses;
            case 7 -> getClothingItems();
            default -> itemRegistry.getAllItems();
        };
    }

    private void drawItemDetails(Graphics2D g) {
        int panelX = 950;
        int panelY = 100;
        int panelWidth = 600;
        int panelHeight = 650;

        g.setColor(new Color(40, 40, 50, 240));
        g.fillRoundRect(panelX, panelY, panelWidth, panelHeight, 16, 16);

        GradientPaint borderGradient = new GradientPaint(panelX, panelY, new Color(100, 200, 255), panelX + panelWidth, panelY + panelHeight, new Color(147, 112, 219));
        g.setPaint(borderGradient);
        g.setStroke(new BasicStroke(3));
        g.drawRoundRect(panelX, panelY, panelWidth, panelHeight, 16, 16);
        g.setStroke(new BasicStroke(1));
        g.setPaint(null);

        int iconBoxSize = 200;
        int iconBoxX = panelX + panelWidth - iconBoxSize - 20;
        int iconBoxY = panelY + 20;
        BufferedImage detailIcon = loadIcon(selectedItem.getIconPath());
        if (detailIcon != null) {
            double zoomFactor = 0.95;
            double scaleX = (iconBoxSize * zoomFactor) / detailIcon.getWidth();
            double scaleY = (iconBoxSize * zoomFactor) / detailIcon.getHeight();
            double scale = Math.min(scaleX, scaleY);
            int drawWidth = (int) (detailIcon.getWidth() * scale);
            int drawHeight = (int) (detailIcon.getHeight() * scale);
            int drawX = iconBoxX + (iconBoxSize - drawWidth) / 2;
            int drawY = iconBoxY + (iconBoxSize - drawHeight) / 2;
            g.drawImage(detailIcon, drawX, drawY, drawWidth, drawHeight, null);
        } else {
            g.setColor(Color.WHITE);
            g.fillRect(iconBoxX, iconBoxY, iconBoxSize, iconBoxSize);
        }
        g.setColor(new Color(100, 100, 100));
        g.drawRect(iconBoxX, iconBoxY, iconBoxSize, iconBoxSize);

        g.setFont(new Font("Segoe UI", Font.BOLD, 36));
        g.setColor(new Color(100, 200, 255));
        String itemName = selectedItem.getName();
        if (itemName == null) itemName = "Unknown";
        g.drawString(itemName, panelX + 20, panelY + 60);

        g.setFont(new Font("Segoe UI", Font.PLAIN, 22));
        g.setColor(new Color(200, 200, 200));
        g.drawString("Type: " + getItemTypeName(selectedItem), panelX + 20, panelY + 85);

        g.setColor(new Color(255, 215, 0));
        g.drawString("Tier: " + selectedItem.getTier(), panelX + 20, panelY + 115);
        g.drawString("Rarity: " + selectedItem.getRarity(), panelX + 20, panelY + 145);

        if (selectedItem instanceof Ranged || selectedItem instanceof Melee) {
            g.setColor(new Color(150, 150, 150));
            g.setFont(new Font("Segoe UI", Font.PLAIN, 20));
            g.drawString("Scroll to cycle tier", panelX + 20, panelY + 175);
        }

        g.setFont(new Font("Segoe UI", Font.BOLD, 26));
        g.setColor(new Color(200, 200, 200));
        g.drawString("Stats:", panelX + 20, panelY + 215);
        g.setFont(new Font("Segoe UI", Font.PLAIN, 22));

        int statY = panelY + 250;
        if (selectedItem instanceof Ranged ranged) {
            g.drawString("Damage: " + ranged.getDamage(), panelX + 20, statY);
            g.drawString("Fire Rate: " + ranged.getFireRate() + "s", panelX + 20, statY + 35);
            g.drawString("Accuracy: " + (ranged.getAccuracy() * 100) + "%", panelX + 20, statY + 70);
            g.drawString("Magazine: " + ranged.getMagazineSize(), panelX + 20, statY + 105);
            g.drawString("Reload: " + ranged.getReloadTime() + "s", panelX + 20, statY + 140);
        } else if (selectedItem instanceof Melee melee) {
            g.drawString("Damage: " + melee.getDamage(), panelX + 20, statY);
            g.drawString("Attack Speed: " + melee.getAttackSpeed() + "/s", panelX + 20, statY + 35);
            g.drawString("Range: " + melee.getRange(), panelX + 20, statY + 70);
            g.drawString("Knockback: " + melee.getKnockback(), panelX + 20, statY + 105);
        } else if (selectedItem instanceof Charm charm) {
            int line = 0;
            int hpPct = (int) Math.round(charm.getMaxHpBonusFraction() * 100);
            int spdPct = (int) Math.round(charm.getSpeedBonusFraction() * 100);
            int dmgPct = (int) Math.round(charm.getDamageBonusFraction() * 100);
            if (hpPct > 0) {
                g.drawString("+" + hpPct + "% max HP (from base)", panelX + 20, statY + line * 35);
                line++;
            }
            if (spdPct > 0) {
                g.drawString("+" + spdPct + "% move speed (from base)", panelX + 20, statY + line * 35);
                line++;
            }
            if (dmgPct > 0) {
                g.drawString("+" + dmgPct + "% damage (from base)", panelX + 20, statY + line * 35);
                line++;
            }
            if (line == 0) {
                g.drawString("See description", panelX + 20, statY);
            }
        } else if (selectedItem instanceof Power power) {
            Move[] moves = power.getMoves();
            int moveY = statY;
            for (int i = 0; i < moves.length; i++) {
                if (moves[i] != null) {
                    g.drawString(moves[i].getName() + " [" + moves[i].getSlot() + "]", panelX + 20, moveY);
                    moveY += 30;
                }
            }
        } else {
            g.drawString("No combat stats", panelX + 20, statY);
        }

        g.setFont(new Font("Segoe UI", Font.BOLD, 26));
        g.setColor(new Color(200, 200, 200));
        g.drawString("Description:", panelX + 20, panelY + 430);
        g.setFont(new Font("Segoe UI", Font.PLAIN, 22));
        String desc = selectedItem.getDescription();
        if (desc != null && !desc.equals("null")) {
            String[] words = desc.split(" ");
            int descY = panelY + 460;
            int descX = panelX + 20;
            int maxWidth = panelWidth - 40;
            String line = "";
            for (String word : words) {
                String testLine = line + word + " ";
                if (g.getFontMetrics().stringWidth(testLine) < maxWidth) {
                    line = testLine;
                } else {
                    g.drawString(line, descX, descY);
                    descY += 30;
                    line = word + " ";
                }
            }
            g.drawString(line, descX, descY);
        } else {
            g.setColor(new Color(150, 150, 150));
            g.drawString("No description", panelX + 20, panelY + 460);
        }
    }

    private void drawClothingDetails(Graphics2D g) {
        int panelX = 950;
        int panelY = 100;
        int panelWidth = 600;
        int panelHeight = 400;

        g.setColor(new Color(40, 40, 50, 240));
        g.fillRoundRect(panelX, panelY, panelWidth, panelHeight, 16, 16);

        GradientPaint border = new GradientPaint(panelX, panelY, new Color(100, 200, 255), panelX + panelWidth, panelY + panelHeight, new Color(147, 112, 219));
        g.setPaint(border);
        g.setStroke(new BasicStroke(3));
        g.drawRoundRect(panelX, panelY, panelWidth, panelHeight, 16, 16);
        g.setStroke(new BasicStroke(1));
        g.setPaint(null);

        // Name
        g.setFont(new Font("Segoe UI", Font.BOLD, 36));
        g.setColor(new Color(100, 200, 255));
        g.drawString(selectedClothingItem.getName(), panelX + 20, panelY + 60);

        // Type
        g.setFont(new Font("Segoe UI", Font.PLAIN, 22));
        g.setColor(new Color(200, 200, 200));
        g.drawString("Type: " + selectedClothingItem.getType().toString(), panelX + 20, panelY + 95);

        // Tier with color
        Color tierColor = switch (selectedClothingItem.getTier()) {
            case 1 -> new Color(200, 200, 200);
            case 2 -> new Color(100, 200, 100);
            case 3 -> new Color(100, 150, 255);
            case 4 -> new Color(200, 100, 255);
            case 5 -> new Color(255, 215, 0);
            default -> Color.WHITE;
        };
        g.setColor(tierColor);
        g.drawString("Tier: " + selectedClothingItem.getTier(), panelX + 20, panelY + 130);

        // Unlock source
        g.setColor(new Color(150, 150, 170));
        g.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        g.drawString(selectedClothingItem.isUnlockedByDefault()
                ? "Default unlock"
                : "Unlock via lootbox or shop", panelX + 20, panelY + 165);

        // Cosmetic note
        g.setColor(new Color(100, 200, 100));
        g.drawString("Cosmetic only — no gameplay effect", panelX + 20, panelY + 195);

        // Icon
        BufferedImage icon = loadIcon(selectedClothingItem.getAssetPath());
        if (icon != null) {
            int iconSize = 150;
            int iconX = panelX + panelWidth - iconSize - 20;
            int iconY = panelY + 20;
            double scale = Math.min((double) iconSize / icon.getWidth(), (double) iconSize / icon.getHeight());
            int drawW = (int) (icon.getWidth() * scale);
            int drawH = (int) (icon.getHeight() * scale);
            g.drawImage(icon, iconX + (iconSize - drawW) / 2, iconY + (iconSize - drawH) / 2, drawW, drawH, null);
            g.setColor(new Color(70, 70, 80));
            g.drawRect(iconX, iconY, iconSize, iconSize);
        }
    }

    private String getItemTypeName(Item item) {
        if (item instanceof Ranged || item instanceof Melee) return "Weapon";
        if (item instanceof Charm) return "Charm";
        if (item instanceof Summon) return "Summon";
        if (item instanceof Power) return "Power";
        if (item instanceof Consumable) return "Consumable";
        return "Unknown";
    }

    private void drawEnemyDetails(Graphics2D g) {
        int panelX = 1200;
        int panelY = 100;
        int panelWidth = 350;
        int panelHeight = 400;

        g.setColor(new Color(50, 50, 60));
        g.fillRect(panelX, panelY, panelWidth, panelHeight);
        g.setColor(Color.WHITE);
        g.drawRect(panelX, panelY, panelWidth, panelHeight);

        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString(selectedEnemy.getClass().getSimpleName(), panelX + 20, panelY + 40);

        g.setFont(new Font("Arial", Font.PLAIN, 18));
        g.drawString("Type: Enemy", panelX + 20, panelY + 65);

        g.setFont(new Font("Arial", Font.BOLD, 22));
        g.drawString("Stats:", panelX + 20, panelY + 155);
        g.setFont(new Font("Arial", Font.PLAIN, 18));

        int statY = panelY + 180;
        g.drawString("HP: " + selectedEnemy.getHp() + " / " + selectedEnemy.getMaxHp(), panelX + 20, statY);
        g.drawString("Damage: " + selectedEnemy.getDamage(), panelX + 20, statY + 25);
        g.drawString("Speed: " + selectedEnemy.getSpeed(), panelX + 20, statY + 50);
    }

    private void drawBossDetails(Graphics2D g) {
        int panelX = 1200;
        int panelY = 100;
        int panelWidth = 350;
        int panelHeight = 400;

        g.setColor(new Color(50, 50, 60));
        g.fillRect(panelX, panelY, panelWidth, panelHeight);
        g.setColor(Color.WHITE);
        g.drawRect(panelX, panelY, panelWidth, panelHeight);

        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString(selectedBoss.getClass().getSimpleName(), panelX + 20, panelY + 40);

        g.setFont(new Font("Arial", Font.PLAIN, 18));
        g.drawString("Type: Boss", panelX + 20, panelY + 65);

        g.setFont(new Font("Arial", Font.BOLD, 22));
        g.drawString("Stats:", panelX + 20, panelY + 155);
        g.setFont(new Font("Arial", Font.PLAIN, 18));

        int statY = panelY + 180;
        g.drawString("HP: " + selectedBoss.getHp() + " / " + selectedBoss.getMaxHp(), panelX + 20, statY);
        g.drawString("Damage: " + selectedBoss.getDamage(), panelX + 20, statY + 25);
        g.drawString("Speed: " + selectedBoss.getSpeed(), panelX + 20, statY + 50);
    }

    public void handleClick(int x, int y) {
        if (backBtn.contains(x, y)) {
            gamePanel.switchScreen("menu");
            return;
        }

        for (int i = 0; i < typeButtons.length; i++) {
            if (typeButtons[i].contains(x, y)) {
                selectedType = i;
                selectedItem = null;
                selectedEnemy = null;
                selectedBoss = null;
                selectedClothingItem = null;
                return;
            }
        }

        // Clothing grid click handling
        if (selectedType == 7) {
            List<ClothingItem> items = getClothingItems();
            ClothingType currentSection = null;
            int currentY = gridStartY;
            int col = 0;

            for (ClothingItem item : items) {
                if (item.getType() != currentSection) {
                    if (col != 0) { currentY += slotSize + slotSpacing + 10; col = 0; }
                    currentSection = item.getType();
                    currentY += 22;
                    col = 0;
                }
                int itemX = gridStartX + col * (slotSize + slotSpacing);
                int itemY = currentY;

                if (x >= itemX && x <= itemX + slotSize && y >= itemY && y <= itemY + slotSize) {
                    selectedClothingItem = item;
                    selectedItem = null;
                    selectedEnemy = null;
                    selectedBoss = null;
                    return;
                }
                col++;
                if (col >= itemsPerRow) { col = 0; currentY += slotSize + slotSpacing; }
            }
            selectedClothingItem = null;
            return;
        }

        // Regular item grid click handling
        List<?> itemsToShow = getItemsForSelectedType();
        for (int i = 0; i < itemsToShow.size(); i++) {
            int row = i / itemsPerRow;
            int col = i % itemsPerRow;
            int slotX = gridStartX + col * (slotSize + slotSpacing);
            int slotY = gridStartY + row * (slotSize + slotSpacing);

            if (x >= slotX && x < slotX + slotSize && y >= slotY && y < slotY + slotSize) {
                Object obj = itemsToShow.get(i);
                if (obj instanceof Item) {
                    selectedItem = (Item) obj;
                    selectedEnemy = null;
                    selectedBoss = null;
                    selectedClothingItem = null;
                } else if (obj instanceof Enemy) {
                    selectedEnemy = (Enemy) obj;
                    selectedItem = null;
                    selectedBoss = null;
                    selectedClothingItem = null;
                } else if (obj instanceof Boss) {
                    selectedBoss = (Boss) obj;
                    selectedItem = null;
                    selectedEnemy = null;
                    selectedClothingItem = null;
                }
                return;
            }
        }

        selectedItem = null;
        selectedEnemy = null;
        selectedBoss = null;
        selectedClothingItem = null;
    }

    private void drawBackgroundPattern(Graphics2D g, int width, int height) {
        g.setColor(new Color(255, 255, 255, 5));
        for (int i = 0; i < width; i += 40) g.drawLine(i, 0, i, height);
        for (int i = 0; i < height; i += 40) g.drawLine(0, i, width, i);
    }
}