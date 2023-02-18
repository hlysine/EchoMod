package echo.patches;

import com.evacipated.cardcrawl.modthespire.lib.ByRef;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.actions.animations.TalkAction;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.beyond.TimeEater;
import echo.EchoMod;
import echo.characters.Echo;

@SpirePatch(
        clz = TimeEater.class,
        method = "takeTurn"
)
public class TimeEaterTalkPatch {
    public static void Prefix(TimeEater __instance, @ByRef boolean[] ___firstTurn) {
        if (___firstTurn[0]) {
            if (AbstractDungeon.player.chosenClass == Echo.Enums.ECHO) {
                MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(EchoMod.makeID(TimeEater.class.getSimpleName()));
                AbstractDungeon.actionManager.addToBottom(new TalkAction(__instance, monsterStrings.DIALOG[0], 0.5F, 2.0F));
                ___firstTurn[0] = false;
            }
        }
    }
}
