package clashofclans;

public class CoC_WarAttackContainer implements Comparable {
    private String attackerTag;
    private String defenderTag;
    private int stars;
    private int destructionPercentage;
    private int order;

    CoC_WarAttackContainer(String attackerTag, String defenderTag, int stars, int destructionPercentage, int order) {
        this.attackerTag = attackerTag;
        this.defenderTag = defenderTag;
        this.stars = stars;
        this.destructionPercentage = destructionPercentage;
        this.order = order;
    }

    String getAttackerTag() {
        return attackerTag;
    }
    String getDefenderTag() {
        return defenderTag;
    }
    int getStars() {
        return stars;
    }
    int getDestructionPercentage() {
        return destructionPercentage;
    }
    int getOrder() { return order; }

    @Override
    public int compareTo(Object o) {
        return 0;
    }
}
