package echo.cards.skills;

import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.vfx.ThoughtBubble;
import echo.EchoMod;
import echo.cards.AbstractBaseCard;
import echo.powers.FlightPower;
import echo.util.RunnableAction;

public class GroundCover extends AbstractBaseCard {

    public static final String ID = EchoMod.makeID(GroundCover.class.getSimpleName());

    private static final CardTarget TARGET = CardTarget.SELF;

    public GroundCover() {
        super(ID, TARGET);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new RunnableAction(() -> {
            if (p.powers.stream().noneMatch(pow -> pow instanceof FlightPower)) {
                addToBot(new GainBlockAction(p, block));
            } else {
                AbstractDungeon.effectList.add(new ThoughtBubble(AbstractDungeon.player.dialogX, AbstractDungeon.player.dialogY, 3.0F, cardStrings.EXTENDED_DESCRIPTION[0], true));
            }
        }));
    }
}
