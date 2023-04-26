package echo.cards.skills;

import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import echo.EchoMod;
import echo.actions.duplicate.DuplicateRandomPlayerAction;
import echo.cards.ChargedCard;
import echo.mechanics.duplicate.ChargedChecker;
import echo.mechanics.duplicate.Duplicator;
import echo.subscribers.AfterCardUseSubscriber;

public class FlexibleCopy extends ChargedCard implements AfterCardUseSubscriber {

    public static final String ID = EchoMod.makeID(FlexibleCopy.class.getSimpleName());

    private static final CardTarget TARGET = CardTarget.SELF;

    private boolean charged;

    public FlexibleCopy() {
        super(ID, TARGET, false);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        charged = ChargedChecker.isCharged();
        if (charged)
            super.use(p, m);
    }

    @Override
    public void afterUse() {
        if (charged) {
            addToBot(new DuplicateRandomPlayerAction(new AbstractPlayer.PlayerClass[]{Duplicator.getTrueClass(), AbstractDungeon.player.chosenClass}));
        } else {
            addToBot(new DrawCardAction(magicNumber));
        }
    }
}
