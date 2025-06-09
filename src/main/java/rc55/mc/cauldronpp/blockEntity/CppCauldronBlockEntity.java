package rc55.mc.cauldronpp.blockEntity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.color.world.BiomeColors;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import rc55.mc.cauldronpp.api.CppCauldronBehavior;
import rc55.mc.cauldronpp.api.CppCauldronLiquidType;
import rc55.mc.cauldronpp.api.PotionHelper;
import rc55.mc.cauldronpp.item.CauldronppItems;

public class CppCauldronBlockEntity extends BlockEntity {
    public CppCauldronBlockEntity(BlockPos pos, BlockState state) {
        super(CauldronppBlockEntityTypes.CAULDRON, pos, state);
    }

    private CppCauldronLiquidType liquidType = CppCauldronLiquidType.NONE;
    private int liquidData;
    private byte potionType;
    private int amount;

    public static void tick(World world, BlockPos pos, BlockState state, CppCauldronBlockEntity blockEntity) {
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.liquidData = nbt.getInt("LiquidData");
        this.potionType = nbt.getByte("PotionType");
        this.amount = nbt.getInt("Level");
        this.liquidType = CppCauldronLiquidType.byId(nbt.getInt("LiquidType"));
    }
    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putInt("LiquidData", this.liquidData);
        nbt.putByte("PotionType", this.potionType);
        nbt.putInt("Level", this.amount);
        nbt.putInt("LiquidType", this.liquidType.getId());
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return this.createNbt();
    }
    //更新锅内流体状态
    public ActionResult updateLiquid(World world, BlockPos pos, BlockState state, PlayerEntity player, Hand hand, ItemStack stack) {
        CppCauldronBehavior behavior = CppCauldronBehavior.BEHAVIORS.getOrDefault(stack.getItem(), CppCauldronBehavior.EMPTY);
        return behavior.interact(world, pos, state, this, player, hand, stack);
    }
    //水，药水
    public ActionResult updatePotion(World world, BlockPos pos, BlockState state, PlayerEntity player, Hand hand, ItemStack stack) {
        String materialProperty = PotionHelper.brewingMaterial.get(stack.getItem());
        byte typeMaterialProperty = PotionHelper.brewingMaterialType.getOrDefault(stack.getItem(), ((byte) -1));
        if (stack.isOf(Items.WATER_BUCKET) && !this.isFull() && (this.canBrew() || this.isEmpty())) {
            //加水（水桶）
            if (this.isEmpty()) this.liquidType = CppCauldronLiquidType.WATER;
            this.amount = CppCauldronBehavior.BUCKET_LEVEL;
            this.liquidData = PotionHelper.applyMaterial(this.liquidData, PotionHelper.WATER_MATERIAL);
            player.setStackInHand(hand, ItemUsage.exchangeStack(stack, player, Items.BUCKET.getDefaultStack()));
            world.playSound(null, pos, SoundEvents.ITEM_BUCKET_EMPTY, SoundCategory.BLOCKS);
            this.markDirty();
            return ActionResult.SUCCESS;
        } else if (((stack.isOf(Items.POTION) && PotionUtil.getPotion(stack) == Potions.WATER) || stack.isOf(CauldronppItems.WATER_BOTTLE)) && !this.isFull() && (this.canBrew() || this.isEmpty())) {
            //加水（水瓶）
            if (this.isEmpty()) this.liquidType = CppCauldronLiquidType.WATER;
            this.increaseAmount(CppCauldronBehavior.BOTTLE_LEVEL);
            this.liquidData = PotionHelper.applyMaterial(this.liquidData, PotionHelper.WATER_MATERIAL);
            player.setStackInHand(hand, ItemUsage.exchangeStack(stack, player, Items.GLASS_BOTTLE.getDefaultStack()));
            world.playSound(null, pos, SoundEvents.ITEM_BOTTLE_EMPTY, SoundCategory.BLOCKS);
            this.markDirty();
            return ActionResult.SUCCESS;
        }
        if (!this.isEmpty() && this.canBrew()) {
            if (materialProperty != null) {//酿造材料
                this.liquidType = CppCauldronLiquidType.POTION;
                this.liquidData = PotionHelper.applyMaterial(this.liquidData, materialProperty);
                if (!player.isCreative()) stack.decrement(1);
                world.playSound(null, pos, SoundEvents.ENTITY_GENERIC_SPLASH, SoundCategory.BLOCKS);
            } else if (typeMaterialProperty != -1) {//药水类型
                if (this.potionType == typeMaterialProperty) return ActionResult.PASS;
                this.liquidType = CppCauldronLiquidType.POTION;
                this.potionType = typeMaterialProperty;
                if (stack.isOf(Items.DRAGON_BREATH)) {
                    player.setStackInHand(hand, ItemUsage.exchangeStack(stack, player, Items.GLASS_BOTTLE.getDefaultStack()));
                    world.playSound(null, pos, SoundEvents.ITEM_BOTTLE_EMPTY, SoundCategory.BLOCKS);
                } else {
                    if (!player.isCreative()) stack.decrement(1);
                    world.playSound(null, pos, SoundEvents.ENTITY_GENERIC_SPLASH, SoundCategory.BLOCKS);
                }
            } else if (stack.isOf(Items.NETHER_WART)) {//地狱疣
                this.liquidType = CppCauldronLiquidType.POTION;
                this.liquidData = PotionHelper.applyMaterialNetherWart(this.liquidData);
                if (!player.isCreative()) stack.decrement(1);
                world.playSound(null, pos, SoundEvents.ENTITY_GENERIC_SPLASH, SoundCategory.BLOCKS);
            } else if (stack.isOf(Items.GLASS_BOTTLE) && this.canDecrease(CppCauldronBehavior.BOTTLE_LEVEL)) {//取出药水
                if (this.isPotion()) {//药水
                    player.setStackInHand(hand, ItemUsage.exchangeStack(stack, player, PotionHelper.getPotionItem(this.potionType, this.liquidData)));
                } else if (this.liquidType == CppCauldronLiquidType.WATER) {//水瓶
                    player.setStackInHand(hand, ItemUsage.exchangeStack(stack, player, CauldronppItems.WATER_BOTTLE.getDefaultStack()));
                }
                this.decreaseAmount(CppCauldronBehavior.BOTTLE_LEVEL);
                world.playSound(null, pos, SoundEvents.ITEM_BOTTLE_FILL, SoundCategory.BLOCKS);
            } else if (stack.isOf(Items.ARROW) && this.isPotion()) {//药箭
                if ((stack.getCount()-16 >= 0 || player.isCreative()) && this.canDecrease(CppCauldronBehavior.BOTTLE_LEVEL)) {
                    if (!player.isCreative()) stack.decrement(15);
                    player.setStackInHand(hand, ItemUsage.exchangeStack(stack, player, PotionHelper.getPotionItem(PotionHelper.ARROW_TYPE, this.liquidData, 16)));
                    this.decreaseAmount(CppCauldronBehavior.BOTTLE_LEVEL);
                    world.playSound(null, pos, SoundEvents.ENTITY_GENERIC_SPLASH, SoundCategory.BLOCKS);
                }
            } else return ActionResult.PASS;
            this.markDirty();
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }

    //获取信息（getter）
    public CppCauldronLiquidType getLiquidType() {
        return this.liquidType;
    }
    public int getAmount() {
        return this.amount;
    }
    public int getPotionType() {
        return this.potionType;
    }
    public int getLiquidData() {
        return this.liquidData;
    }

    //设置信息（setter）
    public void setLiquidData(int liquidData) {
        this.liquidData = liquidData;
    }
    public void setLiquidType(CppCauldronLiquidType type) {
        this.liquidType = type;
    }

    //是否能倒入
    public boolean canIncrease(int amount) {
        return this.amount + amount <= CppCauldronBehavior.MAX_LEVEL;
    }
    //倒入
    public void increaseAmount(int amount) {
        if (this.canIncrease(amount)) {
            this.amount += amount;
            if (this.isPotion()) this.liquidData = PotionHelper.applyMaterial(this.liquidData, PotionHelper.WATER_MATERIAL);
        }
    }
    //是否能取出
    public boolean canDecrease(int amount) {
        return this.amount - amount >= 0;
    }
    //取出
    public void decreaseAmount(int amount) {
        if (this.canDecrease(amount)) {
            this.amount -= amount;
            if (this.amount == 0) this.empty();
        }
    }
    //变为空
    public void empty() {
        this.amount = 0;
        this.potionType = 0;
        this.liquidData = 0;
        this.liquidType = CppCauldronLiquidType.NONE;
    }
    //是否为满
    public boolean isFull() {
        return this.amount >= CppCauldronBehavior.MAX_LEVEL;
    }
    //是否为空
    public boolean isEmpty() {
        return this.amount == 0;
    }
    //是否为药水
    public boolean isPotion() {
        return this.liquidType == CppCauldronLiquidType.POTION;
    }
    //是否能制作药水
    public boolean canBrew() {
        return this.isPotion() || this.liquidType == CppCauldronLiquidType.WATER;
    }
    //实体是否接触水面
    public boolean isEntityTouchingFluid(BlockPos pos, Entity entity) {
        return entity.getY() < pos.getY() + this.getFluidHeight() && entity.getBoundingBox().maxY > pos.getY() + 0.25 && !this.isEmpty();
    }
    //液面高度
    public double getFluidHeight() {
        return this.amount * 0.1875 + 0.375;
    }
    //颜色
    public int getLiquidRenderColor(BlockRenderView world, BlockPos pos) {
        return switch (this.liquidType) {
            case POTION -> PotionHelper.getPotionColor(this.liquidData);
            case WATER -> BiomeColors.getWaterColor(world, pos);
            case COLORED_WATER -> this.liquidData;
            default -> -1;
        };
    }
    //比较器
    public int getComparatorOutput() {
        return this.amount + (this.liquidType.getId() - 1) * CppCauldronBehavior.MAX_LEVEL;
    }
}
