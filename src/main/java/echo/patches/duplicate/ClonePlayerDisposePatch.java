package echo.patches.duplicate;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import echo.mechanics.duplicate.CloningModule;

@SpirePatch(
        clz = AbstractPlayer.class,
        method = "dispose"
)
public class ClonePlayerDisposePatch {
    public static SpireReturn<Void> Prefix(AbstractPlayer __instance) {
        if (CloningModule.isCloning() && __instance == CloningModule.playerData.originalPlayer) {
            return SpireReturn.Return();
        }
        return SpireReturn.Continue();
    }
}
