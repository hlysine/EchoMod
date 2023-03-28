package echo.mechanics.duplicate;

import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.TutorialStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.vfx.ThoughtBubble;
import echo.EchoMod;
import echo.powers.UltimateChargePower;

public class ChargedChecker {
    private static final TutorialStrings tutorialStrings = CardCrawlGame.languagePack.getTutorialString(EchoMod.makeID(ChargedChecker.class.getSimpleName()));

    public static boolean isCharged() {
        AbstractPower power = AbstractDungeon.player.getPower(UltimateChargePower.POWER_ID);
        return power != null && power.amount >= 10;
    }

    public static boolean consumeCharge() {
        AbstractPlayer player = AbstractDungeon.player;
        if (!isCharged()) {
            AbstractDungeon.effectList.add(new ThoughtBubble(player.dialogX, player.dialogY, 3.0F, tutorialStrings.TEXT[0], true));
            return false;
        }
        AbstractDungeon.actionManager.addToBottom(new RemoveSpecificPowerAction(player, player, UltimateChargePower.POWER_ID));
        return true;
    }
}
