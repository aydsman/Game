package save;

import currency.CurrencyManager;
import ui.GamePanel;

public class AutoSave implements Runnable {
    private static final long SAVE_INTERVAL_MS = 30000; // 30 seconds
    private volatile boolean running = true;
    private GamePanel gamePanel;
    
    public AutoSave(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
    }
    
    @Override
    public void run() {
        while (running) {
            try {
                Thread.sleep(SAVE_INTERVAL_MS);
                if (running) {
                    performSave();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
    
    private void performSave() {
        if (gamePanel == null) return;
        
        SaveData data = SaveManager.load();
        
        // Update from GamePanel's currency manager
        CurrencyManager cm = gamePanel.getCurrencyManager();
        if (cm != null) {
            data.setCash(cm.getCash().getAmount());
            data.setGems(cm.getGems().getAmount());
        }
        
        SaveManager.save(data);
    }
    
    public void stop() {
        running = false;
        // Final save on stop
        performSave();
    }
    
    public void start() {
        Thread thread = new Thread(this, "AutoSave-Thread");
        thread.setDaemon(true);
        thread.start();
    }
}
