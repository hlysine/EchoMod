package echo.cards.skills;

import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import echo.EchoMod;
import echo.actions.duplicate.DuplicateRandomPlayerAction;
import echo.cards.AbstractBaseCard;
import echo.characters.Echo;
import echo.mechanics.duplicate.ChargedChecker;
import echo.subscribers.AfterCardUseSubscriber;

public class FlexibleCopy extends AbstractBaseCard implements AfterCardUseSubscriber {

    public static final String ID = EchoMod.makeID(FlexibleCopy.class.getSimpleName());

    private static final CardTarget TARGET = CardTarget.SELF;

    public FlexibleCopy() {
        super(ID, TARGET);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
    }

    @Override
    public void afterUse() {
        if (ChargedChecker.isCharged()) {
            addToBot(new DuplicateRandomPlayerAction(new AbstractPlayer.PlayerClass[]{Echo.Enums.ECHO, AbstractDungeon.player.chosenClass}, true));
        } else {
            addToBot(new DrawCardAction(magicNumber));
        }
    }

    @Override
    public void triggerOnGlowCheck() {
        if (ChargedChecker.isCharged()) {
            this.glowColor = AbstractCard.GOLD_BORDER_GLOW_COLOR.cpy();
        } else {
            this.glowColor = AbstractCard.BLUE_BORDER_GLOW_COLOR.cpy();
        }
    }
}
