package echo.actions.duplicate;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.TutorialStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.vfx.ThoughtBubble;
import echo.EchoMod;
import echo.effects.DuplicateEffect;
import echo.mechanics.duplicate.CloningModule;
import echo.powers.UltimateChargePower;

public class DuplicatePlayerAction extends AbstractGameAction {
    private static final TutorialStrings tutorialStrings = CardCrawlGame.languagePack.getTutorialString(EchoMod.makeID(DuplicatePlayerAction.class.getSimpleName()));

    private final AbstractPlayer.PlayerClass playerClass;
    private final boolean requiresUltimateCharge;

    /**
     * Duplicate a player class, causing the player to become that class, affecting energy, cards, relics and more.
     *
     * @param playerClass            The player class to duplicate.
     * @param requiresUltimateCharge Whether Ultimate Charge is required and consumed for this duplication.
     */
    public DuplicatePlayerAction(AbstractPlayer.PlayerClass playerClass, boolean requiresUltimateCharge) {
        this.playerClass = playerClass;
        this.requiresUltimateCharge = requiresUltimateCharge;
    }

    @Override
    public void update() {
        AbstractPlayer player = AbstractDungeon.player;
        if (!AbstractDungeon.getCurrRoom().isBattleEnding()) {
            if (this.requiresUltimateCharge) {
                AbstractPower power = player.getPower(UltimateChargePower.POWER_ID);
                if (power == null || power.amount < 10) {
                    AbstractDungeon.effectList.add(new ThoughtBubble(player.dialogX, player.dialogY, 3.0F, tutorialStrings.TEXT[0], true));
                    this.isDone = true;
                    return;
                }
                addToBot(new RemoveSpecificPowerAction(player, player, power));
            }
            CloningModule.preCloneSetup();
            addToBot(new VFXAction(AbstractDungeon.player, new DuplicateEffect(() -> {
                CloningModule.startCloning(DuplicatePlayerAction.this.playerClass);
            }), DuplicateEffect.DURATION, true));
        }
        this.isDone = true;
    }
}
