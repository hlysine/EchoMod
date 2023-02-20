package echo.cards.attacks;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import echo.EchoMod;
import echo.cards.AbstractBaseCard;
import echo.mechanics.duplicate.CloningModule;

public class Strike extends AbstractBaseCard {

    public static final String ID = EchoMod.makeID(Strike.class.getSimpleName());

    private static final CardTarget TARGET = CardTarget.ENEMY;

    public Strike() {
        super(ID, TARGET);

        this.tags.add(CardTags.STARTER_STRIKE);
        this.tags.add(CardTags.STRIKE);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new DamageAction(m, new DamageInfo(p, damage, damageTypeForTurn), AbstractGameAction.AttackEffect.SLASH_DIAGONAL));
        addToBot(new AbstractGameAction() {
            @Override
            public void update() {
                if (CloningModule.isCloning()) {
                    CloningModule.stopCloning();
                } else {
                    AbstractPlayer.PlayerClass[] classes = AbstractPlayer.PlayerClass.values();
                    CloningModule.startCloning(classes[AbstractDungeon.cardRandomRng.random(classes.length - 1)]);
                }
                isDone = true;
            }
        });
    }
}
