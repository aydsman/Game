package ui.screens;

import progression.QuestObjective;
import progression.Reward;
import progression.Reward.RewardType;
import save.SaveData;
import save.SaveManager;
import skilltree.SkillEffects;
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
        rewards.add(new Reward("daily_1", "Daily Login", "Log in to receive rewards", 100, 5, 50,
                RewardType.DAILY, dailyExpiry, QuestObjective.NONE, 1));
        rewards.add(new Reward("daily_2", "First Blood", "Defeat 10 enemies", 250, 10, 100,
                RewardType.DAILY, dailyExpiry, QuestObjective.TOTAL_KILLS, 10));
        rewards.add(new Reward("daily_3", "Kill Streak", "Defeat 25 enemies", 500, 25, 250,
                RewardType.DAILY, dailyExpiry, QuestObjective.TOTAL_KILLS, 25));
        
        // WEEKLY REWARDS (5)
        rewards.add(new Reward("weekly_1", "Battle Hardened", "Deal 25,000 total damage", 500, 25, 250,
                RewardType.WEEKLY, weeklyExpiry, QuestObjective.TOTAL_DAMAGE, 25000));
        rewards.add(new Reward("weekly_2", "Arena Regular", "Start 5 runs", 750, 35, 400,
                RewardType.WEEKLY, weeklyExpiry, QuestObjective.RUNS_PLAYED, 5));
        rewards.add(new Reward("weekly_3", "Wave Rider", "Reach wave 5 in a run", 600, 30, 300,
                RewardType.WEEKLY, weeklyExpiry, QuestObjective.HIGHEST_WAVE, 5));
        rewards.add(new Reward("weekly_4", "Collector", "Collect 15 items", 800, 40, 350,
                RewardType.WEEKLY, weeklyExpiry, QuestObjective.ITEMS_COLLECTED, 15));
        rewards.add(new Reward("weekly_5", "Tinkerer", "Discover 3 crafting recipes", 1000, 50, 500,
                RewardType.WEEKLY, weeklyExpiry, QuestObjective.RECIPES_DISCOVERED, 3));
        
        // SEASONAL REWARDS (10, shown 5 at a time)
        rewards.add(new Reward("seasonal_1", "Seasonal Starter", "Reach level 2", 250, 15, 150,
                RewardType.SEASONAL, seasonalExpiry, QuestObjective.REACH_LEVEL, 2));
        rewards.add(new Reward("seasonal_2", "Level 10", "Reach level 10", 500, 25, 250,
                RewardType.SEASONAL, seasonalExpiry, QuestObjective.REACH_LEVEL, 10));
        rewards.add(new Reward("seasonal_3", "Level 25", "Reach level 25", 1000, 50, 500,
                RewardType.SEASONAL, seasonalExpiry, QuestObjective.REACH_LEVEL, 25));
        rewards.add(new Reward("seasonal_4", "Level 50", "Reach level 50", 2000, 100, 1000,
                RewardType.SEASONAL, seasonalExpiry, QuestObjective.REACH_LEVEL, 50));
        rewards.add(new Reward("seasonal_5", "Slayer", "Defeat 500 enemies", 2500, 150, 1500,
                RewardType.SEASONAL, seasonalExpiry, QuestObjective.TOTAL_KILLS, 500));
        rewards.add(new Reward("seasonal_6", "Ultimate Champion", "Reach wave 25 in a run", 3000, 200, 2000,
                RewardType.SEASONAL, seasonalExpiry, QuestObjective.HIGHEST_WAVE, 25));
        rewards.add(new Reward("seasonal_7", "Trailblazer", "Unlock 3 arenas", 2000, 100, 1000,
                RewardType.SEASONAL, seasonalExpiry, QuestObjective.ARENAS_UNLOCKED, 3));
        rewards.add(new Reward("seasonal_8", "Treasure Collector", "Collect 100 items", 1500, 75, 750,
                RewardType.SEASONAL, seasonalExpiry, QuestObjective.ITEMS_COLLECTED, 100));
        rewards.add(new Reward("seasonal_9", "Master at Arms", "Earn 5 mastery medals", 1200, 60, 600,
                RewardType.SEASONAL, seasonalExpiry, QuestObjective.MASTERY_MEDALS, 5));
        rewards.add(new Reward("seasonal_10", "Seasonal Legend", "Deal 500,000 total damage", 5000, 500, 5000,
                RewardType.SEASONAL, seasonalExpiry, QuestObjective.TOTAL_DAMAGE, 500000));

        // Restore claims so rewards can't be farmed by restarting
        SaveData data = SaveManager.load();
        for (Reward r : rewards) {
            if (data.isRewardClaimed(r.getId())) {
                r.setClaimed(true);
            }
        }
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
                SaveData data = SaveManager.load();
                if (!r.isClaimable(data)) {
                    return;
                }
                r.setClaimed(true);
                int cash = SkillEffects.scaleCashGain(r.getGoldAmount());
                int gems = SkillEffects.scaleGemGain(r.getGemAmount());
                gamePanel.getCurrencyManager().addCash(cash);
                gamePanel.getCurrencyManager().addGems(gems);
                if (r.getXpAmount() > 0) {
                    SaveManager.addXP(r.getXpAmount());
                }
                data = SaveManager.load();
                data.markRewardClaimed(r.getId());
                data.setCash(gamePanel.getCurrencyManager().getPersistableCash());
                data.setGems(gamePanel.getCurrencyManager().getPersistableGems());
                SaveManager.save(data);
                System.out.println("Claimed: " + r.getTitle() + " (+$" + cash + ", +" + gems + "◆)");
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
        SaveData data = SaveManager.load();
        boolean complete = reward.isComplete(data);

        Color cardColor;
        if (reward.isClaimed()) {
            cardColor = new Color(100, 100, 100); // Grey for claimed
        } else if (reward.isExpired()) {
            cardColor = new Color(150, 50, 50); // Red for expired
        } else if (!complete) {
            cardColor = new Color(120, 125, 140); // Muted for locked
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
        g.drawString("$ " + SkillEffects.scaleCashGain(reward.getGoldAmount()), x + 15, y + 70);
        
        g.setColor(new Color(52, 152, 219));
        g.drawString("◆ " + SkillEffects.scaleGemGain(reward.getGemAmount()), x + 15 + 95, y + 70);
        
        g.setColor(new Color(241, 196, 15));
        g.drawString("XP " + reward.getXpAmount(), x + 15 + 180, y + 70);

        drawObjectiveProgress(g, reward, data, x + 15, y + 80, width - 30);
        
        // Draw claim button
        int buttonX = x + 15;
        int buttonY = y + 105;
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
        } else if (!complete) {
            // Objective not met yet
            g.setColor(new Color(34, 36, 46));
            g.fillRoundRect(buttonX, buttonY, buttonWidth, buttonHeight, 8, 8);
            g.setColor(new Color(80, 84, 98));
            g.setStroke(new BasicStroke(2));
            g.drawRoundRect(buttonX, buttonY, buttonWidth, buttonHeight, 8, 8);
            g.setStroke(new BasicStroke(1));

            g.setFont(new Font("Segoe UI", Font.BOLD, 14));
            String text = "IN PROGRESS";
            FontMetrics fm = g.getFontMetrics();
            int textX = buttonX + (buttonWidth - fm.stringWidth(text)) / 2;
            int textY = buttonY + (buttonHeight + fm.getAscent() - fm.getDescent()) / 2;
            g.setColor(new Color(140, 146, 165));
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
    
    private void drawObjectiveProgress(Graphics2D g, Reward reward, SaveData data, int x, int y, int width) {
        if (reward.getObjective() == QuestObjective.NONE) {
            return;
        }

        int progress = reward.getProgress(data);
        int goal = reward.getGoal();
        double ratio = Math.min(1.0, progress / (double) goal);

        int barHeight = 6;
        g.setColor(new Color(24, 26, 34));
        g.fillRoundRect(x, y, width, barHeight, 4, 4);
        g.setColor(ratio >= 1.0 ? new Color(46, 204, 113) : new Color(90, 150, 220));
        g.fillRoundRect(x, y, (int) (width * ratio), barHeight, 4, 4);

        g.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        g.setColor(new Color(185, 190, 205));
        g.drawString(progress + " / " + goal, x, y + barHeight + 12);
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


