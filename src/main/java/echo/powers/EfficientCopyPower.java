package echo.powers;

import basemod.interfaces.CloneablePowerInterface;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import echo.EchoMod;
import echo.subscribers.DuplicateSubscriber;
import echo.util.RunnableAction;
import echo.util.TextureLoader;

public class EfficientCopyPower extends AbstractPower implements CloneablePowerInterface, DuplicateSubscriber {

    public static final String POWER_ID = EchoMod.makeID(EfficientCopyPower.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    private static final Texture tex84 = TextureLoader.getTexture("echoResources/images/powers/placeholder_power84.png");
    private static final Texture tex32 = TextureLoader.getTexture("echoResources/images/powers/placeholder_power32.png");

    public EfficientCopyPower(final AbstractCreature owner, final int amount) {
        name = NAME;
        ID = POWER_ID;

        this.owner = owner;
        this.amount = amount;

        type = PowerType.BUFF;
        isTurnBased = false;

        this.region128 = new TextureAtlas.AtlasRegion(tex84, 0, 0, 84, 84);
        this.region48 = new TextureAtlas.AtlasRegion(tex32, 0, 0, 32, 32);

        updateDescription();
    }

    @Override
    public void afterDuplicateStart() {
        addToBot(new RunnableAction(this::flash));
        addToBot(new GainEnergyAction(amount));
        addToBot(new DrawCardAction(amount));
    }

    @Override
    public void updateDescription() {
        description = DESCRIPTIONS[0] + describeNumber(this.amount, 1) + describeNumber(this.amount, 3);
    }

    private String describeNumber(int number, int singularIndex) {
        if (number > 1) return number + DESCRIPTIONS[singularIndex + 1];
        else return number + DESCRIPTIONS[singularIndex];
    }

    @Override
    public AbstractPower makeCopy() {
        return new EfficientCopyPower(owner, amount);
    }
}
