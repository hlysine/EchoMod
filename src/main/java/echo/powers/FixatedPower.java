package echo.powers;

import basemod.interfaces.CloneablePowerInterface;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import echo.EchoMod;
import echo.subscribers.FocusedSubscriber;
import echo.util.TextureLoader;

public class FixatedPower extends AbstractPower implements CloneablePowerInterface, FocusedSubscriber {

    public static final String POWER_ID = EchoMod.makeID(FixatedPower.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    private static final Texture tex84 = TextureLoader.getTexture(EchoMod.makePowerPath("placeholder_power84.png"));
    private static final Texture tex32 = TextureLoader.getTexture(EchoMod.makePowerPath("placeholder_power32.png"));
    private final AbstractCard sourceCard;

    public FixatedPower(final AbstractCreature owner, final int amount, final AbstractCard sourceCard) {
        name = NAME;
        ID = POWER_ID;

        this.owner = owner;
        this.amount = amount;
        this.sourceCard = sourceCard;

        type = PowerType.BUFF;
        isTurnBased = false;

        this.region128 = new TextureAtlas.AtlasRegion(tex84, 0, 0, 84, 84);
        this.region48 = new TextureAtlas.AtlasRegion(tex32, 0, 0, 32, 32);

        updateDescription();
    }

    public FixatedPower(final AbstractCreature owner, final int amount) {
        this(owner, amount, null);
    }

    @Override
    public boolean overrideFocusedCheck(AbstractMonster target) {
        return true;
    }

    @Override
    public void onAfterUseCard(AbstractCard card, UseCardAction action) {
        if (sourceCard != null && card == sourceCard) return;
        if (this.amount > 1) {
            addToBot(new ReducePowerAction(owner, owner, this, 1));
        } else {
            addToBot(new RemoveSpecificPowerAction(owner, owner, this));
        }
    }

    @Override
    public void updateDescription() {
        if (this.amount == 1) {
            description = DESCRIPTIONS[0];
        } else {
            description = DESCRIPTIONS[1] + this.amount + DESCRIPTIONS[2];
        }
    }

    @Override
    public AbstractPower makeCopy() {
        return new FixatedPower(owner, amount, sourceCard);
    }
}
