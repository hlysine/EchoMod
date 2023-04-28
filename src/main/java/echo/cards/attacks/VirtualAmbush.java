package echo.cards.attacks;

import basemod.BaseMod;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import echo.EchoMod;
import echo.cards.AbstractBaseCard;
import echo.mechanics.duplicate.Duplicator;

public class VirtualAmbush extends AbstractBaseCard {

    public static final String ID = EchoMod.makeID(VirtualAmbush.class.getSimpleName());

    private static final CardTarget TARGET = CardTarget.ENEMY;

    public VirtualAmbush() {
        super(ID, TARGET);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        int foreignCount = countForeignCards();

        this.baseDamage = foreignCount * this.magicNumber;
        calculateCardDamage(null);

        addToBot(new DamageAction(m, new DamageInfo(p, damage, damageTypeForTurn), AbstractGameAction.AttackEffect.BLUNT_HEAVY));
    }

    @Override
    public void applyPowers() {
        int foreignCount = countForeignCards();

        if (foreignCount > 0) {
            this.baseDamage = foreignCount * this.magicNumber;
            super.applyPowers();
            this.rawDescription = cardStrings.DESCRIPTION + cardStrings.EXTENDED_DESCRIPTION[0];
            initializeDescription();
        }
    }

    private int countForeignCards() {
        int foreignCount = 0;
        CardColor self = BaseMod.findCharacter(Duplicator.getTrueClass()).getCardColor();
        for (AbstractCard card : AbstractDungeon.actionManager.cardsPlayedThisCombat) {
            if (card.color != self) {
                foreignCount++;
            }
        }
        return foreignCount;
    }

    @Override
    public void onMoveToDiscard() {
        this.rawDescription = cardStrings.DESCRIPTION;
        initializeDescription();
    }

    @Override
    public void calculateCardDamage(AbstractMonster mo) {
        super.calculateCardDamage(mo);
        this.rawDescription = cardStrings.DESCRIPTION;
        this.rawDescription += cardStrings.EXTENDED_DESCRIPTION[0];
        initializeDescription();
    }
}
