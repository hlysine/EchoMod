package echo.cards.skills;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.WeakPower;
import echo.EchoMod;
import echo.cards.AbstractBaseCard;
import echo.powers.StickyBombPower;
import echo.util.RunnableAction;

public class BlindingBombs extends AbstractBaseCard {

    public static final String ID = EchoMod.makeID(BlindingBombs.class.getSimpleName());

    private static final CardTarget TARGET = CardTarget.ALL_ENEMY;

    public BlindingBombs() {
        super(ID, TARGET);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new RunnableAction(() -> {
            for (AbstractMonster monster : AbstractDungeon.getMonsters().monsters) {
                if (!monster.isDeadOrEscaped() && monster.currentHealth > 0) {
                    if (StickyBombPower.getAmount(monster) > 0) {
                        addToBot(new ApplyPowerAction(monster, p, new WeakPower(monster, magicNumber, false)));
                    }
                }
            }
        }));
    }
}
