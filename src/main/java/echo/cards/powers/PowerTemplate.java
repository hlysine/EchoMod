package echo.cards.powers;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.CardQueueItem;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import echo.EchoMod;
import echo.cards.AbstractBaseCard;
import javassist.CannotCompileException;
import javassist.expr.ExprEditor;
import javassist.expr.FieldAccess;

public class PowerTemplate extends AbstractBaseCard {

    public static final String ID = EchoMod.makeID(PowerTemplate.class.getSimpleName());

    private static final CardTarget TARGET = CardTarget.NONE;

    public PowerTemplate() {
        super(ID, TARGET);

        this.selfRetain = true;
        this.returnToHand = true;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        if (this.dontTriggerOnUseCard) {
            addToBot(new GainBlockAction(p, p, block));
        }
    }

    @Override
    public void triggerOnEndOfTurnForPlayingCard() {
        this.dontTriggerOnUseCard = true;
        AbstractDungeon.actionManager.cardQueue.add(new CardQueueItem(this, true));
    }

    @Override
    public boolean canUse(AbstractPlayer p, AbstractMonster m) {
        return false;
    }

    /**
     * Patch for UseCardAction allowing returnToHand to override the power purge mechanic for this specific card.
     */
    @SpirePatch(
            clz = UseCardAction.class,
            method = "update"
    )
    public static class UseCardActionPatch {
        public static ExprEditor Instrument() {
            return new ExprEditor() {
                @Override
                public void edit(FieldAccess f) throws CannotCompileException {
                    if (f.getClassName().equals("com.megacrit.cardcrawl.cards.AbstractCard") && f.getFieldName().equals("type")) {
                        f.replace("{ $_ = (this.targetCard instanceof echo.cards.powers.PowerTemplate && this.targetCard.returnToHand) ? com.megacrit.cardcrawl.cards.AbstractCard.CardType.SKILL : $proceed(); }");
                    }
                }
            };
        }
    }
}
