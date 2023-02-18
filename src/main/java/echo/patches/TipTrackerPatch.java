package echo.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.helpers.TipTracker;

public class TipTrackerPatch {
    @SpirePatch(
            clz = TipTracker.class,
            method = "refresh"
    )
    public static class RefreshPatch {
        public static void Postfix() {
//            TipTracker.tips.put(TimelineUtils.TIMELINE_TIP, TipTracker.pref.getBoolean(TimelineUtils.TIMELINE_TIP, false));
//            TipTracker.tips.put(TimelineUtils.REWIND_TIP, TipTracker.pref.getBoolean(TimelineUtils.REWIND_TIP, false));
//            TipTracker.tips.put(TimelineUtils.SHIFT_TIP, TipTracker.pref.getBoolean(TimelineUtils.SHIFT_TIP, false));
        }
    }

    @SpirePatch(
            clz = TipTracker.class,
            method = "disableAllFtues"
    )
    public static class DisableAllFtuesPatch {
        public static void Postfix() {
//            TipTracker.neverShowAgain(TimelineUtils.TIMELINE_TIP);
//            TipTracker.neverShowAgain(TimelineUtils.REWIND_TIP);
//            TipTracker.neverShowAgain(TimelineUtils.SHIFT_TIP);
        }
    }
}
