package meknuc.particle;

import java.util.function.Supplier;
import mekanism.api.Upgrade;
import mekanism.api.text.EnumColor;
import mekanism.api.text.ILangEntry;
import mekanism.api.text.IHasTextComponent.IHasEnumNameTextComponent;
import mekanism.common.block.attribute.Attribute;
import mekanism.common.block.attribute.AttributeGui;
import mekanism.common.block.attribute.AttributeUpgradeSupport;
import mekanism.common.content.blocktype.BlockTypeTile;
import mekanism.common.content.blocktype.BlockTypeTile.BlockTileBuilder;
import mekanism.common.registration.impl.TileEntityTypeRegistryObject;
import mekanism.common.tile.base.TileEntityMekanism;
import meknuc.blocks.AttributeStateReactorPortMode;
import meknuc.blocks.MeknucBlockReactorLang;
import meknuc.particle.MeknucParticleAcceleratorContainerTypes;
import meknuc.particle.MeknucParticleAcceleratorLang;
import meknuc.particle.MeknucParticleAcceleratorSounds;
import meknuc.particle.tile.TileEntityParticleAcceleratorController;
import meknuc.particle.tile.TileEntityParticleAcceleratorPart;
import meknuc.particle.tile.TileEntityParticleAcceleratorPort;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.state.properties.EnumProperty;

public class MeknucParticleAcceleratorBlockTypes {

    public static final EnumProperty<Mode> MODE = EnumProperty.create("mode", Mode.class);

    public static final AttributeStateReactorPortMode<Mode> MODE_ATTRIBUTE =
          new AttributeStateReactorPortMode<>(MODE, Mode.BEAM_INPUT);

    public static final BlockTypeTile<TileEntityParticleAcceleratorController> PARTICLE_ACCELERATOR_CONTROLLER =
          createExternal(MeknucBlockReactorLang.PARTICLE_ACCELERATOR_CONTROLLER,
                () -> MeknucParticleAcceleratorTileEntityTypes.PARTICLE_ACCELERATOR_CONTROLLER, true,
                Upgrade.SPEED, Upgrade.ENERGY);

    public static final BlockTypeTile<TileEntityParticleAcceleratorPart> PARTICLE_ACCELERATOR_RADIATION_RESISTANT_CASING =
          createExternal(MeknucBlockReactorLang.PARTICLE_ACCELERATOR_RADIATION_RESISTANT_CASING,
                () -> MeknucParticleAcceleratorTileEntityTypes.PARTICLE_ACCELERATOR_RADIATION_RESISTANT_CASING, false);

    public static final BlockTypeTile<TileEntityParticleAcceleratorPart> PARTICLE_ACCELERATOR_SUPPORT_FRAME =
          createExternal(MeknucBlockReactorLang.PARTICLE_ACCELERATOR_SUPPORT_FRAME,
                () -> MeknucParticleAcceleratorTileEntityTypes.PARTICLE_ACCELERATOR_SUPPORT_FRAME, false);

    public static final BlockTypeTile<TileEntityParticleAcceleratorPart> PARTICLE_ACCELERATOR_TARGET_MOUNTING_FRAME =
          createExternal(MeknucBlockReactorLang.PARTICLE_ACCELERATOR_TARGET_MOUNTING_FRAME,
                () -> MeknucParticleAcceleratorTileEntityTypes.PARTICLE_ACCELERATOR_TARGET_MOUNTING_FRAME, false);

    public static final BlockTypeTile<TileEntityParticleAcceleratorPart> PARTICLE_ACCELERATION_COIL =
          createExternal(MeknucBlockReactorLang.PARTICLE_ACCELERATION_COIL,
                () -> MeknucParticleAcceleratorTileEntityTypes.PARTICLE_ACCELERATION_COIL, false);

    public static final BlockTypeTile<TileEntityParticleAcceleratorPort> PARTICLE_ACCELERATOR_PORT =
          createPort(MeknucBlockReactorLang.PARTICLE_ACCELERATOR_PORT,
                () -> MeknucParticleAcceleratorTileEntityTypes.PARTICLE_ACCELERATOR_PORT, MODE_ATTRIBUTE);

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static <TILE extends TileEntityMekanism> BlockTypeTile<TILE> createExternal(
          MeknucBlockReactorLang description, Supplier<TileEntityTypeRegistryObject<TILE>> tileType, boolean gui,
          Upgrade... upgrades) {
        BlockTileBuilder builder = (BlockTileBuilder) BlockTileBuilder.createBlock(tileType, description);
        if (gui) {
            builder.with(new Attribute[]{new AttributeGui(
                  () -> MeknucParticleAcceleratorContainerTypes.PARTICLE_ACCELERATOR,
                  MeknucParticleAcceleratorLang.GUI_TITLE)});
        }
        if (upgrades.length > 0) {
            // 只有控制器接受速度 / 能量升级；TileEntityMekanism 会据此自动创建升级组件
            builder = (BlockTileBuilder) builder.with(new Attribute[]{AttributeUpgradeSupport.create(upgrades)});
        }
        builder = (BlockTileBuilder) builder.withSound(MeknucParticleAcceleratorSounds.PARTICLE_ACCELERATOR_RUNNING);
        return (BlockTypeTile<TILE>) ((BlockTileBuilder) builder.externalMultiblock()).build();
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static BlockTypeTile<TileEntityParticleAcceleratorPort> createPort(
          MeknucBlockReactorLang description,
          Supplier<TileEntityTypeRegistryObject<TileEntityParticleAcceleratorPort>> tileType,
          AttributeStateReactorPortMode<?> mode) {
        BlockTileBuilder builder = (BlockTileBuilder) BlockTileBuilder.createBlock(tileType, description);
        builder.with(new Attribute[]{mode});
        builder = (BlockTileBuilder) builder.withSound(MeknucParticleAcceleratorSounds.PARTICLE_ACCELERATOR_RUNNING);
        return (BlockTypeTile<TileEntityParticleAcceleratorPort>) ((BlockTileBuilder) builder.externalMultiblock()).build();
    }

    public enum Mode implements StringRepresentable, IHasEnumNameTextComponent {
        BEAM_INPUT("beam_input", MeknucBlockReactorLang.PORT_MODE_PARTICLE_ACCELERATOR_BEAM_INPUT, EnumColor.PURPLE),
        BEAM_OUTPUT("beam_output", MeknucBlockReactorLang.PORT_MODE_PARTICLE_ACCELERATOR_BEAM_OUTPUT, EnumColor.INDIGO),
        ENERGY("energy", MeknucBlockReactorLang.PORT_MODE_PARTICLE_ACCELERATOR_ENERGY, EnumColor.RED),
        TARGET_INPUT("target_input", MeknucBlockReactorLang.PORT_MODE_PARTICLE_ACCELERATOR_TARGET_INPUT, EnumColor.PINK),
        TARGET_OUTPUT("target_output", MeknucBlockReactorLang.PORT_MODE_PARTICLE_ACCELERATOR_TARGET_OUTPUT, EnumColor.BRIGHT_PINK),
        ;

        private final String name;
        private final ILangEntry langEntry;
        private final EnumColor color;

        Mode(String name, ILangEntry langEntry, EnumColor color) {
            this.name = name;
            this.langEntry = langEntry;
            this.color = color;
        }

        @Override
        public String getSerializedName() {
            return name;
        }

        @Override
        public Component getTextComponent() {
            return langEntry.translateColored(color);
        }
    }

    private MeknucParticleAcceleratorBlockTypes() {
    }
}
