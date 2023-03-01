package echo.mechanics.duplicate;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.AbstractPower;
import echo.powers.UltimateChargePower;

public class ChargedChecker {
    public static boolean isCharged() {
        AbstractPower power = AbstractDungeon.player.getPower(UltimateChargePower.POWER_ID);
        return power != null && power.amount >= 10;
    }
}
