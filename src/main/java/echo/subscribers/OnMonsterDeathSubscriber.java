package echo.subscribers;

import com.megacrit.cardcrawl.monsters.AbstractMonster;

/**
 * Only applicable to powers. Use the built-in onMonsterDeath method for relics.
 */
public interface OnMonsterDeathSubscriber {
    void onMonsterDeath(AbstractMonster monster);
}
