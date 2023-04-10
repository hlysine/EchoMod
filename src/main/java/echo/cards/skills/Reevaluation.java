package echo.cards.skills;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.PlatedArmorPower;
import com.megacrit.cardcrawl.vfx.ThoughtBubble;
import echo.EchoMod;
import echo.cards.AbstractBaseCard;
import echo.util.RunnableAction;

public class Reevaluation extends AbstractBaseCard {

    public static final String ID = EchoMod.makeID(Reevaluation.class.getSimpleName());

    private static final CardTarget TARGET = CardTarget.SELF;

    public Reevaluation() {
        super(ID, TARGET);

        this.exhaust = true;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new RunnableAction(() -> {
            int unplayableCount = 0;
            nextCard:
            for (AbstractCard card : p.hand.group) {
                if (card.canUse(p, null)) continue;
                for (AbstractMonster mm : AbstractDungeon.getMonsters().monsters) {
                    if (card.canUse(p, mm)) continue nextCard;
                }
                unplayableCount++;
            }
            if (unplayableCount > 0) {
                addToBot(new ApplyPowerAction(p, p, new PlatedArmorPower(p, unplayableCount * magicNumber)));
            } else {
                AbstractDungeon.effectList.add(new ThoughtBubble(AbstractDungeon.player.dialogX, AbstractDungeon.player.dialogY, 3.0F, cardStrings.EXTENDED_DESCRIPTION[0], true));
            }
        }));
    }

    @Override
    public void upgrade() {
        if (!upgraded) {
            this.exhaust = false;
        }
        super.upgrade();
    }
}
