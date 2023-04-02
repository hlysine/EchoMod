package echo.cards;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import echo.EchoMod;
import echo.mechanics.focused.FocusedChecker;
import echo.patches.cards.CustomCardTags;

public abstract class FocusedCard extends AbstractBaseCard {

    private static final CardStrings focusedCardStrings = CardCrawlGame.languagePack.getCardStrings(EchoMod.makeID(FocusedCard.class.getSimpleName()));
    private final boolean enforcePlayability;

    public FocusedCard(String id, CardTarget target, boolean enforcePlayability) {
        super(id, target);
        this.enforcePlayability = enforcePlayability;
        this.tags.add(CustomCardTags.FOCUSED);
    }

    @Override
    public boolean canUse(AbstractPlayer p, AbstractMonster m) {
        boolean canUse = super.canUse(p, m);
        if (!canUse) return false;
        if (enforcePlayability) {
            if (!FocusedChecker.anyFocused()) {
                this.cantUseMessage = focusedCardStrings.EXTENDED_DESCRIPTION[0];
                return false;
            }
            if (m != null && !FocusedChecker.isFocused(m)) {
                this.cantUseMessage = focusedCardStrings.EXTENDED_DESCRIPTION[1];
                return false;
            }
        }
        return true;
    }

    @Override
    public void triggerOnGlowCheck() {
        if (FocusedChecker.anyFocused()) {
            this.glowColor = AbstractCard.GOLD_BORDER_GLOW_COLOR.cpy();
        } else {
            this.glowColor = AbstractCard.BLUE_BORDER_GLOW_COLOR.cpy();
        }
    }
}
