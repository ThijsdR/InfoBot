package clashofclans;

class CoC_Hero {
    private String name;
    private int level;

    CoC_Hero(String name, int level) {
        this.name = name;
        this.level = level;
    }

    String getName() {
        return name;
    }
    int getLevel() {
        return level;
    }
}
