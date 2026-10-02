package meknuc.reactor;

import io.netty.buffer.ByteBuf;
import meknuc.reactor.tile.TileEntityPressurizedWaterReactorPart;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record MeknucReactorOpenGuiPacket(BlockPos pos, int gui) implements CustomPacketPayload {

    public static final Type<MeknucReactorOpenGuiPacket> TYPE =
          new Type<>(ResourceLocation.fromNamespaceAndPath("meknuc", "reactor_open_gui"));

    public static final StreamCodec<ByteBuf, MeknucReactorOpenGuiPacket> STREAM_CODEC = StreamCodec.composite(
          BlockPos.STREAM_CODEC, MeknucReactorOpenGuiPacket::pos,
          ByteBufCodecs.VAR_INT, MeknucReactorOpenGuiPacket::gui,
          MeknucReactorOpenGuiPacket::new);

    @Override
    public @NotNull Type<MeknucReactorOpenGuiPacket> type() {
        return TYPE;
    }

    public static void handle(MeknucReactorOpenGuiPacket payload, IPayloadContext context) {
        Player player = context.player();
        if (player.distanceToSqr(Vec3.atCenterOf(payload.pos())) > 64.0) {
            return;
        }
        Level level = player.level();
        BlockEntity tile = level.getBlockEntity(payload.pos());
        if (tile instanceof TileEntityPressurizedWaterReactorPart reactor
              && player instanceof ServerPlayer serverPlayer) {
            MenuProvider provider = payload.gui() == MeknucReactorGuiTarget.STATS
                  ? MeknucReactorContainerTypes.PRESSURIZED_WATER_REACTOR_STATS
                        .getProvider(MeknucReactorLang.GUI_STATS_TITLE, reactor)
                  : MeknucReactorContainerTypes.PRESSURIZED_WATER_REACTOR
                        .getProvider(MeknucReactorLang.GUI_TITLE, reactor);
            if (provider != null) {
                serverPlayer.openMenu(provider, buffer -> buffer.writeBlockPos(reactor.getBlockPos()));
            }
        }
    }
}
