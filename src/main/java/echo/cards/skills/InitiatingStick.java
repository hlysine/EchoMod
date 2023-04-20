package echo.cards.skills;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.vfx.ThoughtBubble;
import echo.EchoMod;
import echo.cards.AbstractBaseCard;
import echo.patches.cards.CustomCardTags;
import echo.powers.StickyBombPower;
import echo.util.RunnableAction;

public class InitiatingStick extends AbstractBaseCard {

    public static final String ID = EchoMod.makeID(InitiatingStick.class.getSimpleName());

    private static final CardTarget TARGET = CardTarget.ENEMY;

    public InitiatingStick() {
        super(ID, TARGET);

        this.tags.add(CustomCardTags.STICK);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new RunnableAction(() -> {
            int powerAmount = StickyBombPower.getAmount(m);
            if (powerAmount < magicNumber) {
                addToBot(new ApplyPowerAction(m, p, new StickyBombPower(m, magicNumber - powerAmount)));
            } else {
                AbstractDungeon.effectList.add(new ThoughtBubble(AbstractDungeon.player.dialogX, AbstractDungeon.player.dialogY, 3.0F, cardStrings.EXTENDED_DESCRIPTION[0], true));
            }
        }));
    }
}
