package echo.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.TutorialStrings;
import echo.EchoMod;

public class SwapAction extends AbstractGameAction {
    private static final TutorialStrings tutorialStrings = CardCrawlGame.languagePack.getTutorialString(EchoMod.makeID(SwapAction.class.getSimpleName()));
    private static final String[] TEXT = tutorialStrings.TEXT;

    public SwapAction(int amount) {
        this.amount = amount;
    }

    @Override
    public void update() {
        addToTop(new HandSelectAction(amount, c -> true, cards -> {
            for (AbstractCard c : cards) {
                AbstractDungeon.player.hand.moveToDeck(c, false);
            }
        }, cards -> {
        }, TEXT[0], false, false, false, false));
        addToTop(new DrawCardAction(amount));
        this.isDone = true;
    }
}
