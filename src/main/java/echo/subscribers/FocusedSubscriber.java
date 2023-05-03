package echo.subscribers;

import com.megacrit.cardcrawl.monsters.AbstractMonster;

import java.util.Optional;

public interface FocusedSubscriber {
    /**
     * Allows Focused effects to be triggered in additional scenarios.
     *
     * @param target the target to be focused
     * @return whether the focused effect is allowed
     */
    default Optional<Boolean> overrideFocusedCheck(AbstractMonster target) {
        if (shouldBlockFocused(target)) return Optional.of(false);
        if (shouldAllowFocused(target)) return Optional.of(true);
        return Optional.empty();
    }

    default boolean shouldAllowFocused(AbstractMonster target) {
        return false;
    }

    default boolean shouldBlockFocused(AbstractMonster target) {
        return false;
    }
}
