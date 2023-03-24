package echo.mechanics.focused;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class FocusedChecker {
    public static boolean isFocused(AbstractMonster target) {
        return target.currentHealth <= target.maxHealth / 2;
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
