package echo.patches.metrics;

import basemod.DevConsole;
import basemod.abstracts.CustomSavable;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.google.gson.reflect.TypeToken;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.metrics.MetricData;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Type;

public class DevCommandsMetricPatch {

    private static final Logger logger = LogManager.getLogger(DevCommandsMetricPatch.class.getName());

    public static boolean usedDevCommands = false;

    public static class DevCommandsMetricSavable implements CustomSavable<Boolean> {
        @Override
        public Boolean onSave() {
            logger.info("Saving usedDevCommands = " + usedDevCommands);
            return usedDevCommands;
        }

        @Override
        public void onLoad(Boolean object) {
            logger.info("Loading usedDevCommands = " + object);
            usedDevCommands = object != null && object;
        }

        @Override
        public Type savedType() {
            return new TypeToken<Boolean>() {
            }.getType();
        }
    }

    @SpirePatch(
            clz = DevConsole.class,
            method = "execute"
    )
    public static class DevConsoleExecutePatch {
        public static void Postfix() {
            usedDevCommands = true;
            logger.info("Dev command used, setting usedDevCommands to true");
        }
    }

    @SpirePatch(
            clz = CardCrawlGame.class,
            method = "updateFade"
    )
    public static class ResetDevCommandsMetricPatch {
        @SpireInsertPatch(
                locator = Locator.class
        )
        public static void Insert() {
            usedDevCommands = false;
            logger.info("Resetting usedDevCommands to false");
        }

        private static class Locator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(MetricData.class, "clearData");

                return LineFinder.findAllInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }
}
