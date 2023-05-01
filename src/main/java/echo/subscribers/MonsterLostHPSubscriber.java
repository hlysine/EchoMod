package echo.subscribers;

import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

/**
 * Only applicable to relics. Use {@link com.megacrit.cardcrawl.powers.AbstractPower#wasHPLost(DamageInfo, int)} for powers.
 */
public interface MonsterLostHPSubscriber {
    void wasMonsterHPLost(AbstractMonster monster, DamageInfo info, int amount);
}
