package echo.patches.debug;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.cards.CardQueueItem;
import com.megacrit.cardcrawl.core.Settings;
import echo.EchoMod;

import java.util.ArrayList;
import java.util.List;

@SpirePatch(
        clz = GameActionManager.class,
        method = "update"
)
public class InspectActionManagerQueuesPatch {
    public static AbstractGameAction lastCurrentAction = null;
    public static List<AbstractGameAction> lastActions = new ArrayList<>();
    public static List<CardQueueItem> lastCards = new ArrayList<>();

    public static void Prefix(GameActionManager __instance) {
        if (Settings.isDebug) {
            ArrayList<AbstractGameAction> actions = __instance.actions;
            ArrayList<CardQueueItem> cards = __instance.cardQueue;
            AbstractGameAction currentAction = __instance.currentAction;
            if (lastCurrentAction != currentAction
                    || actions.size() != lastActions.size() || actions.stream().anyMatch(a -> !lastActions.contains(a))
                    || cards.size() != lastCards.size() || cards.stream().anyMatch(a -> !lastCards.contains(a))) {
                EchoMod.logger.info("====== Current Action: " + currentAction);
                EchoMod.logger.info("====== Game Actions start ======");
                for (int i = 0; i < actions.size(); i++) {
                    AbstractGameAction action = actions.get(i);
                    EchoMod.logger.info((i + 1) + ". " + action.toString());
                }
                EchoMod.logger.info("====== Game Actions end ======");
                EchoMod.logger.info("====== Card Queue start ======");
                for (int i = 0; i < cards.size(); i++) {
                    CardQueueItem card = cards.get(i);
                    EchoMod.logger.info((i + 1) + ". " + (card.card == null ? "null" : card.card.cardID));
                }
                EchoMod.logger.info("====== Card Queue end ======");
                EchoMod.logger.info("");
                EchoMod.logger.info("");
                EchoMod.logger.info("");
                lastCurrentAction = currentAction;
                lastActions = new ArrayList<>(actions);
                lastCards = new ArrayList<>(cards);
            }
        }
    }
}
