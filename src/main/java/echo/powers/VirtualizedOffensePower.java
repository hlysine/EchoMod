package echo.powers;

import basemod.interfaces.CloneablePowerInterface;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import echo.EchoMod;
import echo.subscribers.DuplicateSubscriber;
import echo.util.RunnableAction;
import echo.util.TextureLoader;

public class VirtualizedOffensePower extends AbstractPower implements CloneablePowerInterface, DuplicateSubscriber {

    public static final String POWER_ID = EchoMod.makeID(VirtualizedOffensePower.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    private static final Texture tex84 = TextureLoader.getTexture(EchoMod.makePowerPath("virtualized_offense84.png"));
    private static final Texture tex32 = TextureLoader.getTexture(EchoMod.makePowerPath("virtualized_offense32.png"));

    private int amountToRemove;

    public VirtualizedOffensePower(final AbstractCreature owner, final int amount) {
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
        addToBot(new ApplyPowerAction(owner, owner, new StrengthPower(owner, amount)));
        this.amountToRemove += (this.amount - 1);
    }

    @Override
    public void afterDuplicateEnd() {
        addToBot(new RunnableAction(this::flash));
        addToBot(new ReducePowerAction(owner, owner, StrengthPower.POWER_ID, amountToRemove));
        amountToRemove = 0;
    }

    @Override
    public void updateDescription() {
        description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1] + (this.amount - 1) + DESCRIPTIONS[2];
    }

    @Override
    public AbstractPower makeCopy() {
        return new VirtualizedOffensePower(owner, amount);
    }
}
