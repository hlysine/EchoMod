package echo.cards;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import echo.EchoMod;
import echo.mechanics.duplicate.ChargedChecker;
import echo.patches.cards.CustomCardTags;

public abstract class ChargedCard extends AbstractBaseCard {

    private static final CardStrings chargedCardStrings = CardCrawlGame.languagePack.getCardStrings(EchoMod.makeID(ChargedCard.class.getSimpleName()));
    private final boolean enforcePlayability;

    public ChargedCard(String id, CardTarget target, boolean enforcePlayability) {
        super(id, target);
        this.enforcePlayability = enforcePlayability;
        this.tags.add(CustomCardTags.CHARGED);
    }

    @Override
    public boolean canUse(AbstractPlayer p, AbstractMonster m) {
        boolean canUse = super.canUse(p, m);
        if (!canUse) return false;
        if (enforcePlayability && !ChargedChecker.isCharged()) {
            this.cantUseMessage = chargedCardStrings.EXTENDED_DESCRIPTION[0];
            return false;
        }
        return true;
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
