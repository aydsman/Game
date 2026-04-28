package ui.screens;

import ui.GamePanel;
import world.dungeon.DungeonGenerator;
import world.dungeon.Room;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.List;

public class GraphTestScreen {

    private GamePanel gamePanel;
    private DungeonGenerator generator;
    private List<Room> rooms;
    private int currentLevel;
    private java.awt.Rectangle backBtn;

    public GraphTestScreen(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
        this.generator = new DungeonGenerator();
        this.currentLevel = 1;
        this.rooms = generator.generateLevel(currentLevel);
        this.backBtn = new java.awt.Rectangle(20, 20, 100, 40);
    }

    public void draw(Graphics2D g) {
        // Draw background
        g.setColor(new Color(30, 30, 30));
        g.fillRect(0, 0, 1600, 900);

        // Draw back button
        g.setColor(new Color(70, 70, 70));
        g.fillRect(backBtn.x, backBtn.y, backBtn.width, backBtn.height);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.drawString("Back", backBtn.x + 30, backBtn.y + 25);

        // Draw title
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 24));
        g.drawString("Dungeon Graph Test - Level " + currentLevel, 600, 40);

        // Draw room count
        g.setFont(new Font("Arial", Font.PLAIN, 16));
        g.drawString("Rooms: " + rooms.size(), 600, 65);

        // Draw hallways (lines between connected rooms)
        g.setColor(Color.GRAY);
        for (Room room : rooms) {
            for (Room connected : room.getConnections()) {
                // Only draw each connection once
                if (room.getId() < connected.getId()) {
                    g.drawLine(room.getCenterX(), room.getCenterY(),
                              connected.getCenterX(), connected.getCenterY());
                }
            }
        }

        // Draw rooms
        for (Room room : rooms) {
            // Draw room rectangle
            g.setColor(room.getType().getColor());
            g.fillRect(room.getX(), room.getY(), room.getWidth(), room.getHeight());
            g.setColor(Color.BLACK);
            g.drawRect(room.getX(), room.getY(), room.getWidth(), room.getHeight());

            // Draw room ID at top right
            g.setColor(Color.BLACK);
            g.setFont(new Font("Arial", Font.PLAIN, 10));
            String idText = String.valueOf(room.getId());
            g.drawString(idText, room.getX() + room.getWidth() - 12, room.getY() + 12);
        }

        // Draw legend
        drawLegend(g);
    }

    private void drawLegend(Graphics2D g) {
        int startX = 20;
        int startY = 120;
        int boxSize = 20;
        int spacing = 35;

        g.setFont(new Font("Arial", Font.PLAIN, 12));
        g.setColor(Color.WHITE);
        g.drawString("Legend:", startX, startY - 30);

        String[] labels = {"Spawn", "Enemy", "Loot", "Mini-boss", "Boss"};
        Room.RoomType[] types = {Room.RoomType.SPAWN, Room.RoomType.ENEMY, Room.RoomType.LOOT,
                                Room.RoomType.MINIBOSS, Room.RoomType.BOSS};

        for (int i = 0; i < labels.length; i++) {
            int y = startY + i * spacing;
            g.setColor(types[i].getColor());
            g.fillRect(startX, y - boxSize, boxSize, boxSize);
            g.setColor(Color.BLACK);
            g.drawRect(startX, y - boxSize, boxSize, boxSize);
            g.setColor(Color.WHITE);
            g.drawString(labels[i], startX + boxSize + 10, y - boxSize / 2 + 4);
        }

        // Draw level info
        g.setColor(Color.WHITE);
        g.drawString("Press SPACE to regenerate", startX, startY + labels.length * spacing + 25);
        g.drawString("Press 1-5 to change level", startX, startY + labels.length * spacing + 45);
    }

    public void regenerateLevel() {
        this.rooms = generator.generateLevel(currentLevel);
    }

    public void setLevel(int level) {
        if (level >= 1 && level <= 5) {
            this.currentLevel = level;
            this.rooms = generator.generateLevel(currentLevel);
        }
    }

    public void handleClick(int x, int y) {
        if (backBtn.contains(x, y)) gamePanel.switchScreen("menu");
    }
}
