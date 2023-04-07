package echo.cards.skills;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import echo.EchoMod;
import echo.actions.duplicate.DuplicateRandomPlayerAction;
import echo.cards.ChargedCard;
import echo.characters.Echo;
import echo.powers.EvasiveCopyPower;
import echo.subscribers.AfterCardUseSubscriber;

public class EvasiveCopy extends ChargedCard implements AfterCardUseSubscriber {

    public static final String ID = EchoMod.makeID(EvasiveCopy.class.getSimpleName());

    private static final CardTarget TARGET = CardTarget.SELF;

    public EvasiveCopy() {
        super(ID, TARGET, true);
    }

    @Override
    public void afterUse() {
        AbstractPlayer p = AbstractDungeon.player;
        addToBot(new DuplicateRandomPlayerAction(new AbstractPlayer.PlayerClass[]{Echo.Enums.ECHO, AbstractDungeon.player.chosenClass}));
        addToBot(new ApplyPowerAction(p, p, new EvasiveCopyPower(p, magicNumber)));
    }
}
