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
import com.megacrit.cardcrawl.powers.WeakPower;
import echo.EchoMod;
import echo.cards.attacks.RapidAssail;
import echo.util.TextureLoader;
import echo.variables.MagicNumber2Variable;

public class RapidAssailPower extends AbstractPower implements NonStackablePower, CloneablePowerInterface {

    public static final String POWER_ID = EchoMod.makeID(RapidAssailPower.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    private static final Texture tex84 = TextureLoader.getTexture(EchoMod.makePowerPath("rapid_assail84.png"));
    private static final Texture tex32 = TextureLoader.getTexture(EchoMod.makePowerPath("rapid_assail32.png"));

    public int damageDealt;
    public final int damagePerWeak;

    /**
     * @deprecated This is for auto-instantiation by DevConsole only.
     */
    @Deprecated
    public RapidAssailPower(final AbstractCreature owner, final int amount) {
        // instantiate with the base value of Rapid Assail power card
        this(owner, amount, EchoMod.getCardInfo(RapidAssail.ID).getBaseValue(MagicNumber2Variable.ID));
    }

    public RapidAssailPower(final AbstractCreature owner, final int amount, final int damagePerWeak) {
        name = NAME;
        ID = POWER_ID;

        this.owner = owner;
        this.amount = amount;
        this.damageDealt = 0;
        this.damagePerWeak = damagePerWeak;

        type = PowerType.DEBUFF;
        isTurnBased = false;

        this.region128 = new TextureAtlas.AtlasRegion(tex84, 0, 0, 84, 84);
        this.region48 = new TextureAtlas.AtlasRegion(tex32, 0, 0, 32, 32);

        updateDescription();
    }

    @Override
    public boolean isStackable(AbstractPower power) {
        return (power instanceof RapidAssailPower) && ((RapidAssailPower) power).damagePerWeak == this.damagePerWeak;
    }

    @Override
    public void wasHPLost(DamageInfo info, int damageAmount) {
        if (damageAmount > 0) {
            flashWithoutSound();
            damageDealt += damageAmount;
            int powerAmount = Math.floorDiv(damageDealt, damagePerWeak) * amount;
            damageDealt = Math.floorMod(damageDealt, damagePerWeak);
            if (powerAmount > 0) {
                addToBot(new ApplyPowerAction(owner, owner, new WeakPower(owner, powerAmount, false)));
            }
            updateDescription();
        }
    }

    @Override
    public void updateDescription() {
        description = DESCRIPTIONS[0] + damagePerWeak + DESCRIPTIONS[1] + this.amount + DESCRIPTIONS[2] + (damagePerWeak - damageDealt) + DESCRIPTIONS[3];
    }

    @Override
    public AbstractPower makeCopy() {
        return new RapidAssailPower(owner, amount, damagePerWeak);
    }
}
