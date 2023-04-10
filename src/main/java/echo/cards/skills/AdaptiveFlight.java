package echo.cards.skills;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.vfx.ThoughtBubble;
import echo.EchoMod;
import echo.cards.AbstractBaseCard;
import echo.powers.FlightPower;

public class AdaptiveFlight extends AbstractBaseCard {

    public static final String ID = EchoMod.makeID(AdaptiveFlight.class.getSimpleName());

    private static final CardTarget TARGET = CardTarget.SELF;

    public AdaptiveFlight() {
        super(ID, TARGET);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        int count = (int) p.powers.stream().filter(pow -> pow.type == AbstractPower.PowerType.BUFF).count();

        if (count > 0) {
            addToBot(new ApplyPowerAction(p, p, new FlightPower(p, count * magicNumber)));
        } else {
            AbstractDungeon.effectList.add(new ThoughtBubble(AbstractDungeon.player.dialogX, AbstractDungeon.player.dialogY, 3.0F, cardStrings.EXTENDED_DESCRIPTION[0], true));
        }
    }
}
