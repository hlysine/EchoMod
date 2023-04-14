package echo.powers;

import basemod.interfaces.CloneablePowerInterface;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import echo.EchoMod;
import echo.mechanics.duplicate.CloningModule;
import echo.subscribers.DuplicateSubscriber;
import echo.util.RunnableAction;
import echo.util.TextureLoader;

public class DeepLearningPower extends AbstractPower implements CloneablePowerInterface, DuplicateSubscriber {

    public static final String POWER_ID = EchoMod.makeID(DeepLearningPower.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    private static final Texture tex84 = TextureLoader.getTexture(EchoMod.makePowerPath("deep_learning84.png"));
    private static final Texture tex32 = TextureLoader.getTexture(EchoMod.makePowerPath("deep_learning32.png"));

    public DeepLearningPower(final AbstractCreature owner) {
        name = NAME;
        ID = POWER_ID;

        this.owner = owner;
        this.amount = -1;

        type = PowerType.BUFF;
        isTurnBased = false;

        this.region128 = new TextureAtlas.AtlasRegion(tex84, 0, 0, 84, 84);
        this.region48 = new TextureAtlas.AtlasRegion(tex32, 0, 0, 32, 32);

        updateDescription();
    }

    @Override
    public void afterDuplicateStart() {
        addToBot(new RunnableAction(this::flash));
        addToBot(new AbstractGameAction() {
            public void update() {
                AbstractPlayer p = AbstractDungeon.player;

                processCardsInGroup(p.hand);
                processCardsInGroup(p.drawPile);
                processCardsInGroup(p.discardPile);
                processCardsInGroup(p.exhaustPile);

                this.isDone = true;
            }

            private void processCardsInGroup(CardGroup cardGroup) {
                for (AbstractCard c : cardGroup.group) {
                    if (CloningModule.isCardTransformed(c) && c.canUpgrade()) {
                        if (cardGroup.type == CardGroup.CardGroupType.HAND) {
                            c.superFlash();
                        }
                        c.upgrade();
                        c.applyPowers();
                    }
                }
            }
        });
    }

    @Override
    public void updateDescription() {
        description = DESCRIPTIONS[0];
    }

    @Override
    public AbstractPower makeCopy() {
        return new DeepLearningPower(owner);
    }
}
