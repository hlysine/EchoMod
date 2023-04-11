package echo.mechanics.duplicate;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.unlock.UnlockTracker;

import java.util.ArrayList;

public class DuplicatedDecks {
    public ArrayList<AbstractCard> masterDeck;
    public ArrayList<AbstractCard> drawPile;
    public ArrayList<AbstractCard> hand;
    public ArrayList<AbstractCard> discardPile;
    public ArrayList<AbstractCard> exhaustPile;
    public ArrayList<AbstractCard> limbo;

    DuplicatedDecks() {
    }

    public static DuplicatedDecks extractFromPlayer(AbstractPlayer player) {
        DuplicatedDecks decks = new DuplicatedDecks();
        decks.masterDeck = new ArrayList<>(player.masterDeck.group);
        decks.drawPile = new ArrayList<>(player.drawPile.group);
        decks.hand = new ArrayList<>(player.hand.group);
        decks.discardPile = new ArrayList<>(player.discardPile.group);
        decks.exhaustPile = new ArrayList<>(player.exhaustPile.group);
        decks.limbo = new ArrayList<>(player.limbo.group);
        return decks;
    }

    public void applyToPlayer(AbstractPlayer player) {
        player.masterDeck.group.clear();
        player.masterDeck.group.addAll(masterDeck);
        player.drawPile.group.clear();
        player.drawPile.group.addAll(drawPile);
        player.hand.group.clear();
        player.hand.group.addAll(hand);
        player.discardPile.group.clear();
        player.discardPile.group.addAll(discardPile);
        player.exhaustPile.group.clear();
        player.exhaustPile.group.addAll(exhaustPile);
        player.limbo.group.clear();
        player.limbo.group.addAll(limbo);

        for (AbstractCard card : player.masterDeck.group) {
            UnlockTracker.markCardAsSeen(card.cardID);
            resetCard(card);
        }
        for (AbstractCard card : player.drawPile.group) {
            UnlockTracker.markCardAsSeen(card.cardID);
            resetCard(card);
        }
        for (AbstractCard card : player.hand.group) {
            UnlockTracker.markCardAsSeen(card.cardID);
            resetCard(card);
        }
        for (AbstractCard card : player.discardPile.group) {
            UnlockTracker.markCardAsSeen(card.cardID);
            resetCard(card);
        }
        for (AbstractCard card : player.exhaustPile.group) {
            UnlockTracker.markCardAsSeen(card.cardID);
            resetCard(card);
        }
        for (AbstractCard card : player.limbo.group) {
            UnlockTracker.markCardAsSeen(card.cardID);
            resetCard(card);
        }
    }

    public static void resetCard(AbstractCard card) {
        if (AbstractDungeon.player.hoveredCard == card) {
            AbstractDungeon.player.releaseCard();
        }
        AbstractDungeon.actionManager.removeFromQueue(card);
        card.unfadeOut();
        card.fadingOut = false;
        card.unhover();
        card.untip();
        card.stopGlowing();
    }
}
