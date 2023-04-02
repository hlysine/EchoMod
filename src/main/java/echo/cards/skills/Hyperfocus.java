package echo.cards.skills;

import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import echo.EchoMod;
import echo.actions.SpecificDiscardPileToHandAction;
import echo.cards.FocusedCard;
import echo.mechanics.focused.FocusedChecker;
import echo.patches.cards.CustomCardTags;

public class Hyperfocus extends FocusedCard {

    public static final String ID = EchoMod.makeID(Hyperfocus.class.getSimpleName());

    private static final CardTarget TARGET = CardTarget.NONE;

    public Hyperfocus() {
        super(ID, TARGET, true);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        if (FocusedChecker.anyFocused()) {
            addToBot(new SpecificDiscardPileToHandAction(c -> c.tags.contains(CustomCardTags.FOCUSED)));
        }
    }
}
