package echo.patches.cards;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.screens.SingleCardViewPopup;
import echo.mechanics.duplicate.DuplicationVfx;

public class CardShaderPatch {

    @SpirePatch(
            clz = AbstractCard.class,
            method = "renderCardBg"
    )
    public static class GridCardBackground {
        public static void Prefix(AbstractCard __instance, SpriteBatch sb) {
            DuplicationVfx.cardPreRender(__instance, sb);
        }

        public static void Postfix(AbstractCard __instance, SpriteBatch sb) {
            DuplicationVfx.cardPostRender(__instance, sb);
        }
    }

    @SpirePatch(
            clz = SingleCardViewPopup.class,
            method = "renderCardBack"
    )
    public static class PortraitGridCardBackground {
        public static void Prefix(SingleCardViewPopup __instance, SpriteBatch sb, AbstractCard ___card) {
            DuplicationVfx.cardPreRender(___card, sb);
        }

        public static void Postfix(SingleCardViewPopup __instance, SpriteBatch sb, AbstractCard ___card) {
            DuplicationVfx.cardPostRender(___card, sb);
        }
    }
}
