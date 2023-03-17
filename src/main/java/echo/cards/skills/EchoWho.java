package echo.cards.skills;

import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
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

public class EchoWho extends AbstractBaseCard implements AfterCardUseSubscriber {

    public static final String ID = EchoMod.makeID(EchoWho.class.getSimpleName());

    private static final CardTarget TARGET = CardTarget.SELF;

    private boolean isCharged = false;

    public EchoWho() {
        super(ID, TARGET);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        this.isCharged = ChargedChecker.isCharged();
        if (!this.isCharged) {
            addToBot(new MakeTempCardInHandAction(AbstractDungeon.returnTrulyRandomColorlessCardInCombat()));
        }
    }

    @Override
    public void afterUse() {
        if (this.isCharged) {
            addToBot(new DuplicateRandomPlayerAction(new AbstractPlayer.PlayerClass[]{AbstractDungeon.player.chosenClass, Echo.Enums.ECHO}, true));
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
