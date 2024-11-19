package games.talesofvalor;

public class TOVEncounter {
    int encounterLevel;
    int enemyCount;

    public TOVEncounter(int encounterLevel, int enemyCount) {
        this.encounterLevel = encounterLevel;
        this.enemyCount = enemyCount;
    }

    public TOVEncounter copy() {
        return new TOVEncounter(encounterLevel, enemyCount);
    }
}
