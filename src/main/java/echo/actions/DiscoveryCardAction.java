package echo.actions;


import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import echo.patches.duplicate.CardRewardScreenPatch;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.function.Consumer;

public class DiscoveryCardAction extends AbstractGameAction {

    private static final Logger logger = LogManager.getLogger(DiscoveryCardAction.class);
    private final ArrayList<AbstractCard> group;
    private final String titleText;
    private final String description;
    private final Consumer<AbstractCard> callback;
    private boolean retrieveCard = false;

    public DiscoveryCardAction(ArrayList<AbstractCard> group, String titleText, String description, Consumer<AbstractCard> callback) {
        this.group = group;
        this.titleText = titleText;
        this.description = description;
        this.callback = callback;
        this.actionType = ActionType.CARD_MANIPULATION;
        this.duration = Settings.ACTION_DUR_FAST;
        this.amount = 1;
    }

    public void update() {
        if (this.duration == Settings.ACTION_DUR_FAST) {
            AbstractDungeon.cardRewardScreen.customCombatOpen(this.group, titleText, false);
            if (this.description != null)
                CardRewardScreenPatch.Fields.tipMsg.set(AbstractDungeon.cardRewardScreen, this.description);
            tickDuration();
            return;
        }
        if (!this.retrieveCard) {
            AbstractCard chosenCard = AbstractDungeon.cardRewardScreen.discoveryCard;
            if (chosenCard != null) {
                this.callback.accept(AbstractDungeon.cardRewardScreen.discoveryCard);
                AbstractDungeon.cardRewardScreen.discoveryCard = null;
            }
            this.retrieveCard = true;
        }
        tickDuration();
    }
}