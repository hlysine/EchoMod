package echo.subscribers;

import com.megacrit.cardcrawl.relics.AbstractRelic;

import java.util.List;

public interface RelicTransformSubscriber {
    /**
     * Return a list of relics to be transformed into when duplicating.
     *
     * @return a list of relics
     */
    List<AbstractRelic> transform();
}
