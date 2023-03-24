package echo.patches;

import com.evacipated.cardcrawl.modthespire.lib.ByRef;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.powers.AbstractPower;
import echo.subscribers.BeforeApplyPowerSubscriber;

@SpirePatch(
        clz = ApplyPowerAction.class,
        method = "update"
)
public class BeforeApplyPowerPatch {
    public static SpireReturn Prefix(ApplyPowerAction __instance, @ByRef AbstractPower[] ___powerToApply) {
        AbstractPower powerToApply = ___powerToApply[0];
        for (AbstractPower power : __instance.target.powers) {
            if (power instanceof BeforeApplyPowerSubscriber) {
                BeforeApplyPowerSubscriber subscriber = (BeforeApplyPowerSubscriber) power;
                powerToApply = subscriber.beforeApplyPower(powerToApply, __instance.source, __instance.target);
                if (powerToApply == null) {
                    __instance.isDone = true;
                    return SpireReturn.Return();
                }
            }
        }
        ___powerToApply[0] = powerToApply;
        return SpireReturn.Continue();
    }
}
