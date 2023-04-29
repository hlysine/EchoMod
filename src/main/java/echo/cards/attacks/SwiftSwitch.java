package echo.cards.attacks;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import echo.EchoMod;
import echo.actions.SwapAction;
import echo.cards.AbstractBaseCard;

public class SwiftSwitch extends AbstractBaseCard {

    public static final String ID = EchoMod.makeID(SwiftSwitch.class.getSimpleName());

    private static final CardTarget TARGET = CardTarget.ENEMY;

    public SwiftSwitch() {
        super(ID, TARGET);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new DamageAction(m, new DamageInfo(p, damage, damageTypeForTurn), AbstractGameAction.AttackEffect.BLUNT_LIGHT));
        addToBot(new SwapAction(magicNumber));
    }
}
