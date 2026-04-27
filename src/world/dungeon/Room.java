package world.dungeon;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class Room {

    public enum RoomType {
        SPAWN(Color.WHITE),
        ENEMY(Color.RED),
        LOOT(Color.YELLOW),
        MINIBOSS(Color.PINK),
        BOSS(new Color(128, 0, 128)); // Purple

        private final Color color;

        RoomType(Color color) {
            this.color = color;
        }

        public Color getColor() {
            return color;
        }
    }

    private int id;
    private int x;
    private int y;
    private int width;
    private int height;
    private RoomType type;
    private List<Room> connections;
    private int centerX;
    private int centerY;

    public Room(int id, int x, int y, int width, int height, RoomType type) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.type = type;
        this.connections = new ArrayList<>();
        this.centerX = x + width / 2;
        this.centerY = y + height / 2;
    }

    public void addConnection(Room room) {
        if (!connections.contains(room)) {
            connections.add(room);
        }
    }

    public int getId() {
        return id;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public RoomType getType() {
        return type;
    }

    public List<Room> getConnections() {
        return connections;
    }

    public int getCenterX() {
        return centerX;
    }

    public int getCenterY() {
        return centerY;
    }
}
