package echo.cards.attacks;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.DamageAllEnemiesAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import echo.EchoMod;
import echo.cards.ChargedCard;
import echo.effects.EchoOverclockEffect;

public class ThisEndsNow extends ChargedCard {

    public static final String ID = EchoMod.makeID(ThisEndsNow.class.getSimpleName());

    private static final CardTarget TARGET = CardTarget.ALL_ENEMY;

    public ThisEndsNow() {
        super(ID, TARGET, true);

        this.isMultiDamage = true;
        this.exhaust = true;
        this.isEthereal = true;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        super.use(p, m);
        addToBot(new VFXAction(new EchoOverclockEffect()));
        addToBot(new DamageAllEnemiesAction(p, this.multiDamage, this.damageTypeForTurn, AbstractGameAction.AttackEffect.NONE, true));
    }
}
