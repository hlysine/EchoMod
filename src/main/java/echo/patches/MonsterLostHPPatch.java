package echo.patches;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import echo.subscribers.MonsterLostHPSubscriber;
import javassist.CannotCompileException;
import javassist.CtBehavior;

import java.util.ArrayList;

public class MonsterLostHPPatch {
    @SpirePatch(
            clz = AbstractMonster.class,
            method = "damage"
    )
    public static class AbstractMonsterPatch {
        @SpireInsertPatch(
                locator = Locator.class,
                localvars = {"damageAmount"}
        )
        public static void Insert(AbstractMonster __instance, DamageInfo info, int damageAmount) {
            for (AbstractRelic relic : AbstractDungeon.player.relics) {
                if (relic instanceof MonsterLostHPSubscriber) {
                    MonsterLostHPSubscriber subscriber = (MonsterLostHPSubscriber) relic;
                    subscriber.wasMonsterHPLost(__instance, info, damageAmount);
                }
            }
        }

        public static class Locator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(AbstractMonster.class, "healthBarUpdatedEvent");
                return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<>(), finalMatcher);
            }
        }
    }
}
