package echo.variables;

import com.megacrit.cardcrawl.cards.AbstractCard;
import echo.cards.AbstractBaseCard;

import static echo.EchoMod.makeID;

public class MagicNumber2Variable extends CustomVariable {

    public static final String ID = makeID("M2");

    @Override
    public String key() {
        return ID;
    }

    @Override
    public void setBaseValue(AbstractCard card, int value) {
        if (card instanceof AbstractBaseCard) {
            AbstractBaseCard rCard = (AbstractBaseCard) card;
            rCard.baseMagicNumber2 = rCard.magicNumber2 = value;
        }
    }

    @Override
    public void upgradeValue(AbstractCard card, int upgradeValue) {
        if (card instanceof AbstractBaseCard) {
            AbstractBaseCard rCard = (AbstractBaseCard) card;
            rCard.upgradeMagicNumber2(upgradeValue);
        }
    }

    @Override
    public boolean isModified(AbstractCard card) {
        return ((AbstractBaseCard) card).isMagicNumber2Modified;
    }

    @Override
    public void setIsModified(AbstractCard card, boolean v) {
        ((AbstractBaseCard) card).isMagicNumber2Modified = false;
    }

    @Override
    public int value(AbstractCard card) {
        return ((AbstractBaseCard) card).magicNumber2;
    }

    @Override
    public int baseValue(AbstractCard card) {
        return ((AbstractBaseCard) card).baseMagicNumber2;
    }

    @Override
    public boolean upgraded(AbstractCard card) {
        return ((AbstractBaseCard) card).upgradedMagicNumber2;
    }
}