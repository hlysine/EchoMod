package echo.cards.skills;

import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import echo.EchoMod;
import echo.cards.ChargedCard;
import echo.mechanics.duplicate.ChargedChecker;

public class SojournCopy extends ChargedCard {

    public static final String ID = EchoMod.makeID(SojournCopy.class.getSimpleName());

    private static final CardTarget TARGET = CardTarget.NONE;

    public SojournCopy() {
        super(ID, TARGET, true);

        this.exhaust = true;
        this.cardsToPreview = getCardsToPreview();
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        if (ChargedChecker.consumeCharge()) {
            addToBot(new MakeTempCardInHandAction(getCardsToPreview()));
        }
    }

    @Override
    public void upgrade() {
        super.upgrade();
        this.cardsToPreview = getCardsToPreview();
    }

    private AbstractCard getCardsToPreview() {
        AbstractCard card = new EchoOverclock();
        for (int i = 0; i < this.timesUpgraded; i++) {
            card.upgrade();
        }
        return card;
    }
}
