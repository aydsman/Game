package world.dungeon;

public class Hallway {

    public enum Orientation {
        HORIZONTAL,
        VERTICAL
    }

    private Room startRoom;
    private Room endRoom;
    private Orientation orientation;
    private int width;
    private int x;
    private int y;
    private int length;

    public Hallway(Room startRoom, Room endRoom, int width) {
        this.startRoom = startRoom;
        this.endRoom = endRoom;
        this.width = width;
        calculateOrientationAndPosition();
    }

    private void calculateOrientationAndPosition() {
        int dx = Math.abs(endRoom.getCenterX() - startRoom.getCenterX());
        int dy = Math.abs(endRoom.getCenterY() - startRoom.getCenterY());

        if (dx > dy) {
            orientation = Orientation.HORIZONTAL;
            this.x = Math.min(startRoom.getCenterX(), endRoom.getCenterX());
            this.y = Math.min(startRoom.getCenterY(), endRoom.getCenterY()) - width / 2;
            this.length = Math.abs(endRoom.getCenterX() - startRoom.getCenterX());
        } else {
            orientation = Orientation.VERTICAL;
            this.x = Math.min(startRoom.getCenterX(), endRoom.getCenterX()) - width / 2;
            this.y = Math.min(startRoom.getCenterY(), endRoom.getCenterY());
            this.length = Math.abs(endRoom.getCenterY() - startRoom.getCenterY());
        }
    }

    public Room getStartRoom() {
        return startRoom;
    }

    public Room getEndRoom() {
        return endRoom;
    }

    public Orientation getOrientation() {
        return orientation;
    }

    public int getWidth() {
        return width;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getLength() {
        return length;
    }
}
