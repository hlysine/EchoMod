package echo.cards.skills;

import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import echo.EchoMod;
import echo.actions.SwapAction;
import echo.cards.AbstractBaseCard;

public class Initialization extends AbstractBaseCard {

    public static final String ID = EchoMod.makeID(Initialization.class.getSimpleName());

    private static final CardTarget TARGET = CardTarget.NONE;

    public Initialization() {
        super(ID, TARGET);

        this.isInnate = true;
        this.exhaust = true;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new SwapAction(magicNumber));
    }
}
