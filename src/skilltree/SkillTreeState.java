package skilltree;

import save.SaveData;

import java.util.HashMap;
import java.util.Map;

public class SkillTreeState {
    private final SaveData saveData;

    public SkillTreeState(SaveData saveData) {
        this.saveData = saveData;
    }

    public int getSkillPoints() {
        return saveData.getSkillPoints();
    }

    public int getRank(String nodeId) {
        return saveData.getSkillRanks().getOrDefault(nodeId, 0);
    }

    public boolean isUnlocked(String nodeId) {
        return getRank(nodeId) > 0;
    }

    public boolean canUnlock(SkillNode node) {
        if (node == null) return false;
        if (isUnlocked(node.getId())) return false;
        if (saveData.getSkillPoints() < node.getCost()) return false;
        for (String parentId : node.getParents()) {
            if (!isUnlocked(parentId)) {
                return false;
            }
        }
        return true;
    }

    public boolean tryUnlock(SkillNode node) {
        if (!canUnlock(node)) {
            return false;
        }
        if (!saveData.trySpendSkillPoints(node.getCost())) {
            return false;
        }
        saveData.getSkillRanks().put(node.getId(), 1);
        return true;
    }

    public void fullRespec() {
        int refund = 0;
        for (SkillNode node : SkillTreeData.allNodes()) {
            if (isUnlocked(node.getId())) {
                refund += node.getCost() * Math.max(1, getRank(node.getId()));
            }
        }
        saveData.addSkillPoints(refund);
        saveData.getSkillRanks().clear();
    }

    public Map<SkillEffectType, Double> getTotalEffects() {
        Map<SkillEffectType, Double> totals = new HashMap<>();
        for (SkillNode node : SkillTreeData.allNodes()) {
            int rank = getRank(node.getId());
            if (rank > 0) {
                totals.merge(node.getEffectType(), node.getEffectValue() * rank, Double::sum);
            }
        }
        return totals;
    }
}
