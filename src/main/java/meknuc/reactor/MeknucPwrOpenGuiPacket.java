package meknuc.reactor;

import io.netty.buffer.ByteBuf;
import meknuc.reactor.tile.TileEntityPwrPart;
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

public record MeknucPwrOpenGuiPacket(BlockPos pos, int gui) implements CustomPacketPayload {

    public static final Type<MeknucPwrOpenGuiPacket> TYPE =
          new Type<>(ResourceLocation.fromNamespaceAndPath("meknuc", "reactor_open_gui"));

    public static final StreamCodec<ByteBuf, MeknucPwrOpenGuiPacket> STREAM_CODEC = StreamCodec.composite(
          BlockPos.STREAM_CODEC, MeknucPwrOpenGuiPacket::pos,
          ByteBufCodecs.VAR_INT, MeknucPwrOpenGuiPacket::gui,
          MeknucPwrOpenGuiPacket::new);

    @Override
    public @NotNull Type<MeknucPwrOpenGuiPacket> type() {
        return TYPE;
    }

    public static void handle(MeknucPwrOpenGuiPacket payload, IPayloadContext context) {
        Player player = context.player();
        if (player.distanceToSqr(Vec3.atCenterOf(payload.pos())) > 64.0) {
            return;
        }
        Level level = player.level();
        BlockEntity tile = level.getBlockEntity(payload.pos());
        if (tile instanceof TileEntityPwrPart reactor
              && player instanceof ServerPlayer serverPlayer) {
            MenuProvider provider = payload.gui() == MeknucPwrGuiTarget.STATS
                  ? MeknucPwrContainerTypes.PRESSURIZED_WATER_REACTOR_STATS
                        .getProvider(MeknucPwrLang.GUI_STATS_TITLE, reactor)
                  : MeknucPwrContainerTypes.PRESSURIZED_WATER_REACTOR
                        .getProvider(MeknucPwrLang.GUI_TITLE, reactor);
            if (provider != null) {
                serverPlayer.openMenu(provider, buffer -> buffer.writeBlockPos(reactor.getBlockPos()));
            }
        }
    }
}
