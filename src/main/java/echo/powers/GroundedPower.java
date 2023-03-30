package echo.powers;

import basemod.interfaces.CloneablePowerInterface;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import echo.EchoMod;
import echo.subscribers.BeforeApplyPowerSubscriber;
import echo.util.TextureLoader;

public class GroundedPower extends AbstractPower implements CloneablePowerInterface, BeforeApplyPowerSubscriber {

    public static final String POWER_ID = EchoMod.makeID(GroundedPower.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    private static final Texture tex84 = TextureLoader.getTexture("echoResources/images/powers/grounded84.png");
    private static final Texture tex32 = TextureLoader.getTexture("echoResources/images/powers/grounded32.png");

    public GroundedPower(final AbstractCreature owner, final int amount) {
        name = NAME;
        ID = POWER_ID;

        this.owner = owner;
        this.amount = amount;

        type = PowerType.DEBUFF;
        isTurnBased = true;

        this.region128 = new TextureAtlas.AtlasRegion(tex84, 0, 0, 84, 84);
        this.region48 = new TextureAtlas.AtlasRegion(tex32, 0, 0, 32, 32);

        updateDescription();
    }

    @Override
    public void atStartOfTurn() {
        if (this.amount > 1) {
            addToBot(new ReducePowerAction(this.owner, this.owner, ID, 1));
        } else {
            addToBot(new RemoveSpecificPowerAction(this.owner, this.owner, ID));
        }
    }

    @Override
    public AbstractPower beforeApplyPower(AbstractPower powerToApply, AbstractCreature source, AbstractCreature target) {
        if (powerToApply instanceof FlightPower) {
            if (target == this.owner) {
                flash();
                return null;
            }
        }
        return powerToApply;
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
        return new GroundedPower(owner, amount);
    }
}
