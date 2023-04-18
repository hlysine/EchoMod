package echo.cards.skills;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import echo.EchoMod;
import echo.cards.AbstractBaseCard;
import echo.mechanics.duplicate.Duplicator;
import echo.patches.cards.CustomCardTags;

public class EchoedAggression extends AbstractBaseCard {

    public static final String ID = EchoMod.makeID(EchoedAggression.class.getSimpleName());

    private static final CardTarget TARGET = CardTarget.NONE;

    public EchoedAggression() {
        super(ID, TARGET);

        this.tags.add(CustomCardTags.CONSTANT);
        this.exhaust = true;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new AbstractGameAction() {
            @Override
            public void update() {
                processCardGroup(p.hand);
                processCardGroup(p.drawPile);
                processCardGroup(p.discardPile);
                processCardGroup(p.exhaustPile);

                this.isDone = true;
            }

            private void processCardGroup(CardGroup cardGroup) {
                for (AbstractCard c : cardGroup.group) {
                    if (c.cost > 0 && c.type == CardType.ATTACK) {
                        c.freeToPlayOnce = true;
                    }
                }
            }
        });
    }


    @Override
    public boolean canUse(AbstractPlayer p, AbstractMonster m) {
        boolean canUse = super.canUse(p, m);
        if (!canUse) return false;
        if (!Duplicator.isDuplicating()) {
            this.cantUseMessage = cardStrings.EXTENDED_DESCRIPTION[0];
            return false;
        }
        return true;
    }

    @Override
    public void triggerOnGlowCheck() {
        if (Duplicator.isDuplicating()) {
            this.glowColor = AbstractCard.GOLD_BORDER_GLOW_COLOR.cpy();
        } else {
            this.glowColor = AbstractCard.BLUE_BORDER_GLOW_COLOR.cpy();
        }
    }

    @Override
    public void upgrade() {
        if (!upgraded) {
            this.exhaust = false;
        }
        super.upgrade();
    }
}
