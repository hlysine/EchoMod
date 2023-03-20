package echo.patches.cards;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.screens.SingleCardViewPopup;
import echo.mechanics.duplicate.CloneVfx;

public class CardShaderPatch {

    @SpirePatch(
            clz = AbstractCard.class,
            method = "renderCardBg"
    )
    public static class GridCardBackground {
        public static void Prefix(AbstractCard __instance, SpriteBatch sb) {
            CloneVfx.cardPreRender(__instance, sb, 512);
        }

        public static void Postfix(AbstractCard __instance, SpriteBatch sb) {
            CloneVfx.cardPostRender(__instance, sb);
        }
    }

    @SpirePatch(
            clz = SingleCardViewPopup.class,
            method = "renderCardBack"
    )
    public static class PortraitGridCardBackground {
        public static void Prefix(SingleCardViewPopup __instance, SpriteBatch sb, AbstractCard ___card) {
            CloneVfx.cardPreRender(___card, sb, 1024); // todo: buggy white region, background dim is gone
        }

        public static void Postfix(SingleCardViewPopup __instance, SpriteBatch sb, AbstractCard ___card) {
            CloneVfx.cardPostRender(___card, sb);
        }
    }
}
