package echo.cards.skills;

import basemod.BaseMod;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInDiscardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.CorruptionPower;
import com.megacrit.cardcrawl.vfx.ThoughtBubble;
import echo.EchoMod;
import echo.cards.AbstractBaseCard;
import echo.cards.attacks.TriShot;
import echo.util.RunnableAction;

public class PeakPower extends AbstractBaseCard {

    public static final String ID = EchoMod.makeID(PeakPower.class.getSimpleName());

    private static final CardTarget TARGET = CardTarget.NONE;

    public PeakPower() {
        super(ID, TARGET);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new RunnableAction(() -> {
            if (p.hand.size() >= BaseMod.MAX_HAND_SIZE) {
                p.createHandIsFullDialog();
                return;
            }
            if (p.exhaustPile.findCardById(TriShot.ID) == null) {
                AbstractDungeon.effectList.add(new ThoughtBubble(p.dialogX, p.dialogY, 3.0F, cardStrings.EXTENDED_DESCRIPTION[0], true));
                return;
            }
            while (p.hand.size() < BaseMod.MAX_HAND_SIZE) {
                AbstractCard card = p.exhaustPile.findCardById(TriShot.ID);
                if (card == null) break;
                card.unfadeOut();
                p.hand.addToHand(card);
                if (AbstractDungeon.player.hasPower(CorruptionPower.POWER_ID) && card.type == AbstractCard.CardType.SKILL) {
                    card.setCostForTurn(-9);
                }
                p.exhaustPile.removeCard(card);
                card.unhover();
                card.fadingOut = false;
                card.stopGlowing();
            }
            p.hand.refreshHandLayout();
        }));
        addToBot(new MakeTempCardInDiscardAction(this.cardsToPreview, magicNumber));
    }
}
