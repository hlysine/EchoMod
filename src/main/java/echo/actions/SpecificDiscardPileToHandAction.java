package echo.actions;

import basemod.BaseMod;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class SpecificDiscardPileToHandAction extends AbstractGameAction {

    private final Predicate<AbstractCard> selector;

    public SpecificDiscardPileToHandAction(final Predicate<AbstractCard> selector) {
        this.selector = selector;
    }

    @Override
    public void update() {
        List<AbstractCard> cardsToMove = new ArrayList<>();
        for (AbstractCard card : AbstractDungeon.player.discardPile.group) {
            if (selector.test(card)) {
                cardsToMove.add(card);
            }
        }
        for (AbstractCard card : cardsToMove) {
            if (AbstractDungeon.player.hand.size() >= BaseMod.MAX_HAND_SIZE) break;

            AbstractDungeon.player.discardPile.removeCard(card);
            AbstractDungeon.player.hand.addToHand(card);
            card.lighten(false);
            card.unhover();
        }
        AbstractDungeon.player.hand.refreshHandLayout();

        this.isDone = true;
    }
}
