package echo.actions;


import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import echo.actions.duplicate.DuplicateRandomPlayerAction;
import echo.patches.duplicate.CardRewardScreenPatch;

import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class DiscoveryChooseCharacterAction extends AbstractGameAction {
    private final ArrayList<DuplicateRandomPlayerAction.CharacterChoice> group;
    private final String titleText;
    private final String description;
    private final Consumer<AbstractCard> callback;
    private boolean retrieveCard = false;

    public DiscoveryChooseCharacterAction(ArrayList<DuplicateRandomPlayerAction.CharacterChoice> group, String titleText, String description, Consumer<AbstractCard> callback) {
        this.group = group;
        this.titleText = titleText;
        this.description = description;
        this.callback = callback;
        this.actionType = AbstractGameAction.ActionType.CARD_MANIPULATION;
        this.duration = Settings.ACTION_DUR_FAST;
        this.amount = 1;
    }

    public void update() {
        if (this.duration == Settings.ACTION_DUR_FAST) {
            AbstractDungeon.cardRewardScreen.customCombatOpen(new ArrayList<>(this.group.stream().map(c -> c.cardToPreview).collect(Collectors.toList())), titleText, false);
            CardRewardScreenPatch.Fields.characterButtons.set(AbstractDungeon.cardRewardScreen, new ArrayList<>(this.group.stream().map(c -> c.button).collect(Collectors.toList())));
            CardRewardScreenPatch.Fields.tipMsg.set(AbstractDungeon.cardRewardScreen, this.description);
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