package echo.patches.togetherInSpire;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import spireTogether.monsters.CharacterEntity;

@SpirePatch(
        cls = "spireTogether.util.SpireHelper",
        method = "abstractPlayerToCustom",
        optional = true
)
public class EchoSkeletonNullRefPatch {
    public static SpireReturn<CharacterEntity> Prefix(AbstractPlayer p) {
        // todo: create custom instance of SpireTogether's network character class
//        if (p instanceof Echo) {
//            EchoMod.logger.info("Initializing TogetherInSpire Echo character");
//            AbstractCustomChar character = new AbstractCustomChar(p);
//            character.ghostAtlasLoc = Echo.SKELETON_ATLAS;
//            character.skeletonUrl = Echo.SKELETON_JSON;
//            return SpireReturn.Return(character);
//        }
        return SpireReturn.Continue();
    }
}
