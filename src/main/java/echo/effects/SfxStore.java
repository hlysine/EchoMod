package echo.effects;

import basemod.BaseMod;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import echo.EchoMod;

public enum SfxStore {
    NANO_BOOST_START("NANO_BOOST_START", "nano_boost_start.ogg");

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

    public void playA(float pitchAdjust) {
        CardCrawlGame.sound.playA(key, pitchAdjust);
    }

    public void play() {
        CardCrawlGame.sound.play(key);
    }

    public void play(float pitchVariation) {
        CardCrawlGame.sound.play(key, pitchVariation);
    }
}
