package echo.cards.attacks;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import echo.EchoMod;
import echo.cards.AbstractBaseCard;
import echo.effects.SfxStore;
import echo.effects.TriShotEffect;
import echo.util.RunnableAction;

public class TriShot extends AbstractBaseCard {

    public static final String ID = EchoMod.makeID(TriShot.class.getSimpleName());

    private static final CardTarget TARGET = CardTarget.ENEMY;

    public TriShot() {
        super(ID, TARGET);

        this.exhaust = true;
    }

    @Override
    public void applyPowers() {
        super.applyPowers();
        ensureNonZeroDamage();
    }

    @Override
    public void calculateCardDamage(AbstractMonster mo) {
        super.calculateCardDamage(mo);
        ensureNonZeroDamage();
    }

    private void ensureNonZeroDamage() {
        this.damage = Math.max(1, this.damage);
        if (this.multiDamage != null) {
            for (int i = 0; i < this.multiDamage.length; i++) {
                this.multiDamage[i] = Math.max(1, this.multiDamage[i]);
            }
        }
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new VFXAction(new TriShotEffect()));
        addToBot(new RunnableAction(() -> SfxStore.TRI_SHOT.play(0.1f)));
        for (int i = 0; i < magicNumber; i++) {
            addToBot(new DamageAction(m, new DamageInfo(p, damage, damageTypeForTurn), AbstractGameAction.AttackEffect.BLUNT_LIGHT, true));
        }
        addToBot(new DrawCardAction(magicNumber2));
    }
}
