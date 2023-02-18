package echo.mechanics;

import basemod.ReflectionHacks;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.blights.AbstractBlight;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.orbs.AbstractOrb;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;

import java.util.ArrayList;
import java.util.List;

public class CloningModule {
    public static AbstractPlayer originalPlayer;
    public static List<AbstractRelic> tempRelics;

    public static boolean isCloning() {
        return originalPlayer != null;
    }

    public static void startCloning(AbstractPlayer.PlayerClass playerClass) {
        originalPlayer = AbstractDungeon.player;

        AbstractPlayer newPlayer = CardCrawlGame.characterManager.recreateCharacter(playerClass);

        newPlayer.id = originalPlayer.id;
        newPlayer.powers = originalPlayer.powers;
        newPlayer.gold = originalPlayer.gold;
        newPlayer.displayGold = originalPlayer.displayGold;
        newPlayer.flipHorizontal = originalPlayer.flipHorizontal;
        newPlayer.flipVertical = originalPlayer.flipVertical;
        newPlayer.maxHealth = newPlayer.maxHealth / 4;
        newPlayer.currentHealth = newPlayer.maxHealth;
        newPlayer.currentBlock = originalPlayer.currentBlock;

        newPlayer.startingMaxHP = newPlayer.startingMaxHP / 4;
        tempRelics = new ArrayList<>(newPlayer.relics);
        newPlayer.relics.addAll(originalPlayer.relics);
        originalPlayer.reorganizeRelics();
        newPlayer.blights = originalPlayer.blights;
        newPlayer.potionSlots = originalPlayer.potionSlots;
        newPlayer.potions = originalPlayer.potions;
        newPlayer.energy.energy = originalPlayer.energy.energy;
        newPlayer.damagedThisCombat = originalPlayer.damagedThisCombat;
        //noinspection deprecation
        newPlayer.cardsPlayedThisTurn = originalPlayer.cardsPlayedThisTurn;

        transferAnimationStates(originalPlayer, newPlayer);

        changePlayerInstance(originalPlayer, newPlayer);

        newPlayer.preBattlePrep();
        newPlayer.applyStartOfCombatPreDrawLogic();
        newPlayer.applyStartOfCombatLogic();
        newPlayer.applyStartOfTurnRelics();
        newPlayer.applyStartOfTurnPreDrawCards();

        AbstractDungeon.actionManager.addToBottom(new AbstractGameAction() {
            @Override
            public void update() {
                newPlayer.applyStartOfTurnCards();
                newPlayer.applyStartOfTurnPowers();
                newPlayer.applyStartOfTurnOrbs();
                newPlayer.applyStartOfTurnPostDrawRelics();
                newPlayer.applyStartOfTurnPostDrawPowers();
                isDone = true;
            }
        });

        newPlayer.healthBarUpdatedEvent();
    }

    public static void stopCloning() {
        AbstractPlayer newPlayer = AbstractDungeon.player;
        AbstractDungeon.player = originalPlayer;

        originalPlayer.powers = newPlayer.powers;
        originalPlayer.gold = newPlayer.gold;
        originalPlayer.displayGold = newPlayer.displayGold;
        originalPlayer.flipHorizontal = newPlayer.flipHorizontal;
        originalPlayer.flipVertical = newPlayer.flipVertical;
        originalPlayer.currentBlock = newPlayer.currentBlock;

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

        transferAnimationStates(newPlayer, originalPlayer);

        changePlayerInstance(newPlayer, originalPlayer);
        originalPlayer = null;
    }

    private static void transferAnimationStates(AbstractPlayer originalPlayer, AbstractPlayer newPlayer) {
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
        // this is to support minions indirectly
        ObjectFieldIterator.iterate(newPlayer, field -> {
            if (field.get(newPlayer) instanceof List) {
                List<?> list = (List<?>) field.get(newPlayer);
                for (Object object : list) {
                    if (object instanceof AbstractMonster)
                        changePlayerInstance(object, oldPlayer, newPlayer);
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
