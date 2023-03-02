package echo.mechanics.duplicate;

import basemod.ReflectionHacks;
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
import echo.actions.duplicate.SelectCardsForDuplicateAction;
import echo.util.RunnableAction;

import java.util.ArrayList;
import java.util.List;

public class CloningModule {
    public static PlayerData playerData;
    public static final List<AbstractRelic> tempRelics = new ArrayList<>();


    public static boolean isCloning() {
        return playerData != null;
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

        clearCardQueues();

        AbstractPlayer originalPlayer = AbstractDungeon.player;

        if (!isCloning()) {
            playerData = PlayerData.extractData();
        }
        clearPools();

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
        originalPlayer.relics.removeAll(tempRelics);
        tempRelics.clear();
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
        newPlayer.endTurnQueued = originalPlayer.endTurnQueued;

        transferVisualStates(originalPlayer, newPlayer);

        changePlayerReferences(originalPlayer, newPlayer);

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

        // only trigger start of combat events for the new relics from the duplicated player
        for (AbstractRelic relic : tempRelics) {
            relic.atPreBattle();
        }
        for (AbstractRelic relic : tempRelics) {
            relic.atBattleStartPreDraw();
        }
        for (AbstractRelic relic : tempRelics) {
            relic.atBattleStart();
        }
        newPlayer.applyStartOfTurnRelics();
        newPlayer.applyStartOfTurnPreDrawCards();

        newPlayer.energy.energy = newPlayer.energy.energyMaster;
        EnergyPanel.totalCount = Math.max(EnergyPanel.totalCount, newPlayer.energy.energy);

        AbstractDungeon.actionManager.addToBottom(new SelectCardsForDuplicateAction(true));
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

        clearCardQueues();

        AbstractPlayer newPlayer = AbstractDungeon.player;
        AbstractPlayer originalPlayer = playerData.originalPlayer;
        playerData.restoreData();

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
        originalPlayer.endTurnQueued = newPlayer.endTurnQueued;

        transferVisualStates(newPlayer, originalPlayer);

        changePlayerReferences(newPlayer, originalPlayer);

        playerData = null;
        tempRelics.clear();
        CloneVfx.cloneVfxTimer = 0.0f;
    }

    private static void clearCardQueues() {
        AbstractDungeon.actionManager.cardQueue.clear();
        AbstractDungeon.player.limbo.group.clear();
        AbstractDungeon.player.releaseCard();
    }

    private static void clearPools() {
        PotionHelper.potions = new ArrayList<>();
        AbstractDungeon.commonCardPool = new CardGroup(CardGroup.CardGroupType.CARD_POOL);
        AbstractDungeon.uncommonCardPool = new CardGroup(CardGroup.CardGroupType.CARD_POOL);
        AbstractDungeon.rareCardPool = new CardGroup(CardGroup.CardGroupType.CARD_POOL);
        AbstractDungeon.colorlessCardPool = new CardGroup(CardGroup.CardGroupType.CARD_POOL);
        AbstractDungeon.curseCardPool = new CardGroup(CardGroup.CardGroupType.CARD_POOL);
        AbstractDungeon.srcCommonCardPool = new CardGroup(CardGroup.CardGroupType.CARD_POOL);
        AbstractDungeon.srcUncommonCardPool = new CardGroup(CardGroup.CardGroupType.CARD_POOL);
        AbstractDungeon.srcRareCardPool = new CardGroup(CardGroup.CardGroupType.CARD_POOL);
        AbstractDungeon.srcColorlessCardPool = new CardGroup(CardGroup.CardGroupType.CARD_POOL);
        AbstractDungeon.srcCurseCardPool = new CardGroup(CardGroup.CardGroupType.CARD_POOL);
        AbstractDungeon.commonRelicPool = new ArrayList<>();
        AbstractDungeon.uncommonRelicPool = new ArrayList<>();
        AbstractDungeon.rareRelicPool = new ArrayList<>();
        AbstractDungeon.shopRelicPool = new ArrayList<>();
        AbstractDungeon.bossRelicPool = new ArrayList<>();
        AbstractDungeon.relicsToRemoveOnStart = new ArrayList<>();
    }

    private static void transferVisualStates(AbstractPlayer originalPlayer, AbstractPlayer newPlayer) {
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
        newPlayer.viewingRelics = originalPlayer.viewingRelics;
        newPlayer.inspectMode = originalPlayer.inspectMode;
    }

    private static void changePlayerReferences(AbstractPlayer oldPlayer, AbstractPlayer newPlayer) {
        AbstractDungeon.player = newPlayer;
        changePlayerReferences(AbstractDungeon.overlayMenu, oldPlayer, newPlayer);

        changePlayerReferences(newPlayer.hoveredCard, oldPlayer, newPlayer);
        changePlayerReferences(newPlayer.toHover, oldPlayer, newPlayer);
        changePlayerReferences(newPlayer.cardInUse, oldPlayer, newPlayer);
        changePlayerReferences(ReflectionHacks.getPrivate(newPlayer, AbstractPlayer.class, "hoveredMonster"), oldPlayer, newPlayer);

        for (AbstractPower power : newPlayer.powers) {
            changePlayerReferences(power, oldPlayer, newPlayer);
        }
        for (AbstractRelic relic : newPlayer.relics) {
            changePlayerReferences(relic, oldPlayer, newPlayer);
        }
        for (AbstractBlight blight : newPlayer.blights) {
            changePlayerReferences(blight, oldPlayer, newPlayer);
        }
        for (AbstractOrb orb : newPlayer.orbs) {
            changePlayerReferences(orb, oldPlayer, newPlayer);
        }
        changePlayerReferences(newPlayer.stance, oldPlayer, newPlayer);
        for (AbstractCard card : newPlayer.masterDeck.group) {
            changePlayerReferences(card, oldPlayer, newPlayer);
        }
        for (AbstractCard card : newPlayer.drawPile.group) {
            changePlayerReferences(card, oldPlayer, newPlayer);
        }
        for (AbstractCard card : newPlayer.discardPile.group) {
            changePlayerReferences(card, oldPlayer, newPlayer);
        }
        for (AbstractCard card : newPlayer.exhaustPile.group) {
            changePlayerReferences(card, oldPlayer, newPlayer);
        }
        for (AbstractCard card : newPlayer.hand.group) {
            changePlayerReferences(card, oldPlayer, newPlayer);
        }
        for (AbstractCard card : newPlayer.limbo.group) {
            changePlayerReferences(card, oldPlayer, newPlayer);
        }
        for (AbstractMonster monster : AbstractDungeon.getMonsters().monsters) {
            changePlayerReferences(monster, oldPlayer, newPlayer);
        }
        for (AbstractGameAction action : AbstractDungeon.actionManager.actions) {
            changePlayerReferences(action, oldPlayer, newPlayer);
        }
        for (AbstractGameAction action : AbstractDungeon.actionManager.preTurnActions) {
            changePlayerReferences(action, oldPlayer, newPlayer);
        }
        for (CardQueueItem item : AbstractDungeon.actionManager.cardQueue) {
            changePlayerReferences(item, oldPlayer, newPlayer);
        }
        for (MonsterQueueItem item : AbstractDungeon.actionManager.monsterQueue) {
            changePlayerReferences(item, oldPlayer, newPlayer);
        }
        changePlayerReferences(AbstractDungeon.actionManager.currentAction, oldPlayer, newPlayer);
        changePlayerReferences(AbstractDungeon.actionManager.previousAction, oldPlayer, newPlayer);
        changePlayerReferences(AbstractDungeon.actionManager.turnStartCurrentAction, oldPlayer, newPlayer);

        // this is to support minions indirectly
        ObjectFieldIterator.iterate(newPlayer, field -> {
            if (field.get(newPlayer) instanceof List) {
                List<?> list = (List<?>) field.get(newPlayer);
                for (Object object : list) {
                    if (object instanceof AbstractMonster) {
                        AbstractMonster monster = (AbstractMonster) object;
                        changePlayerReferences(monster, oldPlayer, newPlayer);
                        for (AbstractPower power : monster.powers) {
                            changePlayerReferences(power, oldPlayer, newPlayer);
                        }
                    }
                }
            }
        });
    }

    private static void changePlayerReferences(Object object, AbstractPlayer oldPlayer, AbstractPlayer newPlayer) {
        ObjectFieldIterator.iterate(object, field -> {
            if (field.get(object) == oldPlayer)
                field.set(object, newPlayer);
        });
    }
}
