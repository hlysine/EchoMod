package echo.cards.skills;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import echo.EchoMod;
import echo.cards.AbstractBaseCard;
import echo.powers.FlightPower;
import echo.powers.GroundedPower;

public class EchoDash extends AbstractBaseCard {

    public static final String ID = EchoMod.makeID(EchoDash.class.getSimpleName());

    private static final CardTarget TARGET = CardTarget.SELF;

    public EchoDash() {
        super(ID, TARGET);

        this.exhaust = true;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new ApplyPowerAction(p, p, new FlightPower(p, magicNumber)));
        addToBot(new ApplyPowerAction(p, p, new GroundedPower(p, magicNumber2)));
    }
}
