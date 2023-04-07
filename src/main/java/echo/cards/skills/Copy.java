package echo.cards.skills;

import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import echo.EchoMod;
import echo.actions.duplicate.DuplicateRandomPlayerAction;
import echo.cards.ChargedCard;
import echo.characters.Echo;
import echo.subscribers.AfterCardUseSubscriber;

public class Copy extends ChargedCard implements AfterCardUseSubscriber {

    public static final String ID = EchoMod.makeID(Copy.class.getSimpleName());

    private static final CardTarget TARGET = CardTarget.SELF;

    public Copy() {
        super(ID, TARGET, true);
    }

    @Override
    public void afterUse() {
        addToBot(new DuplicateRandomPlayerAction(new AbstractPlayer.PlayerClass[]{Echo.Enums.ECHO, AbstractDungeon.player.chosenClass}));
    }
}
