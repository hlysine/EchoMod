package echo.cards.skills;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import echo.EchoMod;
import echo.cards.AbstractBaseCard;
import echo.powers.NanoBoostPower;

public class NanoBoost extends AbstractBaseCard {

    public static final String ID = EchoMod.makeID(NanoBoost.class.getSimpleName());

    private static final CardTarget TARGET = CardTarget.SELF;

    public NanoBoost() {
        super(ID, TARGET);

        this.exhaust = true;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new ApplyPowerAction(p, p, new NanoBoostPower(p, magicNumber)));
    }
}
