package echo.mechanics.duplicate;

import basemod.ReflectionHacks;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.evacipated.cardcrawl.mod.stslib.patches.core.AbstractCreature.TempHPField;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.blights.AbstractBlight;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.CardQueueItem;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PotionHelper;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterQueueItem;
import com.megacrit.cardcrawl.orbs.AbstractOrb;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.SlaversCollar;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;
import com.megacrit.cardcrawl.vfx.cardManip.ExhaustCardEffect;
import echo.EchoMod;
import echo.actions.duplicate.SelectCardsForDuplicateAction;
import echo.util.RunnableAction;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class CloningModule {
    private static final Logger logger = LogManager.getLogger(CloningModule.class);
    public static AbstractPlayer originalPlayer;
    public static int originalEnergy;
    public static ArrayList<String> potionPool;
    public static CardGroup commonCardPool;
    public static CardGroup uncommonCardPool;
    public static CardGroup rareCardPool;
    public static CardGroup colorlessCardPool;
    public static CardGroup curseCardPool;
    public static CardGroup srcCommonCardPool;
    public static CardGroup srcUncommonCardPool;
    public static CardGroup srcRareCardPool;
    public static CardGroup srcColorlessCardPool;
    public static CardGroup srcCurseCardPool;
    public static ArrayList<String> commonRelicPool;
    public static ArrayList<String> uncommonRelicPool;
    public static ArrayList<String> rareRelicPool;
    public static ArrayList<String> shopRelicPool;
    public static ArrayList<String> bossRelicPool;
    public static ArrayList<String> relicsToRemoveOnStart;
    public static final List<AbstractRelic> tempRelics = new ArrayList<>();
    public static float cloneVfxTimer = 0.0f;

    public static FrameBuffer fbo = new FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false, false);
    public static TextureRegion fboRegion = new TextureRegion(fbo.getColorBufferTexture());
    private static final ShaderProgram gridShader;

    static {
        fboRegion.flip(false, true);

        gridShader = new ShaderProgram(
                Gdx.files.internal(EchoMod.makeShaderPath("grid_texture/grid_texture.vert")).readString(),
                Gdx.files.internal(EchoMod.makeShaderPath("grid_texture/grid_texture.frag")).readString()
        );

        if (gridShader.getLog().length() != 0) {
            logger.log(gridShader.isCompiled() ? Level.WARN : Level.ERROR, gridShader.getLog());
        }
    }

    public static void playerPreRender(AbstractPlayer player, SpriteBatch sb) {
        if (isCloning()) {
            boolean isBlendingEnabled = sb.isBlendingEnabled();
            int blendSrcFunc = sb.getBlendSrcFunc();
            int blendDstFunc = sb.getBlendDstFunc();
            Color color = sb.getColor();
            ShaderProgram shader = sb.getShader();
            sb.end();
            fbo.begin();
            Gdx.gl.glClearColor(0, 0, 0, 0);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT | GL20.GL_STENCIL_BUFFER_BIT);
            sb.begin();
            if (isBlendingEnabled) {
                sb.enableBlending();
                sb.setBlendFunction(blendSrcFunc, blendDstFunc);
            } else {
                sb.disableBlending();
            }
            sb.setColor(color);
            sb.setShader(shader);
        }
    }

    public static void playerPostRender(AbstractPlayer player, SpriteBatch sb) {
        if (isCloning()) {
            sb.end();
            fbo.end();
            sb.begin();
            sb.setColor(0.44f, 0.33f, 0.86f, 1f);
            sb.enableBlending();
            sb.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

            gridShader.begin();
            gridShader.setUniformf("res_x", Gdx.graphics.getWidth());
            gridShader.setUniformf("res_y", Gdx.graphics.getHeight());
            cloneVfxTimer += Gdx.graphics.getDeltaTime();
            gridShader.setUniformf("time", cloneVfxTimer);
            gridShader.setUniformf("period", 5.0f);
            gridShader.setUniformi("grayscale", 0);
            gridShader.end();

            ShaderProgram oldShader = sb.getShader();
            sb.setShader(gridShader);
            sb.draw(fboRegion, -Settings.VERT_LETTERBOX_AMT, -Settings.HORIZ_LETTERBOX_AMT);
            sb.setShader(oldShader);
        }
    }

    public static void relicPreRender(AbstractRelic relic, SpriteBatch sb) {
        if (tempRelics.contains(relic)) {
            gridShader.begin();
            gridShader.setUniformf("res_x", 128);
            gridShader.setUniformf("res_y", 128);
            gridShader.setUniformf("time", cloneVfxTimer);
            gridShader.setUniformf("period", 5.0f);
            gridShader.setUniformi("grayscale", relic.grayscale ? 1 : 0);
            gridShader.end();

            sb.setShader(gridShader);
        }
    }

    public static void relicPostRender(AbstractRelic relic, SpriteBatch sb) {
        if (tempRelics.contains(relic)) {
            sb.setShader(null);
        }
    }


    public static boolean isCloning() {
        return originalPlayer != null;
    }

    public static void preCloneSetup() {
        for (CardQueueItem i : AbstractDungeon.actionManager.cardQueue) {
            if (i.autoplayCard) {
                i.card.dontTriggerOnUseCard = true;
                AbstractDungeon.actionManager.addToBottom(new UseCardAction(i.card));
            }
        }
        AbstractDungeon.actionManager.cardQueue.clear();
        for (AbstractCard c : AbstractDungeon.player.limbo.group) {
            AbstractDungeon.effectList.add(new ExhaustCardEffect(c));
        }
        AbstractDungeon.player.limbo.group.clear();
        AbstractDungeon.player.releaseCard();
    }

    public static void startCloning(AbstractPlayer.PlayerClass playerClass) {
        if (isCloning()) {
            throw new RuntimeException("Already cloning");
        }

        AbstractDungeon.actionManager.cardQueue.clear();
        AbstractDungeon.player.limbo.group.clear();
        AbstractDungeon.player.releaseCard();

        originalPlayer = AbstractDungeon.player;
        originalEnergy = EnergyPanel.totalCount;
        potionPool = PotionHelper.potions;
        PotionHelper.potions = new ArrayList<>();
        commonCardPool = AbstractDungeon.commonCardPool;
        AbstractDungeon.commonCardPool = new CardGroup(CardGroup.CardGroupType.CARD_POOL);
        uncommonCardPool = AbstractDungeon.uncommonCardPool;
        AbstractDungeon.uncommonCardPool = new CardGroup(CardGroup.CardGroupType.CARD_POOL);
        rareCardPool = AbstractDungeon.rareCardPool;
        AbstractDungeon.rareCardPool = new CardGroup(CardGroup.CardGroupType.CARD_POOL);
        colorlessCardPool = AbstractDungeon.colorlessCardPool;
        AbstractDungeon.colorlessCardPool = new CardGroup(CardGroup.CardGroupType.CARD_POOL);
        curseCardPool = AbstractDungeon.curseCardPool;
        AbstractDungeon.curseCardPool = new CardGroup(CardGroup.CardGroupType.CARD_POOL);
        srcCommonCardPool = AbstractDungeon.srcCommonCardPool;
        AbstractDungeon.srcCommonCardPool = new CardGroup(CardGroup.CardGroupType.CARD_POOL);
        srcUncommonCardPool = AbstractDungeon.srcUncommonCardPool;
        AbstractDungeon.srcUncommonCardPool = new CardGroup(CardGroup.CardGroupType.CARD_POOL);
        srcRareCardPool = AbstractDungeon.srcRareCardPool;
        AbstractDungeon.srcRareCardPool = new CardGroup(CardGroup.CardGroupType.CARD_POOL);
        srcColorlessCardPool = AbstractDungeon.srcColorlessCardPool;
        AbstractDungeon.srcColorlessCardPool = new CardGroup(CardGroup.CardGroupType.CARD_POOL);
        srcCurseCardPool = AbstractDungeon.srcCurseCardPool;
        AbstractDungeon.srcCurseCardPool = new CardGroup(CardGroup.CardGroupType.CARD_POOL);
        commonRelicPool = AbstractDungeon.commonRelicPool;
        AbstractDungeon.commonRelicPool = new ArrayList<>();
        uncommonRelicPool = AbstractDungeon.uncommonRelicPool;
        AbstractDungeon.uncommonRelicPool = new ArrayList<>();
        rareRelicPool = AbstractDungeon.rareRelicPool;
        AbstractDungeon.rareRelicPool = new ArrayList<>();
        shopRelicPool = AbstractDungeon.shopRelicPool;
        AbstractDungeon.shopRelicPool = new ArrayList<>();
        bossRelicPool = AbstractDungeon.bossRelicPool;
        AbstractDungeon.bossRelicPool = new ArrayList<>();
        relicsToRemoveOnStart = AbstractDungeon.relicsToRemoveOnStart;
        AbstractDungeon.relicsToRemoveOnStart = new ArrayList<>();

        AbstractPlayer newPlayer = CardCrawlGame.characterManager.recreateCharacter(playerClass);

        newPlayer.id = originalPlayer.id;
        newPlayer.powers = originalPlayer.powers;
        newPlayer.gold = originalPlayer.gold;
        newPlayer.displayGold = originalPlayer.displayGold;
        newPlayer.isDying = originalPlayer.isDying;
        newPlayer.isDead = originalPlayer.isDead;
        newPlayer.halfDead = originalPlayer.halfDead;
        newPlayer.escapeTimer = originalPlayer.escapeTimer;
        newPlayer.isEscaping = originalPlayer.isEscaping;
        newPlayer.flipHorizontal = originalPlayer.flipHorizontal;
        newPlayer.flipVertical = originalPlayer.flipVertical;
        newPlayer.maxHealth = newPlayer.maxHealth / 4;
        newPlayer.currentHealth = newPlayer.maxHealth;
        newPlayer.currentBlock = originalPlayer.currentBlock;
        TempHPField.tempHp.set(newPlayer, TempHPField.tempHp.get(originalPlayer));

        newPlayer.startingMaxHP = newPlayer.startingMaxHP / 4;
        tempRelics.addAll(newPlayer.relics);
        newPlayer.relics.addAll(originalPlayer.relics);
        newPlayer.reorganizeRelics();
        newPlayer.blights = originalPlayer.blights;
        newPlayer.potionSlots = originalPlayer.potionSlots;
        newPlayer.potions = originalPlayer.potions;
        newPlayer.damagedThisCombat = originalPlayer.damagedThisCombat;
        //noinspection deprecation
        newPlayer.cardsPlayedThisTurn = originalPlayer.cardsPlayedThisTurn;

        newPlayer.stance = originalPlayer.stance;
        newPlayer.orbs = originalPlayer.orbs;
        newPlayer.maxOrbs = originalPlayer.maxOrbs;
        int newMaxOrbs = newPlayer.masterMaxOrbs + originalPlayer.maxOrbs - originalPlayer.masterMaxOrbs;
        newMaxOrbs = Math.min(10, newMaxOrbs);
        while (newPlayer.maxOrbs != newMaxOrbs) {
            if (newPlayer.maxOrbs < newMaxOrbs) {
                newPlayer.increaseMaxOrbSlots(1, false);
            } else {
                newPlayer.decreaseMaxOrbSlots(1);
            }
        }

        newPlayer.isEndingTurn = originalPlayer.isEndingTurn;
        newPlayer.viewingRelics = originalPlayer.viewingRelics;
        newPlayer.inspectMode = originalPlayer.inspectMode;
        newPlayer.endTurnQueued = originalPlayer.endTurnQueued;

        transferAnimationStates(originalPlayer, newPlayer);

        changePlayerInstance(originalPlayer, newPlayer);

        CardCrawlGame.dungeon.initializePotions();
        CardCrawlGame.dungeon.initializeCardPools();
        ReflectionHacks.privateMethod(AbstractDungeon.class, "initializeRelicList").invoke(CardCrawlGame.dungeon);

        newPlayer.isBloodied = (newPlayer.currentHealth <= newPlayer.maxHealth / 2);

        newPlayer.gameHandSize = newPlayer.masterHandSize;
        newPlayer.drawPile.initializeDeck(newPlayer.masterDeck);
        newPlayer.hand.clear();
        newPlayer.discardPile.clear();
        newPlayer.exhaustPile.clear();
        newPlayer.limbo.clear();

        if (newPlayer.hasRelic("SlaversCollar")) {
            ((SlaversCollar) newPlayer.getRelic("SlaversCollar")).beforeEnergyPrep();
        }

        newPlayer.isEndingTurn = false;

        newPlayer.applyPreCombatLogic();
        newPlayer.applyStartOfCombatPreDrawLogic();
        newPlayer.applyStartOfCombatLogic();
        newPlayer.applyStartOfTurnRelics();
        newPlayer.applyStartOfTurnPreDrawCards();

        newPlayer.energy.energy = newPlayer.energy.energyMaster;
        EnergyPanel.totalCount = Math.max(EnergyPanel.totalCount, newPlayer.energy.energy);

        AbstractDungeon.actionManager.addToBottom(new SelectCardsForDuplicateAction());
        AbstractDungeon.actionManager.addToBottom(new RunnableAction(() -> {
            newPlayer.applyStartOfTurnCards();
            newPlayer.applyStartOfTurnPowers();
            newPlayer.applyStartOfTurnOrbs();
            newPlayer.applyStartOfTurnPostDrawRelics();
            newPlayer.applyStartOfTurnPostDrawPowers();
        }));

        newPlayer.healthBarUpdatedEvent();
        newPlayer.showHealthBar();
    }

    public static void stopCloning() {
        if (!isCloning()) {
            return;
        }

        AbstractDungeon.actionManager.cardQueue.clear();
        AbstractDungeon.player.limbo.group.clear();
        AbstractDungeon.player.releaseCard();

        AbstractPlayer newPlayer = AbstractDungeon.player;
        AbstractDungeon.player = originalPlayer;
        EnergyPanel.totalCount = originalEnergy;
        PotionHelper.potions = potionPool;
        AbstractDungeon.commonCardPool = commonCardPool;
        AbstractDungeon.uncommonCardPool = uncommonCardPool;
        AbstractDungeon.rareCardPool = rareCardPool;
        AbstractDungeon.colorlessCardPool = colorlessCardPool;
        AbstractDungeon.curseCardPool = curseCardPool;
        AbstractDungeon.srcCommonCardPool = srcCommonCardPool;
        AbstractDungeon.srcUncommonCardPool = srcUncommonCardPool;
        AbstractDungeon.srcRareCardPool = srcRareCardPool;
        AbstractDungeon.srcColorlessCardPool = srcColorlessCardPool;
        AbstractDungeon.srcCurseCardPool = srcCurseCardPool;
        AbstractDungeon.commonRelicPool = commonRelicPool;
        AbstractDungeon.uncommonRelicPool = uncommonRelicPool;
        AbstractDungeon.rareRelicPool = rareRelicPool;
        AbstractDungeon.shopRelicPool = shopRelicPool;
        AbstractDungeon.bossRelicPool = bossRelicPool;
        AbstractDungeon.relicsToRemoveOnStart = relicsToRemoveOnStart;

        originalPlayer.powers = newPlayer.powers;
        originalPlayer.gold = newPlayer.gold;
        originalPlayer.displayGold = newPlayer.displayGold;
        originalPlayer.isDying = newPlayer.isDying;
        originalPlayer.isDead = newPlayer.isDead;
        originalPlayer.halfDead = newPlayer.halfDead;
        originalPlayer.escapeTimer = newPlayer.escapeTimer;
        originalPlayer.isEscaping = newPlayer.isEscaping;
        originalPlayer.flipHorizontal = newPlayer.flipHorizontal;
        originalPlayer.flipVertical = newPlayer.flipVertical;
        originalPlayer.currentBlock = newPlayer.currentBlock;
        TempHPField.tempHp.set(originalPlayer, TempHPField.tempHp.get(newPlayer));

        originalPlayer.relics = newPlayer.relics;
        originalPlayer.relics.removeAll(tempRelics);
        originalPlayer.reorganizeRelics();
        originalPlayer.blights = newPlayer.blights;
        originalPlayer.potionSlots = newPlayer.potionSlots;
        originalPlayer.potions = newPlayer.potions;
        originalPlayer.adjustPotionPositions();
        originalPlayer.damagedThisCombat = newPlayer.damagedThisCombat;
        //noinspection deprecation
        originalPlayer.cardsPlayedThisTurn = newPlayer.cardsPlayedThisTurn;

        originalPlayer.stance = newPlayer.stance;
        originalPlayer.orbs = newPlayer.orbs;
        originalPlayer.maxOrbs = newPlayer.maxOrbs;
        int newMaxOrbs = originalPlayer.masterMaxOrbs + newPlayer.maxOrbs - newPlayer.masterMaxOrbs;
        newMaxOrbs = Math.min(10, newMaxOrbs);
        while (originalPlayer.maxOrbs != newMaxOrbs) {
            if (originalPlayer.maxOrbs < newMaxOrbs) {
                originalPlayer.increaseMaxOrbSlots(1, false);
            } else {
                originalPlayer.decreaseMaxOrbSlots(1);
            }
        }

        originalPlayer.isEndingTurn = newPlayer.isEndingTurn;
        originalPlayer.viewingRelics = newPlayer.viewingRelics;
        originalPlayer.inspectMode = newPlayer.inspectMode;
        originalPlayer.endTurnQueued = newPlayer.endTurnQueued;

        transferAnimationStates(newPlayer, originalPlayer);

        changePlayerInstance(newPlayer, originalPlayer);

        originalPlayer = null;
        originalEnergy = 0;
        tempRelics.clear();
        cloneVfxTimer = 0.0f;
    }

    private static void transferAnimationStates(AbstractPlayer originalPlayer, AbstractPlayer newPlayer) {
        ReflectionHacks.setPrivate(newPlayer, AbstractCreature.class, "blockAnimTimer",
                ReflectionHacks.getPrivate(originalPlayer, AbstractCreature.class, "blockAnimTimer"));
        ReflectionHacks.setPrivate(newPlayer, AbstractCreature.class, "blockOffset",
                ReflectionHacks.getPrivate(originalPlayer, AbstractCreature.class, "blockOffset"));
        ReflectionHacks.setPrivate(newPlayer, AbstractCreature.class, "blockScale",
                ReflectionHacks.getPrivate(originalPlayer, AbstractCreature.class, "blockScale"));
        ReflectionHacks.setPrivate(newPlayer, AbstractCreature.class, "blockColor",
                ReflectionHacks.getPrivate(originalPlayer, AbstractCreature.class, "blockColor"));
        ReflectionHacks.setPrivate(newPlayer, AbstractCreature.class, "blockOutlineColor",
                ReflectionHacks.getPrivate(originalPlayer, AbstractCreature.class, "blockOutlineColor"));
        ReflectionHacks.setPrivate(newPlayer, AbstractCreature.class, "blockTextColor",
                ReflectionHacks.getPrivate(originalPlayer, AbstractCreature.class, "blockTextColor"));


        ReflectionHacks.setPrivate(AbstractDungeon.overlayMenu.energyPanel, EnergyPanel.class, "gainEnergyImg",
                newPlayer.getEnergyImage());
        ReflectionHacks.setPrivate(newPlayer, AbstractPlayer.class, "isHoveringCard",
                ReflectionHacks.getPrivate(originalPlayer, AbstractPlayer.class, "isHoveringCard"));
        newPlayer.isHoveringDropZone = originalPlayer.isHoveringDropZone;
        ReflectionHacks.setPrivate(newPlayer, AbstractPlayer.class, "hoverStartLine",
                ReflectionHacks.getPrivate(originalPlayer, AbstractPlayer.class, "hoverStartLine"));
        ReflectionHacks.setPrivate(newPlayer, AbstractPlayer.class, "passedHesitationLine",
                ReflectionHacks.getPrivate(originalPlayer, AbstractPlayer.class, "passedHesitationLine"));
        newPlayer.hoveredCard = originalPlayer.hoveredCard;
        newPlayer.toHover = originalPlayer.toHover;
        newPlayer.cardInUse = originalPlayer.cardInUse;
        newPlayer.isDraggingCard = originalPlayer.isDraggingCard;
        ReflectionHacks.setPrivate(newPlayer, AbstractPlayer.class, "isUsingClickDragControl",
                ReflectionHacks.getPrivate(originalPlayer, AbstractPlayer.class, "isUsingClickDragControl"));
        ReflectionHacks.setPrivate(newPlayer, AbstractPlayer.class, "clickDragTimer",
                ReflectionHacks.getPrivate(originalPlayer, AbstractPlayer.class, "clickDragTimer"));
        newPlayer.inSingleTargetMode = originalPlayer.inSingleTargetMode;
        ReflectionHacks.setPrivate(newPlayer, AbstractPlayer.class, "hoveredMonster",
                ReflectionHacks.getPrivate(originalPlayer, AbstractPlayer.class, "hoveredMonster"));
        newPlayer.hoverEnemyWaitTimer = originalPlayer.hoverEnemyWaitTimer;
        newPlayer.isInKeyboardMode = originalPlayer.isInKeyboardMode;
        ReflectionHacks.setPrivate(newPlayer, AbstractPlayer.class, "skipMouseModeOnce",
                ReflectionHacks.getPrivate(originalPlayer, AbstractPlayer.class, "skipMouseModeOnce"));
        ReflectionHacks.setPrivate(newPlayer, AbstractPlayer.class, "keyboardCardIndex",
                ReflectionHacks.getPrivate(originalPlayer, AbstractPlayer.class, "keyboardCardIndex"));
        ReflectionHacks.setPrivate(newPlayer, AbstractPlayer.class, "touchscreenInspectCount",
                ReflectionHacks.getPrivate(originalPlayer, AbstractPlayer.class, "touchscreenInspectCount"));
    }

    private static void changePlayerInstance(AbstractPlayer oldPlayer, AbstractPlayer newPlayer) {
        AbstractDungeon.player = newPlayer;
        changePlayerInstance(AbstractDungeon.overlayMenu, oldPlayer, newPlayer);

        changePlayerInstance(newPlayer.hoveredCard, oldPlayer, newPlayer);
        changePlayerInstance(newPlayer.toHover, oldPlayer, newPlayer);
        changePlayerInstance(newPlayer.cardInUse, oldPlayer, newPlayer);
        changePlayerInstance(ReflectionHacks.getPrivate(newPlayer, AbstractPlayer.class, "hoveredMonster"), oldPlayer, newPlayer);

        for (AbstractPower power : newPlayer.powers) {
            changePlayerInstance(power, oldPlayer, newPlayer);
        }
        for (AbstractRelic relic : newPlayer.relics) {
            changePlayerInstance(relic, oldPlayer, newPlayer);
        }
        for (AbstractBlight blight : newPlayer.blights) {
            changePlayerInstance(blight, oldPlayer, newPlayer);
        }
        for (AbstractOrb orb : newPlayer.orbs) {
            changePlayerInstance(orb, oldPlayer, newPlayer);
        }
        changePlayerInstance(newPlayer.stance, oldPlayer, newPlayer);
        for (AbstractCard card : newPlayer.masterDeck.group) {
            changePlayerInstance(card, oldPlayer, newPlayer);
        }
        for (AbstractCard card : newPlayer.drawPile.group) {
            changePlayerInstance(card, oldPlayer, newPlayer);
        }
        for (AbstractCard card : newPlayer.discardPile.group) {
            changePlayerInstance(card, oldPlayer, newPlayer);
        }
        for (AbstractCard card : newPlayer.exhaustPile.group) {
            changePlayerInstance(card, oldPlayer, newPlayer);
        }
        for (AbstractCard card : newPlayer.hand.group) {
            changePlayerInstance(card, oldPlayer, newPlayer);
        }
        for (AbstractCard card : newPlayer.limbo.group) {
            changePlayerInstance(card, oldPlayer, newPlayer);
        }
        for (AbstractMonster monster : AbstractDungeon.getMonsters().monsters) {
            changePlayerInstance(monster, oldPlayer, newPlayer);
        }
        for (AbstractGameAction action : AbstractDungeon.actionManager.actions) {
            changePlayerInstance(action, oldPlayer, newPlayer);
        }
        for (AbstractGameAction action : AbstractDungeon.actionManager.preTurnActions) {
            changePlayerInstance(action, oldPlayer, newPlayer);
        }
        for (CardQueueItem item : AbstractDungeon.actionManager.cardQueue) {
            changePlayerInstance(item, oldPlayer, newPlayer);
        }
        for (MonsterQueueItem item : AbstractDungeon.actionManager.monsterQueue) {
            changePlayerInstance(item, oldPlayer, newPlayer);
        }
        changePlayerInstance(AbstractDungeon.actionManager.currentAction, oldPlayer, newPlayer);
        changePlayerInstance(AbstractDungeon.actionManager.previousAction, oldPlayer, newPlayer);
        changePlayerInstance(AbstractDungeon.actionManager.turnStartCurrentAction, oldPlayer, newPlayer);

        // this is to support minions indirectly
        ObjectFieldIterator.iterate(newPlayer, field -> {
            if (field.get(newPlayer) instanceof List) {
                List<?> list = (List<?>) field.get(newPlayer);
                for (Object object : list) {
                    if (object instanceof AbstractMonster) {
                        AbstractMonster monster = (AbstractMonster) object;
                        changePlayerInstance(monster, oldPlayer, newPlayer);
                        for (AbstractPower power : monster.powers) {
                            changePlayerInstance(power, oldPlayer, newPlayer);
                        }
                    }
                }
            }
        });
    }

    private static void changePlayerInstance(Object object, AbstractPlayer oldPlayer, AbstractPlayer newPlayer) {
        ObjectFieldIterator.iterate(object, field -> {
            if (field.get(object) == oldPlayer)
                field.set(object, newPlayer);
        });
    }
}
