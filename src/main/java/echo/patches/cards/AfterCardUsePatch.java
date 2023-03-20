package echo.patches.cards;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import echo.subscribers.AfterCardUseSubscriber;

@SpirePatch(
        clz = UseCardAction.class,
        method = "update"
)
public class AfterCardUsePatch {
    public static void Postfix(UseCardAction __instance, AbstractCard ___targetCard) {
        if (__instance.isDone) {
            if (___targetCard instanceof AfterCardUseSubscriber) {
                ((AfterCardUseSubscriber) ___targetCard).afterUse();
            }
        }
    }
}
