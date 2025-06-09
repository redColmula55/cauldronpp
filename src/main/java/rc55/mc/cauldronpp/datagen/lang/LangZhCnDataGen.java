package rc55.mc.cauldronpp.datagen.lang;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import rc55.mc.cauldronpp.block.CauldronppBlocks;
import rc55.mc.cauldronpp.api.PotionHelper;
import rc55.mc.cauldronpp.item.CauldronppItems;

public class LangZhCnDataGen extends FabricLanguageProvider {
    public LangZhCnDataGen(FabricDataGenerator dataGenerator) {
        super(dataGenerator, "zh_cn");
    }

    private static final String[] potionPrefixesTranslation = new String[]{"平凡的", "枯燥的", "平淡的", "清澈的",
            "乳白的", "弥漫的", "朴实的", "稀薄的", "粗制的", "平坦的",
            "笨重的", "笨拙的", "圆滑的", "平滑的", "倜傥的", "温和的",
            "浓稠的", "高雅的", "花哨的", "迷人的", "迅速的", "精致的",
            "亲切的", "闪亮的", "有力的", "犯规的", "无味的", "稠密的",
            "苛刻的", "辛辣的", "多毛的", "发臭的"};

    @Override
    public void generateTranslations(TranslationBuilder builder) {
        builder.add("modmenu.nameTranslation.cauldronpp", "Cauldron++");
        builder.add("modmenu.descriptionTranslation.cauldronpp", "Cauldron++是一个小型mod，旨在高版本中还原Beta 1.9-pre2中使用炼药锅的酿造系统。");
        builder.add(CauldronppBlocks.CPP_CAULDRON, "炼药锅（旧版）");
        builder.add(CauldronppItems.WATER_BOTTLE, "水瓶");
        PotionHelper.generatePotionNameTranslation(builder, potionPrefixesTranslation, "药水", "喷溅型药水", "滞留型药水", "药箭");
        builder.add(CauldronppItems.CPP_POTION, "%s药水");
        builder.add(CauldronppItems.CPP_SPLASH_POTION, "喷溅型%s药水");
        builder.add(CauldronppItems.CPP_LINGERING_POTION, "滞留型%s药水");
        builder.add(CauldronppItems.CPP_TIPPED_ARROW, "%s药箭");
    }
}
