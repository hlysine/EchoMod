package echo.mechanics.duplicate;

import basemod.BaseMod;
import basemod.ReflectionHacks;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
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
import echo.powers.DuplicatePower;
import echo.subscribers.DuplicateSubscriber;
import echo.util.RunnableAction;
import thePackmaster.SpireAnniversary5Mod;
import thePackmaster.ThePackmaster;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class Duplicator {
    public static PlayerData playerData;
    public static RelicTransformer relicTransformer;

    // fields that should not be copied over from one player instance to another when cloning
    private static final List<String> ignoredPlayerFields = Arrays.asList(
            "power",
            "hb",
            "tips",
            "healthHb",
            "hb_x",
            "hb_y",
            "hb_w",
            "hb_h",
            "atlas",
            "skeleton",
            "state",
            "stateData",
            "chosenClass",
            "gameHandSize",
            "masterHandSize",
            "startingMaxHP",
            "currentHealth",
            "maxHealth",
            "masterDeck",
            "drawPile",
            "hand",
            "discardPile",
            "exhaustPile",
            "limbo",
            "relics",
            "energy",
            "orbs",
            "masterMaxOrbs",
            "maxOrbs",
            "img",
            "shoulderImg",
            "shoulder2Img",
            "corpseImg"
    );

    public static AbstractPlayer.PlayerClass getTrueClass() {
        if (isDuplicating()) {
            return playerData.originalPlayer.chosenClass;
        } else {
            return AbstractDungeon.player.chosenClass;
        }
    }


    public static boolean isDuplicating() {
        return playerData != null;
    }

    public static boolean isCardTransformed(AbstractCard card) {
        return isDuplicating() && findCardInDeck(playerData.originalPlayer, card) == CardGroup.CardGroupType.UNSPECIFIED;
    }

    public static void preDuplicateSetup() {
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

    public static void startDuplication(AbstractPlayer.PlayerClass playerClass, DuplicatedDecks cardDecks, AbstractGameAction followUpAction) {

        clearCardQueues();

        AbstractPlayer originalPlayer = AbstractDungeon.player;

        if (!isDuplicating()) {
            playerData = PlayerData.extractData();
        }
        clearPools();

        AbstractPlayer newPlayer = CardCrawlGame.characterManager.recreateCharacter(playerClass);

        copyPlayerFields(originalPlayer, newPlayer);

        changePlayerReferences(originalPlayer, newPlayer);

        ReflectionHacks.privateMethod(AbstractCreature.class, "refreshHitboxLocation").invoke(newPlayer);

        newPlayer.powers = originalPlayer.powers;
        newPlayer.powers.removeIf(p -> p instanceof DuplicatePower);
        newPlayer.powers.add(0, new DuplicatePower(newPlayer));
        newPlayer.startingMaxHP = newPlayer.startingMaxHP / 5;
        newPlayer.maxHealth = newPlayer.maxHealth / 5;
        newPlayer.currentHealth = newPlayer.maxHealth;

        CardCrawlGame.dungeon.initializePotions();
        initializeCardPools();
        ReflectionHacks.privateMethod(AbstractDungeon.class, "initializeRelicList").invoke(CardCrawlGame.dungeon);

        if (relicTransformer != null)
            originalPlayer.relics = relicTransformer.originalRelics;
        relicTransformer = new RelicTransformer(AbstractDungeon.cardRandomRng, originalPlayer.chosenClass, newPlayer.chosenClass);
        ArrayList<AbstractRelic> newRelics = relicTransformer.transform(originalPlayer.relics);
        if (originalPlayer.relics.stream().noneMatch(r -> r.tier == AbstractRelic.RelicTier.STARTER))
            newPlayer.relics.clear();
        newRelics.removeIf(r -> r.tier == AbstractRelic.RelicTier.STARTER);
        newPlayer.relics.addAll(newRelics);
        newPlayer.reorganizeRelics();
        newPlayer.adjustPotionPositions();

        newPlayer.orbs = originalPlayer.orbs;
        newPlayer.maxOrbs = originalPlayer.maxOrbs;
//        int newMaxOrbs = newPlayer.masterMaxOrbs + originalPlayer.maxOrbs - originalPlayer.masterMaxOrbs;
        int newMaxOrbs = newPlayer.masterMaxOrbs;
        newMaxOrbs = Math.max(0, Math.min(10, newMaxOrbs));
        if (newMaxOrbs == 0 && originalPlayer.maxOrbs > 0)
            newMaxOrbs = 1;
        while (newPlayer.maxOrbs != newMaxOrbs) {
            if (newPlayer.maxOrbs < newMaxOrbs) {
                newPlayer.increaseMaxOrbSlots(1, false);
            } else {
                newPlayer.decreaseMaxOrbSlots(1);
            }
        }

        updateExternalFields(originalPlayer, newPlayer);

        newPlayer.isBloodied = (newPlayer.currentHealth <= newPlayer.maxHealth / 2);
        newPlayer.gameHandSize = newPlayer.masterHandSize;
        if (cardDecks == null) {
            CardTransformer cardTransformer = new CardTransformer(AbstractDungeon.cardRandomRng, originalPlayer.chosenClass, newPlayer.chosenClass);
            cardDecks = cardTransformer.transform(DuplicatedDecks.extractFromPlayer(originalPlayer));
        }
        cardDecks.applyToPlayer(newPlayer);
        newPlayer.drawPile.initializeDeck(newPlayer.masterDeck);

        if (newPlayer.hasRelic("SlaversCollar")) {
            ((SlaversCollar) newPlayer.getRelic("SlaversCollar")).beforeEnergyPrep();
        }

        newPlayer.isEndingTurn = false;

        // only trigger start of combat events for the new relics from the duplicated player
        Collection<AbstractRelic> transformedRelics = newPlayer.relics.stream()
                .filter(r -> !relicTransformer.originalRelics.contains(r))
                .collect(Collectors.toList());
        for (AbstractRelic relic : transformedRelics) {
            relic.onEquip();
        }
        for (AbstractRelic relic : transformedRelics) {
            relic.atPreBattle();
        }
        for (AbstractRelic relic : transformedRelics) {
            relic.atBattleStartPreDraw();
        }
        for (AbstractRelic relic : transformedRelics) {
            relic.atBattleStart();
        }
        newPlayer.applyStartOfTurnRelics();
        newPlayer.applyStartOfTurnPreDrawCards();

        newPlayer.energy.energyMaster = originalPlayer.energy.energyMaster;
        newPlayer.energy.energy = originalPlayer.energy.energy;
        EnergyPanel.totalCount = EnergyPanel.totalCount + newPlayer.energy.energy;

        if (followUpAction != null)
            AbstractDungeon.actionManager.addToTurnStart(followUpAction);
        AbstractDungeon.actionManager.addToTurnStart(new RunnableAction(() -> {
            newPlayer.applyStartOfTurnCards();
            newPlayer.applyStartOfTurnPowers();
            newPlayer.applyStartOfTurnOrbs();
            newPlayer.applyStartOfTurnPostDrawRelics();
            newPlayer.applyStartOfTurnPostDrawPowers();
        }));
        AbstractDungeon.actionManager.addToTurnStart(new DrawCardAction(newPlayer, newPlayer.gameHandSize));

        newPlayer.healthBarUpdatedEvent();
        newPlayer.showHealthBar();
        DuplicationVfx.visibleRadius = 0;

        for (AbstractPower power : AbstractDungeon.player.powers) {
            if (power instanceof DuplicateSubscriber) {
                DuplicateSubscriber subscriber = (DuplicateSubscriber) power;
                subscriber.afterDuplicateStart();
            }
        }
        for (AbstractRelic relic : AbstractDungeon.player.relics) {
            if (relic instanceof DuplicateSubscriber) {
                DuplicateSubscriber subscriber = (DuplicateSubscriber) relic;
                subscriber.afterDuplicateStart();
            }
        }
    }

    public static void stopDuplication(boolean triggerSubscribers) {
        if (!isDuplicating()) {
            return;
        }

        clearCardQueues();

        AbstractPlayer newPlayer = AbstractDungeon.player;
        AbstractPlayer originalPlayer = playerData.originalPlayer;
        playerData.restoreData();

        copyPlayerFields(newPlayer, originalPlayer);

        originalPlayer.powers = newPlayer.powers;
        originalPlayer.powers.removeIf(p -> p instanceof DuplicatePower);

        // only trigger start of combat events for the new relics from the duplicated player
        Collection<AbstractRelic> transformedRelics = newPlayer.relics.stream()
                .filter(r -> !relicTransformer.originalRelics.contains(r))
                .collect(Collectors.toList());
        for (AbstractRelic relic : transformedRelics) {
            relic.onUnequip();
        }
        originalPlayer.relics = relicTransformer.originalRelics;
        originalPlayer.reorganizeRelics();
        originalPlayer.adjustPotionPositions();

        syncDeckChanges(originalPlayer, newPlayer);

        originalPlayer.orbs = newPlayer.orbs;
        originalPlayer.maxOrbs = newPlayer.maxOrbs;
//        int newMaxOrbs = originalPlayer.masterMaxOrbs + newPlayer.maxOrbs - newPlayer.masterMaxOrbs;
        int newMaxOrbs = originalPlayer.masterMaxOrbs;
        newMaxOrbs = Math.max(0, Math.min(10, newMaxOrbs));
        if (newMaxOrbs == 0 && newPlayer.maxOrbs > 0)
            newMaxOrbs = 1;
        while (originalPlayer.maxOrbs != newMaxOrbs) {
            if (originalPlayer.maxOrbs < newMaxOrbs) {
                originalPlayer.increaseMaxOrbSlots(1, false);
            } else {
                originalPlayer.decreaseMaxOrbSlots(1);
            }
        }

        originalPlayer.energy.energyMaster = newPlayer.energy.energyMaster;
        originalPlayer.energy.energy = newPlayer.energy.energy;

        updateExternalFields(newPlayer, originalPlayer);

        changePlayerReferences(newPlayer, originalPlayer);

        playerData = null;
        relicTransformer = null;

        originalPlayer.healthBarUpdatedEvent();
        originalPlayer.showHealthBar();
        DuplicationVfx.visibleRadius = 0;

        if (triggerSubscribers) {
            for (AbstractPower power : AbstractDungeon.player.powers) {
                if (power instanceof DuplicateSubscriber) {
                    DuplicateSubscriber subscriber = (DuplicateSubscriber) power;
                    subscriber.afterDuplicateEnd();
                }
            }
            for (AbstractRelic relic : AbstractDungeon.player.relics) {
                if (relic instanceof DuplicateSubscriber) {
                    DuplicateSubscriber subscriber = (DuplicateSubscriber) relic;
                    subscriber.afterDuplicateEnd();
                }
            }
        }
    }

    private static void clearCardQueues() {
        AbstractDungeon.actionManager.cardQueue.clear();
        AbstractDungeon.player.limbo.group.clear();
        if (AbstractDungeon.currMapNode != null)
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

    private static void updateExternalFields(AbstractPlayer originalPlayer, AbstractPlayer newPlayer) {
        ReflectionHacks.setPrivate(AbstractDungeon.overlayMenu.energyPanel, EnergyPanel.class, "gainEnergyImg",
                newPlayer.getEnergyImage());
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
        if (AbstractDungeon.currMapNode != null)
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

    private static void copyPlayerFields(AbstractPlayer originalPlayer, AbstractPlayer newPlayer) {
        ObjectFieldIterator.iterate(originalPlayer, field -> {
            if (!Modifier.isFinal(field.getModifiers()) && !ignoredPlayerFields.contains(field.getName())) {
                field.set(newPlayer, field.get(originalPlayer));
            }
        }, AbstractPlayer.class, Object.class);
    }

    private static void changePlayerReferences(Object object, AbstractPlayer oldPlayer, AbstractPlayer newPlayer) {
        ObjectFieldIterator.iterate(object, field -> {
            if (field.get(object) == oldPlayer)
                field.set(object, newPlayer);
        });
    }

    private static void initializeCardPools() {
        if (BaseMod.hasModID("anniv5")) {
            if (AbstractDungeon.player.chosenClass == ThePackmaster.Enums.THE_PACKMASTER) {
                SpireAnniversary5Mod.currentPoolPacks.clear();
                SpireAnniversary5Mod.currentPoolPacks.addAll(SpireAnniversary5Mod.allPacks);
            }
        }
        CardCrawlGame.dungeon.initializeCardPools();
    }

    /**
     * For cards that appear in both decks, copy the position of those cards in source to destination.
     *
     * @param destination card positions to copy to
     * @param source      card positions to copy from
     */
    private static void syncDeckChanges(AbstractPlayer destination, AbstractPlayer source) {
        filterAllCards(destination, (card, location) -> {
            DuplicatedDecks.resetCard(card);

            CardGroup.CardGroupType newLocation = findCardInDeck(source, card);
            if (newLocation == location || newLocation == CardGroup.CardGroupType.UNSPECIFIED) {
                return true;
            } else {
                int idx = getCardGroup(source, newLocation).group.indexOf(card);
                ArrayList<AbstractCard> group = getCardGroup(destination, newLocation).group;
                group.add(Math.min(group.size(), Math.max(0, idx)), card);
                return false;
            }
        });
    }

    private static void filterAllCards(AbstractPlayer player, BiFunction<AbstractCard, CardGroup.CardGroupType, Boolean> filter) {
        player.hand.group.removeIf(card -> !filter.apply(card, CardGroup.CardGroupType.HAND));
        player.drawPile.group.removeIf(card -> !filter.apply(card, CardGroup.CardGroupType.DRAW_PILE));
        player.discardPile.group.removeIf(card -> !filter.apply(card, CardGroup.CardGroupType.DISCARD_PILE));
        player.exhaustPile.group.removeIf(card -> !filter.apply(card, CardGroup.CardGroupType.EXHAUST_PILE));
    }

    private static CardGroup.CardGroupType findCardInDeck(AbstractPlayer player, AbstractCard card) {
        if (player.hand.contains(card)) {
            return CardGroup.CardGroupType.HAND;
        }
        if (player.drawPile.contains(card)) {
            return CardGroup.CardGroupType.DRAW_PILE;
        }
        if (player.discardPile.contains(card)) {
            return CardGroup.CardGroupType.DISCARD_PILE;
        }
        if (player.exhaustPile.contains(card)) {
            return CardGroup.CardGroupType.EXHAUST_PILE;
        }
        return CardGroup.CardGroupType.UNSPECIFIED;
    }

    private static CardGroup getCardGroup(AbstractPlayer player, CardGroup.CardGroupType type) {
        switch (type) {
            case DRAW_PILE:
                return player.drawPile;
            case MASTER_DECK:
                return player.masterDeck;
            case HAND:
                return player.hand;
            case DISCARD_PILE:
                return player.discardPile;
            case EXHAUST_PILE:
                return player.exhaustPile;
            default:
                throw new RuntimeException("Invalid card group type");
        }
    }
}
