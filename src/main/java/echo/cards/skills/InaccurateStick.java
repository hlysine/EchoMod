package echo.cards.skills;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import echo.EchoMod;
import echo.cards.AbstractBaseCard;
import echo.patches.cards.CustomCardTags;
import echo.powers.StickyBombPower;

public class InaccurateStick extends AbstractBaseCard {

    public static final String ID = EchoMod.makeID(InaccurateStick.class.getSimpleName());

    private static final CardTarget TARGET = CardTarget.ALL_ENEMY;

    public InaccurateStick() {
        super(ID, TARGET);

        this.tags.add(CustomCardTags.STICK);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        for (AbstractMonster mo : AbstractDungeon.getMonsters().monsters) {
            addToBot(new ApplyPowerAction(mo, p, new StickyBombPower(mo, this.magicNumber), this.magicNumber, true, AbstractGameAction.AttackEffect.NONE));
        }
    }
}
