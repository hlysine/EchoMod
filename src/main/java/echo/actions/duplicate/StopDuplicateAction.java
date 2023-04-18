package echo.actions.duplicate;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import echo.effects.DuplicateEffect;
import echo.effects.SfxStore;
import echo.mechanics.duplicate.Duplicator;
import echo.util.RunnableAction;

public class StopDuplicateAction extends AbstractGameAction {
    @Override
    public void update() {
        if (Duplicator.isDuplicating()) {
            Duplicator.preDuplicateSetup();
            addToBot(new RunnableAction(
                    () -> SfxStore.DUPLICATE_END.play(0.05f)
            ));
            addToBot(new VFXAction(
                    AbstractDungeon.player,
                    new DuplicateEffect(Duplicator::stopDuplication),
                    DuplicateEffect.DURATION,
                    true
            ));
        }
        this.isDone = true;
    }
}
