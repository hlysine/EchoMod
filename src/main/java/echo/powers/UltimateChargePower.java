package echo.powers;

import basemod.interfaces.CloneablePowerInterface;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import echo.EchoMod;
import echo.util.TextureLoader;

public class UltimateChargePower extends AbstractPower implements CloneablePowerInterface {

    public static final String POWER_ID = EchoMod.makeID(UltimateChargePower.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    private static final Texture[] tex84;
    private static final Texture[] tex32;

    static {
        tex84 = new Texture[10];
        tex32 = new Texture[10];
        for (int i = 1; i <= 10; i++) {
            tex84[i - 1] = TextureLoader.getTexture(EchoMod.makePowerPath("ultimate_charge84_" + i + ".png"));
            tex32[i - 1] = TextureLoader.getTexture(EchoMod.makePowerPath("ultimate_charge32_" + i + ".png"));
        }
    }

    public UltimateChargePower(final AbstractCreature owner, final int amount) {
        name = NAME;
        ID = POWER_ID;

        this.owner = owner;
        this.amount = amount;

        type = PowerType.BUFF;
        isTurnBased = false;

        this.region128 = new TextureAtlas.AtlasRegion(getTex84(), 0, 0, 84, 84);
        this.region48 = new TextureAtlas.AtlasRegion(getTex32(), 0, 0, 32, 32);

        updateDescription();
    }

    private Texture getTex84() {
        return tex84[Math.min(tex84.length - 1, this.amount - 1)];
    }

    private Texture getTex32() {
        return tex32[Math.min(tex32.length - 1, this.amount - 1)];
    }

    @Override
    public void stackPower(int stackAmount) {
        super.stackPower(stackAmount);

        this.region128 = new TextureAtlas.AtlasRegion(getTex84(), 0, 0, 84, 84);
        this.region48 = new TextureAtlas.AtlasRegion(getTex32(), 0, 0, 32, 32);
    }

    @Override
    public void updateDescription() {
        description = DESCRIPTIONS[0];
    }

    @Override
    public AbstractPower makeCopy() {
        return new UltimateChargePower(owner, amount);
    }
}
