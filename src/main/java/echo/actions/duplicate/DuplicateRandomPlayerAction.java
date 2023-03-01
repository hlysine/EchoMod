package echo.actions.duplicate;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.random.Random;

import java.util.Arrays;

public class DuplicateRandomPlayerAction extends AbstractGameAction {
    private final AbstractPlayer.PlayerClass classToExclude;
    private final Random rng;
    private final boolean requiresUltimateCharge;

    /**
     * Duplicate a random player class.
     *
     * @param classToExclude         The player class that cannot be duplicated.
     * @param rng                    The random number generator to use.
     * @param requiresUltimateCharge Whether Ultimate Charge is required and consumed for this duplication.
     */
    public DuplicateRandomPlayerAction(AbstractPlayer.PlayerClass classToExclude, Random rng, boolean requiresUltimateCharge) {
        this.classToExclude = classToExclude;
        this.rng = rng;
        this.requiresUltimateCharge = requiresUltimateCharge;
    }

    /**
     * Duplicate a random player class using the card random RNG.
     *
     * @param classToExclude         The player class that cannot be duplicated.
     * @param requiresUltimateCharge Whether Ultimate Charge is required and consumed for this duplication.
     */
    public DuplicateRandomPlayerAction(AbstractPlayer.PlayerClass classToExclude, boolean requiresUltimateCharge) {
        this(classToExclude, AbstractDungeon.cardRandomRng, requiresUltimateCharge);
    }

    @Override
    public void update() {
        AbstractPlayer.PlayerClass[] classes = Arrays.stream(AbstractPlayer.PlayerClass.values())
                .filter(c -> c != classToExclude)
                .toArray(AbstractPlayer.PlayerClass[]::new);
        Random finalRng = rng == null ? new Random() : rng;
        addToTop(new DuplicatePlayerAction(classes[finalRng.random(classes.length - 1)], requiresUltimateCharge));
        isDone = true;
    }
}
