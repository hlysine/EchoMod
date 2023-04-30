package echo.events;

import basemod.BaseMod;
import basemod.abstracts.events.PhasedEvent;
import basemod.abstracts.events.phases.TextPhase;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ScreenShake;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;
import echo.EchoMod;
import echo.cards.skills.Deepfake;
import echo.mechanics.duplicate.Duplicator;
import echo.mechanics.duplicate.PlayerClassManager;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class PreciousDataEvent extends PhasedEvent {
    public static final String ID = EchoMod.makeID(PreciousDataEvent.class.getSimpleName());
    public static final String IMG = EchoMod.makeEventPath("placeholder_event_1.png");
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;
    private static final String TITLE = eventStrings.NAME;
    private static final int CARD_COUNT = 3;
    private static final int DAMAGE = 15;

    private static final String ENCOUNTER_PHASE = "Encounter";
    private static final String DECISION_PHASE = "Decision";
    private static final String ACCEPTED_PHASE = "Accepted";
    private static final String REJECTED_PHASE = "Rejected";

    private final List<AbstractPlayer> playerChoices;
    private final Deepfake previewCard = new Deepfake();
    private AbstractPlayer chosenPlayer;

    public PreciousDataEvent() {
        super(ID, TITLE, IMG);

        playerChoices = PlayerClassManager.getClassList(AbstractDungeon.eventRng, new AbstractPlayer.PlayerClass[]{Duplicator.getTrueClass()}, 3, false)
                .stream().map(BaseMod::findCharacter).collect(Collectors.toList());

        TextPhase encounterPhase = new TextPhase(DESCRIPTIONS[0]);
        for (AbstractPlayer choice : playerChoices) {
            encounterPhase.addOption(
                    OPTIONS[0] + Arrays.stream(choice.title.split(" ")).map(s -> "#y" + s).collect(Collectors.joining(" ")) + OPTIONS[1],
                    i -> {
                        chosenPlayer = playerChoices.get(i);
                        previewCard.targetClass = chosenPlayer.chosenClass;
                        previewCard.initializeDescription();
                        transitionKey(DECISION_PHASE);
                    }
            );
        }
        registerPhase(ENCOUNTER_PHASE, encounterPhase);

        registerPhase(DECISION_PHASE, new TextPhase(DESCRIPTIONS[1])
                .addOption(new TextPhase.OptionInfo(OPTIONS[2] + CARD_COUNT + OPTIONS[3] + DAMAGE + OPTIONS[4], previewCard), i -> {
                    Deepfake card = new Deepfake(chosenPlayer.chosenClass);
                    AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(card.makeStatEquivalentCopy(), Settings.WIDTH / 2f, Settings.HEIGHT / 2f));
                    AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(card.makeStatEquivalentCopy(), Settings.WIDTH / 2f, Settings.HEIGHT / 2f));
                    AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(card.makeStatEquivalentCopy(), Settings.WIDTH / 2f, Settings.HEIGHT / 2f));
                    CardCrawlGame.screenShake.shake(ScreenShake.ShakeIntensity.MED, ScreenShake.ShakeDur.MED, false);
                    CardCrawlGame.sound.play("BLUNT_FAST");
                    AbstractDungeon.player.damage(new DamageInfo(null, DAMAGE));
                    logMetricObtainCardAndDamage(ID, chosenPlayer.id, card, DAMAGE);
                    transitionKey(ACCEPTED_PHASE);
                })
                .addOption(OPTIONS[5], i -> {
                    logMetricIgnored(ID);
                    transitionKey(REJECTED_PHASE);
                })
        );

        registerPhase(ACCEPTED_PHASE, new TextPhase(DESCRIPTIONS[2])
                .addOption(OPTIONS[6], i -> openMap())
        );

        registerPhase(REJECTED_PHASE, new TextPhase(DESCRIPTIONS[3])
                .addOption(OPTIONS[6], i -> openMap())
        );

        transitionKey(ENCOUNTER_PHASE);
    }
}
