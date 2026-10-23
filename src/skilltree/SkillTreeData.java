package skilltree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class SkillTreeData {
    private static final List<SkillNode> NODES;
    private static final Map<String, SkillNode> NODE_BY_ID;

    static {
        List<SkillNode> nodes = new ArrayList<>();

        // Combat (red)
        nodes.add(node("combat_t1", SkillPillar.COMBAT, 1, 1, "Battle Basics", "+5% damage", SkillEffectType.DAMAGE_MULTIPLIER_PCT, 5, 0));
        nodes.add(node("combat_t2_left", SkillPillar.COMBAT, 2, 2, "Quick Hands", "+8% reload speed", SkillEffectType.RELOAD_SPEED_PCT, 8, -1, "combat_t1"));
        nodes.add(node("combat_t2_right", SkillPillar.COMBAT, 2, 2, "Sharpened Blows", "+5% damage", SkillEffectType.DAMAGE_MULTIPLIER_PCT, 5, 1, "combat_t1"));
        nodes.add(node("combat_t3_left", SkillPillar.COMBAT, 3, 3, "Critical Focus", "+3% crit chance", SkillEffectType.CRIT_CHANCE_PCT, 3, -2, "combat_t2_left"));
        nodes.add(node("combat_t3_right", SkillPillar.COMBAT, 3, 3, "Follow-through", "+8% damage", SkillEffectType.DAMAGE_MULTIPLIER_PCT, 8, 2, "combat_t2_right"));
        nodes.add(node("combat_t4_left", SkillPillar.COMBAT, 4, 5, "Steel Rhythm", "+12% reload speed", SkillEffectType.RELOAD_SPEED_PCT, 12, -1, "combat_t3_left"));
        nodes.add(node("combat_t4_right", SkillPillar.COMBAT, 4, 5, "Execution Drill", "+10% damage", SkillEffectType.DAMAGE_MULTIPLIER_PCT, 10, 1, "combat_t3_right"));
        nodes.add(node("combat_t5", SkillPillar.COMBAT, 5, 8, "War Instinct", "+5% crit chance", SkillEffectType.CRIT_CHANCE_PCT, 5, 0, "combat_t4_left", "combat_t4_right"));

        // Vitality (green)
        nodes.add(node("vitality_t1", SkillPillar.VITALITY, 1, 1, "Healthy Start", "+6% max HP", SkillEffectType.MAX_HP_PCT, 6, 0));
        nodes.add(node("vitality_t2_left", SkillPillar.VITALITY, 2, 2, "Fleet Step", "+4% move speed", SkillEffectType.MOVE_SPEED_PCT, 4, -1, "vitality_t1"));
        nodes.add(node("vitality_t2_right", SkillPillar.VITALITY, 2, 2, "Steady Frame", "+6% max HP", SkillEffectType.MAX_HP_PCT, 6, 1, "vitality_t1"));
        nodes.add(node("vitality_t3_left", SkillPillar.VITALITY, 3, 3, "Swift Recovery", "+10% healing received", SkillEffectType.HEALING_RECEIVED_PCT, 10, -2, "vitality_t2_left"));
        nodes.add(node("vitality_t3_right", SkillPillar.VITALITY, 3, 3, "Reinforced Core", "+8% max HP", SkillEffectType.MAX_HP_PCT, 8, 2, "vitality_t2_right"));
        nodes.add(node("vitality_t4_left", SkillPillar.VITALITY, 4, 5, "Reflex Drive", "+5% move speed", SkillEffectType.MOVE_SPEED_PCT, 5, -1, "vitality_t3_left"));
        nodes.add(node("vitality_t4_right", SkillPillar.VITALITY, 4, 5, "Iron Heart", "+10% max HP", SkillEffectType.MAX_HP_PCT, 10, 1, "vitality_t3_right"));
        nodes.add(node("vitality_t5", SkillPillar.VITALITY, 5, 8, "Unbreakable", "+12% max HP", SkillEffectType.MAX_HP_PCT, 12, 0, "vitality_t4_left", "vitality_t4_right"));

        // Abundance (gold)
        nodes.add(node("abundance_t1", SkillPillar.ABUNDANCE, 1, 1, "Lucky Pockets", "+8% cash gain", SkillEffectType.CASH_GAIN_PCT, 8, 0));
        nodes.add(node("abundance_t2_left", SkillPillar.ABUNDANCE, 2, 2, "Shining Finds", "+6% gem gain", SkillEffectType.GEM_GAIN_PCT, 6, -1, "abundance_t1"));
        nodes.add(node("abundance_t2_right", SkillPillar.ABUNDANCE, 2, 2, "Fine Haul", "+6% loot quality", SkillEffectType.LOOT_QUALITY_PCT, 6, 1, "abundance_t1"));
        nodes.add(node("abundance_t3_left", SkillPillar.ABUNDANCE, 3, 3, "Treasure Scent", "+10% loot quality", SkillEffectType.LOOT_QUALITY_PCT, 10, -2, "abundance_t2_left"));
        nodes.add(node("abundance_t3_right", SkillPillar.ABUNDANCE, 3, 3, "Golden Touch", "+10% cash gain", SkillEffectType.CASH_GAIN_PCT, 10, 2, "abundance_t2_right"));
        nodes.add(node("abundance_t4_left", SkillPillar.ABUNDANCE, 4, 5, "Gemcraft", "+10% gem gain", SkillEffectType.GEM_GAIN_PCT, 10, -1, "abundance_t3_left"));
        nodes.add(node("abundance_t4_right", SkillPillar.ABUNDANCE, 4, 5, "Loaded Crates", "+12% loot quality", SkillEffectType.LOOT_QUALITY_PCT, 12, 1, "abundance_t3_right"));
        nodes.add(node("abundance_t5", SkillPillar.ABUNDANCE, 5, 8, "Overflow", "+15% cash gain", SkillEffectType.CASH_GAIN_PCT, 15, 0, "abundance_t4_left", "abundance_t4_right"));

        NODES = Collections.unmodifiableList(nodes);
        Map<String, SkillNode> byId = new LinkedHashMap<>();
        for (SkillNode node : NODES) {
            byId.put(node.getId(), node);
        }
        NODE_BY_ID = Collections.unmodifiableMap(byId);
    }

    private SkillTreeData() {}

    private static SkillNode node(
            String id,
            SkillPillar pillar,
            int tier,
            int cost,
            String title,
            String description,
            SkillEffectType effectType,
            double effectValue,
            int branchOffset,
            String... parents
    ) {
        return new SkillNode(
                id,
                pillar,
                tier,
                cost,
                title,
                description,
                effectType,
                effectValue,
                Arrays.asList(parents),
                branchOffset
        );
    }

    public static List<SkillNode> allNodes() {
        return NODES;
    }

    public static SkillNode getById(String id) {
        return NODE_BY_ID.get(id);
    }

    public static List<SkillNode> nodesForPillar(SkillPillar pillar) {
        List<SkillNode> result = new ArrayList<>();
        for (SkillNode n : NODES) {
            if (n.getPillar() == pillar) {
                result.add(n);
            }
        }
        return result;
    }
}
