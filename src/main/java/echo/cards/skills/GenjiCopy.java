package echo.cards.skills;

import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import echo.EchoMod;
import echo.cards.ChargedCard;
import echo.effects.SfxStore;
import echo.util.RunnableAction;

public class GenjiCopy extends ChargedCard {

    public static final String ID = EchoMod.makeID(GenjiCopy.class.getSimpleName());

    private static final CardTarget TARGET = CardTarget.NONE;

    public GenjiCopy() {
        super(ID, TARGET, true);

        this.exhaust = true;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        super.use(p, m);
        addToBot(new RunnableAction(() -> SfxStore.GENJI_ULTIMATE_START.play(0.1f)));
        addToBot(new MakeTempCardInHandAction(this.cardsToPreview));
    }
}
