package it.polimi.ingsw.controller.network.data;

public class EnemyStrenght extends DataContainer {
    private int enemyStrenght;
    private float playerStrenght;

    public EnemyStrenght(int enemyStrenght, float playerStrenght) {
        this.enemyStrenght = enemyStrenght;
        this.playerStrenght = playerStrenght;
    }

    public int getEnemyStrenght() {
        return enemyStrenght;
    }

    public float getPlayerStrenght() {
        return playerStrenght;
    }
}
