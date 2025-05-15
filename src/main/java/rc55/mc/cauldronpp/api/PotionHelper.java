package rc55.mc.cauldronpp.api;

import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.PotionUtil;
import net.minecraft.registry.Registries;
import rc55.mc.cauldronpp.item.CauldronppItems;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PotionHelper {
    //酿造材料（地狱疣单独计算）
    public static final Map<Item, String> brewingMaterial = Map.of(Items.SUGAR, "+0", Items.GHAST_TEAR, "+11", Items.SPIDER_EYE, "+10+7+5",
            Items.FERMENTED_SPIDER_EYE, "+14+9", Items.BLAZE_POWDER, "+14", Items.MAGMA_CREAM, "+14+6+1");
    //药水类型（普通，喷溅，滞留）转化
    //0为普通，1为喷溅，2为滞留，3为药箭
    public static final Map<Item, Integer> brewingMaterialType = Map.of(Items.GLISTERING_MELON_SLICE, 0, Items.GUNPOWDER, 1, Items.DRAGON_BREATH, 2);

    //特别鸣谢 wwwweeeeee团队及retromcp项目 解析b1.9-pre2酿造逻辑
    //special thanks to wwwweeeeee team and the retromcp project
    public static final Map<StatusEffect, String> potionRequirements = new HashMap<>();//出现特定效果需要满足的条件
    public static final Map<StatusEffect, String> potionAmplifiers = new HashMap<>();//提升效果等级的条件
    //generic helper
    private static int getNumberInBinaryDamage(int index, int i1, int i2, int i3, int i4, int i5) {//返回二进制数index的i1~i5位组成的二进制数
        return (checkFlag(index, i1) ? 16 : 0) | (checkFlag(index, i2) ? 8 : 0) | (checkFlag(index, i3) ? 4 : 0) | (checkFlag(index, i4) ? 2 : 0) | (checkFlag(index, i5) ? 1 : 0);
    }
    private static boolean checkFlag(int i0, int i1) {//二进制数i0的i1位是否为1
        return (i0 & 1 << i1) != 0;
    }
    private static int countFlags(int i0) {//二进制数i0中有多少位为1
        int i1;
        for(i1 = 0; i0 > 0; ++i1) {
            i0 &= i0 - 1;
        }
        return i1;
    }
    private static int isFlagSet(int i0, int i1) {
        return checkFlag(i0, i1) ? 1 : 0;
    }
    private static int isFlagSet2(int i0, int i1) {
        return checkFlag(i0, i1) ? 0 : 1;
    }
    private static boolean checkBoolean(int i0, int i1) {
        return (i0 & 1 << i1 % 15) != 0;
    }
    //药水颜色
    public static int getPotionColor(int potionData) {
        int r = (getNumberInBinaryDamage(potionData, 2, 14, 11, 8, 5) ^ 3) << 3;
        int g = (getNumberInBinaryDamage(potionData, 0, 12, 9, 6, 3) ^ 6) << 3;
        int b = (getNumberInBinaryDamage(potionData, 13, 10, 4, 1, 7) ^ 8) << 3;
        return r << 16 | g << 8 | b;
    }
    //药水名字
    public static String getPotionPrefix(int potionData) {//b1.9-pre2原版
        int i = getNumberInBinaryDamage(potionData, 14, 9, 7, 3, 2);
        return potionPrefixes[i];
    }
    public static String getPotionPrefixTranslationKey(int potionType, int potionData) {//修改版，不同类型名字不同
        int i = getNumberInBinaryDamage(potionData, 14, 9, 7, 3, 2);
        return switch (potionType) {
            case 1 -> String.format(potionPrefixTranslationKeys[i], "potion.splash");
            case 2 -> String.format(potionPrefixTranslationKeys[i], "potion.lingering");
            case 3 -> String.format(potionPrefixTranslationKeys[i], "tippedArrow");
            default -> String.format(potionPrefixTranslationKeys[i], "potion");
        };
    }
    //地狱疣计算
    public static int applyMaterialNetherWart(int potionData) {
        if((potionData & 1) != 0) {
            potionData = ee_e(potionData);
        }

        return ff_fff_(potionData);
    }

    private static int ee_e(int i0) {
        if((i0 & 1) == 0) {
            return i0;
        } else {
            int i1;
            for(i1 = 14; (i0 & 1 << i1) == 0 && i1 >= 0; --i1) {//why is this thing empty??
            }

            if(i1 >= 2 && (i0 & 1 << i1 - 1) == 0) {
                if(i1 >= 0) {
                    i0 &= ~(1 << i1);
                }

                i0 <<= 1;
                if(i1 >= 0) {
                    i0 |= 1 << i1;
                    i0 |= 1 << i1 - 1;
                }

                return i0 & 32767;
            } else {
                return i0;
            }
        }
    }
    private static int ff_fff_(int i0) {
        int i1;
        for(i1 = 14; (i0 & 1 << i1) == 0 && i1 >= 0; --i1) {
        }

        if(i1 >= 0) {
            i0 &= ~(1 << i1);
        }

        int i2 = 0;

        for(int i3 = i0; i3 != i2; i0 = i2) {
            i3 = i0;
            i2 = 0;

            for(int i4 = 0; i4 < 15; ++i4) {
                boolean z5 = checkBoolean(i0, i4);
                if(z5) {
                    if(!checkBoolean(i0, i4 + 1) && checkBoolean(i0, i4 + 2)) {
                        z5 = false;
                    } else if(!checkBoolean(i0, i4 - 1) && checkBoolean(i0, i4 - 2)) {
                        z5 = false;
                    }
                } else {
                    z5 = checkBoolean(i0, i4 - 1) && checkBoolean(i0, i4 + 1);
                }

                if(z5) {
                    i2 |= 1 << i4;
                }
            }
        }

        if(i1 >= 0) {
            i2 |= 1 << i1;
        }

        return i2 & 32767;
    }
    //效果持续时间
    private static int aaA_(String string0, int i1, int i2, int i3) {
        if(i1 < string0.length() && i2 >= 0 && i1 < i2) {
            int i4 = string0.indexOf(124, i1);
            int i5;
            int i17;
            if(i4 >= 0 && i4 < i2) {
                i5 = aaA_(string0, i1, i4 - 1, i3);
                if(i5 > 0) {
                    return i5;
                } else {
                    i17 = aaA_(string0, i4 + 1, i2, i3);
                    return Math.max(i17, 0);
                }
            } else {
                i5 = string0.indexOf(38, i1);
                if(i5 >= 0 && i5 < i2) {
                    i17 = aaA_(string0, i1, i5 - 1, i3);
                    if(i17 <= 0) {
                        return 0;
                    } else {
                        int i18 = aaA_(string0, i5 + 1, i2, i3);
                        return i18 <= 0 ? 0 : (Math.max(i17, i18));
                    }
                } else {
                    boolean z6 = false;
                    boolean z7 = false;
                    boolean z8 = false;
                    boolean z9 = false;
                    boolean z10 = false;
                    byte b11 = -1;
                    int i12 = 0;
                    int i13 = 0;
                    int i14 = 0;

                    for(int i15 = i1; i15 < i2; ++i15) {
                        char c16 = string0.charAt(i15);
                        if(c16 >= 48 && c16 <= 57) {
                            if(z6) {
                                i13 = c16 - 48;
                                z7 = true;
                            } else {
                                i12 *= 10;
                                i12 += c16 - 48;
                                z8 = true;
                            }
                        } else if(c16 == 42) {
                            z6 = true;
                        } else if(c16 == 33) {
                            if(z8) {
                                i14 += a_a_aa(z9, z7, z10, b11, i12, i13, i3);
                                z9 = false;
                                z10 = false;
                                z6 = false;
                                z7 = false;
                                z8 = false;
                                i13 = 0;
                                i12 = 0;
                                b11 = -1;
                            }

                            z9 = true;
                        } else if(c16 == 45) {
                            if(z8) {
                                i14 += a_a_aa(z9, z7, z10, b11, i12, i13, i3);
                                z9 = false;
                                z10 = false;
                                z6 = false;
                                z7 = false;
                                z8 = false;
                                i13 = 0;
                                i12 = 0;
                                b11 = -1;
                            }

                            z10 = true;
                        } else if(c16 != 61 && c16 != 60 && c16 != 62) {
                            if(c16 == 43 && z8) {
                                i14 += a_a_aa(z9, z7, z10, b11, i12, i13, i3);
                                z9 = false;
                                z10 = false;
                                z6 = false;
                                z7 = false;
                                z8 = false;
                                i13 = 0;
                                i12 = 0;
                                b11 = -1;
                            }
                        } else {
                            if(z8) {
                                i14 += a_a_aa(z9, z7, z10, b11, i12, i13, i3);
                                z9 = false;
                                z10 = false;
                                z6 = false;
                                z7 = false;
                                z8 = false;
                                i13 = 0;
                                i12 = 0;
                                b11 = -1;
                            }

                            if(c16 == 61) {
                                b11 = 0;
                            } else if(c16 == 60) {
                                b11 = 2;
                            } else if(c16 == 62) {
                                b11 = 1;
                            }
                        }
                    }

                    if(z8) {
                        i14 += a_a_aa(z9, z7, z10, b11, i12, i13, i3);
                    }

                    return i14;
                }
            }
        } else {
            return 0;
        }
    }
    private static int a_a_aa(boolean z0, boolean z1, boolean z2, int i3, int i4, int i5, int i6) {
        int i7 = 0;
        if(z0) {
            i7 = isFlagSet2(i6, i4);
        } else if(i3 != -1) {
            if(i3 == 0 && countFlags(i6) == i4) {
                i7 = 1;
            } else if(i3 == 1 && countFlags(i6) > i4) {
                i7 = 1;
            } else if(i3 == 2 && countFlags(i6) < i4) {
                i7 = 1;
            }
        } else {
            i7 = isFlagSet(i6, i4);
        }

        if(z1) {
            i7 *= i5;
        }

        if(z2) {
            i7 *= -1;
        }

        return i7;
    }
    //放入材料后转换对应药水数据
    public static int applyMaterial(int potionData, String materialProperty) {
        byte b2 = 0;
        int i3 = materialProperty.length();
        boolean z4 = false;
        boolean z5 = false;
        boolean z6 = false;
        int i7 = 0;

        for(int i8 = b2; i8 < i3; ++i8) {
            char c9 = materialProperty.charAt(i8);
            if(c9 >= 48 && c9 <= 57) {//number 0~9
                i7 *= 10;
                i7 += c9 - 48;
                z4 = true;
            } else if(c9 == 33) {//! ascii 33
                if(z4) {
                    potionData = updatePotionData(potionData, i7, z6, z5);
                    z5 = false;
                    z6 = false;
                    z4 = false;
                    i7 = 0;
                }

                z5 = true;
            } else if(c9 == 45) {//- ascii 45
                if(z4) {
                    potionData = updatePotionData(potionData, i7, z6, z5);
                    z5 = false;
                    z6 = false;
                    z4 = false;
                    i7 = 0;
                }

                z6 = true;
            } else if(c9 == 43 && z4) {//+ ascii 43
                potionData = updatePotionData(potionData, i7, z6, z5);
                z5 = false;
                z6 = false;
                z4 = false;
                i7 = 0;
            }
        }

        if(z4) {
            potionData = updatePotionData(potionData, i7, z6, z5);
        }

        return potionData & 32767;
    }
    private static int updatePotionData(int potionData, int i1, boolean z2, boolean z3) {
        if(z2) {
            potionData &= ~(1 << i1);
        } else if(z3) {
            if((potionData & 1 << i1) != 0) {
                potionData &= ~(1 << i1);
            } else {
                potionData |= 1 << i1;
            }
        } else {
            potionData |= 1 << i1;
        }

        return potionData;
    }
    //获取对应药水的所有效果
    public static List<StatusEffectInstance> getEffects(int potionData) {
        ArrayList<StatusEffectInstance> arrayList1 = new ArrayList<>();
        for (StatusEffect effect : Registries.STATUS_EFFECT) {
            if (effect != null) {
                String effectRequirement = potionRequirements.get(effect);
                if (effectRequirement != null) {
                    int duration = aaA_(effectRequirement, 0, effectRequirement.length(), potionData);
                    if (duration > 0) {
                        int amplifier = 0;
                        String amplifierRequirement = potionAmplifiers.get(effect);
                        if (amplifierRequirement != null) {
                            amplifier = aaA_(amplifierRequirement, 0, amplifierRequirement.length(), potionData);
                            if (amplifier < 0) {
                                amplifier = 0;
                            }
                        }

                        if (effect.isInstant()) {
                            duration = 1;
                        } else {
                            duration = 1200 * (duration * 3 + (duration - 1) * 2);
                            if (!effect.isBeneficial()) {//reduce harmful effect duration
                                duration >>= 1;
                            }
                        }

                        if (arrayList1 == null) {
                            arrayList1 = new ArrayList<>();
                        }

                        arrayList1.add(new StatusEffectInstance(effect, duration, amplifier));
                    }
                }
            }
        }

        return arrayList1;
    }
    //药水名字
    //b1.9-pre2原版
    public static final String[] potionPrefixes = new String[]{"potion.prefix.mundane", "potion.prefix.uninteresting", "potion.prefix.bland", "potion.prefix.clear",
            "potion.prefix.milky", "potion.prefix.diffuse", "potion.prefix.artless", "potion.prefix.thin", "potion.prefix.awkward", "potion.prefix.flat",
            "potion.prefix.bulky", "potion.prefix.bungling", "potion.prefix.buttered", "potion.prefix.smooth", "potion.prefix.suave", "potion.prefix.debonair",
            "potion.prefix.thick", "potion.prefix.elegant", "potion.prefix.fancy", "potion.prefix.charming", "potion.prefix.dashing", "potion.prefix.refined",
            "potion.prefix.cordial", "potion.prefix.sparkling", "potion.prefix.potent", "potion.prefix.foul", "potion.prefix.odorless", "potion.prefix.rank",
            "potion.prefix.harsh", "potion.prefix.acrid", "potion.prefix.gross", "potion.prefix.stinky"};
    //修改版
    public static final String[] potionPrefixTranslationKeys = new String[]{"%s.prefix.mundane", "%s.prefix.uninteresting", "%s.prefix.bland", "%s.prefix.clear",
            "%s.prefix.milky", "%s.prefix.diffuse", "%s.prefix.artless", "%s.prefix.thin", "%s.prefix.awkward", "%s.prefix.flat",
            "%s.prefix.bulky", "%s.prefix.bungling", "%s.prefix.buttered", "%s.prefix.smooth", "%s.prefix.suave", "%s.prefix.debonair",
            "%s.prefix.thick", "%s.prefix.elegant", "%s.prefix.fancy", "%s.prefix.charming", "%s.prefix.dashing", "%s.prefix.refined",
            "%s.prefix.cordial", "%s.prefix.sparkling", "%s.prefix.potent", "%s.prefix.foul", "%s.prefix.odorless", "%s.prefix.rank",
            "%s.prefix.harsh", "%s.prefix.acrid", "%s.prefix.gross", "%s.prefix.stinky"};

    static {
        //原版
        //不同效果的需求
        potionRequirements.put(StatusEffects.SPEED, "!10 & !4 & 5*2+0 & >1 | !7 & !4 & 5*2+0 & >1");
        potionRequirements.put(StatusEffects.SLOWNESS, "10 & 7 & !4 & 7+5+1-0");
        potionRequirements.put(StatusEffects.HASTE, "2 & 12+2+6-1-7 & <8");
        potionRequirements.put(StatusEffects.MINING_FATIGUE, "!2 & !1*2-9 & 14-5");
        potionRequirements.put(StatusEffects.STRENGTH, "9 & 3 & 9+4+5 & <11");
        potionRequirements.put(StatusEffects.INSTANT_HEALTH, "11 & <6");
        potionRequirements.put(StatusEffects.INSTANT_DAMAGE, "!11 & 1 & 10 & !7");
        potionRequirements.put(StatusEffects.JUMP_BOOST, "8 & 2+0 & <5");
        potionRequirements.put(StatusEffects.NAUSEA, "8*2-!7+4-11 & !2 | 13 & 11 & 2*3-1-5");
        potionRequirements.put(StatusEffects.REGENERATION, "!14 & 13*3-!0-!5-8");
        potionRequirements.put(StatusEffects.RESISTANCE, "10 & 4 & 10+5+6 & <9");
        potionRequirements.put(StatusEffects.FIRE_RESISTANCE, "14 & !5 & 6-!1 & 14+13+12");
        potionRequirements.put(StatusEffects.WATER_BREATHING, "0+1+12 & !6 & 10 & !11 & !13");
        potionRequirements.put(StatusEffects.INVISIBILITY, "2+5+13-0-4 & !7 & !1 & >5");
        potionRequirements.put(StatusEffects.BLINDNESS, "9 & !1 & !5 & !3 & =3");
        potionRequirements.put(StatusEffects.NIGHT_VISION, "8*2-!7 & 5 & !0 & >3");
        potionRequirements.put(StatusEffects.HUNGER, ">4>6>8-3-8+2");
        potionRequirements.put(StatusEffects.WEAKNESS, "=1>5>7>9+3-7-2-11 & !10 & !0");
        potionRequirements.put(StatusEffects.POISON, "12+9 & !13 & !0");
        //提升效果等级的需求
        potionAmplifiers.put(StatusEffects.SPEED, "7+!3-!1");
        potionAmplifiers.put(StatusEffects.HASTE, "1+0-!11");
        potionAmplifiers.put(StatusEffects.STRENGTH, "2+7-!12");
        potionAmplifiers.put(StatusEffects.INSTANT_HEALTH, "11+!0-!1-!14");
        potionAmplifiers.put(StatusEffects.INSTANT_DAMAGE, "!11-!14+!0-!1");
        potionAmplifiers.put(StatusEffects.RESISTANCE, "12-!2");
        potionAmplifiers.put(StatusEffects.POISON, "14>5");
    }

    //药水名字本地化数据生成
    public static void generatePotionNameTranslation(FabricLanguageProvider.TranslationBuilder builder, String[] translation, String potionTranslation,
                                                     String splashPotionTranslation, String lingeringPotionTranslation, String tippedArrowTranslation) {
        for (int i = 0; i < potionPrefixes.length; i++) {
            builder.add(String.format(potionPrefixTranslationKeys[i], "potion"), String.format(potionTranslation, translation[i]));
            builder.add(String.format(potionPrefixTranslationKeys[i], "potion.splash"), String.format(splashPotionTranslation, translation[i]));
            builder.add(String.format(potionPrefixTranslationKeys[i], "potion.lingering"), String.format(lingeringPotionTranslation, translation[i]));
            builder.add(String.format(potionPrefixTranslationKeys[i], "tippedArrow"),String.format(tippedArrowTranslation, translation[i]));
        }
    }

    //返回对应药水的物品堆
    public static ItemStack getPotionItem(int potionType, int potionData, int amount) {
        if (potionData == 0 && potionType == 0) return CauldronppItems.WATER_BOTTLE.getDefaultStack();
        ItemStack stack;
        switch (potionType) {
            case 1 -> stack = new ItemStack(CauldronppItems.CPP_SPLASH_POTION, amount);
            case 2 -> stack = new ItemStack(CauldronppItems.CPP_LINGERING_POTION, amount);
            case 3 -> stack = new ItemStack(CauldronppItems.CPP_TIPPED_ARROW, amount);
            default -> stack = new ItemStack(CauldronppItems.CPP_POTION, amount);
        }
        PotionUtil.setCustomPotionEffects(stack, getEffects(potionData));
        stack.getOrCreateNbt().putInt("CustomPotionColor", getPotionColor(potionData));
        stack.getOrCreateNbt().putInt("PotionData", potionData);
        return stack;
    }
    public static ItemStack getPotionItem(int potionType, int potionData) {
        return getPotionItem(potionType, potionData, 1);
    }
}
