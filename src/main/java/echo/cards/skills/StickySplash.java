package echo.cards.skills;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import echo.EchoMod;
import echo.cards.AbstractBaseCard;
import echo.patches.cards.CustomCardTags;
import echo.powers.StickyBombPower;

public class StickySplash extends AbstractBaseCard {

    public static final String ID = EchoMod.makeID(StickySplash.class.getSimpleName());

    private static final CardTarget TARGET = CardTarget.ENEMY;

    public StickySplash() {
        super(ID, TARGET);

        this.tags.add(CustomCardTags.STICK);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new ApplyPowerAction(m, p, new StickyBombPower(m, magicNumber)));
        addToBot(new ApplyPowerAction(p, p, new StickyBombPower(p, magicNumber2)));
    }
}
