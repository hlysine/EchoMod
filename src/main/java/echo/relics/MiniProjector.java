package echo.relics;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import echo.EchoMod;
import echo.subscribers.RelicTransformSubscriber;

import java.util.ArrayList;
import java.util.List;

public class MiniProjector extends AbstractBaseRelic implements RelicTransformSubscriber {

    public static final String ID = EchoMod.makeID(MiniProjector.class.getSimpleName());

    private static final LandingSound LANDING_SOUND = LandingSound.FLAT;

    public MiniProjector() {
        super(ID, LANDING_SOUND);
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }

    @Override
    public List<AbstractRelic> transform() {
        List<AbstractRelic> relics = new ArrayList<>(3);
        for (int i = 0; i < 3; i++) {
            relics.add(AbstractDungeon.returnRandomScreenlessRelic(AbstractDungeon.returnRandomRelicTier()).makeCopy());
        }
        return relics;
    }
}
