package cn.encmys.ykdz.forest.dailyshop.rarity;

public class Rarity {
    private String id;
    private String name;
    private int weight;

    public Rarity(String id, String name, int weight) {
        this.id = id;
        this.name = name;
        this.weight = weight;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getWeight() {
        return weight;
    }
}
