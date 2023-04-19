package echo.cards.attacks;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInDrawPileAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import echo.EchoMod;
import echo.cards.AbstractBaseCard;

public class MeleeCombo extends AbstractBaseCard {

    public static final String ID = EchoMod.makeID(MeleeCombo.class.getSimpleName());

    private static final CardTarget TARGET = CardTarget.ENEMY;

    public MeleeCombo() {
        super(ID, TARGET);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        UnlockTracker.markCardAsSeen(this.cardsToPreview.cardID);
        addToBot(new DamageAction(m, new DamageInfo(p, damage, damageTypeForTurn), AbstractGameAction.AttackEffect.BLUNT_LIGHT));
        addToBot(new MakeTempCardInDrawPileAction(this.cardsToPreview, magicNumber, true, true));
    }
}
