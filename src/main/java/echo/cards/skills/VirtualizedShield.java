package echo.cards.skills;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import echo.EchoMod;
import echo.cards.AbstractBaseCard;
import echo.patches.cards.CustomCardTags;
import echo.powers.VirtualizedShieldPower;

public class VirtualizedShield extends AbstractBaseCard {

    public static final String ID = EchoMod.makeID(VirtualizedShield.class.getSimpleName());

    private static final CardTarget TARGET = CardTarget.SELF;

    public VirtualizedShield() {
        super(ID, TARGET);

        this.selfRetain = true;
        this.tags.add(CustomCardTags.CONSTANT);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new ApplyPowerAction(p, p, new VirtualizedShieldPower(p, magicNumber)));
    }
}
