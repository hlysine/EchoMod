package echo.actions.duplicate;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import echo.effects.DuplicateEffect;
import echo.effects.SfxStore;
import echo.mechanics.duplicate.DuplicatedDecks;
import echo.mechanics.duplicate.Duplicator;
import echo.util.RunnableAction;

public class DuplicatePlayerAction extends AbstractGameAction {

    private final AbstractPlayer.PlayerClass playerClass;
    private final DuplicatedDecks duplicateDeck;
    private final AbstractGameAction followUpAction;

    /**
     * Duplicate a player class, causing the player to become that class, affecting energy, cards, relics and more.
     *
     * @param playerClass    The player class to duplicate.
     * @param duplicateDeck  The deck to use when duplicating, can be null.
     * @param followUpAction The action to follow up with after duplicate starts, can be null.
     */
    public DuplicatePlayerAction(AbstractPlayer.PlayerClass playerClass, DuplicatedDecks duplicateDeck, AbstractGameAction followUpAction) {
        this.playerClass = playerClass;
        this.duplicateDeck = duplicateDeck;
        this.followUpAction = followUpAction;
    }

    @Override
    public void update() {
        if (!AbstractDungeon.getCurrRoom().isBattleEnding()) {
            Duplicator.preDuplicateSetup();
            addToBot(new RunnableAction(
                    () -> SfxStore.DUPLICATE_START.play(0.05f)
            ));
            addToBot(new VFXAction(AbstractDungeon.player, new DuplicateEffect(() -> {
                Duplicator.startDuplication(DuplicatePlayerAction.this.playerClass, this.duplicateDeck, this.followUpAction);
            }), DuplicateEffect.DURATION, true));
        }
        this.isDone = true;
    }
}
