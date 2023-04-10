package echo.mechanics.duplicate;

import basemod.BaseMod;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.random.Random;
import com.megacrit.cardcrawl.unlock.UnlockTracker;

import java.util.*;
import java.util.stream.Collectors;

public class CardTransformer {

    private final Random rng;
    private final AbstractCard.CardColor fromColor;
    private final AbstractCard.CardColor toColor;

    public CardTransformer(Random rng, AbstractPlayer.PlayerClass fromClass, AbstractPlayer.PlayerClass toClass) {
        this.rng = new Random(rng.randomLong());
        this.fromColor = BaseMod.findCharacter(fromClass).getCardColor();
        this.toColor = BaseMod.findCharacter(toClass).getCardColor();
    }

    public Decks transform(Decks decks) {
        List<AbstractCard> cardPool = getCardPool(toColor, false);
        cardPool.addAll(getCardPool(AbstractCard.CardColor.COLORLESS, false));
        cardPool.addAll(getCardPool(AbstractCard.CardColor.CURSE, false));
        Map<UUID, AbstractCard> cache = new HashMap<>();

        Decks newDecks = new Decks();
        newDecks.masterDeck = transform(decks.masterDeck, cache, cardPool);
        newDecks.drawPile = transform(decks.drawPile, cache, cardPool);
        newDecks.hand = transform(decks.hand, cache, cardPool);
        newDecks.discardPile = transform(decks.discardPile, cache, cardPool);
        newDecks.exhaustPile = transform(decks.exhaustPile, cache, cardPool);
        newDecks.limbo = transform(decks.limbo, cache, cardPool);

        return newDecks;
    }

    private ArrayList<AbstractCard> transform(ArrayList<AbstractCard> cards, Map<UUID, AbstractCard> cache, List<AbstractCard> cardPool) {
        ArrayList<AbstractCard> newCards = new ArrayList<>();
        for (AbstractCard card : cards) {
            if (card.color != fromColor) {
                newCards.add(card.makeSameInstanceOf());
                continue;
            }

            if (cache.containsKey(card.uuid)) {
                newCards.add(transformCard(card, cache.get(card.uuid).makeSameInstanceOf()));
                continue;
            }

            List<AbstractCard> rawChoices = cardPool.stream()
                    .sorted(Comparator.comparingInt(c -> rateSimilarity(card, (AbstractCard) c)).reversed())
                    .limit(3)
                    .collect(Collectors.toList());
            List<AbstractCard> choices = new ArrayList<>(5);
            choices.add(rawChoices.remove(0));

            // remove choices after a huge score gap
            for (AbstractCard c : rawChoices) {
                if (Math.abs(rateSimilarity(card, c) - rateSimilarity(card, choices.get(choices.size() - 1))) > 5000)
                    break;
                choices.add(c);
            }

            AbstractCard replacement = choices.get(rng.random(choices.size() - 1)).makeStatEquivalentCopy();
            newCards.add(transformCard(card, replacement.makeSameInstanceOf()));
            cache.put(card.uuid, replacement);
        }
        return newCards;
    }

    private static AbstractCard transformCard(AbstractCard fromCard, AbstractCard toCard) {
        toCard.current_x = fromCard.current_x;
        toCard.current_y = fromCard.current_y;
        toCard.target_x = fromCard.target_x;
        toCard.target_y = fromCard.target_y;
        toCard.angle = fromCard.angle;
        toCard.drawScale = fromCard.drawScale;
        toCard.targetDrawScale = fromCard.targetDrawScale;
        for (int i = 0; i < fromCard.timesUpgraded; i++) {
            toCard.upgrade();
        }
        return toCard;
    }

    private static int rateSimilarity(AbstractCard card1, AbstractCard card2) {
        int score = 0;

        if (card1.color != card2.color) {
            if (card1.color == AbstractCard.CardColor.COLORLESS || card2.color == AbstractCard.CardColor.COLORLESS) {
                score -= 10000;
            }
        }

        if (card1.type != card2.type) {
            if (card1.type == AbstractCard.CardType.STATUS || card2.type == AbstractCard.CardType.STATUS || card1.type == AbstractCard.CardType.CURSE || card2.type == AbstractCard.CardType.CURSE) {
                score -= 100000;
            }
        } else {
            score += 500;
        }

        if (card1.rarity != AbstractCard.CardRarity.CURSE && card2.rarity != AbstractCard.CardRarity.CURSE) {
            score += (6 - Math.abs(card1.rarity.ordinal() - card2.rarity.ordinal())) * 25;
        } else if (card1.rarity != card2.rarity) {
            score -= 100000;
        }

        if (card1.cost == card2.cost) {
            score += 50;
        } else if (card1.cost > 3 && card2.cost > 3) {
            score += 25;
        } else if (card1.cost >= 0 && card2.cost >= 0) {
            score += 10 - Math.abs(Math.min(card1.cost, 4) - Math.min(card2.cost, 4));
        }

        for (AbstractCard.CardTags tag : card1.tags) {
            if (card2.tags.contains(tag)) score += 2;
        }
        return score;
    }

    public static List<AbstractCard> getCardPool(AbstractCard.CardColor cardColor, boolean includeLocked) {
        return CardLibrary.cards.values().stream()
                .filter(c -> c.color == cardColor && (!UnlockTracker.isCardLocked(c.cardID) || Settings.treatEverythingAsUnlocked() || includeLocked))
                .collect(Collectors.toList());
    }

    public static class Decks {
        public ArrayList<AbstractCard> masterDeck;
        public ArrayList<AbstractCard> drawPile;
        public ArrayList<AbstractCard> hand;
        public ArrayList<AbstractCard> discardPile;
        public ArrayList<AbstractCard> exhaustPile;
        public ArrayList<AbstractCard> limbo;

        private Decks() {
        }

        public static Decks extractFromPlayer(AbstractPlayer player) {
            Decks decks = new Decks();
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
            }
            for (AbstractCard card : player.drawPile.group) {
                UnlockTracker.markCardAsSeen(card.cardID);
            }
            for (AbstractCard card : player.hand.group) {
                UnlockTracker.markCardAsSeen(card.cardID);
            }
            for (AbstractCard card : player.discardPile.group) {
                UnlockTracker.markCardAsSeen(card.cardID);
            }
            for (AbstractCard card : player.exhaustPile.group) {
                UnlockTracker.markCardAsSeen(card.cardID);
            }
            for (AbstractCard card : player.limbo.group) {
                UnlockTracker.markCardAsSeen(card.cardID);
            }
        }
    }
}
