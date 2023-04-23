package echo.cards.skills;

import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import echo.EchoMod;
import echo.actions.duplicate.DuplicateRandomPlayerAction;
import echo.cards.ChargedCard;
import echo.characters.Echo;
import echo.subscribers.AfterCardUseSubscriber;
import echo.util.RunnableAction;

public class RecursiveCopy extends ChargedCard implements AfterCardUseSubscriber {

    public static final String ID = EchoMod.makeID(RecursiveCopy.class.getSimpleName());

    private static final CardTarget TARGET = CardTarget.SELF;

    public RecursiveCopy() {
        super(ID, TARGET, true);
    }

    @Override
    public void afterUse() {
        addToBot(new DuplicateRandomPlayerAction(new AbstractPlayer.PlayerClass[]{Echo.Enums.ECHO, AbstractDungeon.player.chosenClass}, new RunnableAction(() -> {
            AbstractCard copy = this.makeStatEquivalentCopy();
            if (this.upgraded)
                copy.freeToPlayOnce = true;
            addToBot(new MakeTempCardInHandAction(copy));
        })));
    }
}
