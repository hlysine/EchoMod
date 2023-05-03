package echo.mechanics.focused;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import echo.subscribers.FocusedSubscriber;

import java.util.Optional;

public class FocusedChecker {

    public static boolean baseFocused(AbstractMonster target, float percentage) {
        return !target.isDeadOrEscaped() && target.currentHealth <= target.maxHealth * percentage;
    }

    public static boolean baseFocused(AbstractMonster target) {
        return baseFocused(target, 0.5f);
    }

    public static boolean isFocused(AbstractMonster target) {
        boolean allow = false;
        boolean block = false;
        for (AbstractCard card : AbstractDungeon.player.hand.group) {
            if (card instanceof FocusedSubscriber) {
                Optional<Boolean> override = ((FocusedSubscriber) card).overrideFocusedCheck(target);
                if (override.isPresent()) {
                    if (override.get()) {
                        allow = true;
                    } else {
                        block = true;
                    }
                }
            }
        }
        for (AbstractPower power : AbstractDungeon.player.powers) {
            if (power instanceof FocusedSubscriber) {
                Optional<Boolean> override = ((FocusedSubscriber) power).overrideFocusedCheck(target);
                if (override.isPresent()) {
                    if (override.get()) {
                        allow = true;
                    } else {
                        block = true;
                    }
                }
            }
        }
        for (AbstractRelic relic : AbstractDungeon.player.relics) {
            if (relic instanceof FocusedSubscriber) {
                Optional<Boolean> override = ((FocusedSubscriber) relic).overrideFocusedCheck(target);
                if (override.isPresent()) {
                    if (override.get()) {
                        allow = true;
                    } else {
                        block = true;
                    }
                }
            }
        }
        for (AbstractPower power : target.powers) {
            if (power instanceof FocusedSubscriber) {
                Optional<Boolean> override = ((FocusedSubscriber) power).overrideFocusedCheck(target);
                if (override.isPresent()) {
                    if (override.get()) {
                        allow = true;
                    } else {
                        block = true;
                    }
                }
            }
        }
        if (block) return false;
        return allow || baseFocused(target);
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
