package echo.patches.duplicate;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireField;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.screens.CardRewardScreen;
import com.megacrit.cardcrawl.screens.custom.CustomModeCharacterButton;
import com.megacrit.cardcrawl.ui.buttons.PeekButton;

import java.util.List;

public class CardRewardScreenPatch {
    @SpirePatch(
            clz = CardRewardScreen.class,
            method = SpirePatch.CLASS
    )
    public static class Fields {
        public static SpireField<List<CustomModeCharacterButton>> characterButtons = new SpireField<>(() -> null);
        public static SpireField<String> tipMsg = new SpireField<String>(() -> null);
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
        public static void Postfix(CardRewardScreen __instance) {
            List<CustomModeCharacterButton> characterButtons = Fields.characterButtons.get(__instance);
            if (characterButtons != null) {
                for (int i = 0; i < characterButtons.size(); i++) {
                    CustomModeCharacterButton button = characterButtons.get(i);
                    AbstractCard card = __instance.rewardGroup.get(i);
                    button.move(card.target_x, card.target_y - AbstractCard.IMG_HEIGHT * 0.5f - 20f * Settings.scale);
                    button.hb.update();
                }
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
            if (characterButtons != null) {
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
