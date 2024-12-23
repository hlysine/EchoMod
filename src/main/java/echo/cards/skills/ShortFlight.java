package echo.cards.skills;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import echo.EchoMod;
import echo.cards.AbstractBaseCard;
import echo.powers.FlightPower;

public class ShortFlight extends AbstractBaseCard {

    public static final String ID = EchoMod.makeID(ShortFlight.class.getSimpleName());

    private static final CardTarget TARGET = CardTarget.SELF;

    public ShortFlight() {
        super(ID, TARGET);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new GainBlockAction(p, p, block));
        addToBot(new ApplyPowerAction(p, p, new FlightPower(p, magicNumber)));
    }
}
