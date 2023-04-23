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
import echo.effects.SfxStore;
import echo.effects.StickyBombEffect;
import echo.subscribers.StickyBombSubscriber;
import echo.util.TextureLoader;

public class StickyBombPower extends AbstractPower implements CloneablePowerInterface {

    public static final String POWER_ID = EchoMod.makeID(StickyBombPower.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    private static final Texture tex84 = TextureLoader.getTexture(EchoMod.makePowerPath("sticky_bomb84.png"));
    private static final Texture tex32 = TextureLoader.getTexture(EchoMod.makePowerPath("sticky_bomb32.png"));

    private int lastStack;

    public StickyBombPower(final AbstractCreature owner, final int amount) {
        name = NAME;
        ID = POWER_ID;

        this.owner = owner;
        this.amount = amount;
        this.lastStack = amount;

        type = PowerType.DEBUFF;
        isTurnBased = true;

        this.region128 = new TextureAtlas.AtlasRegion(tex84, 0, 0, 84, 84);
        this.region48 = new TextureAtlas.AtlasRegion(tex32, 0, 0, 32, 32);

        updateDescription();
    }

    private int getBombsPerTurn() {
        int bombs = 6;
        if (!this.owner.isPlayer) {
            for (AbstractPower power : AbstractDungeon.player.powers) {
                if (power instanceof StickyBombSubscriber) {
                    StickyBombSubscriber subscriber = (StickyBombSubscriber) power;
                    bombs = subscriber.modifyBombsPerTurn(bombs);
                }
            }
        }
        for (AbstractPower power : this.owner.powers) {
            if (power instanceof StickyBombSubscriber) {
                StickyBombSubscriber subscriber = (StickyBombSubscriber) power;
                bombs = subscriber.modifyBombsPerTurn(bombs);
            }
        }
        return bombs;
    }

    @Override
    public void playApplyPowerSfx() {
        if (this.lastStack > 2) {
            SfxStore.STICKY_BOMB_APPLY.play(0.4f);
        } else {
            SfxStore.STICKY_BOMB_APPLY.playV(0.3f);
        }
    }

    @Override
    public void onInitialApplication() {
        for (int i = 0; i < 10 && i < this.amount; i++) {
            AbstractDungeon.effectList.add(new StickyBombEffect(this.owner, i * 0.2f));
        }
    }

    @Override
    public void stackPower(int stackAmount) {
        super.stackPower(stackAmount);
        this.lastStack = stackAmount;
        for (int i = 0; i < 10 && i < stackAmount; i++) {
            AbstractDungeon.effectList.add(new StickyBombEffect(this.owner, i * 0.2f));
        }
    }

    @Override
    public void atStartOfTurn() {
        int bombs = getBombsPerTurn();
        for (int i = this.amount; i > Math.max(0, this.amount - bombs); i--) {
            addToBot(new DamageAction(this.owner, new DamageInfo(AbstractDungeon.player, i, DamageInfo.DamageType.THORNS), AbstractGameAction.AttackEffect.BLUNT_LIGHT, true));
            addToBot(new ReducePowerAction(this.owner, this.owner, ID, 1));
        }
    }

    @Override
    public void updateDescription() {
        int bombs = getBombsPerTurn();
        description = DESCRIPTIONS[0] + describeNumber(bombs, 1) + ((this.amount + Math.max(1, this.amount - bombs + 1)) * Math.min(this.amount, bombs) / 2) + DESCRIPTIONS[3];
    }

    private String describeNumber(int number, int singularIndex) {
        if (number > 1) return number + DESCRIPTIONS[singularIndex + 1];
        else return number + DESCRIPTIONS[singularIndex];
    }

    @Override
    public AbstractPower makeCopy() {
        return new StickyBombPower(owner, amount);
    }

    public static int getAmount(AbstractCreature creature) {
        int amount = 0;
        for (AbstractPower power : creature.powers) {
            if (power instanceof StickyBombPower) {
                amount += power.amount;
            }
        }
        return amount;
    }
}
