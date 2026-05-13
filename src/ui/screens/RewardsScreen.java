package ui.screens;

import progression.Reward;
import progression.Reward.RewardType;
import ui.GamePanel;
import util.MouseHandler;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * RewardsScreen - displays daily, weekly, and seasonal rewards in a card-based UI.
 */
public class RewardsScreen {
    
    private GamePanel gamePanel;
    private ArrayList<Reward> rewards = new ArrayList<>();
    private Map<String, Rectangle> rewardClaimButtons = new HashMap<>();
    private Map<String, Boolean> hoveredRewards = new HashMap<>();
    private String hoveredButton = null;
    private Rectangle backBtn = new Rectangle(50, 50, 120, 50);
    
    // Tab system
    private String selectedTab = "daily"; // daily, weekly, seasonal
    private Rectangle dailyTab = new Rectangle(100, 100, 150, 50);
    private Rectangle weeklyTab = new Rectangle(300, 100, 150, 50);
    private Rectangle seasonalTab = new Rectangle(500, 100, 150, 50);
    
    // Pagination for seasonal (5 at a time)
    private int seasonalPage = 0;
    
    public RewardsScreen(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
        initializeRewards();
    }
    
    private void initializeRewards() {
        long now = System.currentTimeMillis();
        long dailyExpiry = now + (24 * 60 * 60 * 1000); // 24 hours
        long weeklyExpiry = now + (7 * 24 * 60 * 60 * 1000); // 7 days
        long seasonalExpiry = now + (30 * 24 * 60 * 60 * 1000); // 30 days
        
        // DAILY REWARDS (3)
        rewards.add(new Reward("daily_1", "Daily Login", "Log in to receive rewards", 100, 5, 50, RewardType.DAILY, dailyExpiry));
        rewards.add(new Reward("daily_2", "First Victory", "Win your first battle today", 250, 10, 100, RewardType.DAILY, dailyExpiry));
        rewards.add(new Reward("daily_3", "Kill Streak", "Get 25 kills in one session", 500, 25, 250, RewardType.DAILY, dailyExpiry));
        
        // WEEKLY REWARDS (5)
        rewards.add(new Reward("weekly_1", "Weekly Grind", "Earn 1000 XP this week", 500, 25, 250, RewardType.WEEKLY, weeklyExpiry));
        rewards.add(new Reward("weekly_2", "Arena Master", "Complete 5 arena runs", 750, 35, 400, RewardType.WEEKLY, weeklyExpiry));
        rewards.add(new Reward("weekly_3", "Dungeon Delver", "Complete 3 dungeon runs", 600, 30, 300, RewardType.WEEKLY, weeklyExpiry));
        rewards.add(new Reward("weekly_4", "Collector", "Collect 50 different items", 800, 40, 350, RewardType.WEEKLY, weeklyExpiry));
        rewards.add(new Reward("weekly_5", "Boss Hunter", "Defeat 5 bosses this week", 1000, 50, 500, RewardType.WEEKLY, weeklyExpiry));
        
        // SEASONAL REWARDS (10, shown 5 at a time)
        rewards.add(new Reward("seasonal_1", "Seasonal Starter", "Begin your seasonal adventure", 250, 15, 150, RewardType.SEASONAL, seasonalExpiry));
        rewards.add(new Reward("seasonal_2", "Level 10", "Reach level 10", 500, 25, 250, RewardType.SEASONAL, seasonalExpiry));
        rewards.add(new Reward("seasonal_3", "Level 25", "Reach level 25", 1000, 50, 500, RewardType.SEASONAL, seasonalExpiry));
        rewards.add(new Reward("seasonal_4", "Level 50", "Reach level 50", 2000, 100, 1000, RewardType.SEASONAL, seasonalExpiry));
        rewards.add(new Reward("seasonal_5", "Legendary Hunt", "Defeat 100 legendary enemies", 2500, 150, 1500, RewardType.SEASONAL, seasonalExpiry));
        rewards.add(new Reward("seasonal_6", "Ultimate Champion", "Reach wave 100 in arena", 3000, 200, 2000, RewardType.SEASONAL, seasonalExpiry));
        rewards.add(new Reward("seasonal_7", "Dungeon Master", "Complete all 5 dungeon levels", 2000, 100, 1000, RewardType.SEASONAL, seasonalExpiry));
        rewards.add(new Reward("seasonal_8", "Treasure Collector", "Collect 500 items total", 1500, 75, 750, RewardType.SEASONAL, seasonalExpiry));
        rewards.add(new Reward("seasonal_9", "Accuracy Master", "Achieve 90% accuracy", 1200, 60, 600, RewardType.SEASONAL, seasonalExpiry));
        rewards.add(new Reward("seasonal_10", "Seasonal Legend", "Complete all seasonal rewards", 5000, 500, 5000, RewardType.SEASONAL, seasonalExpiry));
    }
    
    public void handleClick(int x, int y) {
        if (backBtn.contains(x, y)) {
            gamePanel.switchScreen("menu");
            return;
        }
        
        // Tab switching
        if (dailyTab.contains(x, y)) {
            selectedTab = "daily";
            return;
        }
        if (weeklyTab.contains(x, y)) {
            selectedTab = "weekly";
            return;
        }
        if (seasonalTab.contains(x, y)) {
            selectedTab = "seasonal";
            return;
        }
        
        // Check claim buttons
        for (Map.Entry<String, Rectangle> entry : rewardClaimButtons.entrySet()) {
            if (entry.getValue().contains(x, y)) {
                claimReward(entry.getKey());
                return;
            }
        }
    }
    
    public void handleMouseMove(int x, int y) {
        hoveredButton = null;
        
        if (backBtn.contains(x, y)) {
            hoveredButton = "back";
        }
        
        for (String rewardId : rewardClaimButtons.keySet()) {
            if (rewardClaimButtons.get(rewardId).contains(x, y)) {
                hoveredRewards.put(rewardId, true);
            } else {
                hoveredRewards.put(rewardId, false);
            }
        }
    }
    
    private void claimReward(String rewardId) {
        for (Reward r : rewards) {
            if (r.getId().equals(rewardId)) {
                if (!r.isClaimed() && !r.isExpired()) {
                    r.setClaimed(true);
                    // Add currency rewards to player
                    gamePanel.getCurrencyManager().addCash(r.getGoldAmount());
                    gamePanel.getCurrencyManager().addGems(r.getGemAmount());
                    // TODO: Add XP to player when player system is integrated
                    System.out.println("Claimed: " + r.getTitle() + " (+$" + r.getGoldAmount() + ", +" + r.getGemAmount() + "◆)");
                }
                return;
            }
        }
    }
    
    public void draw(Graphics2D g, int screenWidth, int screenHeight) {
        // Draw background gradient
        GradientPaint bgGradient = new GradientPaint(0, 0, new Color(30, 30, 40), 0, screenHeight, new Color(20, 20, 30));
        g.setPaint(bgGradient);
        g.fillRect(0, 0, screenWidth, screenHeight);
        
        // Draw back button
        drawStyledButton(g, backBtn, "Back", new Color(149, 165, 166), hoveredButton != null && hoveredButton.equals("back"));
        
        // Draw title
        g.setFont(new Font("Press Start 2P", Font.BOLD, 48));
        if (!g.getFont().getFamily().equals("Press Start 2P")) {
            g.setFont(new Font("Monospaced", Font.BOLD, 48));
        }
        g.setColor(Color.WHITE);
        FontMetrics fm = g.getFontMetrics();
        String title = "REWARDS";
        int titleX = (screenWidth - fm.stringWidth(title)) / 2;
        g.drawString(title, titleX, 80);
        
        // Draw tabs
        drawTab(g, dailyTab, "Daily", selectedTab.equals("daily"));
        drawTab(g, weeklyTab, "Weekly", selectedTab.equals("weekly"));
        drawTab(g, seasonalTab, "Seasonal", selectedTab.equals("seasonal"));
        
        // Clear button rectangles for this frame
        rewardClaimButtons.clear();
        
        // Draw rewards
        if (selectedTab.equals("daily")) {
            drawRewardCards(g, getDailyRewards(), screenWidth, screenHeight);
        } else if (selectedTab.equals("weekly")) {
            drawRewardCards(g, getWeeklyRewards(), screenWidth, screenHeight);
        } else if (selectedTab.equals("seasonal")) {
            drawSeasonalRewardCards(g, getSeasonalRewards(), screenWidth, screenHeight);
        }
    }
    
    private void drawTab(Graphics2D g, Rectangle tab, String label, boolean isActive) {
        if (isActive) {
            GradientPaint gradient = new GradientPaint(tab.x, tab.y, new Color(52, 152, 219), tab.x, tab.y + tab.height, new Color(30, 130, 200));
            g.setPaint(gradient);
        } else {
            GradientPaint gradient = new GradientPaint(tab.x, tab.y, new Color(50, 50, 60), tab.x, tab.y + tab.height, new Color(40, 40, 50));
            g.setPaint(gradient);
        }
        g.fillRoundRect(tab.x, tab.y, tab.width, tab.height, 12, 12);
        g.setPaint(null);
        
        g.setColor(isActive ? new Color(100, 200, 255) : new Color(80, 80, 90));
        g.setStroke(new BasicStroke(2));
        g.drawRoundRect(tab.x, tab.y, tab.width, tab.height, 12, 12);
        g.setStroke(new BasicStroke(1));
        
        g.setColor(Color.WHITE);
        g.setFont(new Font("Segoe UI", Font.BOLD, 18));
        FontMetrics fm = g.getFontMetrics();
        int textX = tab.x + (tab.width - fm.stringWidth(label)) / 2;
        int textY = tab.y + (tab.height + fm.getAscent() - fm.getDescent()) / 2;
        g.drawString(label, textX, textY);
    }
    
    private void drawRewardCards(Graphics2D g, ArrayList<Reward> rewardList, int screenWidth, int screenHeight) {
        int cardWidth = 280;
        int cardHeight = 160;
        int cardSpacing = 20;
        int cardsPerRow = (screenWidth - 100) / (cardWidth + cardSpacing);
        
        int startX = 50;
        int startY = 200;
        
        for (int i = 0; i < rewardList.size(); i++) {
            Reward reward = rewardList.get(i);
            
            int col = i % cardsPerRow;
            int row = i / cardsPerRow;
            
            int cardX = startX + (col * (cardWidth + cardSpacing));
            int cardY = startY + (row * (cardHeight + cardSpacing));
            
            drawRewardCard(g, reward, cardX, cardY, cardWidth, cardHeight);
        }
    }
    
    private void drawSeasonalRewardCards(Graphics2D g, ArrayList<Reward> rewardList, int screenWidth, int screenHeight) {
        // Show 5 per page
        int itemsPerPage = 5;
        int startIdx = seasonalPage * itemsPerPage;
        int endIdx = Math.min(startIdx + itemsPerPage, rewardList.size());
        
        ArrayList<Reward> pageRewards = new ArrayList<>();
        for (int i = startIdx; i < endIdx; i++) {
            pageRewards.add(rewardList.get(i));
        }
        
        int cardWidth = 280;
        int cardHeight = 160;
        int cardSpacing = 20;
        int cardsPerRow = (screenWidth - 100) / (cardWidth + cardSpacing);
        
        int startX = 50;
        int startY = 200;
        
        for (int i = 0; i < pageRewards.size(); i++) {
            Reward reward = pageRewards.get(i);
            
            int col = i % cardsPerRow;
            int row = i / cardsPerRow;
            
            int cardX = startX + (col * (cardWidth + cardSpacing));
            int cardY = startY + (row * (cardHeight + cardSpacing));
            
            drawRewardCard(g, reward, cardX, cardY, cardWidth, cardHeight);
        }
        
        // Draw pagination info
        int totalPages = (rewardList.size() + itemsPerPage - 1) / itemsPerPage;
        if (totalPages > 1) {
            g.setColor(Color.WHITE);
            g.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            String pageText = "Page " + (seasonalPage + 1) + " of " + totalPages;
            g.drawString(pageText, screenWidth - 200, screenHeight - 40);
        }
    }
    
    private void drawRewardCard(Graphics2D g, Reward reward, int x, int y, int width, int height) {
        Color cardColor;
        if (reward.isClaimed()) {
            cardColor = new Color(100, 100, 100); // Grey for claimed
        } else if (reward.isExpired()) {
            cardColor = new Color(150, 50, 50); // Red for expired
        } else {
            // Color based on type
            if (reward.getType() == RewardType.DAILY) {
                cardColor = new Color(52, 152, 219); // Blue for daily
            } else if (reward.getType() == RewardType.WEEKLY) {
                cardColor = new Color(155, 89, 182); // Purple for weekly
            } else {
                cardColor = new Color(46, 204, 113); // Green for seasonal
            }
        }
        
        // Draw card background
        g.setColor(new Color(40, 40, 50, 200));
        g.fillRoundRect(x, y, width, height, 12, 12);
        
        // Draw card border with gradient
        GradientPaint borderGradient = new GradientPaint(x, y, cardColor, x + width, y + height, cardColor.darker());
        g.setPaint(borderGradient);
        g.setStroke(new BasicStroke(3));
        g.drawRoundRect(x, y, width, height, 12, 12);
        g.setStroke(new BasicStroke(1));
        g.setPaint(null);
        
        // Draw title
        g.setColor(Color.WHITE);
        g.setFont(new Font("Segoe UI", Font.BOLD, 16));
        g.drawString(reward.getTitle(), x + 15, y + 30);
        
        // Draw description
        g.setColor(new Color(200, 200, 200));
        g.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        String desc = reward.getDescription();
        if (desc.length() > 30) {
            desc = desc.substring(0, 27) + "...";
        }
        g.drawString(desc, x + 15, y + 50);
        
        // Draw reward amounts
        g.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        g.setColor(new Color(46, 204, 113));
        g.drawString("$ " + reward.getGoldAmount(), x + 15, y + 70);
        
        g.setColor(new Color(52, 152, 219));
        g.drawString("◆ " + reward.getGemAmount(), x + 15 + 120, y + 70);
        
        g.setColor(new Color(241, 196, 15));
        g.drawString("XP " + reward.getXpAmount(), x + 15 + 240, y + 70);
        
        // Draw claim button
        int buttonX = x + 15;
        int buttonY = y + 80;
        int buttonWidth = width - 30;
        int buttonHeight = 40;
        
        Rectangle claimBtn = new Rectangle(buttonX, buttonY, buttonWidth, buttonHeight);
        rewardClaimButtons.put(reward.getId(), claimBtn);
        
        boolean isHovered = hoveredRewards.getOrDefault(reward.getId(), false);
        
        if (reward.isClaimed()) {
            // Claimed state
            g.setColor(new Color(100, 100, 100));
            g.fillRoundRect(buttonX, buttonY, buttonWidth, buttonHeight, 8, 8);
            g.setColor(Color.WHITE);
            g.setStroke(new BasicStroke(2));
            g.drawRoundRect(buttonX, buttonY, buttonWidth, buttonHeight, 8, 8);
            g.setStroke(new BasicStroke(1));
            
            g.setFont(new Font("Segoe UI", Font.BOLD, 14));
            String text = "CLAIMED";
            FontMetrics fm = g.getFontMetrics();
            int textX = buttonX + (buttonWidth - fm.stringWidth(text)) / 2;
            int textY = buttonY + (buttonHeight + fm.getAscent() - fm.getDescent()) / 2;
            g.setColor(new Color(150, 150, 150));
            g.drawString(text, textX, textY);
        } else if (reward.isExpired()) {
            // Expired state
            g.setColor(new Color(150, 50, 50));
            g.fillRoundRect(buttonX, buttonY, buttonWidth, buttonHeight, 8, 8);
            g.setColor(Color.WHITE);
            g.setStroke(new BasicStroke(2));
            g.drawRoundRect(buttonX, buttonY, buttonWidth, buttonHeight, 8, 8);
            g.setStroke(new BasicStroke(1));
            
            g.setFont(new Font("Segoe UI", Font.BOLD, 14));
            String text = "EXPIRED";
            FontMetrics fm = g.getFontMetrics();
            int textX = buttonX + (buttonWidth - fm.stringWidth(text)) / 2;
            int textY = buttonY + (buttonHeight + fm.getAscent() - fm.getDescent()) / 2;
            g.setColor(Color.WHITE);
            g.drawString(text, textX, textY);
        } else {
            // Claimable state
            if (isHovered) {
                GradientPaint btnGradient = new GradientPaint(buttonX, buttonY, cardColor.brighter(), buttonX, buttonY + buttonHeight, cardColor);
                g.setPaint(btnGradient);
            } else {
                GradientPaint btnGradient = new GradientPaint(buttonX, buttonY, new Color(50, 50, 70), buttonX, buttonY + buttonHeight, new Color(40, 40, 50));
                g.setPaint(btnGradient);
            }
            g.fillRoundRect(buttonX, buttonY, buttonWidth, buttonHeight, 8, 8);
            g.setPaint(null);
            
            g.setColor(isHovered ? cardColor.brighter() : cardColor);
            g.setStroke(new BasicStroke(2));
            g.drawRoundRect(buttonX, buttonY, buttonWidth, buttonHeight, 8, 8);
            g.setStroke(new BasicStroke(1));
            
            g.setFont(new Font("Segoe UI", Font.BOLD, 14));
            String text = "CLAIM";
            FontMetrics fm = g.getFontMetrics();
            int textX = buttonX + (buttonWidth - fm.stringWidth(text)) / 2;
            int textY = buttonY + (buttonHeight + fm.getAscent() - fm.getDescent()) / 2;
            g.setColor(Color.WHITE);
            g.drawString(text, textX, textY);
        }
    }
    
    private void drawStyledButton(Graphics2D g, Rectangle btn, String label, Color accentColor, boolean isHovered) {
        // Button background with gradient
        if (isHovered) {
            GradientPaint gradient = new GradientPaint(btn.x, btn.y, accentColor.brighter(), btn.x, btn.y + btn.height, accentColor);
            g.setPaint(gradient);
        } else {
            GradientPaint gradient = new GradientPaint(btn.x, btn.y, new Color(50, 50, 60), btn.x, btn.y + btn.height, new Color(40, 40, 50));
            g.setPaint(gradient);
        }
        g.fillRoundRect(btn.x, btn.y, btn.width, btn.height, 12, 12);
        g.setPaint(null);
        
        // Border
        g.setColor(isHovered ? accentColor : new Color(80, 80, 90));
        g.setStroke(new BasicStroke(2));
        g.drawRoundRect(btn.x, btn.y, btn.width, btn.height, 12, 12);
        g.setStroke(new BasicStroke(1));
        
        // Text
        g.setColor(Color.WHITE);
        g.setFont(new Font("Segoe UI", Font.BOLD, 16));
        FontMetrics fm = g.getFontMetrics();
        int textX = btn.x + (btn.width - fm.stringWidth(label)) / 2;
        int textY = btn.y + (btn.height + fm.getAscent() - fm.getDescent()) / 2;
        g.drawString(label, textX, textY);
    }
    
    private ArrayList<Reward> getDailyRewards() {
        ArrayList<Reward> daily = new ArrayList<>();
        for (Reward r : rewards) {
            if (r.getType() == RewardType.DAILY) {
                daily.add(r);
            }
        }
        return daily;
    }
    
    private ArrayList<Reward> getWeeklyRewards() {
        ArrayList<Reward> weekly = new ArrayList<>();
        for (Reward r : rewards) {
            if (r.getType() == RewardType.WEEKLY) {
                weekly.add(r);
            }
        }
        return weekly;
    }
    
    private ArrayList<Reward> getSeasonalRewards() {
        ArrayList<Reward> seasonal = new ArrayList<>();
        for (Reward r : rewards) {
            if (r.getType() == RewardType.SEASONAL) {
                seasonal.add(r);
            }
        }
        return seasonal;
    }
}


