package echo.cards.attacks;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import echo.EchoMod;
import echo.cards.FocusedCard;
import echo.mechanics.focused.FocusedChecker;

public class FlashBeam extends FocusedCard {

    public static final String ID = EchoMod.makeID(FlashBeam.class.getSimpleName());

    private static final CardTarget TARGET = CardTarget.ENEMY;

    public FlashBeam() {
        super(ID, TARGET, false);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        if (FocusedChecker.isFocused(m)) {
            addToBot(new DamageAction(m, new DamageInfo(p, damage, damageTypeForTurn), AbstractGameAction.AttackEffect.SMASH));
        } else {
            addToBot(new DrawCardAction(magicNumber));
        }
    }
}
