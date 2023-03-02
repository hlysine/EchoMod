package echo.patches.duplicate;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import echo.mechanics.duplicate.CloneVfx;
import javassist.CannotCompileException;
import javassist.CtBehavior;

import java.util.ArrayList;

public class CloningVfxPatch {
    @SpirePatch(
            clz = AbstractPlayer.class,
            method = "render"
    )
    public static class PlayerPreRenderPatch {
        @SpireInsertPatch(
                locator = Locator.class
        )
        public static void Insert(AbstractPlayer __instance, SpriteBatch sb) {
            CloneVfx.playerPreRender(__instance, sb);
        }

        public static class Locator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
                Matcher finalMatcher = new Matcher.FieldAccessMatcher(AbstractPlayer.class, "renderCorpse");
                return new int[]{LineFinder.findInOrder(ctMethodToPatch, new ArrayList<>(), finalMatcher)[0] - 2};
            }
        }
    }

    @SpirePatch(
            clz = AbstractPlayer.class,
            method = "render"
    )
    public static class PlayerPostRenderPatch {
        public static void Postfix(AbstractPlayer __instance, SpriteBatch sb) {
            CloneVfx.playerPostRender(__instance, sb);
        }
    }

    @SpirePatch(
            clz = AbstractRelic.class,
            method = "renderInTopPanel"
    )
    public static class RelicPreRenderPatch {
        @SpireInsertPatch(
                locator = Locator.class
        )
        public static void Insert(AbstractRelic __instance, SpriteBatch sb) {
            CloneVfx.relicPreRender(__instance, sb);
        }

        public static class Locator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(SpriteBatch.class, "draw");
                return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<>(), finalMatcher);
            }
        }
    }

    @SpirePatch(
            clz = AbstractRelic.class,
            method = "renderInTopPanel"
    )
    public static class RelicPostRenderPatch {
        @SpireInsertPatch(
                locator = Locator.class
        )
        public static void Insert(AbstractRelic __instance, SpriteBatch sb) {
            CloneVfx.relicPostRender(__instance, sb);
        }

        public static class Locator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(SpriteBatch.class, "draw");
                return new int[]{LineFinder.findInOrder(ctMethodToPatch, new ArrayList<>(), finalMatcher)[0] + 1};
            }
        }
    }
}
