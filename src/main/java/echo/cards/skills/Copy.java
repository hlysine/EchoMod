package echo.cards.skills;

import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import echo.EchoMod;
import echo.actions.duplicate.DuplicateRandomPlayerAction;
import echo.cards.ChargedCard;
import echo.characters.Echo;
import echo.mechanics.duplicate.ChargedChecker;
import echo.subscribers.AfterCardUseSubscriber;

public class Copy extends ChargedCard implements AfterCardUseSubscriber {

    public static final String ID = EchoMod.makeID(Copy.class.getSimpleName());

    private static final CardTarget TARGET = CardTarget.SELF;

    public Copy() {
        super(ID, TARGET, true);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
    }

    @Override
    public void afterUse() {
        if (ChargedChecker.isCharged()) {
            addToBot(new DuplicateRandomPlayerAction(new AbstractPlayer.PlayerClass[]{Echo.Enums.ECHO, AbstractDungeon.player.chosenClass}, true));
        }
    }
}
