package echo.subscribers;

import java.util.Optional;

public interface ChargedSubscriber {
    /**
     * Allows Charged effects to be triggered in additional scenarios.
     *
     * @return whether the charged effect is allowed
     */
    default Optional<Boolean> overrideChargedCheck() {
        if (shouldBlockCharged()) return Optional.of(false);
        if (shouldAllowCharged()) return Optional.of(true);
        return Optional.empty();
    }

    default boolean shouldAllowCharged() {
        return false;
    }

    default boolean shouldBlockCharged() {
        return false;
    }
}
