package net.islandearth.queste.quest.trigger;

public abstract class QuestTrigger {

    private final int count;

    public QuestTrigger(int count) {
        this.count = count;
    }

    public int getCount() {
        return count;
    }

    public abstract String getName();
}
