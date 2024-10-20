package echo.powers;

import basemod.interfaces.CloneablePowerInterface;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import echo.EchoMod;
import echo.cards.attacks.TriShot;
import echo.util.TextureLoader;
import hlysine.STSItemInfo.CardInfo;

import java.util.function.Consumer;

public class CalibratedPower extends AbstractPower implements CloneablePowerInterface {

    public static final String POWER_ID = EchoMod.makeID(CalibratedPower.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    private static final Texture tex84 = TextureLoader.getTexture(EchoMod.makePowerPath("calibrated84.png"));
    private static final Texture tex32 = TextureLoader.getTexture(EchoMod.makePowerPath("calibrated32.png"));

    public CalibratedPower(final AbstractCreature owner, final int amount) {
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
    public void onInitialApplication() {
        updateExistingTriShot();
    }


    @Override
    public void stackPower(int stackAmount) {
        super.stackPower(stackAmount);
        updateExistingTriShot();
    }

    private void updateExistingTriShot() {
        CardInfo info = EchoMod.getCardInfo(TriShot.ID);
        int baseDamage = info.getBaseValue("D");
        int upgradedDamage = baseDamage + info.getUpgradeValue("D");

        Consumer<CardGroup> updateInGroup = group -> {
            for (AbstractCard c : group.group) {
                if (c instanceof TriShot) {
                    if (!c.upgraded) {
                        c.baseDamage = baseDamage + this.amount;
                    } else {
                        c.baseDamage = upgradedDamage + this.amount;
                    }
                }
            }
        };

        updateInGroup.accept(AbstractDungeon.player.hand);
        updateInGroup.accept(AbstractDungeon.player.drawPile);
        updateInGroup.accept(AbstractDungeon.player.discardPile);
        updateInGroup.accept(AbstractDungeon.player.exhaustPile);
    }

    public void onDrawOrDiscard() {
        updateExistingTriShot();
    }

    @Override
    public void updateDescription() {
        description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1];
    }

    @Override
    public AbstractPower makeCopy() {
        return new CalibratedPower(owner, amount);
    }
}
