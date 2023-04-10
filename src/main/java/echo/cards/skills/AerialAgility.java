package echo.cards.skills;

import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import echo.EchoMod;
import echo.cards.AbstractBaseCard;
import echo.powers.FlightPower;

public class AerialAgility extends AbstractBaseCard {

    public static final String ID = EchoMod.makeID(AerialAgility.class.getSimpleName());

    private static final CardTarget TARGET = CardTarget.SELF;

    public AerialAgility() {
        super(ID, TARGET);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        int flightCount = 0;
        for (AbstractPower power : p.powers) {
            if (power instanceof FlightPower) {
                flightCount += power.amount;
            }
        }

        this.baseBlock = flightCount * this.magicNumber;
        calculateCardDamage(null);

        if (block > 0)
            addToBot(new GainBlockAction(p, block));
    }

    @Override
    public void applyPowers() {
        int flightCount = 0;
        for (AbstractPower power : AbstractDungeon.player.powers) {
            if (power instanceof FlightPower) {
                flightCount += power.amount;
            }
        }

        if (flightCount > 0) {
            this.baseBlock = flightCount * this.magicNumber;
            super.applyPowers();
            this.rawDescription = cardStrings.DESCRIPTION + cardStrings.EXTENDED_DESCRIPTION[0];
            initializeDescription();
        }
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
        if (block > 0)
            this.rawDescription += cardStrings.EXTENDED_DESCRIPTION[0];
        initializeDescription();
    }
}
