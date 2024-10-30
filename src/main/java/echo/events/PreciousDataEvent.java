package echo.events;

import basemod.BaseMod;
import basemod.abstracts.events.PhasedEvent;
import basemod.abstracts.events.phases.TextPhase;
import com.megacrit.cardcrawl.cards.AbstractCard;
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

import java.util.ArrayList;
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
    private static final int DAMAGE = 3;
    private static final int DAMAGE_SCALE = 3;

    private static final String ENCOUNTER_PHASE = "Encounter";
    private static final String ACCEPTED_PHASE = "Accepted";
    private static final String[] DECISION_PHASE = {"Decision 1", "Decision 2", "Decision 3", ACCEPTED_PHASE};
    private static final String REJECTED_PHASE = "Rejected";

    List<Deepfake> obtainedCards = new ArrayList<>();
    int hpLoss = 0;

    public PreciousDataEvent() {
        super(ID, TITLE, IMG);

        registerPhase(ENCOUNTER_PHASE, new TextPhase(DESCRIPTIONS[0])
                .addOption(OPTIONS[0], i -> {
                    obtainedCards.clear();
                    hpLoss = 0;
                    transitionKey(DECISION_PHASE[0]);
                })
        );

        for (int phase = 0; phase < 3; phase++) {
            final int finalPhase = phase;
            List<AbstractPlayer> playerChoices = PlayerClassManager.getClassList(AbstractDungeon.eventRng, new AbstractPlayer.PlayerClass[]{Duplicator.getTrueClass()}, 3, false)
                    .stream().map(BaseMod::findCharacter).collect(Collectors.toList());

            TextPhase encounterPhase = new TextPhase(DESCRIPTIONS[1 + finalPhase]);
            for (AbstractPlayer choice : playerChoices) {
                Deepfake previewCard = new Deepfake(choice.chosenClass);
                previewCard.initializeDescription();
                encounterPhase.addOption(
                        new TextPhase.OptionInfo(OPTIONS[1] + Arrays.stream(choice.title.split(" ")).map(s -> "#y" + s).collect(Collectors.joining(" ")) + OPTIONS[2] + DAMAGE + OPTIONS[3], previewCard),
                        i -> {
                            AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(previewCard.makeStatEquivalentCopy(), Settings.WIDTH / 2f, Settings.HEIGHT / 2f));
                            CardCrawlGame.screenShake.shake(ScreenShake.ShakeIntensity.MED, ScreenShake.ShakeDur.MED, false);
                            CardCrawlGame.sound.play("BLUNT_FAST");
                            AbstractDungeon.player.decreaseMaxHealth(DAMAGE + DAMAGE_SCALE * finalPhase);

                            obtainedCards.add(previewCard);
                            hpLoss += DAMAGE + DAMAGE_SCALE * finalPhase;

                            transitionKey(DECISION_PHASE[finalPhase + 1]);
                        }
                );
            }
            encounterPhase.addOption(OPTIONS[4], i -> {
                if (finalPhase == 0) {
                    transitionKey(REJECTED_PHASE);
                } else {
                    transitionKey(ACCEPTED_PHASE);
                }
            });
            registerPhase(DECISION_PHASE[phase], encounterPhase);
        }

        registerPhase(ACCEPTED_PHASE, new TextPhase(DESCRIPTIONS[4])
                .addOption(OPTIONS[5], i -> {
                    logMetricObtainCardsLoseMapHP(
                            ID,
                            obtainedCards.stream().map(AbstractCard::getMetricID).collect(Collectors.joining(", ")),
                            obtainedCards.stream().map(c -> c.cardID).collect(Collectors.toList()),
                            hpLoss
                    );
                    openMap();
                })
        );

        registerPhase(REJECTED_PHASE, new TextPhase(DESCRIPTIONS[5])
                .addOption(OPTIONS[5], i -> {
                    logMetricIgnored(ID);
                    openMap();
                })
        );

        transitionKey(ENCOUNTER_PHASE);
    }
}
