package echo.cards.skills;

import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import echo.EchoMod;
import echo.cards.FocusedCard;
import echo.mechanics.focused.FocusedChecker;

public class TargetedDefense extends FocusedCard {

    public static final String ID = EchoMod.makeID(TargetedDefense.class.getSimpleName());

    private static final CardTarget TARGET = CardTarget.SELF;

    public TargetedDefense() {
        super(ID, TARGET, false);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new GainBlockAction(p, p, block));
        if (FocusedChecker.anyFocused()) {
            addToBot(new GainBlockAction(p, p, block));
        }
    }
}
