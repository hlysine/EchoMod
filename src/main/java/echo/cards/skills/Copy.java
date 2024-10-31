package echo.cards.skills;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import echo.EchoMod;
import echo.actions.duplicate.DuplicateRandomPlayerAction;
import echo.cards.ChargedCard;
import echo.mechanics.duplicate.ChargedChecker;
import echo.mechanics.duplicate.Duplicator;
import echo.powers.UltimateChargePower;
import echo.subscribers.AfterCardUseSubscriber;

public class Copy extends ChargedCard implements AfterCardUseSubscriber {

    public static final String ID = EchoMod.makeID(Copy.class.getSimpleName());

    private static final CardTarget TARGET = CardTarget.SELF;

    public Copy() {
        super(ID, TARGET, false);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new ApplyPowerAction(p, p, new UltimateChargePower(p, magicNumber)));
    }

    @Override
    public void triggerOnGlowCheck() {
        if (ChargedChecker.isCharged() || ChargedChecker.baseCharged(10 - magicNumber)) {
            this.glowColor = AbstractCard.GOLD_BORDER_GLOW_COLOR.cpy();
        } else {
            this.glowColor = AbstractCard.BLUE_BORDER_GLOW_COLOR.cpy();
        }
    }

    @Override
    public void afterUse() {
        AbstractPlayer p = AbstractDungeon.player;
        if (ChargedChecker.isCharged()) {
            super.use(p, null);
            addToBot(new DuplicateRandomPlayerAction(new AbstractPlayer.PlayerClass[]{Duplicator.getTrueClass(), p.chosenClass}));
        }
    }
}
