package echo.cards.skills;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;
import echo.EchoMod;
import echo.cards.AbstractBaseCard;

public class PowerBlock extends AbstractBaseCard {

    public static final String ID = EchoMod.makeID(PowerBlock.class.getSimpleName());

    private static final CardTarget TARGET = CardTarget.SELF;

    public PowerBlock() {
        super(ID, TARGET);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new DrawCardAction(magicNumber, new AbstractGameAction() {
            @Override
            public void update() {
                int totalCost = 0;
                for (AbstractCard card : DrawCardAction.drawnCards) {
                    if (card.freeToPlay()) continue;
                    if (card.costForTurn >= 0) {
                        totalCost += card.costForTurn;
                    } else if (card.costForTurn == -1) {
                        totalCost += EnergyPanel.getCurrentEnergy();
                    }
                }
                addToTop(new GainBlockAction(p, p, totalCost * magicNumber2));
                this.isDone = true;
            }
        }));
    }
}
