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
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.city.BronzeAutomaton;
import com.megacrit.cardcrawl.monsters.city.Champ;
import com.megacrit.cardcrawl.monsters.city.GremlinLeader;
import com.megacrit.cardcrawl.monsters.city.Snecko;
import com.megacrit.cardcrawl.monsters.exordium.*;
import gremlin.patches.GremlinEnum;
import guardian.patches.GuardianEnum;
import slimebound.patches.SlimeboundEnum;
import sneckomod.TheSnecko;
import theHexaghost.TheHexaghost;

public class EnemyMapping {
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
}
