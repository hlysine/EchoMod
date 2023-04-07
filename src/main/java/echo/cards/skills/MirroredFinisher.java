package echo.cards.skills;

import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import echo.EchoMod;
import echo.actions.duplicate.DuplicateRandomPlayerAction;
import echo.cards.FocusedCard;
import echo.characters.Echo;
import echo.subscribers.AfterCardUseSubscriber;

public class MirroredFinisher extends FocusedCard implements AfterCardUseSubscriber {

    public static final String ID = EchoMod.makeID(MirroredFinisher.class.getSimpleName());

    private static final CardTarget TARGET = CardTarget.SELF;

    public MirroredFinisher() {
        super(ID, TARGET, true);

        this.exhaust = true;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
    }

    @Override
    public void afterUse() {
        addToBot(new DuplicateRandomPlayerAction(new AbstractPlayer.PlayerClass[]{Echo.Enums.ECHO, AbstractDungeon.player.chosenClass}));
    }
}
