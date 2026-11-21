package meknuc.reactor;

import java.util.function.Supplier;
import mekanism.api.text.ILangEntry;
import mekanism.common.block.attribute.Attribute;
import mekanism.common.block.attribute.AttributeGui;
import mekanism.common.block.attribute.Attributes;
import mekanism.common.content.blocktype.BlockTypeTile;
import mekanism.common.content.blocktype.BlockTypeTile.BlockTileBuilder;
import mekanism.common.registration.impl.SoundEventRegistryObject;
import mekanism.common.registration.impl.TileEntityTypeRegistryObject;
import mekanism.common.tile.base.TileEntityMekanism;
import net.minecraft.sounds.SoundEvent;

public final class MeknucReactorBlockTypes {

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <TILE extends TileEntityMekanism> BlockTypeTile<TILE> createExternal(
          ILangEntry description, Supplier<TileEntityTypeRegistryObject<TILE>> tileType,
          AttributeGui gui, SoundEventRegistryObject<SoundEvent> sound) {
        BlockTileBuilder builder = (BlockTileBuilder) BlockTileBuilder.createBlock(tileType, description);
        if (gui != null) {
            builder.with(gui);
        }
        builder = (BlockTileBuilder) builder.withSound(sound);
        return (BlockTypeTile<TILE>) ((BlockTileBuilder) builder.externalMultiblock()).build();
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <TILE extends TileEntityMekanism> BlockTypeTile<TILE> createPort(
          ILangEntry description, Supplier<TileEntityTypeRegistryObject<TILE>> tileType,
          Attribute state, AttributeGui gui, SoundEventRegistryObject<SoundEvent> sound) {
        BlockTileBuilder builder = (BlockTileBuilder) BlockTileBuilder.createBlock(tileType, description);
        builder.with(new Attribute[]{state, gui});
        builder = (BlockTileBuilder) builder.withSound(sound);
        builder = (BlockTileBuilder) builder.externalMultiblock();
        return (BlockTypeTile<TILE>) builder.build();
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <TILE extends TileEntityMekanism> BlockTypeTile<TILE> createLogicAdapter(
          ILangEntry description, Supplier<TileEntityTypeRegistryObject<TILE>> tileType,
          Attribute[] modeAttributes, AttributeGui gui, SoundEventRegistryObject<SoundEvent> sound) {
        BlockTileBuilder builder = (BlockTileBuilder) BlockTileBuilder.createBlock(tileType, description);
        builder.with(modeAttributes);
        builder.with(new Attribute[]{Attributes.REDSTONE});
        builder.with(gui);
        builder = (BlockTileBuilder) builder.withSound(sound);
        return (BlockTypeTile<TILE>) ((BlockTileBuilder) builder.externalMultiblock()).build();
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <TILE extends TileEntityMekanism> BlockTypeTile<TILE> createInternal(
          ILangEntry description, Supplier<TileEntityTypeRegistryObject<TILE>> tileType,
          SoundEventRegistryObject<SoundEvent> sound) {
        BlockTileBuilder builder = (BlockTileBuilder) BlockTileBuilder.createBlock(tileType, description);
        builder = (BlockTileBuilder) builder.withSound(sound);
        return (BlockTypeTile<TILE>) ((BlockTileBuilder) builder.internalMultiblock()).build();
    }

    private MeknucReactorBlockTypes() {
    }
}
