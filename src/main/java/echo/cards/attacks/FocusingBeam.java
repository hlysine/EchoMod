package echo.cards.attacks;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import echo.EchoMod;
import echo.cards.FocusedCard;

public class FocusingBeam extends FocusedCard {

    public static final String ID = EchoMod.makeID(FocusingBeam.class.getSimpleName());

    private static final CardTarget TARGET = CardTarget.ENEMY;

    public FocusingBeam() {
        super(ID, TARGET, true);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        for (int i = 0; i < magicNumber; i++) {
            addToBot(new DamageAction(m, new DamageInfo(p, damage, damageTypeForTurn), AbstractGameAction.AttackEffect.SMASH, true));
        }
    }
}
