package world;

import world.arena.Arena;
import world.dungeon.Room;
import world.dungeon.Hallway;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

public class DungeonArena extends Arena {

    private List<Room> rooms;
    private List<Hallway> hallways;
    private Room spawnRoom;

    public DungeonArena(List<Room> rooms, List<Hallway> hallways, Room spawnRoom) {
        super(2000, 2000); // Large bounds for dungeon
        this.rooms = rooms;
        this.hallways = hallways;
        this.spawnRoom = spawnRoom;
    }

    @Override
    public void draw(Graphics2D g, int screenWidth, int screenHeight, int cameraX, int cameraY) {
        // Draw background (black)
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, screenWidth, screenHeight);

        // Draw hallways (light grey)
        g.setColor(new Color(180, 180, 180));
        for (Hallway hallway : hallways) {
            if (hallway.getOrientation() == Hallway.Orientation.HORIZONTAL) {
                g.fillRect(hallway.getX() - cameraX, hallway.getY() - cameraY,
                          hallway.getLength(), hallway.getWidth());
            } else {
                g.fillRect(hallway.getX() - cameraX, hallway.getY() - cameraY,
                          hallway.getWidth(), hallway.getLength());
            }
        }

        // Draw rooms (colored by type)
        for (Room room : rooms) {
            Color roomColor;
            switch (room.getType()) {
                case SPAWN:    roomColor = Color.WHITE; break;
                case ENEMY:    roomColor = Color.RED; break;
                case LOOT:     roomColor = Color.YELLOW; break;
                case MINIBOSS: roomColor = Color.PINK; break;
                case BOSS:     roomColor = new Color(128, 0, 128); break; // Purple
                default:       roomColor = new Color(120, 120, 120); break; // Grey
            }
            g.setColor(roomColor);
            g.fillRect(room.getX() - cameraX, room.getY() - cameraY,
                      room.getWidth(), room.getHeight());

            // Draw room border
            g.setColor(Color.BLACK);
            g.drawRect(room.getX() - cameraX, room.getY() - cameraY,
                      room.getWidth(), room.getHeight());
        }
    }

    public List<Room> getRooms() {
        return rooms;
    }

    public List<Hallway> getHallways() {
        return hallways;
    }

    public Room getSpawnRoom() {
        return spawnRoom;
    }
}
