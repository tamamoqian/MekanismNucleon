package meknuc.reactor;

import io.netty.buffer.ByteBuf;
import meknuc.reactor.tile.TileEntityPwrLogicAdapter;
import meknuc.reactor.tile.TileEntityPwrLogicAdapter.PressurizedWaterReactorLogic;
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

public record MeknucPwrLogicPacket(BlockPos pos, int mode) implements CustomPacketPayload {

    public static final Type<MeknucPwrLogicPacket> TYPE =
          new Type<>(ResourceLocation.fromNamespaceAndPath("meknuc", "reactor_logic_mode"));

    public static final StreamCodec<ByteBuf, MeknucPwrLogicPacket> STREAM_CODEC = StreamCodec.composite(
          BlockPos.STREAM_CODEC, MeknucPwrLogicPacket::pos,
          ByteBufCodecs.VAR_INT, MeknucPwrLogicPacket::mode,
          MeknucPwrLogicPacket::new);

    @Override
    public @NotNull Type<MeknucPwrLogicPacket> type() {
        return TYPE;
    }

    public static void handle(MeknucPwrLogicPacket payload, IPayloadContext context) {
        Player player = context.player();
        if (player.distanceToSqr(Vec3.atCenterOf(payload.pos())) > 64.0) {
            return;
        }
        Level level = player.level();
        BlockEntity tile = level.getBlockEntity(payload.pos());
        if (tile instanceof TileEntityPwrLogicAdapter adapter) {
            adapter.setLogicTypeFromPacket(PressurizedWaterReactorLogic.BY_ID.apply(payload.mode()));
        }
    }
}
