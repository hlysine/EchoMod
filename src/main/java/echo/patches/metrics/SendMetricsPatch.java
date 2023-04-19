package echo.patches.metrics;

import basemod.ReflectionHacks;
import com.evacipated.cardcrawl.modthespire.Loader;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.metrics.Metrics;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import com.megacrit.cardcrawl.screens.GameOverScreen;
import echo.EchoMod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.HashMap;
import java.util.stream.Collectors;

public class SendMetricsPatch {
    //    public static final String UPLOAD_URL = "http://127.0.0.1:3000/api/metrics";
    public static final String UPLOAD_URL = "https://sts-echo.vercel.app/api/metrics";

    private static final Logger logger = LogManager.getLogger(SendMetricsPatch.class.getName());

    @SpirePatch(
            clz = GameOverScreen.class,
            method = "shouldUploadMetricData"
    )
    public static class ShouldUploadMetricDataPatch {
        public static boolean Postfix(boolean __result) { // it is important to define this parameter so that the return value is replaced
            if (Settings.UPLOAD_DATA)
                logger.info("Allowing metrics upload for modded content");
            return Settings.UPLOAD_DATA;
        }
    }

    @SpirePatch(
            clz = Metrics.class,
            method = "gatherAllData"
    )
    public static class MetricsGatherAllDataPatch {
        public static void Prefix(Metrics __instance, boolean death, boolean trueVictor, MonsterGroup monsters, HashMap<Object, Object> ___params) {
            logger.info("Gathering metrics data");
            ___params.put("installed_mods", Arrays.stream(Loader.MODINFOS).map(modInfo -> modInfo.ID).collect(Collectors.toList()));
            ___params.put("used_dev_commands", DevCommandsMetricPatch.usedDevCommands);
            ___params.put("duplicate_choices", DuplicateChoicesMetricPatch.duplicateChoices);
            ___params.put("mod_version", EchoMod.class.getPackage().getSpecificationVersion());
        }
    }

    @SpirePatch(
            clz = Metrics.class,
            method = "run"
    )
    public static class MetricsRunPatch {
        public static void Prefix(Metrics __instance) {
            logger.info("Patched Metrics.run");
            if (Settings.isModded) {
                logger.info("Uploading modded metrics");
                switch (__instance.type) {
                    case UPLOAD_CRASH:
                        gatherAllDataAndSend(__instance, __instance.death, false, __instance.monsters);
                        break;
                    case UPLOAD_METRICS:
                        gatherAllDataAndSend(__instance, __instance.death, __instance.trueVictory, __instance.monsters);
                        break;
                }
            }
        }

        private static void gatherAllDataAndSend(Metrics instance, boolean death, boolean trueVictor, MonsterGroup monsters) {
            if (Settings.UPLOAD_DATA) {
                ReflectionHacks.privateMethod(Metrics.class, "gatherAllData", boolean.class, boolean.class, MonsterGroup.class).invoke(instance, death, trueVictor, monsters);
                ReflectionHacks.privateMethod(Metrics.class, "sendPost", String.class, String.class).invoke(instance, UPLOAD_URL, null);
                logger.info("Modded metrics sent to " + UPLOAD_URL);
            }
        }
    }
}
