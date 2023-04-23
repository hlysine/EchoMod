package echo.subscribers;

public interface StickyBombSubscriber {
    default int modifyBombsPerTurn(int original) {
        return original;
    }
}
