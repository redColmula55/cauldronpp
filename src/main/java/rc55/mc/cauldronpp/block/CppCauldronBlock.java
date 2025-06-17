package rc55.mc.cauldronpp.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;
import rc55.mc.cauldronpp.api.CppCauldronBehavior;
import rc55.mc.cauldronpp.api.CppCauldronLiquidType;
import rc55.mc.cauldronpp.blockEntity.CauldronppBlockEntityTypes;
import rc55.mc.cauldronpp.blockEntity.CppCauldronBlockEntity;

import java.util.Optional;

public class CppCauldronBlock extends BlockWithEntity {

    private static final VoxelShape RAYCAST_SHAPE = createCuboidShape(2.0, 4.0, 2.0, 14.0, 16.0, 14.0);
    protected static final VoxelShape OUTLINE_SHAPE = VoxelShapes.combineAndSimplify(
            VoxelShapes.fullCube(),
            VoxelShapes.union(
                    createCuboidShape(0.0, 0.0, 4.0, 16.0, 3.0, 12.0),
                    createCuboidShape(4.0, 0.0, 0.0, 12.0, 3.0, 16.0),
                    createCuboidShape(2.0, 0.0, 2.0, 14.0, 3.0, 14.0),
                    RAYCAST_SHAPE
            ),
            BooleanBiFunction.ONLY_FIRST
    );

    public static final BooleanProperty EMITS_LIGHT = BooleanProperty.of("emits_light");

    protected CppCauldronBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.getStateManager().getDefaultState().with(EMITS_LIGHT, false));
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return createCodec(CppCauldronBlock::new);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(EMITS_LIGHT);
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new CppCauldronBlockEntity(pos, state);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return OUTLINE_SHAPE;
    }

    @Override
    public VoxelShape getRaycastShape(BlockState state, BlockView world, BlockPos pos) {
        return RAYCAST_SHAPE;
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return validateTicker(type, CauldronppBlockEntityTypes.CAULDRON, CppCauldronBlockEntity::tick);
    }
    //比较器
    @Override
    public boolean hasComparatorOutput(BlockState state) {
        return true;
    }
    @Override
    public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        return world.getBlockEntity(pos) instanceof CppCauldronBlockEntity cauldron ? cauldron.getComparatorOutput() : 0;
    }
    //右键
    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        Hand hand = player.getActiveHand();
        Optional<CppCauldronBlockEntity> optional = world.getBlockEntity(pos, CauldronppBlockEntityTypes.CAULDRON);
        if (optional.isPresent()) {
            CppCauldronBlockEntity cauldron = optional.get();
            return cauldron.updateLiquid(world, pos, state, player, hand, player.getStackInHand(hand));
        }
        return ActionResult.FAIL;
    }
    //实体碰撞
    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        Optional<CppCauldronBlockEntity> optional = world.getBlockEntity(pos, CauldronppBlockEntityTypes.CAULDRON);
        if (optional.isPresent()) {
            CppCauldronBlockEntity cauldron = optional.get();
            if (cauldron.isEntityTouchingFluid(pos, entity)) {
                switch (cauldron.getLiquidType()) {
                    case POWDER_SNOW -> {
                        if (!(entity instanceof LivingEntity) || entity.getBlockStateAtPos().isOf(this)) {
                            if (world.isClient) {
                                Random random = world.getRandom();
                                boolean bl = entity.lastRenderX != entity.getX() || entity.lastRenderZ != entity.getZ();
                                if (bl && random.nextBoolean()) {
                                    world.addParticle(
                                            ParticleTypes.SNOWFLAKE,
                                            entity.getX(),
                                            pos.getY() + 1,
                                            entity.getZ(),
                                            MathHelper.nextBetween(random, -1.0F, 1.0F) * 0.083333336F,
                                            0.05F,
                                            MathHelper.nextBetween(random, -1.0F, 1.0F) * 0.083333336F
                                    );
                                }
                            }
                        }
                        entity.setInPowderSnow(true);

                        if (entity.isOnFire() && cauldron.canDecrease(1) && !world.isClient) {
                            entity.extinguishWithSound();
                            if (entity.canModifyAt(world, pos)) {
                                cauldron.decreaseAmount(1);
                                cauldron.setLiquidType(CppCauldronLiquidType.WATER);
                                world.emitGameEvent(entity, GameEvent.BLOCK_CHANGE, pos);
                                world.updateListeners(pos, state, state, Block.NOTIFY_ALL);
                                world.updateComparators(pos, this);
                            }
                        }
                    }
                    case LAVA -> entity.setOnFireFromLava();
                    case WATER, COLORED_WATER -> {
                        if (entity.isOnFire() && cauldron.canDecrease(1) && !world.isClient) {
                            entity.extinguishWithSound();
                            if (entity.canModifyAt(world, pos)) {
                                cauldron.decreaseAmount(1);
                                world.emitGameEvent(entity, GameEvent.BLOCK_CHANGE, pos);
                                world.updateListeners(pos, state, state, Block.NOTIFY_ALL);
                                world.updateComparators(pos, this);
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void precipitationTick(BlockState state, World world, BlockPos pos, Biome.Precipitation precipitation) {
        Optional<CppCauldronBlockEntity> optional = world.getBlockEntity(pos, CauldronppBlockEntityTypes.CAULDRON);
        if (optional.isPresent()) {
            CppCauldronBlockEntity cauldron = optional.get();
            if (canFillWithPrecipitation(world, precipitation)) {
                if (precipitation == Biome.Precipitation.RAIN) {
                    if (cauldron.canIncrease(CppCauldronBehavior.BOTTLE_LEVEL) && (cauldron.isEmpty() || cauldron.getLiquidType() == CppCauldronLiquidType.WATER)) {
                        cauldron.setLiquidType(CppCauldronLiquidType.WATER);
                        cauldron.increaseAmount(CppCauldronBehavior.BOTTLE_LEVEL);
                    }
                    world.emitGameEvent(null, GameEvent.BLOCK_CHANGE, pos);
                    world.updateListeners(pos, state, state, Block.NOTIFY_ALL);
                    world.updateComparators(pos, this);
                } else if (precipitation == Biome.Precipitation.SNOW) {
                    if (cauldron.canIncrease(CppCauldronBehavior.BOTTLE_LEVEL) && (cauldron.isEmpty() || cauldron.getLiquidType() == CppCauldronLiquidType.POWDER_SNOW)) {
                        cauldron.setLiquidType(CppCauldronLiquidType.POWDER_SNOW);
                        cauldron.increaseAmount(CppCauldronBehavior.BOTTLE_LEVEL);
                    }
                    world.emitGameEvent(null, GameEvent.BLOCK_CHANGE, pos);
                    world.updateListeners(pos, state, state, Block.NOTIFY_ALL);
                    world.updateComparators(pos, this);
                }
            }
        }
    }
    //计划刻
    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        BlockPos dripPos = PointedDripstoneBlock.getDripPos(world, pos);
        if (dripPos != null) {
            Optional<CppCauldronBlockEntity> optional = world.getBlockEntity(pos, CauldronppBlockEntityTypes.CAULDRON);
            if (optional.isPresent()) {
                Fluid fluid = PointedDripstoneBlock.getDripFluid(world, dripPos);//滴水石锥
                if (fluid.isIn(FluidTags.WATER)) {//水
                    CppCauldronBlockEntity cauldron = optional.get();
                    if (cauldron.isEmpty() || (cauldron.getLiquidType() == CppCauldronLiquidType.WATER && cauldron.canIncrease(CppCauldronBehavior.BOTTLE_LEVEL))) cauldron.increaseAmount(CppCauldronBehavior.BOTTLE_LEVEL);
                    cauldron.setLiquidType(CppCauldronLiquidType.WATER);
                    world.emitGameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Emitter.of(state));
                    world.updateListeners(pos, state, state, Block.NOTIFY_ALL);
                    world.updateComparators(pos, this);
                } else if (fluid.isIn(FluidTags.LAVA)) {//岩浆
                    CppCauldronBlockEntity cauldron = optional.get();
                    if (cauldron.isEmpty() || (cauldron.getLiquidType() == CppCauldronLiquidType.LAVA && cauldron.canIncrease(CppCauldronBehavior.BOTTLE_LEVEL))) cauldron.increaseAmount(CppCauldronBehavior.BOTTLE_LEVEL);
                    cauldron.setLiquidType(CppCauldronLiquidType.LAVA);
                    world.setBlockState(pos, state.with(EMITS_LIGHT, true));
                    world.emitGameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Emitter.of(state));
                    world.updateListeners(pos, state, state, Block.NOTIFY_ALL);
                    world.updateComparators(pos, this);
                }
            }
        }
    }
    private static boolean canFillWithPrecipitation(World world, Biome.Precipitation precipitation) {
        if (precipitation == Biome.Precipitation.RAIN) {
            return world.getRandom().nextFloat() < 0.05F;
        } else {
            return precipitation == Biome.Precipitation.SNOW && world.getRandom().nextFloat() < 0.1F;
        }
    }
}
