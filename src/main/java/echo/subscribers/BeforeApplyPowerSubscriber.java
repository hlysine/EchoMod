package echo.subscribers;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.AbstractPower;

public interface BeforeApplyPowerSubscriber {
    /**
     * Modify the power before it is applied
     *
     * @param powerToApply the power to apply
     * @param source       the source creature
     * @param target       the target creature
     * @return the modified power, return null to cancel the power application
     */
    AbstractPower beforeApplyPower(AbstractPower powerToApply, AbstractCreature source, AbstractCreature target);
}
