package echo.patches;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.events.beyond.SensoryStone;
import com.megacrit.cardcrawl.localization.EventStrings;
import echo.EchoMod;
import javassist.CannotCompileException;
import javassist.CtBehavior;

import java.util.ArrayList;
import java.util.Collections;

@SpirePatch(
        clz = SensoryStone.class,
        method = "getRandomMemory"
)
public class SensoryStonePatch {

    @SpireInsertPatch(
            locator = Locator.class,
            localvars = {"memories"}
    )
    public static void Insert(SensoryStone __instance, ArrayList<String> memories) {
        EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(EchoMod.makeID(SensoryStone.class.getSimpleName()));
        memories.add(eventStrings.DESCRIPTIONS[0]);
    }

    public static class Locator extends SpireInsertLocator {
        public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
            Matcher finalMatcher = new Matcher.MethodCallMatcher(Collections.class, "shuffle");
            return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<>(), finalMatcher);
        }
    }
}
