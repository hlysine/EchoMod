package echo.actions.duplicate;

import basemod.BaseMod;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.TutorialStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.random.Random;
import com.megacrit.cardcrawl.screens.custom.CustomModeCharacterButton;
import echo.EchoMod;
import echo.actions.DiscoveryChooseCharacterAction;
import echo.mechanics.duplicate.CardTransformer;
import echo.mechanics.duplicate.EnemyMapping;

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


        List<AbstractPlayer.PlayerClass> enemies = new ArrayList<>();
        for (AbstractMonster monster : AbstractDungeon.getMonsters().monsters) {
            AbstractPlayer.PlayerClass enemyClass = EnemyMapping.getEnemyClass(monster);
            if (enemyClass != null) {
                enemies.add(enemyClass);
            }
        }
        enemies.removeIf(c -> Arrays.stream(excludedClasses).anyMatch(e -> e == c));
        if (!enemies.isEmpty()) {
            choices.add(enemies.get(finalRng.random(enemies.size() - 1)));
        }

        while (!classes.isEmpty() && choices.size() < CHOICES) {
            AbstractPlayer.PlayerClass c = classes.remove(finalRng.random(classes.size() - 1));
            if (!choices.contains(c)) {
                choices.add(c);
            }
        }

        Map<AbstractCard, CharacterChoice> choiceMap = new HashMap<>();
        for (AbstractPlayer.PlayerClass choice : choices) {
            CharacterChoice characterChoice = CharacterChoice.constructChoice(choice, finalRng);
            choiceMap.put(characterChoice.cardToPreview, characterChoice);
        }

        addToTop(new DiscoveryChooseCharacterAction(new ArrayList<>(choiceMap.values()), tutorialStrings.TEXT[0], tutorialStrings.TEXT[1], card -> {
            card.unhover();
            CharacterChoice finalChoice = choiceMap.get(card);
            addToTop(new DuplicatePlayerAction(finalChoice.chosenClass, finalChoice.decks, requiresUltimateCharge));
        }));

        isDone = true;
    }

    public static class CharacterChoice {
        public final AbstractPlayer.PlayerClass chosenClass;
        public final CardTransformer.Decks decks;
        public final AbstractCard cardToPreview;
        public CustomModeCharacterButton button;

        private CharacterChoice(AbstractPlayer.PlayerClass chosenClass, CardTransformer.Decks decks, AbstractCard cardToPreview) {
            this.chosenClass = chosenClass;
            this.decks = decks;
            this.cardToPreview = cardToPreview;
        }

        public static CharacterChoice constructChoice(AbstractPlayer.PlayerClass chosenClass, Random rng) {
            CardTransformer cardTransformer = new CardTransformer(rng, AbstractDungeon.player.chosenClass, chosenClass);
            CardTransformer.Decks decks = cardTransformer.transform(CardTransformer.Decks.extractFromPlayer(AbstractDungeon.player));
            AbstractCard cardToPreview = decks.hand.stream().max(Comparator.comparingInt(CharacterChoice::getImportance)).orElse(null);

            CharacterChoice choice = new CharacterChoice(chosenClass, decks, cardToPreview);
            choice.button = new CustomModeCharacterButton(BaseMod.findCharacter(chosenClass), false);
            return choice;
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
