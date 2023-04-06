package echo.subscribers;

import com.megacrit.cardcrawl.monsters.AbstractMonster;

public interface FocusedSubscriber {
    /**
     * Allows Focused effects to be triggered in additional scenarios.
     *
     * @param target the target to be focused
     * @return whether the focused effect is allowed
     */
    boolean overrideFocusedCheck(AbstractMonster target);
}
