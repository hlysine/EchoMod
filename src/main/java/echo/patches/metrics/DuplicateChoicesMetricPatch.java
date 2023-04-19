package echo.patches.metrics;

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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DuplicateChoicesMetricPatch {

    private static final Logger logger = LogManager.getLogger(DuplicateChoicesMetricPatch.class.getName());

    public static List<Map<String, Object>> duplicateChoices = new ArrayList<>();

    public static class DuplicateChoicesMetricSavable implements CustomSavable<List<Map<String, Object>>> {
        @Override
        public List<Map<String, Object>> onSave() {
            logger.info("Saving " + duplicateChoices.size() + " duplicate choices.");
            return duplicateChoices;
        }

        @Override
        public void onLoad(List<Map<String, Object>> object) {
            if (object == null) {
                logger.info("No saved duplicate choices.");
                duplicateChoices = new ArrayList<>();
            } else {
                logger.info("Loading " + object.size() + " duplicate choices.");
                duplicateChoices = object;
            }
        }

        @Override
        public Type savedType() {
            return new TypeToken<List<Map<String, Object>>>() {
            }.getType();
        }
    }

    @SpirePatch(
            clz = CardCrawlGame.class,
            method = "updateFade"
    )
    public static class ResetDuplicateChoicesMetricPatch {
        @SpireInsertPatch(
                locator = Locator.class
        )
        public static void Insert() {
            duplicateChoices.clear();
            logger.info("Clearing duplicate choices");
        }

        private static class Locator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(MetricData.class, "clearData");

                return LineFinder.findAllInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }
}
