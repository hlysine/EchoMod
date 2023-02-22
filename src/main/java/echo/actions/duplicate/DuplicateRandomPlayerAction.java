package echo.actions.duplicate;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.random.Random;

import java.util.Arrays;

public class DuplicateRandomPlayerAction extends AbstractGameAction {
    private final AbstractPlayer.PlayerClass classToExclude;
    private final Random rng;

    /**
     * Duplicate a random player class.
     *
     * @param classToExclude The player class that cannot be duplicated.
     * @param rng            The random number generator to use.
     */
    public DuplicateRandomPlayerAction(AbstractPlayer.PlayerClass classToExclude, Random rng) {
        this.classToExclude = classToExclude;
        this.rng = rng;
    }

    /**
     * Duplicate a random player class using the card random RNG.
     *
     * @param classToExclude The player class that cannot be duplicated.
     */
    public DuplicateRandomPlayerAction(AbstractPlayer.PlayerClass classToExclude) {
        this(classToExclude, AbstractDungeon.cardRandomRng);
    }

    @Override
    public void update() {
        AbstractPlayer.PlayerClass[] classes = Arrays.stream(AbstractPlayer.PlayerClass.values())
                .filter(c -> c != classToExclude)
                .toArray(AbstractPlayer.PlayerClass[]::new);
        Random finalRng = rng == null ? new Random() : rng;
        addToTop(new DuplicatePlayerAction(classes[finalRng.random(classes.length - 1)]));
        isDone = true;
    }
}
