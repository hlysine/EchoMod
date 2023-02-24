package echo.actions.duplicate;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import echo.effects.DuplicateEffect;
import echo.mechanics.duplicate.CloningModule;

public class DuplicatePlayerAction extends AbstractGameAction {
    private final AbstractPlayer.PlayerClass playerClass;

    /**
     * Duplicate a player class, causing the player to become that class, affecting energy, cards, relics and more.
     *
     * @param playerClass The player class to duplicate.
     */
    public DuplicatePlayerAction(AbstractPlayer.PlayerClass playerClass) {
        this.playerClass = playerClass;
    }

    @Override
    public void update() {
        if (!AbstractDungeon.getCurrRoom().isBattleEnding()) {
            CloningModule.preCloneSetup();
            addToBot(new VFXAction(AbstractDungeon.player, new DuplicateEffect(() -> {
                CloningModule.startCloning(DuplicatePlayerAction.this.playerClass);
            }), DuplicateEffect.DURATION, true));
        }
        this.isDone = true;
    }
}
