package echo.cards.skills;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.vfx.ThoughtBubble;
import echo.EchoMod;
import echo.cards.AbstractBaseCard;
import echo.patches.cards.CustomCardTags;
import echo.powers.FlightPower;
import echo.powers.StickyBombPower;
import echo.util.RunnableAction;

public class LongRangeTarget extends AbstractBaseCard {

    public static final String ID = EchoMod.makeID(LongRangeTarget.class.getSimpleName());

    private static final CardTarget TARGET = CardTarget.ENEMY;

    public LongRangeTarget() {
        super(ID, TARGET);

        this.tags.add(CustomCardTags.STICK);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new RunnableAction(() -> {
            if (p.powers.stream().anyMatch(pow -> pow instanceof FlightPower)) {
                addToBot(new ApplyPowerAction(m, p, new StickyBombPower(m, magicNumber)));
            } else {
                AbstractDungeon.effectList.add(new ThoughtBubble(AbstractDungeon.player.dialogX, AbstractDungeon.player.dialogY, 3.0F, cardStrings.EXTENDED_DESCRIPTION[0], true));
            }
        }));
    }
}
