package echo.effects;

import basemod.BaseMod;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import echo.EchoMod;

public enum SfxStore {
    NANO_BOOST_APPLY("NANO_BOOST_APPLY", "nano_boost_apply.ogg"),
    STICKY_BOMB_APPLY("STICKY_BOMB_APPLY", "sticky_bomb_apply.ogg"),
    FLIGHT_APPLY("FLIGHT_APPLY", "flight_apply.ogg"),
    DUPLICATE_START("DUPLICATE_START", "duplicate_start.ogg"),
    DUPLICATE_END("DUPLICATE_END", "duplicate_end.ogg"),
    GENJI_ULTIMATE_START("GENJI_ULTIMATE_START", "genji_ultimate_start.ogg"),
    GENJI_ULTIMATE_FIRE("GENJI_ULTIMATE_FIRE", "genji_ultimate_fire.ogg"),
    SOJOURN_ULTIMATE_START("SOJOURN_ULTIMATE_START", "sojourn_ultimate_start.ogg"),
    SOJOURN_ULTIMATE_FIRE("SOJOURN_ULTIMATE_FIRE", "sojourn_ultimate_fire.ogg"),
    CASSIDY_ULTIMATE_START("CASSIDY_ULTIMATE_START", "cassidy_ultimate_start.ogg"),
    CASSIDY_ULTIMATE_FIRE("CASSIDY_ULTIMATE_FIRE", "cassidy_ultimate_fire.ogg"),
    TRI_SHOT("TRI_SHOT", "tri_shot.ogg");

    private final String key;
    private final String path;

    SfxStore(String key, String path) {
        this.key = EchoMod.makeID(key);
        this.path = EchoMod.makeSoundPath(path);
    }

    public static void initialize() {
        for (SfxStore value : SfxStore.values()) {
            BaseMod.addAudio(value.key, value.path);
        }
    }

    public String getKey() {
        return key;
    }

    public String getPath() {
        return path;
    }

    public void playA(float pitchAdjust) {
        CardCrawlGame.sound.playA(key, pitchAdjust);
    }

    public void play() {
        CardCrawlGame.sound.play(key);
    }

    public void play(float pitchVariation) {
        CardCrawlGame.sound.play(key, pitchVariation);
    }

    public void playV(float volumeMod) {
        CardCrawlGame.sound.playV(key, volumeMod);
    }

    public void playAV(float pitchAdjust, float volumeMod) {
        CardCrawlGame.sound.playAV(key, pitchAdjust, volumeMod);
    }
}
