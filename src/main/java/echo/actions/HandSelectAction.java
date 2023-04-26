package echo.actions;


import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class HandSelectAction extends AbstractGameAction {
    private boolean first;
    private final Predicate<AbstractCard> requirement;
    private final String text;
    private final AbstractPlayer p;

    private final ArrayList<AbstractCard> invalidCards;

    private final Consumer<ArrayList<AbstractCard>> process; //when cards are selected.
    private final Consumer<ArrayList<AbstractCard>> finale; //always occurs at end, even if no cards are affected

    private final boolean returnSelected;
    private final boolean anyAmount;
    private final boolean canPickZero;
    private final boolean dontRefresh;

    public HandSelectAction(int amount, Predicate<AbstractCard> requirement, Consumer<ArrayList<AbstractCard>> process, Consumer<ArrayList<AbstractCard>> finale, String selectionText, boolean returnSelected, boolean anyAmount, boolean canPickZero, boolean dontRefresh) {
        this.p = AbstractDungeon.player;

        this.actionType = ActionType.CARD_MANIPULATION;
        this.amount = amount;
        this.first = true;
        this.requirement = requirement;
        this.process = process;
        this.finale = finale;
        this.text = selectionText;

        this.invalidCards = new ArrayList<>();

        this.returnSelected = returnSelected;
        this.anyAmount = anyAmount;
        this.canPickZero = canPickZero;
        this.dontRefresh = dontRefresh;
    }

    public HandSelectAction(int amount, Predicate<AbstractCard> requirement, Consumer<ArrayList<AbstractCard>> process, String selectionText) {
        this(amount, requirement, process, null, selectionText, true, false, false, false);
    }

    @Override
    public void update() {
        if (first) {
            first = false;

            ArrayList<AbstractCard> validCards = new ArrayList<>();

            if (p.hand.isEmpty()) {
                if (finale != null)
                    finale.accept(new ArrayList<>());
                this.isDone = true;
                return;
            }

            for (AbstractCard c : p.hand.group) {
                if (requirement.test(c))
                    validCards.add(c);
            }

            if (validCards.isEmpty()) {
                if (finale != null)
                    finale.accept(new ArrayList<>());
                this.isDone = true;
                return;
            }

            if (validCards.size() <= this.amount && !anyAmount) {
                if (!returnSelected)
                    AbstractDungeon.player.hand.group.removeAll(validCards);

                process.accept(validCards);
                if (finale != null)
                    finale.accept(validCards);
                this.isDone = true;
                return;
            }

            invalidCards.addAll(p.hand.group);
            invalidCards.removeIf(validCards::contains);
            p.hand.group.removeAll(invalidCards);

            if (p.hand.isEmpty()) //should theoretically mean validCards is empty. Theoretically should never arrive here.
            {
                returnCards();
                if (!returnSelected)
                    AbstractDungeon.player.hand.group.removeAll(validCards);
                if (finale != null)
                    finale.accept(validCards);
                this.isDone = true;
                return;
            }

            AbstractDungeon.handCardSelectScreen.open(text, this.amount, anyAmount, canPickZero);
            return;
        }

        ArrayList<AbstractCard> result = new ArrayList<>();
        if (!AbstractDungeon.handCardSelectScreen.wereCardsRetrieved) {
            result.addAll(AbstractDungeon.handCardSelectScreen.selectedCards.group);

            process.accept(result);

            if (returnSelected)
                for (AbstractCard c : AbstractDungeon.handCardSelectScreen.selectedCards.group)
                    this.p.hand.addToTop(c);

            returnCards();

            AbstractDungeon.handCardSelectScreen.wereCardsRetrieved = true;
        }

        if (finale != null)
            finale.accept(result);

        this.isDone = true;
    }

    private void returnCards() {
        for (AbstractCard c : this.invalidCards) {
            this.p.hand.addToTop(c);
        }

        if (!dontRefresh)
            this.p.hand.refreshHandLayout();
    }
}
