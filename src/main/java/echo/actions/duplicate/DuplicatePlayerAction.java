package echo.actions.duplicate;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import echo.effects.DuplicateEffect;
import echo.effects.SfxStore;
import echo.mechanics.duplicate.CardTransformer;
import echo.mechanics.duplicate.CloningModule;
import echo.util.RunnableAction;

public class DuplicatePlayerAction extends AbstractGameAction {

    private final AbstractPlayer.PlayerClass playerClass;
    private final CardTransformer.Decks duplicateDeck;

    /**
     * Duplicate a player class, causing the player to become that class, affecting energy, cards, relics and more.
     *
     * @param playerClass   The player class to duplicate.
     * @param duplicateDeck The deck to use when duplicating, can be null.
     */
    public DuplicatePlayerAction(AbstractPlayer.PlayerClass playerClass, CardTransformer.Decks duplicateDeck) {
        this.playerClass = playerClass;
        this.duplicateDeck = duplicateDeck;
    }

    @Override
    public void update() {
        if (!AbstractDungeon.getCurrRoom().isBattleEnding()) {
            CloningModule.preCloneSetup();
            addToBot(new RunnableAction(
                    () -> SfxStore.DUPLICATE_START.play(0.05f)
            ));
            addToBot(new VFXAction(AbstractDungeon.player, new DuplicateEffect(() -> {
                CloningModule.startCloning(DuplicatePlayerAction.this.playerClass, this.duplicateDeck);
            }), DuplicateEffect.DURATION, true));
        }
        this.isDone = true;
    }
}
