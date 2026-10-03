package meknuc.blocks;

import java.util.List;

import meknuc.blockentities.MeknucBlockEntityRTG;
import meknuc.blockentities.MeknucTileEntityTypes;
import meknuc.menu.MeknucMenuTypes;
import mekanism.common.block.attribute.Attribute;
import mekanism.common.block.attribute.AttributeGui;
import mekanism.common.block.attribute.AttributeUpgradeSupport;
import mekanism.common.block.interfaces.IHasTileEntity;
import mekanism.common.block.interfaces.ITypeBlock;
import mekanism.common.content.blocktype.BlockType;
import mekanism.common.registration.impl.TileEntityTypeRegistryObject;
import mekanism.common.tile.base.TileEntityMekanism;
import mekanism.common.util.WorldUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

public class MeknucBlockRadioisotopeThermoelectricGenerator extends MeknucBlockBase
      implements IHasTileEntity<MeknucBlockEntityRTG>, ITypeBlock {

    private static final BlockType TYPE = BlockType.BlockTypeBuilder
          .createBlock(() -> "container.meknuc.radioisotope_thermoelectric_generator")
          .with(new AttributeGui(() -> MeknucMenuTypes.RTG, null))
          .with(AttributeUpgradeSupport.MUFFLING_ONLY)
          .build();

    public MeknucBlockRadioisotopeThermoelectricGenerator(Properties properties) {
        super(properties);
    }

    @NotNull
    @Override
    public BlockType getType() {
        return TYPE;
    }

    @NotNull
    @Override
    public TileEntityTypeRegistryObject<MeknucBlockEntityRTG> getTileType() {
        return MeknucTileEntityTypes.RADIOISOTOPE_THERMOELECTRIC_GENERATOR;
    }

    @NotNull
    @Override
    protected InteractionResult useWithoutItem(@NotNull BlockState state, @NotNull Level world, @NotNull BlockPos pos,
        @NotNull Player player, @NotNull BlockHitResult hit) {
            TileEntityMekanism tile = WorldUtils.getTileEntity(TileEntityMekanism.class, world, pos);
            if (tile == null) {
                return InteractionResult.PASS;
            } else if (world.isClientSide) {
                return Attribute.has(this, AttributeGui.class) ? InteractionResult.SUCCESS : InteractionResult.PASS;
            }
        return tile.openGui(player);
    }

    @NotNull
    @Override
    protected List<ItemStack> getDrops(@NotNull BlockState state, @NotNull LootParams.Builder builder) {
        List<ItemStack> drops = super.getDrops(state, builder);
        if (!drops.isEmpty() && builder.getOptionalParameter(LootContextParams.BLOCK_ENTITY) instanceof MeknucBlockEntityRTG tile) {
            tile.saveToItem(drops.get(0), builder.getLevel().registryAccess());
        }
        return drops;
    }
}
