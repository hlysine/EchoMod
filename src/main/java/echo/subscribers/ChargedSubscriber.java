package echo.subscribers;

public interface ChargedSubscriber {
    /**
     * Allows Charged effects to be triggered in additional scenarios.
     *
     * @return whether the charged effect is allowed
     */
    boolean overrideChargedCheck();
}
