package echo.potions;

import basemod.abstracts.CustomPotion;
import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.colorless.Metamorphosis;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.helpers.CardHelper;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.localization.PotionStrings;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import echo.EchoMod;

public class ButterflyInAJar extends CustomPotion {

    public static final String POTION_ID = EchoMod.makeID(ButterflyInAJar.class.getSimpleName());
    private static final PotionStrings potionStrings = CardCrawlGame.languagePack.getPotionString(POTION_ID);

    public static final Color LIQUID_COLOR = CardHelper.getColor(135, 50, 200);
    public static final Color HYBRID_COLOR = CardHelper.getColor(200, 170, 220);
    public static final Color SPOTS_COLOR = CardHelper.getColor(60, 25, 150);

    public static final String NAME = potionStrings.NAME;
    public static final String[] DESCRIPTIONS = potionStrings.DESCRIPTIONS;

    private static final PotionRarity RARITY = PotionRarity.UNCOMMON;
    private static final PotionSize SIZE = PotionSize.JAR;
    private static final PotionColor COLOR = PotionColor.FAIRY;

    public ButterflyInAJar() {
        super(NAME, POTION_ID, RARITY, SIZE, COLOR);

        isThrown = false;
        initializeData();
    }

    @Override
    public void use(AbstractCreature target) {
        AbstractCard card = new Metamorphosis();
        card.setCostForTurn(0);
        addToBot(new MakeTempCardInHandAction(card, this.potency));
    }

    @Override
    public void initializeData() {
        this.potency = getPotency();
        if (this.potency == 1) {
            this.description = potionStrings.DESCRIPTIONS[0];
        } else {
            this.description = potionStrings.DESCRIPTIONS[1] + this.potency + potionStrings.DESCRIPTIONS[2];
        }
        this.tips.clear();
        this.tips.add(new PowerTip(this.name, this.description));
    }

    @Override
    public AbstractPotion makeCopy() {
        return new ButterflyInAJar();
    }

    @Override
    public int getPotency(final int ascensionLevel) {
        return 1;
    }
}
