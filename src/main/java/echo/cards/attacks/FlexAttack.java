package echo.cards.attacks;

import basemod.BaseMod;
import com.megacrit.cardcrawl.actions.utility.NewQueueCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.random.Random;
import echo.EchoMod;
import echo.actions.DiscoveryCardAction;
import echo.cards.AbstractBaseCard;
import echo.mechanics.duplicate.CardTransformer;
import echo.mechanics.duplicate.Duplicator;
import echo.mechanics.duplicate.PlayerClassManager;
import echo.util.RunnableAction;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FlexAttack extends AbstractBaseCard {

    public static final String ID = EchoMod.makeID(FlexAttack.class.getSimpleName());

    private static final CardTarget TARGET = CardTarget.ENEMY;

    public FlexAttack() {
        super(ID, TARGET);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        Random rng = AbstractDungeon.cardRandomRng;

        addToBot(new RunnableAction(() -> {
            List<AbstractPlayer.PlayerClass> playerClasses = PlayerClassManager.getClassList(
                    rng,
                    new AbstractPlayer.PlayerClass[]{Duplicator.getTrueClass(), p.chosenClass},
                    0,
                    true
            );

            ArrayList<AbstractCard> finalChoices = new ArrayList<>(magicNumber);
            while (finalChoices.size() < magicNumber) {
                List<AbstractCard> cardPool;
                if (playerClasses.size() > 0) {
                    cardPool = CardTransformer.getCardPool(BaseMod.findCharacter(playerClasses.get(0)).getCardColor(), false);
                } else {
                    cardPool = CardTransformer.getCardPool(CardColor.COLORLESS, false);
                }
                List<AbstractCard> normalChoices = cardPool.stream().filter(c -> c.type == CardType.ATTACK).collect(Collectors.toList());
                if (normalChoices.size() > 0) {
                    finalChoices.add(normalChoices.get(rng.random(normalChoices.size() - 1)));
                } else if (playerClasses.size() == 0) {
                    break;
                }
                if (playerClasses.size() > 0)
                    playerClasses.remove(0);
            }

            addToTop(new DiscoveryCardAction(finalChoices, cardStrings.EXTENDED_DESCRIPTION[0], cardStrings.EXTENDED_DESCRIPTION[1], card -> {
                addToTop(new NewQueueCardAction(card, m, true, true));
            }));
        }));
    }
}
