package echo.actions.duplicate;

import basemod.BaseMod;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.TutorialStrings;
import com.megacrit.cardcrawl.random.Random;
import com.megacrit.cardcrawl.screens.custom.CustomModeCharacterButton;
import echo.EchoMod;
import echo.actions.DiscoveryChooseCharacterAction;
import echo.mechanics.duplicate.CardTransformer;
import echo.mechanics.duplicate.DuplicatedDecks;
import echo.mechanics.duplicate.PlayerClassManager;

import java.util.*;
import java.util.stream.Collectors;

public class DuplicateRandomPlayerAction extends AbstractGameAction {
    private static final TutorialStrings tutorialStrings = CardCrawlGame.languagePack.getTutorialString(EchoMod.makeID(DuplicateRandomPlayerAction.class.getSimpleName()));
    private static final int CHOICES = 3;
    private final AbstractPlayer.PlayerClass[] excludedClasses;
    private final Random rng;
    private final AbstractGameAction followUpAction;

    /**
     * Duplicate a random player class.
     *
     * @param excludedClasses The player classes that cannot be duplicated.
     * @param rng             The random number generator to use.
     * @param followUpAction  The action to follow up with after duplicate starts, can be null.
     */
    public DuplicateRandomPlayerAction(AbstractPlayer.PlayerClass[] excludedClasses, Random rng, AbstractGameAction followUpAction) {
        this.excludedClasses = excludedClasses;
        this.rng = rng;
        this.followUpAction = followUpAction;
    }

    /**
     * Duplicate a random player class using the card random RNG.
     *
     * @param excludedClasses The player classes that cannot be duplicated.
     * @param followUpAction  The action to follow up with after duplicate starts, can be null.
     */
    public DuplicateRandomPlayerAction(AbstractPlayer.PlayerClass[] excludedClasses, AbstractGameAction followUpAction) {
        this(excludedClasses, AbstractDungeon.cardRandomRng, followUpAction);
    }

    /**
     * Duplicate a random player class using the card random RNG.
     *
     * @param excludedClasses The player classes that cannot be duplicated.
     */
    public DuplicateRandomPlayerAction(AbstractPlayer.PlayerClass[] excludedClasses) {
        this(excludedClasses, AbstractDungeon.cardRandomRng, null);
    }

    @Override
    public void update() {
        if (AbstractDungeon.getCurrRoom().isBattleEnding()) {
            this.isDone = true;
            return;
        }
        Random finalRng = rng == null ? AbstractDungeon.cardRandomRng : rng;

        List<AbstractPlayer.PlayerClass> choices = PlayerClassManager.getClassList(finalRng, excludedClasses, CHOICES, true);

        Map<AbstractCard, CharacterChoice> choiceMap = new HashMap<>();
        for (AbstractPlayer.PlayerClass choice : choices) {
            CharacterChoice characterChoice = CharacterChoice.constructChoice(choice, finalRng);
            choiceMap.put(characterChoice.cardToPreview, characterChoice);
        }

        addToTop(new DiscoveryChooseCharacterAction(new ArrayList<>(choiceMap.values()), tutorialStrings.TEXT[0], tutorialStrings.TEXT[1], card -> {
            card.unhover();
            CharacterChoice finalChoice = choiceMap.get(card);
            addToTop(new DuplicatePlayerAction(finalChoice.chosenClass, finalChoice.decks, this.followUpAction));
        }));

        isDone = true;
    }

    public static class CharacterChoice {
        public final AbstractPlayer.PlayerClass chosenClass;
        public final DuplicatedDecks decks;
        public final AbstractCard cardToPreview;
        public CustomModeCharacterButton button;

        private CharacterChoice(AbstractPlayer.PlayerClass chosenClass, DuplicatedDecks decks, AbstractCard cardToPreview) {
            this.chosenClass = chosenClass;
            this.decks = decks;
            this.cardToPreview = cardToPreview;
        }

        public static CharacterChoice constructChoice(AbstractPlayer.PlayerClass chosenClass, Random rng) {
            CardTransformer cardTransformer = new CardTransformer(rng, AbstractDungeon.player.chosenClass, chosenClass);
            DuplicatedDecks decks = cardTransformer.transform(DuplicatedDecks.extractFromPlayer(AbstractDungeon.player));
            AbstractCard.CardColor targetCardColor = BaseMod.findCharacter(chosenClass).getCardColor();
            List<AbstractCard> handCards = getHandCards(decks);

            AbstractCard cardToPreview = handCards.stream()
                    .filter(card -> card.color == targetCardColor)
                    .max(Comparator.comparingInt(CharacterChoice::getImportance))
                    .orElse(null);
            if (cardToPreview != null) {
                cardToPreview.beginGlowing();
            } else {
                cardToPreview = decks.masterDeck.stream()
                        .filter(card -> card.color == targetCardColor)
                        .max(Comparator.comparingInt(CharacterChoice::getImportance))
                        .orElse(null);
                if (cardToPreview != null) {
                    cardToPreview.stopGlowing();
                } else {
                    cardToPreview = handCards.stream()
                            .max(Comparator.comparingInt(CharacterChoice::getImportance))
                            .map(AbstractCard::makeSameInstanceOf)
                            .orElse(null);
                    if (cardToPreview != null) {
                        cardToPreview.stopGlowing();
                    } else {
                        cardToPreview = decks.masterDeck.stream()
                                .max(Comparator.comparingInt(CharacterChoice::getImportance))
                                .map(AbstractCard::makeSameInstanceOf)
                                .orElse(null);
                    }
                }
            }

            CharacterChoice choice = new CharacterChoice(chosenClass, decks, cardToPreview);
            choice.button = new CustomModeCharacterButton(BaseMod.findCharacter(chosenClass), false);
            return choice;
        }

        private static List<AbstractCard> getHandCards(DuplicatedDecks decks) {
            List<AbstractCard> hand = new ArrayList<>(decks.hand);
            if (hand.size() >= 5) return hand;

            int drawPileCount = Math.min(decks.drawPile.size(), 5 - hand.size());
            hand.addAll(decks.drawPile.stream().skip(decks.drawPile.size() - drawPileCount).collect(Collectors.toList()));
            return hand;
        }

        private static int getImportance(AbstractCard card) {
            int score = 0;
            switch (card.rarity) {
                case BASIC:
                    score += 0;
                    break;
                case COMMON:
                    score += 1;
                    break;
                case UNCOMMON:
                case SPECIAL:
                    score += 2;
                    break;
                case RARE:
                    score += 3;
                    break;
                default:
                    score -= 1;
                    break;
            }
            if (card.type == AbstractCard.CardType.STATUS || card.type == AbstractCard.CardType.CURSE)
                score -= 10;
            return score;
        }
    }
}
