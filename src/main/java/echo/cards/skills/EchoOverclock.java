package echo.cards.skills;

import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import echo.EchoMod;
import echo.cards.ChargedCard;

public class EchoOverclock extends ChargedCard {

    public static final String ID = EchoMod.makeID(EchoOverclock.class.getSimpleName());

    private static final CardTarget TARGET = CardTarget.NONE;

    public EchoOverclock() {
        super(ID, TARGET, true);

        this.exhaust = true;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        super.use(p, m);
        addToBot(new MakeTempCardInHandAction(this.cardsToPreview));
    }
}
