package echo.powers;

import basemod.interfaces.CloneablePowerInterface;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import echo.EchoMod;
import echo.util.TextureLoader;

public class StickyBombPower extends AbstractPower implements CloneablePowerInterface {

    public static final String POWER_ID = EchoMod.makeID(StickyBombPower.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    private static final Texture tex84 = TextureLoader.getTexture("echoResources/images/powers/sticky_bomb84.png");
    private static final Texture tex32 = TextureLoader.getTexture("echoResources/images/powers/sticky_bomb32.png");

    public StickyBombPower(final AbstractCreature owner, final int amount) {
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
    public void atEndOfRound() {
        for (int i = this.amount; i > 0; i--) {
            addToBot(new DamageAction(this.owner, new DamageInfo(AbstractDungeon.player, i, DamageInfo.DamageType.THORNS), AbstractGameAction.AttackEffect.BLUNT_LIGHT));
            addToBot(new ReducePowerAction(this.owner, this.owner, ID, 1));
        }
    }

    @Override
    public void updateDescription() {
        description = DESCRIPTIONS[0] + ((this.amount + 1) * this.amount / 2) + DESCRIPTIONS[1];
    }

    @Override
    public AbstractPower makeCopy() {
        return new StickyBombPower(owner, amount);
    }
}
