package echo.cards.attacks;

import com.evacipated.cardcrawl.mod.stslib.actions.common.StunMonsterAction;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import echo.EchoMod;
import echo.cards.FocusedCard;
import echo.mechanics.focused.FocusedChecker;

public class QuickAssassination extends FocusedCard {

    public static final String ID = EchoMod.makeID(QuickAssassination.class.getSimpleName());

    private static final CardTarget TARGET = CardTarget.ENEMY;

    public QuickAssassination() {
        super(ID, TARGET, false);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new DamageAction(m, new DamageInfo(p, damage, damageTypeForTurn), AbstractGameAction.AttackEffect.SLASH_DIAGONAL));
        if (FocusedChecker.isFocused(m)) {
            addToBot(new StunMonsterAction(m, p, magicNumber));
        }
    }
}
