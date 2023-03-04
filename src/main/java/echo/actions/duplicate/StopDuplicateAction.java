package echo.actions.duplicate;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import echo.effects.DuplicateEffect;
import echo.mechanics.duplicate.CloningModule;

public class StopDuplicateAction extends AbstractGameAction {
    @Override
    public void update() {
        if (CloningModule.isCloning()) {
            CloningModule.preCloneSetup();
            AbstractDungeon.actionManager.addToBottom(new VFXAction(
                    AbstractDungeon.player,
                    new DuplicateEffect(CloningModule::stopCloning),
                    DuplicateEffect.DURATION,
                    true
            ));
        }
        this.isDone = true;
    }
}
