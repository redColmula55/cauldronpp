package rc55.mc.cauldronpp.api;

import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import org.jetbrains.annotations.Nullable;

public enum CppCauldronLiquidType {
    NONE(0, false),
    WATER(1, Items.WATER_BUCKET, SoundEvents.ITEM_BUCKET_FILL, true),
    POTION(2, true),
    LAVA(3, Items.LAVA_BUCKET, SoundEvents.ITEM_BUCKET_FILL_LAVA, false),
    POWDER_SNOW(4, Items.POWDER_SNOW_BUCKET, SoundEvents.ITEM_BUCKET_FILL_POWDER_SNOW, false),
    COLORED_WATER(5, true);

    public static CppCauldronLiquidType byId(int id) {
        return switch (id) {
            case 1 -> WATER;
            case 2 -> POTION;
            case 3 -> LAVA;
            case 4 -> POWDER_SNOW;
            case 5 -> COLORED_WATER;
            default -> NONE;
        };
    }

    private final int id;
    private final Item bucketItem;
    private final SoundEvent bucketFillSound;
    private final boolean shouldRenderColor;

    CppCauldronLiquidType(int id, Item bucketItem, SoundEvent bucketFillSound, boolean shouldRenderColor) {
        this.id = id;
        this.bucketItem = bucketItem;
        this.bucketFillSound = bucketFillSound;
        this.shouldRenderColor = shouldRenderColor;
    }
    CppCauldronLiquidType(int id, boolean shouldRenderColor) {
        this(id, null, null, shouldRenderColor);
    }

    public int getId() {
        return this.id;
    }
    @Nullable
    public Item getBucketItem() {
        return this.bucketItem;
    }
    @Nullable
    public SoundEvent getBucketFillSound() {
        return this.bucketFillSound;
    }
    public boolean shouldRenderColor() {
        return this.shouldRenderColor;
    }
}
