package echo.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

@SpirePatch(
        clz = GameActionManager.class,
        method = "addToBottom"
)
@SpirePatch(
        clz = GameActionManager.class,
        method = "addToTop"
)
@SpirePatch(
        clz = GameActionManager.class,
        method = "addToTurnStart"
)
public class ActionManagerNPEPatch {
    public static SpireReturn<Void> Prefix(GameActionManager __instance) {
        if (AbstractDungeon.getCurrRoom() == null) {
            return SpireReturn.Return();
        } else {
            return SpireReturn.Continue();
        }
    }
}
