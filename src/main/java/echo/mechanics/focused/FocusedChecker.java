package echo.mechanics.focused;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import echo.subscribers.FocusedSubscriber;

public class FocusedChecker {
    public static boolean isFocused(AbstractMonster target) {
        boolean isFocused = false;
        for (AbstractPower power : AbstractDungeon.player.powers) {
            if (power instanceof FocusedSubscriber) {
                if (((FocusedSubscriber) power).overrideFocusedCheck(target)) {
                    isFocused = true;
                    break;
                }
            }
        }
        for (AbstractRelic relic : AbstractDungeon.player.relics) {
            if (relic instanceof FocusedSubscriber) {
                if (((FocusedSubscriber) relic).overrideFocusedCheck(target)) {
                    isFocused = true;
                    break;
                }
            }
        }
        for (AbstractPower power : target.powers) {
            if (power instanceof FocusedSubscriber) {
                if (((FocusedSubscriber) power).overrideFocusedCheck(target)) {
                    isFocused = true;
                    break;
                }
            }
        }
        return isFocused || (!target.isDeadOrEscaped() && target.currentHealth <= target.maxHealth / 2);
    }

    public static boolean anyFocused() {
        for (AbstractMonster target : AbstractDungeon.getMonsters().monsters) {
            if (isFocused(target)) {
                return true;
            }
        }
        return false;
    }
}
