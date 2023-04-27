package echo.cards.skills;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.NextTurnBlockPower;
import echo.EchoMod;
import echo.actions.SwapAction;
import echo.cards.AbstractBaseCard;

public class Preprocess extends AbstractBaseCard {

    public static final String ID = EchoMod.makeID(Preprocess.class.getSimpleName());

    private static final CardTarget TARGET = CardTarget.SELF;

    public Preprocess() {
        super(ID, TARGET);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new SwapAction(magicNumber));
        addToBot(new ApplyPowerAction(p, p, new NextTurnBlockPower(p, block)));
    }
}
