package echo.actions.duplicate;

import basemod.ReflectionHacks;
import com.badlogic.gdx.math.Vector2;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.Soul;
import com.megacrit.cardcrawl.cards.SoulGroup;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.TutorialStrings;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import echo.EchoMod;
import echo.actions.SelectCardsAction;
import echo.mechanics.duplicate.Duplicator;

import java.util.ArrayList;

public class SelectCardsForDuplicateAction extends AbstractGameAction {
    private static final TutorialStrings tutorialStrings = CardCrawlGame.languagePack.getTutorialString(EchoMod.makeID(SelectCardsForDuplicateAction.class.getSimpleName()));
    private static final String[] TEXT = tutorialStrings.TEXT;

    private final boolean editMasterDeck;

    public SelectCardsForDuplicateAction(boolean editMasterDeck) {
        this.editMasterDeck = editMasterDeck;
    }

    @Override
    public void update() {
        if (!Duplicator.isDuplicating()) {
            this.isDone = true;
            return;
        }

        CardGroup choices = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
        int cardCount = AbstractDungeon.player.getLoadout().cardDraw;

        for (int i = 0; i < cardCount * 3; i++) {
            AbstractCard card = AbstractDungeon.getCard(AbstractDungeon.rollRarity()).makeCopy();

            boolean containsDupe = true;
            int repeatCount = 0;
            while (containsDupe && repeatCount < 100) {
                containsDupe = false;
                repeatCount++;

                for (AbstractCard c : choices.group) {
                    if (c.cardID.equals(card.cardID)) {
                        containsDupe = true;
                        card = AbstractDungeon.getCard(AbstractDungeon.rollRarity()).makeCopy();
                    }
                }
            }

            for (AbstractRelic r : AbstractDungeon.player.relics) {
                r.onPreviewObtainCard(card);
            }
            choices.addToBottom(card);
        }

        for (AbstractCard c : choices.group) {
            UnlockTracker.markCardAsSeen(c.cardID);
            if (editMasterDeck)
                AbstractDungeon.player.masterDeck.addToTop(c.makeSameInstanceOf());
        }

        addToTop(new SelectCardsAction(choices.group, cardCount, TEXT[0] + cardCount + TEXT[1], false, c -> true, list -> {
            for (AbstractCard card : list) {
                addToTop(new MakeTempCardInHandAction(card));
            }
            choices.group.removeAll(list);
            int count = 0;
            for (AbstractCard card : choices.group) {
                count++;
                if (card.type != AbstractCard.CardType.CURSE && card.type != AbstractCard.CardType.STATUS && AbstractDungeon.player.hasPower("MasterRealityPower")) {
                    card.upgrade();
                }
                shuffleFromCurrentPosition(card, count > 11);
            }
            AbstractDungeon.player.drawPile.shuffle();
        }));

        this.isDone = true;
    }

    private void shuffleFromCurrentPosition(AbstractCard card, boolean isInvisible) {
        AbstractDungeon.getCurrRoom().souls.shuffle(card, isInvisible);
        ArrayList<Soul> souls = ReflectionHacks.getPrivate(AbstractDungeon.getCurrRoom().souls, SoulGroup.class, "souls");
        for (Soul soul : souls) {
            if (soul.card == card) {
                ReflectionHacks.setPrivate(soul, Soul.class, "pos", new Vector2(card.current_x, card.current_y));
            }
        }
    }
}
