package echo.cards.attacks;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import echo.EchoMod;
import echo.cards.AbstractBaseCard;

public class ComboBurst extends AbstractBaseCard {

    public static final String ID = EchoMod.makeID(ComboBurst.class.getSimpleName());

    private static final CardTarget TARGET = CardTarget.ENEMY;

    public ComboBurst() {
        super(ID, TARGET);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        int triShotCount = 0;
        for (AbstractCard card : AbstractDungeon.player.exhaustPile.group) {
            if (card instanceof TriShot) {
                triShotCount++;
            }
        }

        this.baseDamage = triShotCount * this.magicNumber;
        calculateCardDamage(null);

        addToBot(new DamageAction(m, new DamageInfo(p, damage, damageTypeForTurn), AbstractGameAction.AttackEffect.BLUNT_HEAVY));
    }

    public void applyPowers() {
        int triShotCount = 0;
        for (AbstractCard card : AbstractDungeon.player.exhaustPile.group) {
            if (card instanceof TriShot) {
                triShotCount++;
            }
        }

        if (triShotCount > 0) {
            this.baseDamage = triShotCount * this.magicNumber;
            super.applyPowers();
            this.rawDescription = cardStrings.DESCRIPTION + cardStrings.EXTENDED_DESCRIPTION[0];
            initializeDescription();
        }
    }

    public void onMoveToDiscard() {
        this.rawDescription = cardStrings.DESCRIPTION;
        initializeDescription();
    }

    public void calculateCardDamage(AbstractMonster mo) {
        super.calculateCardDamage(mo);
        this.rawDescription = cardStrings.DESCRIPTION;
        this.rawDescription += cardStrings.EXTENDED_DESCRIPTION[0];
        initializeDescription();
    }
}
