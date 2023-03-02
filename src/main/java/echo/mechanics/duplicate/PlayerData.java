package echo.mechanics.duplicate;

import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PotionHelper;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;

import java.util.ArrayList;

public class PlayerData {
    public AbstractPlayer originalPlayer;
    public int originalEnergy;
    public ArrayList<String> potionPool;
    public CardGroup commonCardPool;
    public CardGroup uncommonCardPool;
    public CardGroup rareCardPool;
    public CardGroup colorlessCardPool;
    public CardGroup curseCardPool;
    public CardGroup srcCommonCardPool;
    public CardGroup srcUncommonCardPool;
    public CardGroup srcRareCardPool;
    public CardGroup srcColorlessCardPool;
    public CardGroup srcCurseCardPool;
    public ArrayList<String> commonRelicPool;
    public ArrayList<String> uncommonRelicPool;
    public ArrayList<String> rareRelicPool;
    public ArrayList<String> shopRelicPool;
    public ArrayList<String> bossRelicPool;
    public ArrayList<String> relicsToRemoveOnStart;

    private PlayerData() {
    }

    public static PlayerData extractData() {
        PlayerData data = new PlayerData();

        data.originalPlayer = AbstractDungeon.player;
        data.originalEnergy = EnergyPanel.totalCount;
        data.potionPool = PotionHelper.potions;
        PotionHelper.potions = new ArrayList<>();
        data.commonCardPool = AbstractDungeon.commonCardPool;
        AbstractDungeon.commonCardPool = new CardGroup(CardGroup.CardGroupType.CARD_POOL);
        data.uncommonCardPool = AbstractDungeon.uncommonCardPool;
        AbstractDungeon.uncommonCardPool = new CardGroup(CardGroup.CardGroupType.CARD_POOL);
        data.rareCardPool = AbstractDungeon.rareCardPool;
        AbstractDungeon.rareCardPool = new CardGroup(CardGroup.CardGroupType.CARD_POOL);
        data.colorlessCardPool = AbstractDungeon.colorlessCardPool;
        AbstractDungeon.colorlessCardPool = new CardGroup(CardGroup.CardGroupType.CARD_POOL);
        data.curseCardPool = AbstractDungeon.curseCardPool;
        AbstractDungeon.curseCardPool = new CardGroup(CardGroup.CardGroupType.CARD_POOL);
        data.srcCommonCardPool = AbstractDungeon.srcCommonCardPool;
        AbstractDungeon.srcCommonCardPool = new CardGroup(CardGroup.CardGroupType.CARD_POOL);
        data.srcUncommonCardPool = AbstractDungeon.srcUncommonCardPool;
        AbstractDungeon.srcUncommonCardPool = new CardGroup(CardGroup.CardGroupType.CARD_POOL);
        data.srcRareCardPool = AbstractDungeon.srcRareCardPool;
        AbstractDungeon.srcRareCardPool = new CardGroup(CardGroup.CardGroupType.CARD_POOL);
        data.srcColorlessCardPool = AbstractDungeon.srcColorlessCardPool;
        AbstractDungeon.srcColorlessCardPool = new CardGroup(CardGroup.CardGroupType.CARD_POOL);
        data.srcCurseCardPool = AbstractDungeon.srcCurseCardPool;
        AbstractDungeon.srcCurseCardPool = new CardGroup(CardGroup.CardGroupType.CARD_POOL);
        data.commonRelicPool = AbstractDungeon.commonRelicPool;
        AbstractDungeon.commonRelicPool = new ArrayList<>();
        data.uncommonRelicPool = AbstractDungeon.uncommonRelicPool;
        AbstractDungeon.uncommonRelicPool = new ArrayList<>();
        data.rareRelicPool = AbstractDungeon.rareRelicPool;
        AbstractDungeon.rareRelicPool = new ArrayList<>();
        data.shopRelicPool = AbstractDungeon.shopRelicPool;
        AbstractDungeon.shopRelicPool = new ArrayList<>();
        data.bossRelicPool = AbstractDungeon.bossRelicPool;
        AbstractDungeon.bossRelicPool = new ArrayList<>();
        data.relicsToRemoveOnStart = AbstractDungeon.relicsToRemoveOnStart;
        AbstractDungeon.relicsToRemoveOnStart = new ArrayList<>();

        return data;
    }

    public void restoreData() {
        AbstractDungeon.player = originalPlayer;
        EnergyPanel.totalCount = originalEnergy;
        PotionHelper.potions = potionPool;
        AbstractDungeon.commonCardPool = commonCardPool;
        AbstractDungeon.uncommonCardPool = uncommonCardPool;
        AbstractDungeon.rareCardPool = rareCardPool;
        AbstractDungeon.colorlessCardPool = colorlessCardPool;
        AbstractDungeon.curseCardPool = curseCardPool;
        AbstractDungeon.srcCommonCardPool = srcCommonCardPool;
        AbstractDungeon.srcUncommonCardPool = srcUncommonCardPool;
        AbstractDungeon.srcRareCardPool = srcRareCardPool;
        AbstractDungeon.srcColorlessCardPool = srcColorlessCardPool;
        AbstractDungeon.srcCurseCardPool = srcCurseCardPool;
        AbstractDungeon.commonRelicPool = commonRelicPool;
        AbstractDungeon.uncommonRelicPool = uncommonRelicPool;
        AbstractDungeon.rareRelicPool = rareRelicPool;
        AbstractDungeon.shopRelicPool = shopRelicPool;
        AbstractDungeon.bossRelicPool = bossRelicPool;
        AbstractDungeon.relicsToRemoveOnStart = relicsToRemoveOnStart;
    }
}
