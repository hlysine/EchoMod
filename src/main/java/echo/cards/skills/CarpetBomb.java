package echo.cards.skills;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import echo.EchoMod;
import echo.cards.AbstractBaseCard;
import echo.powers.StickyBombPower;

public class CarpetBomb extends AbstractBaseCard {

    public static final String ID = EchoMod.makeID(CarpetBomb.class.getSimpleName());

    private static final CardTarget TARGET = CardTarget.ALL_ENEMY;

    public CarpetBomb() {
        super(ID, TARGET);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        int effect = doXCostEffect() * magicNumber + magicNumber2;

        if (effect > 0) {
            for (AbstractMonster monster : AbstractDungeon.getMonsters().monsters) {
                if (!monster.isDeadOrEscaped())
                    addToBot(new ApplyPowerAction(monster, p, new StickyBombPower(monster, effect)));
            }
        }
    }
}
