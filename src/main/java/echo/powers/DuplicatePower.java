package echo.powers;

import basemod.interfaces.CloneablePowerInterface;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import echo.EchoMod;
import echo.actions.duplicate.StopDuplicateAction;
import echo.mechanics.duplicate.CloningModule;
import echo.subscribers.DeathPreProtectionSubscriber;
import echo.util.TextureLoader;

public class DuplicatePower extends AbstractPower implements CloneablePowerInterface, DeathPreProtectionSubscriber {

    public static final String POWER_ID = EchoMod.makeID(DuplicatePower.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    private static final Texture tex84 = TextureLoader.getTexture("echoResources/images/powers/placeholder_power84.png");
    private static final Texture tex32 = TextureLoader.getTexture("echoResources/images/powers/placeholder_power32.png");

    public DuplicatePower(final AbstractCreature owner) {
        name = NAME;
        ID = POWER_ID;

        this.owner = owner;
        this.amount = -1;

        type = PowerType.BUFF;
        isTurnBased = true;

        this.region128 = new TextureAtlas.AtlasRegion(tex84, 0, 0, 84, 84);
        this.region48 = new TextureAtlas.AtlasRegion(tex32, 0, 0, 32, 32);

        updateDescription();
    }

    @Override
    public boolean onDeathPreProtection(DamageInfo damageInfo, DeathInfo deathInfo, boolean canDie) {
        if (CloningModule.isCloning()) {
            addToBot(new StopDuplicateAction());
            return false;
        }
        return true;
    }

    @Override
    public void updateDescription() {
        description = DESCRIPTIONS[0];
    }

    @Override
    public AbstractPower makeCopy() {
        return new DuplicatePower(owner);
    }
}
