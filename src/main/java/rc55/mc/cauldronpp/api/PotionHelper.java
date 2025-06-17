package rc55.mc.cauldronpp.api;

import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.Potions;
import net.minecraft.registry.entry.RegistryEntry;
import rc55.mc.cauldronpp.item.CauldronppItems;

import java.util.*;

public class PotionHelper {
    //酿造材料（地狱疣单独计算）
    public static final Map<Item, String> brewingMaterial = new HashMap<>();
    //加水后改变
    public static final String WATER_MATERIAL = "-1-3-5-7-9-11-13";
    //药水类型
    //0为普通，1为喷溅，2为滞留，3为药箭
    public static final byte DEFAULT_TYPE = 0;
    public static final byte SPLASH_TYPE = 1;
    public static final byte LINGERING_TYPE = 2;
    public static final byte ARROW_TYPE = 3;
    //类型（普通，喷溅，滞留）转化
    public static final Map<Item, Byte> brewingMaterialType = Map.of(Items.GLISTERING_MELON_SLICE, DEFAULT_TYPE, Items.GUNPOWDER, SPLASH_TYPE, Items.DRAGON_BREATH, LINGERING_TYPE);

    //特别鸣谢 wwwweeeeee团队及retromcp项目 解析b1.9-pre2酿造逻辑
    //special thanks to wwwweeeeee team and the retromcp project
    public static final Map<RegistryEntry<StatusEffect>, String> potionRequirements = new HashMap<>();//出现特定效果需要满足的条件
    public static final Map<RegistryEntry<StatusEffect>, String> potionAmplifiers = new HashMap<>();//提升效果等级的条件
    //二进制数相关计算
    private static int getNumberInBinary(int index, int i1, int i2, int i3, int i4, int i5) {//返回二进制数index的i1~i5位组成的二进制数
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
    private static int isFlagNotSet(int i0, int i1) {
        return checkFlag(i0, i1) ? 0 : 1;
    }
    private static boolean checkBoolean(int i0, int i1) {
        return (i0 & 1 << i1 % 15) != 0;
    }
    //药水颜色
    public static int getPotionColor(int potionData) {
        int r = (getNumberInBinary(potionData, 2, 14, 11, 8, 5) ^ 3) << 3;
        int g = (getNumberInBinary(potionData, 0, 12, 9, 6, 3) ^ 6) << 3;
        int b = (getNumberInBinary(potionData, 13, 10, 4, 1, 7) ^ 8) << 3;
        return r << 16 | g << 8 | b;
    }
    //药水名字
    public static String getPotionPrefixTranslationKey(int potionData) {
        int i = getNumberInBinary(potionData, 14, 9, 7, 3, 2);
        return potionPrefixTranslationKeys[i];
    }
    //地狱疣计算
    public static int applyMaterialNetherWart(int potionData) {
        if((potionData & 1) != 0) {
            potionData = applyNetherWart1(potionData);
        }

        return applyNetherWart(potionData);
    }

    private static int applyNetherWart1(int i0) {
        if((i0 & 1) == 0) {
            return i0;
        } else {
            int i1;
            for(i1 = 14; (i0 & 1 << i1) == 0 && i1 >= 0; --i1) {//i0有多少位
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
    private static int applyNetherWart(int i0) {
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
    //效果等级&持续时间 不符合要求返回0
    private static int getEffectMultiplier(String requirement, int startPos, int endPos, int potionData) {
        if(startPos < requirement.length() && endPos >= 0 && startPos < endPos) {
            int i4 = requirement.indexOf(124, startPos);// | ascii 124 符号|所在位置
            int i5;
            if(i4 >= 0 && i4 < endPos) {//具有 “|” (或) 运算符
                int front = getEffectMultiplier(requirement, startPos, i4 - 1, potionData);//前半
                if(front > 0) {
                    return front;
                } else {
                    int back = getEffectMultiplier(requirement, i4 + 1, endPos, potionData);//后半
                    return Math.max(back, 0);
                }
            } else {
                i5 = requirement.indexOf(38, startPos);// & ascii 38 符号&所在位置
                if(i5 >= 0 && i5 < endPos) {//具有 “&” (与) 运算符
                    int front = getEffectMultiplier(requirement, startPos, i5 - 1, potionData);
                    if(front <= 0) {
                        return 0;
                    } else {
                        int back = getEffectMultiplier(requirement, i5 + 1, endPos, potionData);
                        return back <= 0 ? 0 : (Math.max(front, back));
                    }
                } else {
                    boolean z6 = false;
                    boolean z7 = false;
                    boolean needsValueUpdate = false;
                    boolean z9 = false;
                    boolean z10 = false;
                    byte operation = -1;//模式 未指定为-1 =为0 >为1 <为2
                    int flag = 0;
                    int i13 = 0;
                    int i14 = 0;

                    for(int i15 = startPos; i15 < endPos; ++i15) {//遍历要求
                        char thisChar = requirement.charAt(i15);//当前位置
                        if(thisChar >= 48 && thisChar <= 57) {//数字0~9
                            if(z6) {//乘数
                                i13 = thisChar - 48;
                                z7 = true;
                            } else {//位数
                                flag *= 10;
                                flag += thisChar - 48;
                                //计算后更新计算用属性
                                //以符号分隔为一组（e.g. 在+1-15<13中，有+1 -15 <13 3组）
                                //检测到第二组才更新第一组
                                //遍历完成后更新最后一组
                                needsValueUpdate = true;
                            }
                        } else if(thisChar == 42) {// * ascii 42
                            z6 = true;
                        } else if(thisChar == 33) {// ! ascii 33
                            if(needsValueUpdate) {
                                i14 += updateEffectWeigh(z9, z7, z10, operation, flag, i13, potionData);
                                z9 = false;
                                z10 = false;
                                z6 = false;
                                z7 = false;
                                needsValueUpdate = false;
                                i13 = 0;
                                flag = 0;
                                operation = -1;
                            }

                            z9 = true;
                        } else if(thisChar == 45) {// - ascii 45
                            if(needsValueUpdate) {
                                i14 += updateEffectWeigh(z9, z7, z10, operation, flag, i13, potionData);
                                z9 = false;
                                z10 = false;
                                z6 = false;
                                z7 = false;
                                needsValueUpdate = false;
                                i13 = 0;
                                flag = 0;
                                operation = -1;
                            }

                            z10 = true;
                        } else if(thisChar != 61 && thisChar != 60 && thisChar != 62) { // ascii 60< 61= 62>
                            if(thisChar == 43 && needsValueUpdate) {// + ascii 43
                                i14 += updateEffectWeigh(z9, z7, z10, operation, flag, i13, potionData);
                                z9 = false;
                                z10 = false;
                                z6 = false;
                                z7 = false;
                                needsValueUpdate = false;
                                i13 = 0;
                                flag = 0;
                                operation = -1;
                            }
                        } else {
                            if(needsValueUpdate) {
                                i14 += updateEffectWeigh(z9, z7, z10, operation, flag, i13, potionData);
                                z9 = false;
                                z10 = false;
                                z6 = false;
                                z7 = false;
                                needsValueUpdate = false;
                                i13 = 0;
                                flag = 0;
                                operation = -1;
                            }
                            //设置操作
                            if(thisChar == 61) {// = ascii 61
                                operation = 0;
                            } else if(thisChar == 60) {// < ascii 60
                                operation = 2;
                            } else if(thisChar == 62) {// > ascii 62
                                operation = 1;
                            }
                        }
                    }

                    if(needsValueUpdate) {//更新最后一组属性
                        i14 += updateEffectWeigh(z9, z7, z10, operation, flag, i13, potionData);
                    }

                    return i14;
                }
            }
        } else {
            return 0;//没有效果或者不提升等级
        }
    }
    private static int updateEffectWeigh(boolean z0, boolean z1, boolean z2, int operation, int flagCount, int i5, int potionData) {
        int i7 = 0;
        if(z0) {// !
            i7 = isFlagNotSet(potionData, flagCount);
        } else if(operation != -1) {//判断模式 未指定为-1 =为0 >为1 <为2
            if(operation == 0 && countFlags(potionData) == flagCount) {
                i7 = 1;
            } else if(operation == 1 && countFlags(potionData) > flagCount) {
                i7 = 1;
            } else if(operation == 2 && countFlags(potionData) < flagCount) {
                i7 = 1;
            }
        } else {
            i7 = isFlagSet(potionData, flagCount);
        }

        if(z1) {// *
            i7 *= i5;
        }

        if(z2) {// -
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

        for(int i8 = b2; i8 < i3; ++i8) {//遍历
            char thisChar = materialProperty.charAt(i8);
            if(thisChar >= 48 && thisChar <= 57) {//数字 0~9
                i7 *= 10;
                i7 += thisChar - 48;
                z4 = true;
            } else if(thisChar == 33) {//! ascii 33
                if(z4) {
                    potionData = updatePotionData(potionData, i7, z6, z5);
                    z5 = false;
                    z6 = false;
                    z4 = false;
                    i7 = 0;
                }

                z5 = true;
            } else if(thisChar == 45) {//- ascii 45
                if(z4) {
                    potionData = updatePotionData(potionData, i7, z6, z5);
                    z5 = false;
                    z6 = false;
                    z4 = false;
                    i7 = 0;
                }

                z6 = true;
            } else if(thisChar == 43 && z4) {//+ ascii 43
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
    private static int updatePotionData(int potionData, int flag, boolean z2, boolean z3) {
        if(z2) {//设置为0
            potionData &= ~(1 << flag);
        } else if(z3) {//反转
            if((potionData & 1 << flag) != 0) {//原为1,设为0
                potionData &= ~(1 << flag);
            } else {//原为0,设为1
                potionData |= 1 << flag;
            }
        } else {//设置为1
            potionData |= 1 << flag;
        }

        return potionData;
    }
    //获取对应药水的所有效果
    public static List<StatusEffectInstance> getEffects(int potionData) {
        ArrayList<StatusEffectInstance> effects = new ArrayList<>();
        for (RegistryEntry<StatusEffect> registryEntry : potionRequirements.keySet()) {
            StatusEffect effect = registryEntry.value();
            if (effect != null) {
                String effectRequirement = potionRequirements.get(registryEntry);
                if (effectRequirement != null) {
                    int duration = getEffectMultiplier(effectRequirement, 0, effectRequirement.length(), potionData);
                    if (duration > 0) {
                        int amplifier = 0;
                        String amplifierRequirement = potionAmplifiers.get(registryEntry);
                        if (amplifierRequirement != null) {
                            amplifier = getEffectMultiplier(amplifierRequirement, 0, amplifierRequirement.length(), potionData);
                            if (amplifier < 0) {
                                amplifier = 0;
                            }
                        }

                        if (effect.isInstant()) {
                            duration = 1;
                        } else {
                            duration = 1200 * (duration * 3 + (duration - 1) * 2);
                            if (!effect.isBeneficial()) {//减少有害效果时常
                                duration >>= 1;
                            }
                        }

                        effects.add(new StatusEffectInstance(registryEntry, duration, amplifier));
                    }
                }
            }
        }

        return effects;
    }
    //药水名字
    private static final String[] potionPrefixTranslationKeys = new String[]{"potion.prefix.mundane", "potion.prefix.uninteresting", "potion.prefix.bland", "potion.prefix.clear",
            "potion.prefix.milky", "potion.prefix.diffuse", "potion.prefix.artless", "potion.prefix.thin", "potion.prefix.awkward", "potion.prefix.flat",
            "potion.prefix.bulky", "potion.prefix.bungling", "potion.prefix.buttered", "potion.prefix.smooth", "potion.prefix.suave", "potion.prefix.debonair",
            "potion.prefix.thick", "potion.prefix.elegant", "potion.prefix.fancy", "potion.prefix.charming", "potion.prefix.dashing", "potion.prefix.refined",
            "potion.prefix.cordial", "potion.prefix.sparkling", "potion.prefix.potent", "potion.prefix.foul", "potion.prefix.odorless", "potion.prefix.rank",
            "potion.prefix.harsh", "potion.prefix.acrid", "potion.prefix.gross", "potion.prefix.stinky"};

    static {
        //原版
        //不同效果的需求 感谢wwwweeeeee团队 解读此部分逻辑
        //数字为位数 +为某位是否为1 -为某位是否为0 !为为1的位数不为多少 >=<为为1的位数大于/等于/小于多少 |为或者(满足一个条件即可) &为并且(所有条件均需要满足) *暂时不明
        //逻辑优先计算或（|） 其次为与（&）
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
        //酿造材料
        //数字为位数 +为将某位设置为1 -为将某位设置为0 !为反转某位(若它为1,将其设置为0;若它为0,将其设置为1)
        brewingMaterial.put(Items.SUGAR, "+0");
        brewingMaterial.put(Items.GHAST_TEAR, "+11");
        brewingMaterial.put(Items.SPIDER_EYE, "+10+7+5");
        brewingMaterial.put(Items.FERMENTED_SPIDER_EYE, "+14+9");
        brewingMaterial.put(Items.BLAZE_POWDER, "+14");
        brewingMaterial.put(Items.MAGMA_CREAM, "+14+6+1");
        //新增
        //效果
        potionRequirements.put(StatusEffects.WITHER, "=6>7 & +14+2");
        potionRequirements.put(StatusEffects.HEALTH_BOOST, "6+8 & !3-9 & =10>14");
        potionRequirements.put(StatusEffects.ABSORPTION, "=1>4 & !5!3 | 2*4+5<13");
        potionRequirements.put(StatusEffects.SLOW_FALLING, "+5+7+9-14");
        potionRequirements.put(StatusEffects.SATURATION, "=0=5=7=8");
        potionRequirements.put(StatusEffects.LEVITATION, "!11 & =3>9");
        potionRequirements.put(StatusEffects.LUCK, "8>12 & !4 & -1+6-0");
        potionRequirements.put(StatusEffects.UNLUCK, "2-8 & !14");
        potionRequirements.put(StatusEffects.BAD_OMEN, "6+7-13 & >9");
        potionRequirements.put(StatusEffects.DARKNESS, "!9 & =0=6=11");
        potionRequirements.put(StatusEffects.GLOWING, "6>13 & -1+4 & !14-!0+!7");
        potionRequirements.put(StatusEffects.OOZING, "<6<5<1 & +9-8+!12 | =14-7-2>13 & -1*5+6");
        potionRequirements.put(StatusEffects.WEAVING, "2>3 & +5+8-11 & !10 & !14");
        potionRequirements.put(StatusEffects.WIND_CHARGED, "-3-7-12 & 5+13 & !0");
        potionRequirements.put(StatusEffects.INFESTED, "=5+6+9 & !11 | >9 & 3+4 & !8 & 10*6-1");
        //提升效果等级的需求
        potionAmplifiers.put(StatusEffects.WITHER, "6-5 & !1 & =8");
        potionAmplifiers.put(StatusEffects.HEALTH_BOOST, "!5 & !3 & =11");
        potionAmplifiers.put(StatusEffects.ABSORPTION, "=11=4 & -5+14");
        potionAmplifiers.put(StatusEffects.MINING_FATIGUE, "!6 & 2 & -1-9");
        potionAmplifiers.put(StatusEffects.SATURATION, "=1=9+8-10");
        potionAmplifiers.put(StatusEffects.LEVITATION, "!7 & !5 & 12+2");
        potionAmplifiers.put(StatusEffects.LUCK, "=6=8=9 | <14<7 & !2");
        potionAmplifiers.put(StatusEffects.UNLUCK, "-4+6 & !0 & >11");
        potionAmplifiers.put(StatusEffects.BAD_OMEN, "6+2-1 & >5>8 & !13");
        //酿造材料
        brewingMaterial.put(Items.RABBIT_FOOT, "!8-10+1");
        brewingMaterial.put(Items.GLOWSTONE_DUST, "+8+3");
        brewingMaterial.put(Items.REDSTONE, "-6-1+0");
        brewingMaterial.put(Items.GOLDEN_CARROT, "-5+12!4");
        brewingMaterial.put(Items.PHANTOM_MEMBRANE, "+5+7+11");
        brewingMaterial.put(Items.SLIME_BALL, "+11-13");
        brewingMaterial.put(Items.CHORUS_FRUIT, "!12-4+6");
    }

    //药水名字本地化数据生成
    public static void generatePotionNameTranslation(FabricLanguageProvider.TranslationBuilder builder, String[] translation, String potionTranslation,
                                                     String splashPotionTranslation, String lingeringPotionTranslation, String tippedArrowTranslation) {
        for (int i = 0; i < potionPrefixTranslationKeys.length; i++) {
            builder.add(potionPrefixTranslationKeys[i], translation[i]);
        }
        builder.add(CauldronppItems.CPP_POTION.getTranslationKey()+".default_name", potionTranslation);
        builder.add(CauldronppItems.CPP_SPLASH_POTION.getTranslationKey()+".default_name", splashPotionTranslation);
        builder.add(CauldronppItems.CPP_LINGERING_POTION.getTranslationKey()+".default_name", lingeringPotionTranslation);
        builder.add(CauldronppItems.CPP_TIPPED_ARROW.getTranslationKey()+".default_name", tippedArrowTranslation);
    }

    //返回对应药水的物品堆
    public static ItemStack getPotionItem(byte potionType, int potionData, int amount) {
        if (potionData == 0 && potionType == 0) return CauldronppItems.WATER_BOTTLE.getDefaultStack();
        ItemStack stack;
        switch (potionType) {
            case SPLASH_TYPE -> stack = new ItemStack(CauldronppItems.CPP_SPLASH_POTION, amount);
            case LINGERING_TYPE -> stack = new ItemStack(CauldronppItems.CPP_LINGERING_POTION, amount);
            case ARROW_TYPE -> stack = new ItemStack(CauldronppItems.CPP_TIPPED_ARROW, amount);
            default -> stack = new ItemStack(CauldronppItems.CPP_POTION, amount);
        }
        NbtCompound nbt = new NbtCompound();
        nbt.putInt("PotionData", potionData);
        stack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(nbt));
        stack.set(DataComponentTypes.POTION_CONTENTS, new PotionContentsComponent(Optional.empty(), Optional.of(getPotionColor(potionData)), getEffects(potionData)));
        return stack;
    }
    public static ItemStack getPotionItem(byte potionType, int potionData) {
        return getPotionItem(potionType, potionData, 1);
    }
    //是否为水瓶
    public static boolean isWaterBottle(ItemStack stack) {
        PotionContentsComponent component = stack.getOrDefault(DataComponentTypes.POTION_CONTENTS, PotionContentsComponent.DEFAULT);
        return stack.isOf(CauldronppItems.WATER_BOTTLE) || (stack.isOf(Items.POTION) && component.matches(Potions.WATER));
    }
}
