package echo.patches.duplicate;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.screens.CardRewardScreen;
import com.megacrit.cardcrawl.screens.custom.CustomModeCharacterButton;
import com.megacrit.cardcrawl.ui.buttons.PeekButton;
import javassist.CannotCompileException;
import javassist.CtBehavior;

import java.util.ArrayList;
import java.util.List;

public class CardRewardScreenPatch {
    @SpirePatch(
            clz = CardRewardScreen.class,
            method = SpirePatch.CLASS
    )
    public static class Fields {
        public static SpireField<List<CustomModeCharacterButton>> characterButtons = new SpireField<>(() -> null);
        public static SpireField<String> tipMsg = new SpireField<>(() -> null);
    }

    @SpirePatch(
            clz = CardRewardScreen.class,
            method = "open"
    )
    @SpirePatch(
            clz = CardRewardScreen.class,
            method = "customCombatOpen"
    )
    @SpirePatch(
            clz = CardRewardScreen.class,
            method = "chooseOneOpen"
    )
    @SpirePatch(
            clz = CardRewardScreen.class,
            method = "draftOpen"
    )
    @SpirePatch(
            clz = CardRewardScreen.class,
            method = "discoveryOpen",
            paramtypez = {}
    )
    @SpirePatch(
            clz = CardRewardScreen.class,
            method = "discoveryOpen",
            paramtypez = {AbstractCard.CardType.class}
    )
    @SpirePatch(
            clz = CardRewardScreen.class,
            method = "carveRealityOpen"
    )
    @SpirePatch(
            clz = CardRewardScreen.class,
            method = "foreignInfluenceOpen"
    )
    @SpirePatch(
            clz = CardRewardScreen.class,
            method = "codexOpen"
    )
    public static class OpenPatch {
        public static void Postfix(CardRewardScreen __instance) {
            Fields.characterButtons.set(__instance, null);
            Fields.tipMsg.set(__instance, null);
        }
    }

    @SpirePatch(
            clz = CardRewardScreen.class,
            method = "update"
    )
    public static class UpdatePatch {
        @SpireInsertPatch(
                locator = Locator.class
        )
        public static void Insert(CardRewardScreen __instance) {
            List<CustomModeCharacterButton> characterButtons = Fields.characterButtons.get(__instance);
            if (characterButtons != null) {
                for (int i = 0; i < characterButtons.size(); i++) {
                    CustomModeCharacterButton button = characterButtons.get(i);
                    AbstractCard card = __instance.rewardGroup.get(i);
                    button.move(card.target_x, card.target_y - AbstractCard.IMG_HEIGHT * 0.5f - 20f * Settings.scale);
                    if (button.hb.hovered && InputHelper.justClickedLeft) {
                        button.hb.clickStarted = true;
                    }
                    button.hb.update();
                }
            }
        }

        public static class Locator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(CardRewardScreen.class, "cardSelectUpdate");
                return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<>(), finalMatcher);
            }
        }
    }

    @SpirePatch(
            clz = CardRewardScreen.class,
            method = "cardSelectUpdate"
    )
    public static class CardButtonLinkingPatch {
        @SpireInsertPatch(
                locator = Locator.class,
                localvars = {"c"}
        )
        public static void Insert(CardRewardScreen __instance, AbstractCard c) {
            List<CustomModeCharacterButton> characterButtons = Fields.characterButtons.get(__instance);
            if (characterButtons != null) {
                CustomModeCharacterButton button = characterButtons.get(__instance.rewardGroup.indexOf(c));
                c.hb.hovered = c.hb.hovered || button.hb.hovered;
                if (c.hb.hovered)
                    c.hover();
                else
                    c.unhover();
                c.hb.justHovered = c.hb.justHovered || button.hb.justHovered;
                c.hb.clickStarted = c.hb.clickStarted || button.hb.clickStarted;
                c.hb.clicked = c.hb.clicked || button.hb.clicked;
            }
        }

        public static class Locator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(AbstractCard.class, "updateHoverLogic");
                return new int[]{LineFinder.findInOrder(ctMethodToPatch, new ArrayList<>(), finalMatcher)[0] + 1};
            }
        }
    }

    @SpirePatch(
            clz = CardRewardScreen.class,
            method = "render"
    )
    public static class RenderPatch {
        public static void Postfix(CardRewardScreen __instance, SpriteBatch sb) {
            List<CustomModeCharacterButton> characterButtons = Fields.characterButtons.get(__instance);
            if (characterButtons != null && !PeekButton.isPeeking) {
                for (CustomModeCharacterButton button : characterButtons) {
                    button.render(sb);
                }
            }

            String tipMsg = Fields.tipMsg.get(__instance);
            if (tipMsg != null && !tipMsg.isEmpty() && !PeekButton.isPeeking)
                FontHelper.renderDeckViewTip(sb, tipMsg, 96.0F * Settings.scale, Settings.CREAM_COLOR);
        }
    }
}
