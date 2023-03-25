package echo.patches;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import com.megacrit.cardcrawl.monsters.beyond.AwakenedOne;
import com.megacrit.cardcrawl.monsters.beyond.Darkling;
import com.megacrit.cardcrawl.powers.AbstractPower;
import echo.subscribers.OnMonsterDeathSubscriber;
import javassist.CannotCompileException;
import javassist.CtBehavior;

import java.util.ArrayList;

public class OnMonsterDeathPatch {
    @SpirePatch(
            clz = AbstractMonster.class,
            method = "die",
            paramtypez = {boolean.class}
    )
    public static class AbstractMonsterPatch {
        @SpireInsertPatch(
                locator = Locator.class
        )
        public static void Insert(AbstractMonster __instance) {
            for (AbstractPower power : AbstractDungeon.player.powers) {
                if (power instanceof OnMonsterDeathSubscriber) {
                    OnMonsterDeathSubscriber subscriber = (OnMonsterDeathSubscriber) power;
                    subscriber.onMonsterDeath(__instance);
                }
            }
        }

        public static class Locator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(MonsterGroup.class, "areMonstersBasicallyDead");
                return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<>(), finalMatcher);
            }
        }
    }

    @SpirePatch(
            clz = AwakenedOne.class,
            method = "damage",
            paramtypez = {DamageInfo.class}
    )
    @SpirePatch(
            clz = Darkling.class,
            method = "damage",
            paramtypez = {DamageInfo.class}
    )
    public static class SpecificMonstersPatch {
        @SpireInsertPatch(
                locator = Locator.class
        )
        public static void Insert(AbstractMonster __instance) {
            for (AbstractPower power : AbstractDungeon.player.powers) {
                if (power instanceof OnMonsterDeathSubscriber) {
                    OnMonsterDeathSubscriber subscriber = (OnMonsterDeathSubscriber) power;
                    subscriber.onMonsterDeath(__instance);
                }
            }
        }

        public static class Locator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
                Matcher finalMatcher = new Matcher.FieldAccessMatcher(AbstractPlayer.class, "relics");
                return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<>(), finalMatcher);
            }
        }
    }
}
