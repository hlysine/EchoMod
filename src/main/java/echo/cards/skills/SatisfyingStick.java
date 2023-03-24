package echo.cards.skills;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import echo.EchoMod;
import echo.cards.AbstractBaseCard;
import echo.patches.cards.CustomCardTags;
import echo.powers.StickyBombPower;

public class SatisfyingStick extends AbstractBaseCard {

    public static final String ID = EchoMod.makeID(SatisfyingStick.class.getSimpleName());

    private static final CardTarget TARGET = CardTarget.ENEMY;

    public SatisfyingStick() {
        super(ID, TARGET);

        this.tags.add(CustomCardTags.STICK);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        if (m.powers.stream().anyMatch(pow -> pow instanceof StickyBombPower)) {
            addToBot(new ApplyPowerAction(m, p, new StickyBombPower(m, magicNumber)));
        }
    }

    @Override
    public void triggerOnGlowCheck() {
        if (AbstractDungeon.getMonsters().monsters.stream().anyMatch(m -> m.powers.stream().anyMatch(pow -> pow instanceof StickyBombPower))) {
            this.glowColor = AbstractCard.GOLD_BORDER_GLOW_COLOR.cpy();
        } else {
            this.glowColor = AbstractCard.BLUE_BORDER_GLOW_COLOR.cpy();
        }
    }
}
