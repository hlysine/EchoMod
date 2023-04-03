package echo.cards.skills;

import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import echo.EchoMod;
import echo.cards.ChargedCard;
import echo.mechanics.duplicate.ChargedChecker;

public class CassidyCopy extends ChargedCard {

    public static final String ID = EchoMod.makeID(CassidyCopy.class.getSimpleName());

    private static final CardTarget TARGET = CardTarget.NONE;

    public CassidyCopy() {
        super(ID, TARGET, true);

        this.exhaust = true;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        if (ChargedChecker.consumeCharge()) {
            addToBot(new MakeTempCardInHandAction(this.cardsToPreview));
        }
    }
}
