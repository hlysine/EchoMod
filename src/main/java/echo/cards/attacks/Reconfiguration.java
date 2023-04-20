package echo.cards.attacks;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.vfx.BorderLongFlashEffect;
import echo.EchoMod;
import echo.cards.AbstractBaseCard;

import java.util.Comparator;

public class Reconfiguration extends AbstractBaseCard {

    public static final String ID = EchoMod.makeID(Reconfiguration.class.getSimpleName());

    private static final CardTarget TARGET = CardTarget.ENEMY;

    public Reconfiguration() {
        super(ID, TARGET);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new DamageAction(m, new DamageInfo(p, damage, damageTypeForTurn), AbstractGameAction.AttackEffect.SLASH_HORIZONTAL));
        addToBot(new VFXAction(new BorderLongFlashEffect(EchoMod.ECHO_COLOR.cpy())));
        addToBot(new AbstractGameAction() {
            @Override
            public void update() {
                tickDuration();
                if (this.isDone) {
                    p.drawPile.group.sort(Comparator.<AbstractCard>comparingInt(c -> c.freeToPlay() ? 0 : (c.costForTurn < 0 ? 999 : c.costForTurn)).reversed());
                }
            }
        });
    }
}
