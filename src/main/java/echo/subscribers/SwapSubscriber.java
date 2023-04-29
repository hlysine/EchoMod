package echo.subscribers;

import com.megacrit.cardcrawl.cards.AbstractCard;

import java.util.List;

public interface SwapSubscriber {
    void onSwap(List<AbstractCard> toHand, List<AbstractCard> toDrawPile);
}
