package echo.cards.skills;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import echo.EchoMod;
import echo.cards.AbstractBaseCard;
import echo.patches.cards.CustomCardTags;

public class BFS extends AbstractBaseCard {

    public static final String ID = EchoMod.makeID(BFS.class.getSimpleName());

    private static final CardTarget TARGET = CardTarget.NONE;

    public BFS() {
        super(ID, TARGET);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new DrawCardAction(this.magicNumber, new AbstractGameAction() {
            @Override
            public void update() {
                tickDuration();
                if (this.isDone) {
                    for (AbstractCard c : DrawCardAction.drawnCards) {
                        if (!c.tags.contains(CustomCardTags.STICK)) {
                            AbstractDungeon.player.hand.moveToDiscardPile(c);
                            c.triggerOnManualDiscard();
                            GameActionManager.incrementDiscard(false);
                        }
                    }
                }
            }
        }));
    }
}
