package save;

import ui.GamePanel;

public class AutoSave {
    private final GamePanel panel;
    private Thread thread;

    public AutoSave(GamePanel panel) { this.panel = panel; }

    public void start() {
        thread = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(30000);
                    SaveManager.save(SaveManager.load());
                } catch (InterruptedException e) {
                    break;
                }
            }
        }, "auto-save");
        thread.setDaemon(true);
        thread.start();
    }
}
