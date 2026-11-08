package meknuc.reactor;

import io.netty.buffer.ByteBuf;
import meknuc.reactor.tile.TileEntityBwrLogicAdapter;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record MeknucBwrLogicPacket(BlockPos pos, int mode) implements CustomPacketPayload {

    public static final Type<MeknucBwrLogicPacket> TYPE =
          new Type<>(ResourceLocation.fromNamespaceAndPath("meknuc", "bwr_logic_mode"));

    public static final StreamCodec<ByteBuf, MeknucBwrLogicPacket> STREAM_CODEC = StreamCodec.composite(
          BlockPos.STREAM_CODEC, MeknucBwrLogicPacket::pos,
          ByteBufCodecs.VAR_INT, MeknucBwrLogicPacket::mode,
          MeknucBwrLogicPacket::new);

    @Override
    public @NotNull Type<MeknucBwrLogicPacket> type() {
        return TYPE;
    }

    public static void handle(MeknucBwrLogicPacket payload, IPayloadContext context) {
        Player player = context.player();
        if (player.distanceToSqr(Vec3.atCenterOf(payload.pos())) > 64.0) {
            return;
        }
        Level level = player.level();
        BlockEntity tile = level.getBlockEntity(payload.pos());
        if (tile instanceof TileEntityBwrLogicAdapter adapter) {
            adapter.setLogicTypeFromPacket(BwrLogicAdapterMode.MODES[
                  Math.floorMod(payload.mode(), BwrLogicAdapterMode.MODES.length)]);
        }
    }
}
