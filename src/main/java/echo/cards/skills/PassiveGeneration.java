package echo.cards.skills;

import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import echo.EchoMod;
import echo.cards.AbstractBaseCard;

public class PassiveGeneration extends AbstractBaseCard {

    public static final String ID = EchoMod.makeID(PassiveGeneration.class.getSimpleName());

    private static final CardTarget TARGET = CardTarget.SELF;

    public PassiveGeneration() {
        super(ID, TARGET);

        this.selfRetain = true;
        this.exhaust = true;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new GainEnergyAction(magicNumber));
    }

    @Override
    public void onRetained() {
        upgradeMagicNumber(this.magicNumber2);
    }

    @Override
    public void upgrade() {
        if (!upgraded) {
            this.isInnate = true;
        }
        super.upgrade();
    }
}
