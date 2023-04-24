package echo.cards.attacks;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.DamageAllEnemiesAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import echo.EchoMod;
import echo.cards.AbstractBaseCard;
import echo.effects.EchoHighNoonEffect;

public class EchoHighNoon extends AbstractBaseCard {

    public static final String ID = EchoMod.makeID(EchoHighNoon.class.getSimpleName());

    private static final CardTarget TARGET = CardTarget.ALL_ENEMY;

    public EchoHighNoon() {
        super(ID, TARGET);

        this.isMultiDamage = true;
        this.selfRetain = true;
        this.exhaust = true;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new VFXAction(new EchoHighNoonEffect()));
        addToBot(new DamageAllEnemiesAction(p, this.multiDamage, this.damageTypeForTurn, AbstractGameAction.AttackEffect.NONE, true));
    }

    @Override
    public void onRetained() {
        upgradeDamage(this.magicNumber);
    }
}
