package echo.cards.skills;

import com.megacrit.cardcrawl.actions.common.MakeTempCardInDrawPileAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import echo.EchoMod;
import echo.cards.AbstractBaseCard;
import echo.cards.attacks.TriShot;

public class HandReload extends AbstractBaseCard {

    public static final String ID = EchoMod.makeID(HandReload.class.getSimpleName());

    private static final CardTarget TARGET = CardTarget.NONE;

    public HandReload() {
        super(ID, TARGET);

        this.cardsToPreview = getCardsToPreview();
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new MakeTempCardInDrawPileAction(getCardsToPreview(), magicNumber, true, true));
    }

    @Override
    public void upgrade() {
        super.upgrade();
        this.cardsToPreview = getCardsToPreview();
    }

    private AbstractCard getCardsToPreview() {
        AbstractCard card = new TriShot();
        for (int i = 0; i < this.timesUpgraded; i++) {
            card.upgrade();
        }
        return card;
    }
}
