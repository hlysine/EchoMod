package echo.cards.attacks;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.vfx.ThoughtBubble;
import echo.EchoMod;
import echo.cards.AbstractBaseCard;
import echo.patches.cards.CustomCardTags;
import echo.powers.StickyBombPower;

public class StickySituation extends AbstractBaseCard {

    public static final String ID = EchoMod.makeID(StickySituation.class.getSimpleName());

    private static final CardTarget TARGET = CardTarget.ENEMY;

    public StickySituation() {
        super(ID, TARGET);

        this.tags.add(CustomCardTags.STICK);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new DamageAction(m, new DamageInfo(p, damage, damageTypeForTurn), AbstractGameAction.AttackEffect.SLASH_HORIZONTAL));
        int count = (int) m.powers.stream().filter(pow -> pow.type == AbstractPower.PowerType.DEBUFF).count();
        if (count > 0) {
            addToBot(new ApplyPowerAction(m, p, new StickyBombPower(m, count * magicNumber)));
        } else {
            AbstractDungeon.effectList.add(new ThoughtBubble(AbstractDungeon.player.dialogX, AbstractDungeon.player.dialogY, 3.0F, cardStrings.EXTENDED_DESCRIPTION[0], true));
        }
    }
}
