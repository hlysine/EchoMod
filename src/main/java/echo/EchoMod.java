package echo;

import basemod.AutoAdd;
import basemod.BaseMod;
import basemod.ModLabeledToggleButton;
import basemod.ModPanel;
import basemod.abstracts.CustomRelic;
import basemod.interfaces.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.evacipated.cardcrawl.mod.stslib.Keyword;
import com.evacipated.cardcrawl.modthespire.Loader;
import com.evacipated.cardcrawl.modthespire.lib.SpireConfig;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.google.gson.Gson;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.CardHelper;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.localization.*;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import echo.cards.AbstractBaseCard;
import echo.characters.Echo;
import echo.effects.SfxStore;
import echo.patches.metrics.DevCommandsMetricPatch;
import echo.potions.ChargedBottle;
import echo.powers.UltimateChargePower;
import echo.relics.AbstractBaseRelic;
import echo.util.ModIdCheck;
import echo.util.TextureLoader;
import echo.variables.CustomVariable;
import echo.variables.MagicNumber2Variable;
import hlysine.STSItemInfo.*;
import javassist.ClassPool;
import javassist.CtClass;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@SpireInitializer
public class EchoMod implements
        EditCardsSubscriber,
        EditRelicsSubscriber,
        EditStringsSubscriber,
        EditKeywordsSubscriber,
        EditCharactersSubscriber,
        PostInitializeSubscriber,
        AddAudioSubscriber {

    public static final Logger logger = LogManager.getLogger(EchoMod.class.getName());
    private static String modID;

    //This is for the in-game mod settings panel.
    private static final String MOD_NAME = "Echo";
    private static final String AUTHOR = "Lysine";
    private static final String DESCRIPTION = "A character mod for Echo.";

    // Mod-settings settings. This is if you want an on/off savable button
    public static Properties defaultSettings = new Properties();
    public static final String REDUCED_FULLSCREEN_EFFECTS_SETTING = "reducedFullscreenEffects";
    public static boolean reducedFullscreenEffects = false;

    // Character Color
    public static final Color ECHO_COLOR = CardHelper.getColor(106, 110, 247);

    // Card backgrounds - The actual rectangular card.
    private static final String CARD_BG_ATTACK = "echoResources/images/cardui/bg_attack_512_blue.png";
    private static final String CARD_BG_SKILL = "echoResources/images/cardui/bg_skill_512_blue.png";
    private static final String CARD_BG_POWER = "echoResources/images/cardui/bg_power_512_blue.png";

    private static final String ENERGY_ORB_SMALL = "echoResources/images/cardui/small_energy_icon.png";
    private static final String ENERGY_ORB_CARD_CORNER = "echoResources/images/cardui/card_corner_energy.png";
    private static final String ENERGY_ORB_LARGE = "echoResources/images/cardui/large_energy_icon.png";

    private static final String CARD_BG_ATTACK_LARGE = "echoResources/images/cardui/bg_attack_1024_blue.png";
    private static final String CARD_BG_SKILL_LARGE = "echoResources/images/cardui/bg_skill_1024_blue.png";
    private static final String CARD_BG_POWER_LARGE = "echoResources/images/cardui/bg_power_1024_blue.png";

    // Character assets
    private static final String ECHO_BUTTON = "echoResources/images/characterSelect/button.png";
    private static final String ECHO_PORTRAIT = "echoResources/images/characterSelect/background.png";
    public static final String ECHO_SHOULDER_1 = "echoResources/images/character/shoulder.png";
    public static final String ECHO_SHOULDER_2 = "echoResources/images/character/shoulder2.png";
    public static final String ECHO_CORPSE = "echoResources/images/character/corpse.png";

    //Mod Badge - A small icon that appears in the mod settings menu.
    public static final String BADGE_IMAGE = "echoResources/images/badge.png";

    private static CardInfoRepository cardInfoRepository;
    private static RelicInfoRepository relicInfoRepository;

    public static CardInfo getCardInfo(String cardId) {
        return cardInfoRepository.getCardInfo(cardId);
    }

    public static RelicInfo getRelicInfo(String relicId) {
        return relicInfoRepository.getRelicInfo(relicId);
    }

    public static String makeCardPath(String resourcePath) {
        return getModID() + "Resources/images/cards/" + resourcePath;
    }

    public static String makeRelicPath(String resourcePath) {
        return getModID() + "Resources/images/relics/" + resourcePath;
    }

    public static String makeRelicOutlinePath(String resourcePath) {
        return getModID() + "Resources/images/relics/outline/" + resourcePath;
    }

    public static String makeEffectPath(String resourcePath) {
        return getModID() + "Resources/images/effects/" + resourcePath;
    }

    public static String makeOrbPath(String resourcePath) {
        return getModID() + "Resources/images/orbs/" + resourcePath;
    }

    public static String makePowerPath(String resourcePath) {
        return getModID() + "Resources/images/powers/" + resourcePath;
    }

    public static String makeEventPath(String resourcePath) {
        return getModID() + "Resources/images/events/" + resourcePath;
    }

    public static String makeUiPath(String resourcePath) {
        return getModID() + "Resources/images/ui/" + resourcePath;
    }

    public static String makeShaderPath(String resourcePath) {
        return getModID() + "Resources/shaders/" + resourcePath;
    }

    public static String makeSoundPath(String resourcePath) {
        return getModID() + "Resources/sounds/" + resourcePath;
    }

    public static SpireConfig getConfig() throws IOException {
        return new SpireConfig(modID, modID + "Config", defaultSettings);
    }

    public EchoMod() {
        logger.info("Subscribe to BaseMod hooks");

        BaseMod.subscribe(this);

        setModID("echo");

        logger.info("Done subscribing");

        logger.info("Creating the color " + Echo.Enums.CARD_COLOR.toString());

        BaseMod.addColor(Echo.Enums.CARD_COLOR,
                ECHO_COLOR,
                ECHO_COLOR,
                ECHO_COLOR,
                ECHO_COLOR,
                ECHO_COLOR,
                ECHO_COLOR,
                ECHO_COLOR,
                CARD_BG_ATTACK,
                CARD_BG_SKILL,
                CARD_BG_POWER,
                ENERGY_ORB_CARD_CORNER,
                CARD_BG_ATTACK_LARGE,
                CARD_BG_SKILL_LARGE,
                CARD_BG_POWER_LARGE,
                ENERGY_ORB_LARGE,
                ENERGY_ORB_SMALL);

        logger.info("Done creating the color");

        logger.info("Adding mod settings");
        // This loads the mod settings.
        defaultSettings.setProperty(REDUCED_FULLSCREEN_EFFECTS_SETTING, "FALSE");
        try {
            SpireConfig config = getConfig();
            config.load();
            reducedFullscreenEffects = config.getBool(REDUCED_FULLSCREEN_EFFECTS_SETTING);
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.info("Done adding mod settings");
    }

    public static void setModID(String ID) {
        Gson coolG = new Gson();
        InputStream in = EchoMod.class.getResourceAsStream("/IDCheckStringsDONT-EDIT-AT-ALL.json");
        assert in != null;
        ModIdCheck EXCEPTION_STRINGS = coolG.fromJson(new InputStreamReader(in, StandardCharsets.UTF_8), ModIdCheck.class);
        logger.info("You are attempting to set your mod ID as: " + ID);
        if (ID.equals(EXCEPTION_STRINGS.DEFAULTID)) {
            throw new RuntimeException(EXCEPTION_STRINGS.EXCEPTION);
        } else if (ID.equals(EXCEPTION_STRINGS.DEVID)) {
            modID = EXCEPTION_STRINGS.DEFAULTID;
        } else {
            modID = ID;
        }
        logger.info("Success! ID is " + modID);
    }

    public static String getModID() {
        return modID;
    }

    private static void pathCheck() {
        Gson coolG = new Gson();
        InputStream in = EchoMod.class.getResourceAsStream("/IDCheckStringsDONT-EDIT-AT-ALL.json");
        assert in != null;
        ModIdCheck EXCEPTION_STRINGS = coolG.fromJson(new InputStreamReader(in, StandardCharsets.UTF_8), ModIdCheck.class);
        String packageName = EchoMod.class.getPackage().getName();
        FileHandle resourcePathExists = Gdx.files.internal(getModID() + "Resources");
        if (!modID.equals(EXCEPTION_STRINGS.DEVID)) {
            if (!packageName.equals(getModID())) {
                throw new RuntimeException(EXCEPTION_STRINGS.PACKAGE_EXCEPTION + getModID());
            }
            if (!resourcePathExists.exists()) {
                throw new RuntimeException(EXCEPTION_STRINGS.RESOURCE_FOLDER_EXCEPTION + getModID() + "Resources");
            }
        }
    }

    public static void initialize() {
        logger.info("========================= Initializing Echo Mod. =========================");
        @SuppressWarnings("unused") EchoMod echoMod = new EchoMod();
        logger.info("========================= /Echo Mod Initialized./ =========================");
    }

    @Override
    public void receiveEditCharacters() {
        logger.info("Beginning to edit characters. " + "Add " + Echo.Enums.ECHO.toString());

        BaseMod.addCharacter(new Echo("Echo", Echo.Enums.ECHO),
                ECHO_BUTTON, ECHO_PORTRAIT, Echo.Enums.ECHO);

        receiveEditPotions();
        logger.info("Added " + Echo.Enums.ECHO.toString());
    }

    @Override
    public void receivePostInitialize() {
        logger.info("Loading badge image and mod options");

        Texture badgeTexture = TextureLoader.getTexture(BADGE_IMAGE);

        // Create the Mod Menu
        ModPanel settingsPanel = new ModPanel();

        UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(makeID("SettingsPanel"));

        // Create the on/off button:
        ModLabeledToggleButton enableNormalsButton = new ModLabeledToggleButton(uiStrings.TEXT[0],
                350.0f, 700.0f, Settings.CREAM_COLOR, FontHelper.charDescFont,
                reducedFullscreenEffects,
                settingsPanel,
                (label) -> {
                },
                (button) -> { // on click
                    reducedFullscreenEffects = button.enabled;
                    try {
                        SpireConfig config = getConfig();
                        config.setBool(REDUCED_FULLSCREEN_EFFECTS_SETTING, reducedFullscreenEffects);
                        config.save();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });

        settingsPanel.addUIElement(enableNormalsButton);

        BaseMod.registerModBadge(badgeTexture, MOD_NAME, AUTHOR, DESCRIPTION, settingsPanel);

        BaseMod.addSaveField(EchoMod.makeID(DevCommandsMetricPatch.DevCommandsMetricSavable.class.getSimpleName()), new DevCommandsMetricPatch.DevCommandsMetricSavable());

        logger.info("Done loading badge image and mod options");
    }

    @Override
    public void receiveAddAudio() {
        SfxStore.initialize();
    }

    public void receiveEditPotions() {
        logger.info("Beginning to edit potions");

        BaseMod.addPotion(ChargedBottle.class,
                ChargedBottle.LIQUID_COLOR,
                ChargedBottle.HYBRID_COLOR,
                ChargedBottle.SPOTS_COLOR,
                ChargedBottle.POTION_ID);

        logger.info("Done editing potions");
    }

    @Override
    public void receiveEditRelics() {
        logger.info("Loading card info");

        relicInfoRepository = new RelicInfoRepository(
                Objects.requireNonNull(EchoMod.class.getResourceAsStream("/" + getModID() + "Resources/Relic-Info.json"))
        );

        logger.info("Adding relics");

        new AutoAdd("EchoMod")
                .packageFilter(AbstractBaseRelic.class)
                .any(CustomRelic.class, (info, relic) -> {
                    if (!info.ignore) {
                        BaseMod.addRelicToCustomPool(relic, Echo.Enums.CARD_COLOR);
                        if (info.seen) {
                            UnlockTracker.markRelicAsSeen(relic.relicId);
                        }
                    }
                });

        logger.info("Done adding relics!");

        logger.info("Adding powers");

        Collection<CtClass> classes = new AutoAdd("EchoMod")
                .packageFilter(UltimateChargePower.class)
                .findClasses(AbstractPower.class);
        ClassPool classPool = Loader.getClassPool();
        for (CtClass ctClass : classes) {
            if (ctClass.hasAnnotation(AutoAdd.Ignore.class)) continue;
            try {
                Class<? extends AbstractPower> clazz = (Class<? extends AbstractPower>) classPool.getClassLoader().loadClass(ctClass.getName());
                BaseMod.addPower(clazz, (String) clazz.getField("POWER_ID").get(null));
            } catch (NoSuchFieldException | IllegalAccessException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

        logger.info("Done adding powers!");
    }

    @Override
    public void receiveEditCards() {
        pathCheck();

        List<CustomVariable> customVariables = new ArrayList<>();
        customVariables.add(new MagicNumber2Variable());

        logger.info("Loading card info");

        cardInfoRepository = new CardInfoRepository(
                EchoMod.class.getResourceAsStream("/" + getModID() + "Resources/Card-Info.json"),
                customVariables.stream().map(variable -> (ValueHandler) variable).collect(Collectors.toList())
        );

        logger.info("Adding variables");

        for (CustomVariable variable : customVariables) {
            BaseMod.addDynamicVariable(variable);
        }

        logger.info("Adding cards");

        new AutoAdd("EchoMod")
                .packageFilter(AbstractBaseCard.class)
                .setDefaultSeen(false)
                .cards();

        logger.info("Done adding cards!");
    }

    @Override
    public void receiveEditStrings() {
        logger.info("Beginning to edit strings for mod with ID: " + getModID());

        BaseMod.loadCustomStringsFile(CardStrings.class,
                getModID() + "Resources/localization/eng/EchoMod-Card-Strings.json");

        BaseMod.loadCustomStringsFile(PowerStrings.class,
                getModID() + "Resources/localization/eng/EchoMod-Power-Strings.json");

        BaseMod.loadCustomStringsFile(RelicStrings.class,
                getModID() + "Resources/localization/eng/EchoMod-Relic-Strings.json");

        BaseMod.loadCustomStringsFile(EventStrings.class,
                getModID() + "Resources/localization/eng/EchoMod-Event-Strings.json");

        BaseMod.loadCustomStringsFile(PotionStrings.class,
                getModID() + "Resources/localization/eng/EchoMod-Potion-Strings.json");

        BaseMod.loadCustomStringsFile(CharacterStrings.class,
                getModID() + "Resources/localization/eng/EchoMod-Character-Strings.json");

        BaseMod.loadCustomStringsFile(OrbStrings.class,
                getModID() + "Resources/localization/eng/EchoMod-Orb-Strings.json");

        BaseMod.loadCustomStringsFile(TutorialStrings.class,
                getModID() + "Resources/localization/eng/EchoMod-Tutorial-Strings.json");

        BaseMod.loadCustomStringsFile(MonsterStrings.class,
                getModID() + "Resources/localization/eng/EchoMod-Monster-Strings.json");

        BaseMod.loadCustomStringsFile(UIStrings.class,
                getModID() + "Resources/localization/eng/EchoMod-UI-Strings.json");

        logger.info("Done editing strings");
    }

    @Override
    public void receiveEditKeywords() {
        Gson gson = new Gson();
        String json = Gdx.files.internal(getModID() + "Resources/localization/eng/EchoMod-Keyword-Strings.json").readString(String.valueOf(StandardCharsets.UTF_8));
        com.evacipated.cardcrawl.mod.stslib.Keyword[] keywords = gson.fromJson(json, com.evacipated.cardcrawl.mod.stslib.Keyword[].class);

        if (keywords != null) {
            for (Keyword keyword : keywords) {
                BaseMod.addKeyword(getModID().toLowerCase(), keyword.PROPER_NAME, keyword.NAMES, keyword.DESCRIPTION);
            }
        }
    }

    public static String makeID(String idText) {
        return getModID() + ":" + idText;
    }
}
