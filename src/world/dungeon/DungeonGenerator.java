package world.dungeon;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DungeonGenerator {

    private Random random;
    private List<Room> rooms;
    private List<Hallway> hallways;
    private int nextRoomId;
    private Room spawnRoom;
    private Room bossRoom;

    public DungeonGenerator() {
        this.random = new Random();
        this.rooms = new ArrayList<>();
        this.hallways = new ArrayList<>();
        this.nextRoomId = 0;
    }

    public List<Room> generateLevel(int level) {
        return generateLevel(level, false);
    }

    public List<Room> generateLevel(int level, boolean forGameplay) {
        rooms.clear();
        hallways.clear();
        nextRoomId = 0;

        // Determine room count based on level
        int minRooms, maxRooms;
        switch (level) {
            case 1: minRooms = 13; maxRooms = 16; break;
            case 2: minRooms = 14; maxRooms = 17; break;
            case 3: minRooms = 15; maxRooms = 18; break;
            case 4: minRooms = 15; maxRooms = 18; break;
            case 5: minRooms = 16; maxRooms = 20; break;
            default: minRooms = 13; maxRooms = 16;
        }

        int targetRoomCount = minRooms + random.nextInt(maxRooms - minRooms + 1);

        // Scale factor for gameplay (10x larger rooms)
        int scale = forGameplay ? 10 : 1;

        // Create spawn room
        int spawnX = forGameplay ? 2500 : 640;
        int spawnY = forGameplay ? 1500 : 360;
        int spawnSize = 60 * scale;
        spawnRoom = new Room(nextRoomId++, spawnX, spawnY, spawnSize, spawnSize, Room.RoomType.SPAWN);
        rooms.add(spawnRoom);

        // Generate rooms from spawn with branching
        generateRoomsFromSpawn(spawnRoom, targetRoomCount, level, scale, forGameplay);

        // Remove any disconnected rooms BEFORE generating hallways
        removeDisconnectedRooms();

        // Assign room types (excluding spawn and boss)
        assignRoomTypes(level);

        // Generate hallways between connected rooms
        generateHallways(scale, forGameplay);

        return new ArrayList<>(rooms);
    }

    private void removeDisconnectedRooms() {
        // Find all rooms connected to spawn using BFS
        List<Room> connected = new ArrayList<>();
        List<Room> toVisit = new ArrayList<>();
        toVisit.add(spawnRoom);

        while (!toVisit.isEmpty()) {
            Room current = toVisit.remove(0);
            if (!connected.contains(current)) {
                connected.add(current);
                toVisit.addAll(current.getConnections());
            }
        }

        // Keep only connected rooms
        rooms.retainAll(connected);
    }

    private void generateRoomsFromSpawn(Room startRoom, int targetCount, int level, int scale, boolean forGameplay) {
        // Determine number of connections from spawn
        int connections = 1;
        if (random.nextDouble() < 0.75) connections++;
        if (random.nextDouble() < 0.50) connections++;
        if (random.nextDouble() < 0.25) connections++;

        // Generate initial rooms from spawn
        List<Room> frontier = new ArrayList<>();
        int[] directions = {0, 1, 2, 3}; // N, E, S, W
        shuffleArray(directions);

        for (int i = 0; i < connections && i < 4; i++) {
            Room newRoom = createRoomInDirection(startRoom, directions[i], scale, forGameplay);
            if (newRoom != null) {
                startRoom.addConnection(newRoom);
                newRoom.addConnection(startRoom);
                rooms.add(newRoom);
                frontier.add(newRoom);
            }
        }

        // Continue generating until target count reached
        int attempts = 0;
        int maxAttempts = targetCount * 10; // Prevent infinite loop

        while (rooms.size() < targetCount && attempts < maxAttempts) {
            attempts++;

            // If frontier is empty, try adding from any existing room
            Room current;
            if (frontier.isEmpty()) {
                if (rooms.isEmpty()) break;
                current = rooms.get(random.nextInt(rooms.size()));
            } else {
                current = frontier.remove(random.nextInt(frontier.size()));
            }

            // Try to add 1-2 more connections
            int newConnections = 1 + (random.nextDouble() < 0.5 ? 1 : 0);
            shuffleArray(directions);

            for (int i = 0; i < newConnections && rooms.size() < targetCount; i++) {
                Room newRoom = createRoomInDirection(current, directions[i % 4], scale, forGameplay);
                if (newRoom != null) {
                    current.addConnection(newRoom);
                    newRoom.addConnection(current);
                    rooms.add(newRoom);
                    frontier.add(newRoom);
                }
            }

            // Re-add current to frontier if it still has space for more connections
            if (current.getConnections().size() < 4 && rooms.size() < targetCount) {
                if (!frontier.contains(current)) {
                    frontier.add(current);
                }
            }
        }

        // Mark the furthest room as boss room for level 5
        if (level == 5) {
            bossRoom = findFurthestRoom(spawnRoom);
            if (bossRoom != null) {
                int bossRoomId = bossRoom.getId();
                List<Room> originalConnections = new ArrayList<>(bossRoom.getConnections());
                bossRoom = new Room(bossRoomId, bossRoom.getX(), bossRoom.getY(),
                                   bossRoom.getWidth(), bossRoom.getHeight(), Room.RoomType.BOSS);
                // Preserve connections from original room
                for (Room conn : originalConnections) {
                    bossRoom.addConnection(conn);
                    // Update the connected room's connections to point to the new boss room
                    conn.getConnections().removeIf(r -> r.getId() == bossRoomId);
                    conn.addConnection(bossRoom);
                }
                rooms.removeIf(r -> r.getId() == bossRoomId);
                rooms.add(bossRoom);
            }
        }
    }

    private Room createRoomInDirection(Room from, int direction, int scale, boolean forGameplay) {
        int distance = (80 + random.nextInt(40)) * scale;
        int newX = from.getCenterX();
        int newY = from.getCenterY();
        int width = (50 + random.nextInt(30)) * scale;
        int height = (50 + random.nextInt(30)) * scale;

        switch (direction) {
            case 0: newY -= distance; break; // North
            case 1: newX += distance; break; // East
            case 2: newY += distance; break; // South
            case 3: newX -= distance; break; // West
        }

        newX -= width / 2;
        newY -= height / 2;

        // Check bounds (keep within reasonable area)
        int boundX = forGameplay ? 5000 : 1200;
        int boundY = forGameplay ? 3000 : 650;
        if (newX < 50 || newX > boundX || newY < 50 || newY > boundY) {
            return null;
        }

        // Check for overlap with existing rooms
        for (Room existing : rooms) {
            if (roomsOverlap(newX, newY, width, height, existing)) {
                return null;
            }
        }

        return new Room(nextRoomId++, newX, newY, width, height, Room.RoomType.ENEMY);
    }

    private boolean roomsOverlap(int x1, int y1, int w1, int h1, Room r2) {
        return x1 < r2.getX() + r2.getWidth() &&
               x1 + w1 > r2.getX() &&
               y1 < r2.getY() + r2.getHeight() &&
               y1 + h1 > r2.getY();
    }

    private Room findFurthestRoom(Room start) {
        Room furthest = null;
        double maxDist = 0;

        for (Room room : rooms) {
            if (room.getType() != Room.RoomType.SPAWN) {
                double dist = distance(start, room);
                if (dist > maxDist) {
                    maxDist = dist;
                    furthest = room;
                }
            }
        }
        return furthest;
    }

    private double distance(Room r1, Room r2) {
        double dx = r1.getCenterX() - r2.getCenterX();
        double dy = r1.getCenterY() - r2.getCenterY();
        return Math.sqrt(dx * dx + dy * dy);
    }

    private void assignRoomTypes(int level) {
        // Count non-spawn, non-boss rooms
        List<Room> assignableRooms = new ArrayList<>();
        for (Room room : rooms) {
            if (room.getType() != Room.RoomType.SPAWN && room.getType() != Room.RoomType.BOSS) {
                assignableRooms.add(room);
            }
        }

        // Assign loot rooms
        int lootCount = 1;
        if (level >= 4) {
            // Levels 4-5: 100% for 2nd, 50% for 3rd
            lootCount++;
            if (random.nextDouble() < 0.50) lootCount++;
        } else {
            // Levels 1-3: 66% for 2nd, 33% for 3rd
            if (random.nextDouble() < 0.66) lootCount++;
            if (random.nextDouble() < 0.33) lootCount++;
        }

        for (int i = 0; i < lootCount && !assignableRooms.isEmpty(); i++) {
            // Prefer dead ends for loot
            Room lootRoom = findDeadEnd(assignableRooms);
            if (lootRoom == null || assignableRooms.size() <= lootCount - i) {
                lootRoom = assignableRooms.remove(random.nextInt(assignableRooms.size()));
            } else {
                assignableRooms.remove(lootRoom);
            }
            int lootRoomId = lootRoom.getId();
            List<Room> originalConnections = new ArrayList<>(lootRoom.getConnections());
            lootRoom = new Room(lootRoomId, lootRoom.getX(), lootRoom.getY(),
                               lootRoom.getWidth(), lootRoom.getHeight(), Room.RoomType.LOOT);
            // Preserve connections from original room
            for (Room conn : originalConnections) {
                lootRoom.addConnection(conn);
                // Update the connected room's connections to point to the new loot room
                conn.getConnections().removeIf(r -> r.getId() == lootRoomId);
                conn.addConnection(lootRoom);
            }
            rooms.removeIf(r -> r.getId() == lootRoomId);
            rooms.add(lootRoom);
        }

        // Assign mini-boss for levels 2-4
        if (level >= 2 && level <= 4 && random.nextDouble() < 0.33 && !assignableRooms.isEmpty()) {
            Room minibossRoom = assignableRooms.remove(random.nextInt(assignableRooms.size()));
            int minibossRoomId = minibossRoom.getId();
            List<Room> originalConnections = new ArrayList<>(minibossRoom.getConnections());
            minibossRoom = new Room(minibossRoomId, minibossRoom.getX(), minibossRoom.getY(),
                                   minibossRoom.getWidth(), minibossRoom.getHeight(), Room.RoomType.MINIBOSS);
            // Preserve connections from original room
            for (Room conn : originalConnections) {
                minibossRoom.addConnection(conn);
                // Update the connected room's connections to point to the new miniboss room
                conn.getConnections().removeIf(r -> r.getId() == minibossRoomId);
                conn.addConnection(minibossRoom);
            }
            rooms.removeIf(r -> r.getId() == minibossRoomId);
            rooms.add(minibossRoom);
        }
    }

    private Room findDeadEnd(List<Room> candidateRooms) {
        for (Room room : candidateRooms) {
            if (room.getConnections().size() == 1 && room.getType() != Room.RoomType.SPAWN) {
                return room;
            }
        }
        return null;
    }

    private void shuffleArray(int[] array) {
        for (int i = array.length - 1; i > 0; i--) {
            int index = random.nextInt(i + 1);
            int temp = array[index];
            array[index] = array[i];
            array[i] = temp;
        }
    }

    private void generateHallways(int scale, boolean forGameplay) {
        // Create hallways between connected rooms
        int hallwayWidth = forGameplay ? 150 : 100; // 150px for gameplay, 100px for graph test
        for (Room room : rooms) {
            for (Room connected : room.getConnections()) {
                // Only create hallway once per connection (check by ID)
                if (room.getId() < connected.getId()) {
                    Hallway hallway = new Hallway(room, connected, hallwayWidth);
                    hallways.add(hallway);
                }
            }
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
