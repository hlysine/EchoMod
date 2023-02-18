package echo.relics;

import basemod.abstracts.CustomRelic;
import echo.util.TextureLoader;
import hlysine.STSItemInfo.RelicInfo;

import static echo.EchoMod.*;

public abstract class AbstractBaseRelic extends CustomRelic {

    protected final RelicInfo info;

    public AbstractBaseRelic(String id, LandingSound sfx) {
        super(
                id,
                TextureLoader.getTexture(makeRelicPath(getRelicInfo(id).getImage())),
                TextureLoader.getTexture(makeRelicOutlinePath(getRelicInfo(id).getImage())),
                getRelicInfo(id).getRarity(),
                sfx
        );

        info = getRelicInfo(id);
    }
}
