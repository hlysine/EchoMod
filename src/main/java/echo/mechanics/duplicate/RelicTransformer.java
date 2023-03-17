package echo.mechanics.duplicate;

import basemod.BaseMod;
import basemod.ReflectionHacks;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.daily.mods.*;
import com.megacrit.cardcrawl.helpers.ModHelper;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.random.Random;
import com.megacrit.cardcrawl.relics.*;
import com.megacrit.cardcrawl.unlock.UnlockTracker;

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
        List<AbstractRelic> oldPool = getRelicPool(fromClass);
        List<AbstractRelic> newPool = getRelicPool(toClass);
        HashMap<String, AbstractRelic> sharedRelics = ReflectionHacks.getPrivateStatic(RelicLibrary.class, "sharedRelics");
        List<AbstractRelic> sharedPool = new ArrayList<>(sharedRelics.values());

        for (AbstractRelic relic : relics) {
            // do not transform starter relics because they are replaced by special logic
            if (relic.tier != AbstractRelic.RelicTier.STARTER && oldPool.stream().anyMatch(r -> r.relicId.equals(relic.relicId))) {
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
            newRelics.add(relic);
        }

        return newRelics;
    }

    /**
     * Get a list of obtainable relics for a character. Both base game and modded characters are supported.
     *
     * @param chosenClass The character class to get relics for.
     * @return A list of relics that can be obtained.
     */
    public static List<AbstractRelic> getRelicPool(AbstractPlayer.PlayerClass chosenClass) {
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
        if (!Settings.treatEverythingAsUnlocked()) {
            relicPool.removeIf(r -> UnlockTracker.isRelicLocked(r.relicId));
        }

        return relicPool;
    }
}
