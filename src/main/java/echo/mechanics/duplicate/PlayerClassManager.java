package echo.mechanics.duplicate;

import automaton.AutomatonChar;
import champ.ChampChar;
import charbosses.bosses.Defect.CharBossDefect;
import charbosses.bosses.Hermit.CharBossHermit;
import charbosses.bosses.Ironclad.CharBossIronclad;
import charbosses.bosses.Silent.CharBossSilent;
import charbosses.bosses.Watcher.CharBossWatcher;
import com.evacipated.cardcrawl.modthespire.Loader;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.city.BronzeAutomaton;
import com.megacrit.cardcrawl.monsters.city.Champ;
import com.megacrit.cardcrawl.monsters.city.GremlinLeader;
import com.megacrit.cardcrawl.monsters.city.Snecko;
import com.megacrit.cardcrawl.monsters.exordium.*;
import com.megacrit.cardcrawl.random.Random;
import gremlin.patches.GremlinEnum;
import guardian.patches.GuardianEnum;
import slimebound.patches.SlimeboundEnum;
import sneckomod.TheSnecko;
import theHexaghost.TheHexaghost;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class PlayerClassManager {
    public static AbstractPlayer.PlayerClass getEnemyClass(AbstractMonster monster) {
        if (Loader.isModLoaded("downfall")) {
            if (monster instanceof BronzeAutomaton) {
                return AutomatonChar.Enums.THE_AUTOMATON;
            }
            if (monster instanceof Champ) {
                return ChampChar.Enums.THE_CHAMP;
            }
            if (monster instanceof GremlinLeader
                    || monster instanceof GremlinFat
                    || monster instanceof GremlinNob
                    || monster instanceof GremlinWizard
                    || monster instanceof GremlinThief
                    || monster instanceof GremlinTsundere
                    || monster instanceof GremlinWarrior) {
                return GremlinEnum.GREMLIN;
            }
            if (monster instanceof TheGuardian) {
                return GuardianEnum.GUARDIAN;
            }
            if (monster instanceof Hexaghost) {
                return TheHexaghost.Enums.THE_SPIRIT;
            }
            if (monster instanceof SlimeBoss
                    || monster instanceof ApologySlime
                    || monster instanceof AcidSlime_L
                    || monster instanceof AcidSlime_M
                    || monster instanceof AcidSlime_S
                    || monster instanceof SpikeSlime_L
                    || monster instanceof SpikeSlime_M
                    || monster instanceof SpikeSlime_S) {
                return SlimeboundEnum.SLIMEBOUND;
            }
            if (monster instanceof Snecko) {
                return TheSnecko.Enums.THE_SNECKO;
            }

            if (monster instanceof CharBossIronclad) {
                return AbstractPlayer.PlayerClass.IRONCLAD;
            }
            if (monster instanceof CharBossSilent) {
                return AbstractPlayer.PlayerClass.THE_SILENT;
            }
            if (monster instanceof CharBossDefect) {
                return AbstractPlayer.PlayerClass.DEFECT;
            }
            if (monster instanceof CharBossWatcher) {
                return AbstractPlayer.PlayerClass.WATCHER;
            }
            if (monster instanceof CharBossHermit) {
                return hermit.characters.hermit.Enums.HERMIT;
            }
        }

        return null;
    }

    /**
     * Get a list of player classes in random order.
     *
     * @param rng             the RNG to use
     * @param excludedClasses player classes that should be excluded from the list
     * @param count           the number of player classes to return, 0 to return all
     * @param preferEnemies   whether player classes corresponding to current enemies will be placed in front of the list
     * @return a list of player classes
     */
    public static List<AbstractPlayer.PlayerClass> getClassList(Random rng, AbstractPlayer.PlayerClass[] excludedClasses, int count, boolean preferEnemies) {
        List<AbstractPlayer.PlayerClass> classes = Arrays.stream(AbstractPlayer.PlayerClass.values())
                .filter(c -> Arrays.stream(excludedClasses).noneMatch(e -> e == c))
                .collect(Collectors.toList());

        List<AbstractPlayer.PlayerClass> choices = new ArrayList<>(count <= 0 ? 5 : count);

        if (preferEnemies) {
            List<AbstractPlayer.PlayerClass> enemies = new ArrayList<>();
            for (AbstractMonster monster : AbstractDungeon.getMonsters().monsters) {
                AbstractPlayer.PlayerClass enemyClass = getEnemyClass(monster);
                if (enemyClass != null) {
                    enemies.add(enemyClass);
                }
            }
            enemies.removeIf(c -> Arrays.stream(excludedClasses).anyMatch(e -> e == c));
            if (!enemies.isEmpty()) {
                choices.add(enemies.get(rng.random(enemies.size() - 1)));
            }
        }

        while (!classes.isEmpty() && (choices.size() < count || count <= 0)) {
            AbstractPlayer.PlayerClass c = classes.remove(rng.random(classes.size() - 1));
            if (!choices.contains(c)) {
                choices.add(c);
            }
        }

        return choices;
    }
}
