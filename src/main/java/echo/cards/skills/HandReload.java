package echo.cards.skills;

import com.megacrit.cardcrawl.actions.common.MakeTempCardInDrawPileAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import echo.EchoMod;
import echo.cards.AbstractBaseCard;

public class HandReload extends AbstractBaseCard {

    public static final String ID = EchoMod.makeID(HandReload.class.getSimpleName());

    private static final CardTarget TARGET = CardTarget.NONE;

    public HandReload() {
        super(ID, TARGET);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new MakeTempCardInDrawPileAction(this.cardsToPreview, magicNumber, true, true));
    }
}
