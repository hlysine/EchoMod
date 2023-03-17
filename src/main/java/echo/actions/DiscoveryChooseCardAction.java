package echo.actions;


import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import java.util.ArrayList;
import java.util.function.Consumer;

public class DiscoveryChooseCardAction extends AbstractGameAction {
    private final ArrayList<AbstractCard> group;
    private final String titleText;
    private final Consumer<AbstractCard> callback;
    private boolean retrieveCard = false;

    public DiscoveryChooseCardAction(ArrayList<AbstractCard> group, String titleText, Consumer<AbstractCard> callback) {
        this.group = group;
        this.titleText = titleText;
        this.callback = callback;
        this.actionType = AbstractGameAction.ActionType.CARD_MANIPULATION;
        this.duration = Settings.ACTION_DUR_FAST;
        this.amount = 1;
    }

    public void update() {
        if (this.duration == Settings.ACTION_DUR_FAST) {
            AbstractDungeon.cardRewardScreen.customCombatOpen(this.group, titleText, false);
            tickDuration();
            return;
        }
        if (!this.retrieveCard) {
            if (AbstractDungeon.cardRewardScreen.discoveryCard != null) {
                this.callback.accept(AbstractDungeon.cardRewardScreen.discoveryCard);
                AbstractDungeon.cardRewardScreen.discoveryCard = null;
            }
            this.retrieveCard = true;
        }
        tickDuration();
    }
}