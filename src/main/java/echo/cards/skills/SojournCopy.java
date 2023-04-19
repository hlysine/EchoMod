package echo.cards.skills;

import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import echo.EchoMod;
import echo.cards.ChargedCard;

public class SojournCopy extends ChargedCard {

    public static final String ID = EchoMod.makeID(SojournCopy.class.getSimpleName());

    private static final CardTarget TARGET = CardTarget.NONE;

    public SojournCopy() {
        super(ID, TARGET, true);

        this.exhaust = true;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        super.use(p, m);
        UnlockTracker.markCardAsSeen(this.cardsToPreview.cardID);
        addToBot(new MakeTempCardInHandAction(this.cardsToPreview));
    }
}
