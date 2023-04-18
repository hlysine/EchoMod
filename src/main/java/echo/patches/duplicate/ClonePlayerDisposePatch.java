package echo.patches.duplicate;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import echo.mechanics.duplicate.Duplicator;

@SpirePatch(
        clz = AbstractPlayer.class,
        method = "dispose"
)
public class ClonePlayerDisposePatch {
    public static SpireReturn<Void> Prefix(AbstractPlayer __instance) {
        if (Duplicator.isDuplicating() && __instance == Duplicator.playerData.originalPlayer) {
            return SpireReturn.Return();
        }
        return SpireReturn.Continue();
    }
}
