package echo.cards.skills;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import com.megacrit.cardcrawl.powers.WeakPower;
import echo.EchoMod;
import echo.cards.AbstractBaseCard;

public class SkillGap extends AbstractBaseCard {

    public static final String ID = EchoMod.makeID(SkillGap.class.getSimpleName());

    private static final CardTarget TARGET = CardTarget.ENEMY;

    public SkillGap() {
        super(ID, TARGET);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new ApplyPowerAction(m, p, new VulnerablePower(m, magicNumber, false)));
        addToBot(new ApplyPowerAction(m, p, new WeakPower(m, magicNumber, false)));
    }
}
