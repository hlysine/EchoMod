package echo.powers;

import basemod.interfaces.CloneablePowerInterface;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.NonStackablePower;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import echo.EchoMod;
import echo.mechanics.duplicate.Duplicator;
import echo.subscribers.DuplicateSubscriber;
import echo.util.RunnableAction;
import echo.util.TextureLoader;

public class FastGenerationPower extends AbstractPower implements NonStackablePower, CloneablePowerInterface, DuplicateSubscriber {

    public static final String POWER_ID = EchoMod.makeID(FastGenerationPower.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    private static final Texture tex84 = TextureLoader.getTexture(EchoMod.makePowerPath("fast_generation84.png"));
    private static final Texture tex32 = TextureLoader.getTexture(EchoMod.makePowerPath("fast_generation32.png"));

    public int damageDealt;
    public final int damagePerCharge;

    /**
     * @deprecated This is for auto-instantiation by DevConsole only.
     */
    @Deprecated
    public FastGenerationPower(final AbstractCreature owner, final int amount) {
        this(owner, amount, 6);
    }

    public FastGenerationPower(final AbstractCreature owner, final int amount, final int damagePerCharge) {
        name = NAME;
        ID = POWER_ID;

        this.owner = owner;
        this.amount = amount;
        this.damageDealt = 0;
        this.damagePerCharge = damagePerCharge;

        type = PowerType.BUFF;
        isTurnBased = false;

        this.region128 = new TextureAtlas.AtlasRegion(tex84, 0, 0, 84, 84);
        this.region48 = new TextureAtlas.AtlasRegion(tex32, 0, 0, 32, 32);

        updateDescription();
    }

    @Override
    public boolean isStackable(AbstractPower power) {
        return (power instanceof FastGenerationPower) && ((FastGenerationPower) power).damagePerCharge == this.damagePerCharge;
    }

    @Override
    public void afterDuplicateStart() {
        addToBot(new RunnableAction(this::flash));
    }

    @Override
    public void onAttack(DamageInfo info, int damageAmount, AbstractCreature target) {
        if (target != this.owner && damageAmount > 0 && Duplicator.isDuplicating()) {
            flashWithoutSound();
            damageDealt += damageAmount;
            int powerAmount = Math.floorDiv(damageAmount, damagePerCharge) * amount;
            damageDealt = Math.floorMod(damageDealt, damagePerCharge);
            if (powerAmount > 0) {
                addToBot(new ApplyPowerAction(owner, owner, new UltimateChargePower(owner, powerAmount)));
            }
            updateDescription();
        }
    }

    @Override
    public void updateDescription() {
        description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1] + damagePerCharge + DESCRIPTIONS[2] + (damagePerCharge - damageDealt) + DESCRIPTIONS[3];
    }

    @Override
    public AbstractPower makeCopy() {
        return new FastGenerationPower(owner, amount, damagePerCharge);
    }
}
