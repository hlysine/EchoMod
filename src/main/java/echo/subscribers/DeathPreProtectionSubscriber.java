package echo.subscribers;

import com.megacrit.cardcrawl.cards.DamageInfo;

public interface DeathPreProtectionSubscriber {
    /**
     * Triggered when the creature (monster or player) is about to die.
     * This is triggered before protections such as Lizard Tail and Fairy In A Bottle take effect.
     *
     * @param damageInfo The {@link DamageInfo} that caused the death.
     * @param deathInfo  Additional info related to the death.
     * @param canDie     If false, other subscribers have already prevented death.
     * @return Whether to allow the creature to die.
     */
    boolean onDeathPreProtection(DamageInfo damageInfo, DeathInfo deathInfo, boolean canDie);

    class DeathInfo {
        public final int healthBeforeDamage;
        public final int finalHPDamage;

        public DeathInfo(int healthBeforeDamage, int finalHPDamage) {
            this.healthBeforeDamage = healthBeforeDamage;
            this.finalHPDamage = finalHPDamage;
        }
    }
}
