package echo.cards.statuses;

import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import echo.EchoMod;
import echo.cards.AbstractBaseCard;
import echo.subscribers.ChargedSubscriber;
import echo.subscribers.FocusedSubscriber;

public class Hacked extends AbstractBaseCard implements ChargedSubscriber, FocusedSubscriber {

    public static final String ID = EchoMod.makeID(Hacked.class.getSimpleName());

    private static final CardTarget TARGET = CardTarget.SELF;

    public Hacked() {
        super(ID, TARGET);

        this.isEthereal = true;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
    }

    @Override
    public boolean canUse(AbstractPlayer p, AbstractMonster m) {
        return false;
    }

    @Override
    public boolean shouldBlockCharged() {
        return true;
    }

    @Override
    public boolean shouldBlockFocused(AbstractMonster target) {
        return true;
    }
}
