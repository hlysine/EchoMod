package echo.cards;

import basemod.abstracts.CustomCard;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.relics.ChemicalX;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;
import echo.EchoMod;
import hlysine.STSItemInfo.CardInfo;

import static com.megacrit.cardcrawl.core.CardCrawlGame.languagePack;
import static echo.EchoMod.getCardInfo;
import static echo.EchoMod.makeCardPath;

public abstract class AbstractBaseCard extends CustomCard {

    public static final String ID = EchoMod.makeID(AbstractBaseCard.class.getSimpleName());

    protected final CardStrings cardStrings;
    protected final CardInfo info;

    public int magicNumber2 = 0;
    public int baseMagicNumber2 = 0;
    public boolean upgradedMagicNumber2 = false;
    public boolean isMagicNumber2Modified = false;

    public AbstractBaseCard(final String id, final CardTarget target) {
        super(
                id,
                languagePack.getCardStrings(id).NAME,
                makeCardPath(getCardInfo(id).getImage()),
                getCardInfo(id).getBaseCost(),
                languagePack.getCardStrings(id).DESCRIPTION,
                getCardInfo(id).getType(),
                getCardInfo(id).getColor(),
                getCardInfo(id).getRarity(),
                target
        );
        cardStrings = languagePack.getCardStrings(id);
        info = getCardInfo(id);

        info.setBaseValues(this);
    }

    public void displayUpgrades() {
        super.displayUpgrades();
        if (upgradedMagicNumber2) {
            magicNumber2 = baseMagicNumber2;
            isMagicNumber2Modified = true;
        }
    }

    public void upgradeMagicNumber2(int amount) {
        baseMagicNumber2 += amount;
        magicNumber2 = baseMagicNumber2;
        upgradedMagicNumber2 = true;
    }

    @Override
    public AbstractCard makeStatEquivalentCopy() {
        AbstractBaseCard copy = (AbstractBaseCard) super.makeStatEquivalentCopy();
        copy.baseMagicNumber2 = this.baseMagicNumber2;
        copy.isMagicNumber2Modified = this.isMagicNumber2Modified;
        copy.magicNumber2 = this.magicNumber2;
        copy.upgradedMagicNumber2 = this.upgradedMagicNumber2;
        return copy;
    }

    protected void upgradeValues() {
        info.upgradeValues(this);
    }

    @Override
    public void upgrade() {
        if (!upgraded) {
            upgradeName();
            upgradeValues();
            initializeDescription();
        }
    }

    public int doXCostEffect() {
        int effect = EnergyPanel.totalCount;
        if (this.energyOnUse != -1) {
            effect = this.energyOnUse;
        }

        if (AbstractDungeon.player.hasRelic(ChemicalX.ID)) {
            effect += 2;
            AbstractDungeon.player.getRelic(ChemicalX.ID).flash();
        }

        if (effect > 0 && !this.freeToPlayOnce) {
            AbstractDungeon.player.energy.use(EnergyPanel.totalCount);
        }

        return effect;
    }
}