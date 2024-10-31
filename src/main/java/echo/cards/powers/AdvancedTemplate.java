package echo.cards.powers;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import echo.EchoMod;
import echo.powers.UltimateChargePower;

public class AdvancedTemplate extends AbstractEndTurnAutoplayCard {

    public static final String ID = EchoMod.makeID(AdvancedTemplate.class.getSimpleName());

    private static final CardTarget TARGET = CardTarget.NONE;

    public AdvancedTemplate() {
        super(ID, TARGET);

        this.selfRetain = true;
        this.returnToHand = true;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        if (this.dontTriggerOnUseCard) {
            addToBot(new ApplyPowerAction(p, p, new UltimateChargePower(p, magicNumber)));
        }
    }
}
