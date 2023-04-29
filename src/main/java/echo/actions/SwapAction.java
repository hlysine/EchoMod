package echo.actions;

import basemod.BaseMod;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.TutorialStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.vfx.ThoughtBubble;
import echo.EchoMod;
import echo.subscribers.SwapSubscriber;

import java.util.List;

public class SwapAction extends AbstractGameAction {
    private static final TutorialStrings tutorialStrings = CardCrawlGame.languagePack.getTutorialString(EchoMod.makeID(SwapAction.class.getSimpleName()));
    private static final String[] TEXT = tutorialStrings.TEXT;

    public SwapAction(int amount) {
        this.amount = amount;
    }

    @Override
    public void update() {
        AbstractPlayer p = AbstractDungeon.player;
        int maxSwap = BaseMod.MAX_HAND_SIZE - p.hand.size();

        if (maxSwap == 0) {
            AbstractDungeon.effectList.add(new ThoughtBubble(p.dialogX, p.dialogY, 3.0F, TEXT[1], true));
        }

        addToTop(new HandSelectAction(Math.min(amount, maxSwap), c -> true, cards -> {
            triggerOnSwap(DrawCardAction.drawnCards, cards);
            for (AbstractCard c : cards) {
                p.hand.moveToDeck(c, false);
            }
        }, cards -> {
        }, TEXT[0], false, false, false, false));
        addToTop(new DrawCardAction(Math.min(amount, maxSwap)));
        this.isDone = true;
    }

    private void triggerOnSwap(List<AbstractCard> toHand, List<AbstractCard> toDrawPile) {
        for (AbstractPower power : AbstractDungeon.player.powers) {
            if (power instanceof SwapSubscriber) {
                SwapSubscriber subscriber = (SwapSubscriber) power;
                subscriber.onSwap(toHand, toDrawPile);
            }
        }
        for (AbstractRelic relic : AbstractDungeon.player.relics) {
            if (relic instanceof SwapSubscriber) {
                SwapSubscriber subscriber = (SwapSubscriber) relic;
                subscriber.onSwap(toHand, toDrawPile);
            }
        }
    }
}
