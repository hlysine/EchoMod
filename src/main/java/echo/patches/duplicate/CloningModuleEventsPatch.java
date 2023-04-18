package echo.patches.duplicate;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.actions.common.DiscardAtEndOfTurnAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import echo.actions.duplicate.StopDuplicateAction;
import echo.effects.DuplicateEffect;
import echo.mechanics.duplicate.Duplicator;
import javassist.CannotCompileException;
import javassist.CtBehavior;

import java.util.ArrayList;

public class CloningModuleEventsPatch {
    @SpirePatch(
            clz = AbstractRoom.class,
            method = "endTurn"
    )
    public static class EndOfTurnPatch {
        @SpireInsertPatch(
                locator = Locator.class
        )
        public static void Insert() {
            if (Duplicator.isDuplicating()) {
                new StopDuplicateAction().update();
            }
        }

        public static class Locator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
                Matcher finalMatcher = new Matcher.NewExprMatcher(DiscardAtEndOfTurnAction.class);
                return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<>(), finalMatcher);
            }
        }
    }

    @SpirePatch(
            clz = AbstractPlayer.class,
            method = "onVictory"
    )
    public static class OnVictoryPatch {
        public static void Postfix() {
            if (Duplicator.isDuplicating()) {
                Duplicator.stopDuplication();
                AbstractDungeon.topLevelEffects.add(new DuplicateEffect(null));
            }
        }
    }

    @SpirePatch(
            clz = GameActionManager.class,
            method = "clear"
    )
    public static class ClearPatch {
        public static void Postfix() {
            Duplicator.stopDuplication();
        }
    }

    @SpirePatch(
            clz = AbstractDungeon.class,
            method = "resetPlayer"
    )
    public static class ResetPlayerPatch {
        public static void Prefix() {
            Duplicator.stopDuplication();
        }
    }
}
