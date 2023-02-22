package echo.util;

import com.megacrit.cardcrawl.actions.AbstractGameAction;

public class RunnableAction extends AbstractGameAction {
    private final Runnable runnable;

    public RunnableAction(Runnable runnable) {
        this.runnable = runnable;
    }

    @Override
    public void update() {
        this.runnable.run();
        this.isDone = true;
    }
}
