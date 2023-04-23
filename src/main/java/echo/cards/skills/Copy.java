package echo.cards.skills;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import echo.EchoMod;
import echo.actions.duplicate.DuplicateRandomPlayerAction;
import echo.cards.ChargedCard;
import echo.characters.Echo;
import echo.mechanics.duplicate.ChargedChecker;
import echo.powers.UltimateChargePower;
import echo.subscribers.AfterCardUseSubscriber;

public class Copy extends ChargedCard implements AfterCardUseSubscriber {

    public static final String ID = EchoMod.makeID(Copy.class.getSimpleName());

    private static final CardTarget TARGET = CardTarget.SELF;

    private boolean charged;

    public Copy() {
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
        AbstractPlayer p = AbstractDungeon.player;
        if (charged) {
            addToBot(new DuplicateRandomPlayerAction(new AbstractPlayer.PlayerClass[]{Echo.Enums.ECHO, p.chosenClass}));
        } else {
            addToBot(new ApplyPowerAction(p, p, new UltimateChargePower(p, magicNumber)));
        }
    }
}
