package echo.cards.skills;

import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import echo.EchoMod;
import echo.cards.AbstractBaseCard;
import echo.powers.UltimateChargePower;

public class ChargeConversion extends AbstractBaseCard {

    public static final String ID = EchoMod.makeID(ChargeConversion.class.getSimpleName());

    private static final CardTarget TARGET = CardTarget.SELF;

    public ChargeConversion() {
        super(ID, TARGET);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new ReducePowerAction(p, p, UltimateChargePower.POWER_ID, magicNumber));
        addToBot(new GainEnergyAction(magicNumber2));
    }
}
