package echo.powers;

import basemod.interfaces.CloneablePowerInterface;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import echo.EchoMod;
import echo.effects.SfxStore;
import echo.util.TextureLoader;

public class FlightPower extends AbstractPower implements CloneablePowerInterface {

    public static final String POWER_ID = EchoMod.makeID(FlightPower.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    private static final Texture tex84 = TextureLoader.getTexture(EchoMod.makePowerPath("flight84.png"));
    private static final Texture tex32 = TextureLoader.getTexture(EchoMod.makePowerPath("flight32.png"));

    public FlightPower(final AbstractCreature owner, final int amount) {
        name = NAME;
        ID = POWER_ID;

        this.owner = owner;
        this.amount = amount;

        type = PowerType.BUFF;
        isTurnBased = true;

        this.region128 = new TextureAtlas.AtlasRegion(tex84, 0, 0, 84, 84);
        this.region48 = new TextureAtlas.AtlasRegion(tex32, 0, 0, 32, 32);

        updateDescription();
    }

    @Override
    public void playApplyPowerSfx() {
        SfxStore.FLIGHT_APPLY.play(0.05f);
    }

    private void consumeFlight() {
        if (this.amount > 1) {
            addToBot(new ReducePowerAction(this.owner, this.owner, ID, 1));
        } else {
            addToBot(new RemoveSpecificPowerAction(this.owner, this.owner, ID));
        }
    }

    private float calculateDamageTakenAmount(float damage, DamageInfo.DamageType type) {
        return damage * (1 - 0.3f);
    }

    @Override
    public void atStartOfTurn() {
        consumeFlight();
    }

    @Override
    public float atDamageFinalReceive(float damage, DamageInfo.DamageType type) {
        return calculateDamageTakenAmount(damage, type);
    }

    @Override
    public void wasHPLost(DamageInfo info, int damageAmount) {
        if (info.owner != null && damageAmount > 0) {
            flash();
            consumeFlight();
        }
    }

    @Override
    public void updateDescription() {
        description = DESCRIPTIONS[0] + describeNumber(this.amount, 1);
    }

    private String describeNumber(int number, int singularIndex) {
        if (number > 1) return number + DESCRIPTIONS[singularIndex + 1];
        else return number + DESCRIPTIONS[singularIndex];
    }

    @Override
    public AbstractPower makeCopy() {
        return new FlightPower(owner, amount);
    }
}
