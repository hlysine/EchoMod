package echo.cards.skills;

import basemod.BaseMod;
import basemod.abstracts.CustomSavable;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import echo.EchoMod;
import echo.actions.duplicate.DuplicatePlayerAction;
import echo.cards.AbstractBaseCard;
import echo.characters.Echo;
import echo.subscribers.AfterCardUseSubscriber;

import java.util.Arrays;
import java.util.stream.Collectors;

public class Deepfake extends AbstractBaseCard implements AfterCardUseSubscriber, CustomSavable<AbstractPlayer.PlayerClass> {

    public static final String ID = EchoMod.makeID(Deepfake.class.getSimpleName());

    private static final CardTarget TARGET = CardTarget.SELF;

    public AbstractPlayer.PlayerClass targetClass;

    public Deepfake(AbstractPlayer.PlayerClass target) {
        super(ID, TARGET);
        this.targetClass = target;
        if (this.targetClass == null)
            this.targetClass = Echo.Enums.ECHO;

        this.exhaust = true;
    }

    public Deepfake() {
        this(Echo.Enums.ECHO);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
    }

    @Override
    public void afterUse() {
        addToBot(new DuplicatePlayerAction(targetClass, null, null));
    }

    @Override
    public void applyPowers() {
        super.applyPowers();
        initializeDescription();
    }

    @Override
    public void initializeDescription() {
        String originalDescription = cardStrings != null ? cardStrings.DESCRIPTION : rawDescription;
        if (targetClass != null) {
            AbstractPlayer target = CardCrawlGame.characterManager != null ? BaseMod.findCharacter(targetClass) : null;
            String name = Arrays.stream((target != null ? target.title : targetClass.name()).split(" "))
                    .map(s -> "*" + s)
                    .collect(Collectors.joining(" "));
            rawDescription = originalDescription.replace("$CHARACTER", name);
        } else {
            rawDescription = originalDescription.replace("$CHARACTER", "a character");
        }
        super.initializeDescription();
    }

    @Override
    public AbstractPlayer.PlayerClass onSave() {
        return this.targetClass;
    }

    @Override
    public void onLoad(AbstractPlayer.PlayerClass object) {
        this.targetClass = object;
        if (this.targetClass == null)
            this.targetClass = Echo.Enums.ECHO;
        initializeDescription();
    }

    @Override
    public AbstractCard makeCopy() {
        return new Deepfake(this.targetClass);
    }
}
