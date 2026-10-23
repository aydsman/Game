package skilltree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SkillNode {
    private final String id;
    private final SkillPillar pillar;
    private final int tier;
    private final int cost;
    private final String title;
    private final String description;
    private final SkillEffectType effectType;
    private final double effectValue;
    private final List<String> parents;
    private final int branchOffset;

    public SkillNode(
            String id,
            SkillPillar pillar,
            int tier,
            int cost,
            String title,
            String description,
            SkillEffectType effectType,
            double effectValue,
            List<String> parents,
            int branchOffset
    ) {
        this.id = id;
        this.pillar = pillar;
        this.tier = tier;
        this.cost = cost;
        this.title = title;
        this.description = description;
        this.effectType = effectType;
        this.effectValue = effectValue;
        this.parents = Collections.unmodifiableList(new ArrayList<>(parents));
        this.branchOffset = branchOffset;
    }

    public String getId() { return id; }
    public SkillPillar getPillar() { return pillar; }
    public int getTier() { return tier; }
    public int getCost() { return cost; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public SkillEffectType getEffectType() { return effectType; }
    public double getEffectValue() { return effectValue; }
    public List<String> getParents() { return parents; }
    public int getBranchOffset() { return branchOffset; }
}
