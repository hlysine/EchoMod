package echo.cards.attacks;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import echo.EchoMod;
import echo.actions.duplicate.DuplicateRandomPlayerAction;
import echo.cards.AbstractBaseCard;
import echo.characters.Echo;
import echo.subscribers.AfterCardUseSubscriber;

public class Strike extends AbstractBaseCard implements AfterCardUseSubscriber {

    public static final String ID = EchoMod.makeID(Strike.class.getSimpleName());

    private static final CardTarget TARGET = CardTarget.ENEMY;

    public Strike() {
        super(ID, TARGET);

        this.tags.add(CardTags.STARTER_STRIKE);
        this.tags.add(CardTags.STRIKE);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new DamageAction(m, new DamageInfo(p, damage, damageTypeForTurn), AbstractGameAction.AttackEffect.SLASH_DIAGONAL));
    }

    @Override
    public void afterUse() {
        addToBot(new DuplicateRandomPlayerAction(Echo.Enums.ECHO));
    }
}
