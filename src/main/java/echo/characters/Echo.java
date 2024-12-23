package echo.characters;

import basemod.abstracts.CustomEnergyOrb;
import basemod.abstracts.CustomPlayer;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.evacipated.cardcrawl.modthespire.lib.SpireEnum;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.EnergyManager;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ScreenShake;
import com.megacrit.cardcrawl.localization.CharacterStrings;
import com.megacrit.cardcrawl.screens.CharSelectInfo;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import echo.EchoMod;
import echo.cards.attacks.Strike;
import echo.cards.attacks.SwiftSwitch;
import echo.cards.skills.Copy;
import echo.cards.skills.Defend;
import echo.cards.skills.ShortFlight;
import echo.effects.SfxStore;
import echo.mechanics.duplicate.Duplicator;
import echo.relics.FlightCore;
import echo.util.NoAnimation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

import static echo.EchoMod.*;

//Wiki-page https://github.com/daviscook477/BaseMod/wiki/Custom-Characters
//and https://github.com/daviscook477/BaseMod/wiki/Migrating-to-5.0
//All text (starting description and loadout, anything labeled TEXT[]) can be found in DefaultMod-character-Strings.json in the resources

public class Echo extends CustomPlayer {
    public static final Logger logger = LogManager.getLogger(EchoMod.class.getName());

    // =============== CHARACTER ENUMERATORS =================
    // These are enums for your Characters color (both general color and for the card library) as well as
    // an enum for the name of the player class - IRONCLAD, THE_SILENT, DEFECT, YOUR_CLASS ...
    // These are all necessary for creating a character. If you want to find out where and how exactly they are used
    // in the basegame (for fun and education) Ctrl+click on the PlayerClass, CardColor and/or LibraryType below and go down the
    // Ctrl+click rabbit hole

    public static class Enums {
        @SpireEnum
        public static AbstractPlayer.PlayerClass ECHO;
        @SpireEnum(name = "ECHO_BLUE") // These two HAVE to have the same absolutely identical name.
        public static AbstractCard.CardColor CARD_COLOR;
        @SpireEnum(name = "ECHO_BLUE")
        @SuppressWarnings("unused")
        public static CardLibrary.LibraryType LIBRARY_COLOR;
    }

    // =============== CHARACTER ENUMERATORS  =================


    // =============== BASE STATS =================

    public static final int ENERGY_PER_TURN = 3;
    public static final int STARTING_HP = 77;
    public static final int MAX_HP = 77;
    public static final int STARTING_GOLD = 99;
    public static final int CARD_DRAW = 5;
    public static final int ORB_SLOTS = 0;

    // =============== /BASE STATS/ =================


    // =============== STRINGS =================

    private static final String ID = makeID("EchoCharacter");
    private static final CharacterStrings characterStrings = CardCrawlGame.languagePack.getCharacterString(ID);
    private static final String[] NAMES = characterStrings.NAMES;
    private static final String[] TEXT = characterStrings.TEXT;

    // =============== /STRINGS/ =================

    // Atlas and JSON files for the Animations
    public static final String SKELETON_ATLAS = "echoResources/images/character/spine/skeleton.atlas";
    public static final String SKELETON_JSON = "echoResources/images/character/spine/skeleton.json";
    public static final String CHARACTER_PORTRAIT = "echoResources/images/character/portrait.png";


    // =============== TEXTURES OF BIG ENERGY ORB ===============

    public static final String[] orbTextures = {
            "echoResources/images/energyOrb/layer1.png",
            "echoResources/images/energyOrb/layer2.png",
            "echoResources/images/energyOrb/layer3.png",
            "echoResources/images/energyOrb/layer4.png",
            "echoResources/images/energyOrb/layer5.png",
            "echoResources/images/energyOrb/layer6.png",
            "echoResources/images/energyOrb/layer7.png",
            "echoResources/images/energyOrb/layer8.png",
            "echoResources/images/energyOrb/layer9.png",
            "echoResources/images/energyOrb/layer10.png",
            "echoResources/images/energyOrb/layer1d.png",
            "echoResources/images/energyOrb/layer2d.png",
            "echoResources/images/energyOrb/layer3d.png",
            "echoResources/images/energyOrb/layer4d.png",
            "echoResources/images/energyOrb/layer5d.png",
            "echoResources/images/energyOrb/layer6d.png",
            "echoResources/images/energyOrb/layer7d.png",
            "echoResources/images/energyOrb/layer8d.png",
            "echoResources/images/energyOrb/layer9d.png",};

    // =============== /TEXTURES OF BIG ENERGY ORB/ ===============

    // =============== CHARACTER CLASS START =================

    public Echo(String name, PlayerClass setClass) {
        super(name, setClass,
                new EchoEnergyOrb(orbTextures,
                        "echoResources/images/energyOrb/vfx.png",
                        new float[]{
                                0, 0, -15f, 0, 6f, -12f, 14f, 0, 10f
                        }),
                new NoAnimation());


        // =============== TEXT BUBBLE LOCATION =================

        dialogX = (drawX + 0.0F * Settings.scale); // set location for text bubbles
        dialogY = (drawY + 220.0F * Settings.scale); // you can just copy these values

        // =============== /TEXT BUBBLE LOCATION/ =================


        // =============== TEXTURES, ENERGY, LOADOUT =================

        initializeClass(CHARACTER_PORTRAIT, // required call to load textures and setup energy/loadout.
                // I left these in DefaultMod.java (Ctrl+click them to see where they are, Ctrl+hover to see what they read.)
                ECHO_SHOULDER_2, // campfire pose
                ECHO_SHOULDER_1, // another campfire pose
                ECHO_CORPSE, // dead corpse
                getLoadout(), 0f, 0f, 250f, 300f, new EnergyManager(ENERGY_PER_TURN)); // energy manager

        // =============== /TEXTURES, ENERGY, LOADOUT/ =================


        // =============== ANIMATIONS =================

        loadAnimation(
                SKELETON_ATLAS,
                SKELETON_JSON,
                1.0f);
        this.atlas.dispose();
        this.atlas = null;
//        AnimationState.TrackEntry e = state.setAnimation(0, "animation", true);
//        e.setTime(e.getEndTime() * MathUtils.random());

        // =============== /ANIMATIONS/ =================
    }

    // =============== /CHARACTER CLASS END/ =================

    // Starting description and loadout
    @Override
    public CharSelectInfo getLoadout() {
        return new CharSelectInfo(
                NAMES[0], TEXT[0],
                STARTING_HP, MAX_HP, ORB_SLOTS, STARTING_GOLD, CARD_DRAW, this, getStartingRelics(),
                getStartingDeck(), false);
    }

    // Starting Deck
    @Override
    public ArrayList<String> getStartingDeck() {
        ArrayList<String> retVal = new ArrayList<>();

        logger.info("Begin loading starter Deck Strings");

        retVal.add(Strike.ID);
        retVal.add(Strike.ID);
        retVal.add(Strike.ID);
        retVal.add(Strike.ID);

        retVal.add(Defend.ID);
        retVal.add(Defend.ID);
        retVal.add(Defend.ID);
        retVal.add(Defend.ID);

        retVal.add(SwiftSwitch.ID);
        retVal.add(ShortFlight.ID);

        UnlockTracker.markCardAsSeen(Strike.ID);
        UnlockTracker.markCardAsSeen(Defend.ID);
        UnlockTracker.markCardAsSeen(SwiftSwitch.ID);
        UnlockTracker.markCardAsSeen(ShortFlight.ID);

        return retVal;
    }

    // Starting Relics
    public ArrayList<String> getStartingRelics() {
        ArrayList<String> retVal = new ArrayList<>();

        retVal.add(FlightCore.ID);

        // Mark relics as seen - makes it visible in the compendium immediately
        // If you don't have this it won't be visible in the compendium until you see them in game
        UnlockTracker.markRelicAsSeen(FlightCore.ID);

        return retVal;
    }

    @Override
    public ArrayList<AbstractCard> getCardPool(ArrayList<AbstractCard> tmpPool) {
        if (Duplicator.isDuplicating() && AbstractDungeon.player.chosenClass != Enums.ECHO) {
            return CardCrawlGame.characterManager.getCharacter(AbstractDungeon.player.chosenClass).getCardPool(tmpPool);
        }
        return super.getCardPool(tmpPool);
    }

    // character Select screen effect
    @Override
    public void doCharSelectScreenSelectEffect() {
        SfxStore.TRI_SHOT.playA(1f);
        CardCrawlGame.screenShake.shake(ScreenShake.ShakeIntensity.LOW, ScreenShake.ShakeDur.SHORT,
                false); // Screen Effect
    }

    // character Select on-button-press sound effect
    @Override
    public String getCustomModeCharacterButtonSoundKey() {
        return SfxStore.TRI_SHOT.getKey();
    }

    // Should return how much HP your maximum HP reduces by when starting a run at
    // Ascension 14 or higher. (ironclad loses 5, defect and silent lose 4 hp respectively)
    @Override
    public int getAscensionMaxHPLoss() {
        return 4;
    }

    // Should return the card color enum to be associated with your character.
    @Override
    public AbstractCard.CardColor getCardColor() {
        return Enums.CARD_COLOR;
    }

    // Should return a color object to be used to color the trail of moving cards
    @Override
    public Color getCardTrailColor() {
        return EchoMod.ECHO_COLOR.cpy();
    }

    // Should return a BitmapFont object that you can use to customize how your
    // energy is displayed from within the energy orb.
    @Override
    public BitmapFont getEnergyNumFont() {
        return FontHelper.energyNumFontBlue;
    }

    // Should return class name as it appears in run history screen.
    @Override
    public String getLocalizedCharacterName() {
        return NAMES[0];
    }

    //Which card should be obtainable from the Match and Keep event?
    @Override
    public AbstractCard getStartCardForEvent() {
        return new Copy();
    }

    // The class name as it appears next to your player name in-game
    @Override
    public String getTitle(AbstractPlayer.PlayerClass playerClass) {
        return NAMES[1];
    }

    // Should return a new instance of your character, sending name as its name parameter.
    @Override
    public AbstractPlayer newInstance() {
        return new Echo(name, chosenClass);
    }

    // Should return a Color object to be used to color the miniature card images in run history.
    @Override
    public Color getCardRenderColor() {
        return EchoMod.ECHO_COLOR.cpy();
    }

    // Should return a Color object to be used as screen tint effect when your
    // character attacks the heart.
    @Override
    public Color getSlashAttackColor() {
        return EchoMod.ECHO_COLOR.cpy();
    }

    // Should return an AttackEffect array of any size greater than 0. These effects
    // will be played in sequence as your character's finishing combo on the heart.
    // Attack effects are the same as used in DamageAction and the like.
    @Override
    public AbstractGameAction.AttackEffect[] getSpireHeartSlashEffect() {
        return new AbstractGameAction.AttackEffect[]{
                AbstractGameAction.AttackEffect.SMASH,
                AbstractGameAction.AttackEffect.SMASH,
                AbstractGameAction.AttackEffect.BLUNT_LIGHT,
                AbstractGameAction.AttackEffect.BLUNT_LIGHT,
                AbstractGameAction.AttackEffect.LIGHTNING};
    }

    // Should return a string containing what text is shown when your character is
    // about to attack the heart. For example, the defect is "NL You charge your
    // core to its maximum..."
    @Override
    public String getSpireHeartText() {
        return TEXT[1];
    }

    // The vampire events refer to the base game characters as "brother", "sister",
    // and "broken one" respectively.This method should return a String containing
    // the full text that will be displayed as the first screen of the vampires event.
    @Override
    public String getVampireText() {
        return TEXT[2];
    }

    public static class EchoEnergyOrb extends CustomEnergyOrb {
        public EchoEnergyOrb(String[] orbTexturePaths, String orbVfxPath, float[] layerSpeeds) {
            super(orbTexturePaths, orbVfxPath, layerSpeeds);
        }

        @Override
        public void updateOrb(int energyCount) {
            for (int i = 0; i < angles.length; i++) {
                angles[i] += Gdx.graphics.getDeltaTime() * layerSpeeds[i] / (energyCount == 0 ? 4f : 1f);
            }
        }
    }

}
