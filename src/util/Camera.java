package util;

public class Camera {

    public int x, y;

    public void follow(int targetX, int targetY, int targetW, int targetH, int screenWidth, int screenHeight, int arenaWidth, int arenaHeight) {
        x = targetX - screenWidth / 2 + targetW / 2;
        y = targetY - screenHeight / 2 + targetH / 2;

        // clamp camera to arena bounds
        x = Math.max(0, Math.min(x, arenaWidth - screenWidth));
        y = Math.max(0, Math.min(y, arenaHeight - screenHeight));
    }
}
