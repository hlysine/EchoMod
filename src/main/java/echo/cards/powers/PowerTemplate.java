package echo.cards.powers;

import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import echo.EchoMod;

public class PowerTemplate extends AbstractEndTurnAutoplayCard {

    public static final String ID = EchoMod.makeID(PowerTemplate.class.getSimpleName());

    private static final CardTarget TARGET = CardTarget.NONE;

    public PowerTemplate() {
        super(ID, TARGET);

        this.selfRetain = true;
        this.returnToHand = true;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        if (this.dontTriggerOnUseCard) {
            addToBot(new GainBlockAction(p, p, block));
        }
    }
}
