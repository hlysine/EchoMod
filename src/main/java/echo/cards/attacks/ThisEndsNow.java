package echo.cards.attacks;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import echo.EchoMod;
import echo.cards.ChargedCard;
import echo.effects.EchoOverclockEffect;

public class ThisEndsNow extends ChargedCard {

    public static final String ID = EchoMod.makeID(ThisEndsNow.class.getSimpleName());

    private static final CardTarget TARGET = CardTarget.ENEMY;

    public ThisEndsNow() {
        super(ID, TARGET, true);

        this.exhaust = true;
        this.isEthereal = true;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        super.use(p, m);
        addToBot(new VFXAction(new EchoOverclockEffect()));
        addToBot(new DamageAction(m, new DamageInfo(p, damage, damageTypeForTurn), AbstractGameAction.AttackEffect.NONE));
    }
}
