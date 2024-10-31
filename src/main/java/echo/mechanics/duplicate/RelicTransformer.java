package echo.mechanics.duplicate;

import basemod.BaseMod;
import basemod.ReflectionHacks;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.daily.mods.*;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ModHelper;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.random.Random;
import com.megacrit.cardcrawl.relics.*;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import echo.subscribers.RelicTransformSubscriber;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class RelicTransformer {

    private final Random rng;
    private final AbstractPlayer.PlayerClass fromClass;
    private final AbstractPlayer.PlayerClass toClass;
    public ArrayList<AbstractRelic> originalRelics;

    public RelicTransformer(Random rng, AbstractPlayer.PlayerClass fromClass, AbstractPlayer.PlayerClass toClass) {
        this.rng = new Random(rng.randomLong());
        this.fromClass = fromClass;
        this.toClass = toClass;
    }

    public ArrayList<AbstractRelic> transform(List<AbstractRelic> relics) {
        originalRelics = new ArrayList<>(relics);

        ArrayList<AbstractRelic> newRelics = new ArrayList<>();
        List<AbstractRelic> oldPool = getRelicPool(fromClass, true);
        List<AbstractRelic> newPool = getRelicPool(toClass, false);
        HashMap<String, AbstractRelic> sharedRelics = ReflectionHacks.getPrivateStatic(RelicLibrary.class, "sharedRelics");
        List<AbstractRelic> sharedPool = new ArrayList<>(sharedRelics.values());

        for (AbstractRelic relic : relics) {
            // do not transform starter relics because they are replaced by special logic
            if (relic.tier != AbstractRelic.RelicTier.STARTER) {
                if (relic instanceof RelicTransformSubscriber) {
                    RelicTransformSubscriber subscriber = (RelicTransformSubscriber) relic;
                    List<AbstractRelic> list = subscriber.transform();
                    if (list != null) {
                        newRelics.addAll(list);
                        continue;
                    }
                }
                if (oldPool.stream().anyMatch(r -> r.relicId.equals(relic.relicId))) {
                    List<AbstractRelic> options = newPool.stream().filter(r -> r.tier == relic.tier).collect(Collectors.toList());
                    if (options.size() > 0) {
                        AbstractRelic newRelic = options.get(rng.random(options.size() - 1));
                        newRelics.add(newRelic.makeCopy());
                        continue;
                    } else {
                        options = sharedPool.stream().filter(r -> r.tier == relic.tier).collect(Collectors.toList());
                        if (options.size() > 0) {
                            AbstractRelic newRelic = options.get(rng.random(options.size() - 1));
                            newRelics.add(newRelic.makeCopy());
                            continue;
                        }
                    }
                }
            }
            newRelics.add(relic);
        }

        int circletCount = 0;
        int redCircletCount = 0;
        for (AbstractRelic relic : newRelics) {
            if (relic instanceof Circlet) {
                circletCount += relic.counter;
            } else if (relic instanceof RedCirclet) {
                redCircletCount += relic.counter;
            }
        }
        newRelics.removeIf(r -> r instanceof Circlet || r instanceof RedCirclet);
        if (circletCount > 0) {
            Circlet circlet = new Circlet();
            circlet.counter = circletCount;
            newRelics.add(circlet);
        }
        if (redCircletCount > 0) {
            RedCirclet redCirclet = new RedCirclet();
            redCirclet.counter = redCircletCount;
            newRelics.add(redCirclet);
        }

        return newRelics;
    }

    /**
     * Get a list of obtainable relics for a character. Both base game and modded characters are supported.
     *
     * @param chosenClass   The character class to get relics for.
     * @param includeLocked Whether to include locked relics.
     * @return A list of relics that can be obtained.
     */
    public static List<AbstractRelic> getRelicPool(AbstractPlayer.PlayerClass chosenClass, boolean includeLocked) {
        // get the relic pool
        List<AbstractRelic> relicPool;
        if (BaseMod.isBaseGameCharacter(chosenClass)) {
            HashMap<String, AbstractRelic> relicMap = new HashMap<>();
            if (chosenClass == AbstractPlayer.PlayerClass.IRONCLAD) {
                relicMap = ReflectionHacks.getPrivateStatic(RelicLibrary.class, "redRelics");
            } else if (chosenClass == AbstractPlayer.PlayerClass.THE_SILENT) {
                relicMap = ReflectionHacks.getPrivateStatic(RelicLibrary.class, "greenRelics");
            } else if (chosenClass == AbstractPlayer.PlayerClass.DEFECT) {
                relicMap = ReflectionHacks.getPrivateStatic(RelicLibrary.class, "blueRelics");
            } else if (chosenClass == AbstractPlayer.PlayerClass.WATCHER) {
                relicMap = ReflectionHacks.getPrivateStatic(RelicLibrary.class, "purpleRelics");
            }
            relicPool = new ArrayList<>(relicMap.values());
        } else {
            relicPool = new ArrayList<>(BaseMod.getRelicsInCustomPool(BaseMod.findCharacter(chosenClass).getCardColor()).values());
        }

        // remove relics that conflict with daily mods
        List<String> relicsToRemove = new ArrayList<>();
        if (ModHelper.isModEnabled(Flight.ID) || ModHelper.isModEnabled(CertainFuture.ID)) {
            relicsToRemove.add(WingBoots.ID);
        }

        if (ModHelper.isModEnabled(Diverse.ID)) {
            relicsToRemove.add(PrismaticShard.ID);
        }

        if (ModHelper.isModEnabled(DeadlyEvents.ID)) {
            relicsToRemove.add(JuzuBracelet.ID);
        }

        if (ModHelper.isModEnabled(Hoarder.ID)) {
            relicsToRemove.add(SmilingMask.ID);
        }

        if (ModHelper.isModEnabled(Draft.ID) || ModHelper.isModEnabled(SealedDeck.ID) || ModHelper.isModEnabled(Shiny.ID) ||
                ModHelper.isModEnabled(Insanity.ID)) {
            relicsToRemove.add(PandorasBox.ID);
        }

        relicPool.removeIf(r -> relicsToRemove.contains(r.relicId));

        // remove locked relics
        if (!Settings.treatEverythingAsUnlocked() && !includeLocked)
            relicPool.removeIf(r -> UnlockTracker.isRelicLocked(r.relicId));

        return relicPool;
    }

    /**
     * Properly unequip a relic for those that don't have {@link AbstractRelic#onUnequip()} implemented.
     */
    public static void unequipRelic(AbstractRelic relic) {
        if (relic instanceof PotionBelt) {
            AbstractDungeon.player.potionSlots -= 2;
            while (AbstractDungeon.player.potions.size() > AbstractDungeon.player.potionSlots)
                AbstractDungeon.player.potions.remove(AbstractDungeon.player.potions.size() - 1);
        } else if (relic instanceof Strawberry) {
            AbstractDungeon.player.decreaseMaxHealth(7);
        } else if (relic instanceof Pear) {
            AbstractDungeon.player.decreaseMaxHealth(10);
        } else if (relic instanceof Mango) {
            AbstractDungeon.player.decreaseMaxHealth(14);
        } else if (relic instanceof OldCoin) {
            AbstractDungeon.player.loseGold(300);
        } else if (relic instanceof Waffle) {
            AbstractDungeon.player.decreaseMaxHealth(7);
        } else if (relic instanceof TinyHouse) {
            AbstractDungeon.player.decreaseMaxHealth(5);
            AbstractDungeon.player.loseGold(50);
        } else {
            relic.onUnequip();
        }
    }
}
