package echo.mechanics.duplicate;

import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.TutorialStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.vfx.ThoughtBubble;
import echo.EchoMod;
import echo.powers.UltimateChargePower;
import echo.subscribers.ChargedSubscriber;

import java.util.Optional;

public class ChargedChecker {
    private static final TutorialStrings tutorialStrings = CardCrawlGame.languagePack.getTutorialString(EchoMod.makeID(ChargedChecker.class.getSimpleName()));

    public static boolean baseCharged(int count) {
        AbstractPower power = AbstractDungeon.player.getPower(UltimateChargePower.POWER_ID);
        return power != null && power.amount >= count;
    }

    public static boolean baseCharged() {
        return baseCharged(10);
    }

    /**
     * Check whether the player is charged.
     *
     * @return whether the player is charged
     */
    public static boolean isCharged() {
        boolean allow = false;
        boolean block = false;
        for (AbstractCard card : AbstractDungeon.player.hand.group) {
            if (card instanceof ChargedSubscriber) {
                Optional<Boolean> override = ((ChargedSubscriber) card).overrideChargedCheck();
                if (override.isPresent()) {
                    if (override.get()) {
                        allow = true;
                    } else {
                        block = true;
                    }
                }
            }
        }
        for (AbstractPower power : AbstractDungeon.player.powers) {
            if (power instanceof ChargedSubscriber) {
                Optional<Boolean> override = ((ChargedSubscriber) power).overrideChargedCheck();
                if (override.isPresent()) {
                    if (override.get()) {
                        allow = true;
                    } else {
                        block = true;
                    }
                }
            }
        }
        for (AbstractRelic relic : AbstractDungeon.player.relics) {
            if (relic instanceof ChargedSubscriber) {
                Optional<Boolean> override = ((ChargedSubscriber) relic).overrideChargedCheck();
                if (override.isPresent()) {
                    if (override.get()) {
                        allow = true;
                    } else {
                        block = true;
                    }
                }
            }
        }
        if (block) return false;
        return allow || baseCharged();
    }

    /**
     * Check whether the player is charged, displaying a thought bubble if not.
     *
     * @return whether the player is charged
     */
    public static boolean requiresCharged() {
        if (!isCharged()) {
            AbstractPlayer player = AbstractDungeon.player;
            AbstractDungeon.effectList.add(new ThoughtBubble(player.dialogX, player.dialogY, 3.0F, tutorialStrings.TEXT[0], true));
            return false;
        }
        return true;
    }

    public static void consumeCharge() {
        AbstractPlayer player = AbstractDungeon.player;
        AbstractDungeon.actionManager.addToBottom(new ReducePowerAction(player, player, UltimateChargePower.POWER_ID, 10));
    }
}
