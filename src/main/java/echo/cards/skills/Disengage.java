package echo.cards.skills;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.WeakPower;
import echo.EchoMod;
import echo.cards.AbstractBaseCard;
import echo.powers.FlightPower;

public class Disengage extends AbstractBaseCard {

    public static final String ID = EchoMod.makeID(Disengage.class.getSimpleName());

    private static final CardTarget TARGET = CardTarget.ENEMY;

    public Disengage() {
        super(ID, TARGET);

        this.exhaust = true;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new ApplyPowerAction(p, p, new FlightPower(p, magicNumber)));
        addToBot(new ApplyPowerAction(m, p, new WeakPower(m, magicNumber2, false)));
    }
}
