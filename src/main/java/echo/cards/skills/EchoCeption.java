package echo.cards.skills;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import echo.EchoMod;
import echo.actions.duplicate.DuplicatePlayerAction;
import echo.cards.AbstractBaseCard;
import echo.characters.Echo;
import echo.mechanics.duplicate.ChargedChecker;
import echo.powers.UltimateChargePower;
import echo.subscribers.AfterCardUseSubscriber;

public class EchoCeption extends AbstractBaseCard implements AfterCardUseSubscriber {

    public static final String ID = EchoMod.makeID(EchoCeption.class.getSimpleName());

    private static final CardTarget TARGET = CardTarget.SELF;

    private boolean isCharged = false;

    public EchoCeption() {
        super(ID, TARGET);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        this.isCharged = ChargedChecker.isCharged();
        if (!this.isCharged) {
            addToBot(new ApplyPowerAction(p, p, new UltimateChargePower(p, magicNumber)));
        }
    }

    @Override
    public void afterUse() {
        if (this.isCharged) {
            addToBot(new DuplicatePlayerAction(Echo.Enums.ECHO, true));
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
