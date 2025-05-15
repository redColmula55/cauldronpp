package rc55.mc.cauldronpp.blockEntity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
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
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import rc55.mc.cauldronpp.api.PotionHelper;
import rc55.mc.cauldronpp.item.CauldronppItems;

public class CppCauldronBlockEntity extends BlockEntity {
    public CppCauldronBlockEntity(BlockPos pos, BlockState state) {
        super(CauldronppBlockEntityTypes.CAULDRON, pos, state);
    }

    private int liquidType;
    private int potionData;
    private int potionType;
    private int amount;

    public static void tick(World world, BlockPos pos, BlockState state, CppCauldronBlockEntity blockEntity) {
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.potionData = nbt.getInt("PotionData");
        this.potionType = nbt.getInt("PotionType");
        this.amount = nbt.getInt("Level");
    }
    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putInt("PotionData", this.potionData);
        nbt.putInt("PotionType", this.potionType);
        nbt.putInt("Level", this.amount);
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

    @Override
    public @Nullable Object getRenderData() {
        return PotionHelper.getPotionColor(this.potionData);
    }

    public boolean isEmpty() {
        return this.amount == 0;
    }

    public ActionResult updatePotion(World world, BlockPos pos, BlockState state, PlayerEntity player, Hand hand, ItemStack stack) {
        String materialProperty = PotionHelper.brewingMaterial.get(stack.getItem());
        int typeMaterialProperty = PotionHelper.brewingMaterialType.getOrDefault(stack.getItem(), -1);
        if (stack.isOf(Items.WATER_BUCKET) && !this.isFull()) {//加水（水桶）
            this.amount = 3;
            this.potionData = PotionHelper.applyMaterial(this.potionData, "-1-3-5-7-9-11-13");
            player.setStackInHand(hand, ItemUsage.exchangeStack(stack, player, Items.BUCKET.getDefaultStack()));
            world.playSound(null, pos, SoundEvents.ITEM_BUCKET_EMPTY, SoundCategory.PLAYERS);
            this.markDirty();
            return ActionResult.SUCCESS;
        } else if (((stack.isOf(Items.POTION) && PotionUtil.getPotion(stack) == Potions.WATER) || stack.isOf(CauldronppItems.WATER_BOTTLE)) && !this.isFull()) {//加水（水瓶）
            this.amount += 1;
            this.potionData = PotionHelper.applyMaterial(this.potionData, "-1-3-5-7-9-11-13");
            player.setStackInHand(hand, ItemUsage.exchangeStack(stack, player, Items.GLASS_BOTTLE.getDefaultStack()));
            this.markDirty();
            return ActionResult.SUCCESS;
        }
        if (!this.isEmpty()) {
            if (materialProperty != null) {//酿造材料
                this.potionData = PotionHelper.applyMaterial(this.potionData, materialProperty);
                if (!player.isCreative()) stack.decrement(1);
            } else if (typeMaterialProperty != -1) {//药水类型
                if (this.potionType == typeMaterialProperty) return ActionResult.FAIL;
                this.potionType = typeMaterialProperty;
                if (stack.isOf(Items.DRAGON_BREATH)) {
                    player.setStackInHand(hand, ItemUsage.exchangeStack(stack, player, Items.GLASS_BOTTLE.getDefaultStack()));
                } else {
                    if (!player.isCreative()) stack.decrement(1);
                }
            } else if (stack.isOf(Items.NETHER_WART)) {//地狱疣
                this.potionData = PotionHelper.applyMaterialNetherWart(this.potionData);
                if (!player.isCreative()) stack.decrement(1);
            } else if (stack.isOf(Items.GLASS_BOTTLE) && this.canDecrease(1)) {//取出药水
                player.setStackInHand(hand, ItemUsage.exchangeStack(stack, player, PotionHelper.getPotionItem(this.potionType, this.potionData)));
                this.decreaseAmount(1);
                //this.sendDebugMsg(player);
            } else if (stack.isOf(Items.ARROW)) {//药箭
                if ((stack.getCount()-16 >= 0 || player.isCreative()) && this.canDecrease(1)) {
                    if (!player.isCreative()) stack.decrement(15);
                    player.setStackInHand(hand, ItemUsage.exchangeStack(stack, player, PotionHelper.getPotionItem(3, this.potionData, 16)));
                    this.decreaseAmount(1);
                    //this.sendDebugMsg(player);
                }
            }
            this.markDirty();
            return ActionResult.SUCCESS;
        }
        return ActionResult.FAIL;
    }

    public int getAmount() {
        return this.amount;
    }
    public int getPotionType() {
        return this.potionType;
    }
    public int getPotionData() {
        return this.potionData;
    }

    public boolean canDecrease(int amount) {
        return this.amount - amount >= 0;
    }
    public void decreaseAmount(int amount) {
        if (this.canDecrease(amount)) {
            this.amount -= 1;
            if (this.amount == 0) this.empty();
        }
    }
    public void empty() {
        this.amount = 0;
        this.potionType = 0;
        this.potionData = 0;
        this.liquidType = 0;
    }
    public boolean isFull() {
        return this.amount >= 3;
    }
    private void sendDebugMsg(PlayerEntity player) {
        player.sendMessage(Text.of("Data: " + this.potionData));
        player.sendMessage(Text.of("Name: " + Text.translatable(PotionHelper.getPotionPrefixTranslationKey(this.potionType, this.potionData)).getString()));
        player.sendMessage(Text.of("Color: " + PotionHelper.getPotionColor(this.potionData)));
        if (!PotionHelper.getEffects(this.potionData).isEmpty()) {
            for (StatusEffectInstance instance : PotionHelper.getEffects(this.potionData)) {
                player.sendMessage(Text.of(Text.translatable(instance.getTranslationKey()).getString() +", Level: "+instance.getAmplifier()+", Duration: "+instance.getDuration()));
            }
        }
    }
}
