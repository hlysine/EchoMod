package echo.cards.powers;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import echo.EchoMod;
import echo.cards.AbstractBaseCard;
import echo.powers.AutoReloadPower;

public class AutoReload extends AbstractBaseCard {

    public static final String ID = EchoMod.makeID(AutoReload.class.getSimpleName());

    private static final CardTarget TARGET = CardTarget.SELF;

    public AutoReload() {
        super(ID, TARGET);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new ApplyPowerAction(p, p, new AutoReloadPower(p, magicNumber)));
    }
}
