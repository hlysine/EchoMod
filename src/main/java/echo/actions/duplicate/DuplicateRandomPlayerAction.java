package echo.actions.duplicate;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.TutorialStrings;
import com.megacrit.cardcrawl.random.Random;
import echo.EchoMod;
import echo.actions.DiscoveryChooseCardAction;
import echo.mechanics.duplicate.CardTransformer;

import java.util.*;
import java.util.stream.Collectors;

public class DuplicateRandomPlayerAction extends AbstractGameAction {
    private static final TutorialStrings tutorialStrings = CardCrawlGame.languagePack.getTutorialString(EchoMod.makeID(DuplicateRandomPlayerAction.class.getSimpleName()));
    private static final int CHOICES = 3;
    private final AbstractPlayer.PlayerClass[] excludedClasses;
    private final Random rng;
    private final boolean requiresUltimateCharge;

    /**
     * Duplicate a random player class.
     *
     * @param excludedClasses        The player classes that cannot be duplicated.
     * @param rng                    The random number generator to use.
     * @param requiresUltimateCharge Whether Ultimate Charge is required and consumed for this duplication.
     */
    public DuplicateRandomPlayerAction(AbstractPlayer.PlayerClass[] excludedClasses, Random rng, boolean requiresUltimateCharge) {
        this.excludedClasses = excludedClasses;
        this.rng = rng;
        this.requiresUltimateCharge = requiresUltimateCharge;
    }

    /**
     * Duplicate a random player class using the card random RNG.
     *
     * @param excludedClasses        The player classes that cannot be duplicated.
     * @param requiresUltimateCharge Whether Ultimate Charge is required and consumed for this duplication.
     */
    public DuplicateRandomPlayerAction(AbstractPlayer.PlayerClass[] excludedClasses, boolean requiresUltimateCharge) {
        this(excludedClasses, AbstractDungeon.cardRandomRng, requiresUltimateCharge);
    }

    @Override
    public void update() {
        List<AbstractPlayer.PlayerClass> classes = Arrays.stream(AbstractPlayer.PlayerClass.values())
                .filter(c -> Arrays.stream(excludedClasses).noneMatch(e -> e == c))
                .collect(Collectors.toList());
        Random finalRng = rng == null ? new Random() : rng;

        List<AbstractPlayer.PlayerClass> choices = new ArrayList<>(CHOICES);
        for (int i = 0; i < CHOICES && !classes.isEmpty(); i++) {
            choices.add(classes.remove(finalRng.random(classes.size() - 1)));
        }

        Map<AbstractCard, CharacterChoice> choiceMap = new HashMap<>();
        for (AbstractPlayer.PlayerClass choice : choices) {
            CharacterChoice characterChoice = CharacterChoice.constructChoice(choice, finalRng);
            choiceMap.put(characterChoice.cardToPreview, characterChoice);
        }

        addToTop(new DiscoveryChooseCardAction(new ArrayList<>(choiceMap.keySet()), tutorialStrings.TEXT[0], card -> {
            card.unhover();
            CharacterChoice finalChoice = choiceMap.get(card);
            addToTop(new DuplicatePlayerAction(finalChoice.chosenClass, finalChoice.decks, requiresUltimateCharge));
        }));

        isDone = true;
    }

    public static class CharacterChoice {
        private final AbstractPlayer.PlayerClass chosenClass;
        private final CardTransformer.Decks decks;
        private final AbstractCard cardToPreview;

        private CharacterChoice(AbstractPlayer.PlayerClass chosenClass, CardTransformer.Decks decks, AbstractCard cardToPreview) {
            this.chosenClass = chosenClass;
            this.decks = decks;
            this.cardToPreview = cardToPreview;
        }

        public static CharacterChoice constructChoice(AbstractPlayer.PlayerClass chosenClass, Random rng) {
            CardTransformer cardTransformer = new CardTransformer(rng, AbstractDungeon.player.chosenClass, chosenClass);
            CardTransformer.Decks decks = cardTransformer.transform(CardTransformer.Decks.extractFromPlayer(AbstractDungeon.player));
            AbstractCard cardToPreview = decks.hand.stream().max(Comparator.comparingInt(c -> getRarityScore(c.rarity))).orElse(null);
            return new CharacterChoice(chosenClass, decks, cardToPreview);
        }

        private static int getRarityScore(AbstractCard.CardRarity rarity) {
            switch (rarity) {
                case BASIC:
                    return 0;
                case COMMON:
                    return 1;
                case UNCOMMON:
                case SPECIAL:
                    return 2;
                case RARE:
                    return 3;
                default:
                    return -1;
            }
        }
    }
}
