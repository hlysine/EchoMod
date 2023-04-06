package echo.cards.skills;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import echo.EchoMod;
import echo.cards.AbstractBaseCard;
import echo.patches.cards.CustomCardTags;
import echo.powers.StickyFlarePower;

public class StickyFlare extends AbstractBaseCard {

    public static final String ID = EchoMod.makeID(StickyFlare.class.getSimpleName());

    private static final CardTarget TARGET = CardTarget.SELF;

    public StickyFlare() {
        super(ID, TARGET);

        this.tags.add(CustomCardTags.STICK);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new ApplyPowerAction(p, p, new StickyFlarePower(p, this.magicNumber)));
    }
}
