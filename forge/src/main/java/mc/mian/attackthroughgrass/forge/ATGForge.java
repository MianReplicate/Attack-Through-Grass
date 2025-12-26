package mc.mian.attackthroughgrass.forge;

import mc.mian.attackthroughgrass.common.networking.DisableModS2CPayload;
import mc.mian.attackthroughgrass.common.util.ATGConfig;
import mc.mian.attackthroughgrass.common.util.ATGConstants;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.*;
import net.minecraftforge.network.payload.PayloadProtocol;

@Mod(ATGConstants.MOD_ID)
public class ATGForge {
    private static final int PROTOCOL_VERSION = 0;
    public static final PayloadProtocol<RegistryFriendlyByteBuf, CustomPacketPayload> CHANNEL_BUILDER = ChannelBuilder
            .named(Identifier.fromNamespaceAndPath(ATGConstants.MOD_ID, "main"))
            .networkProtocolVersion(PROTOCOL_VERSION)
            .optional()
            .payloadChannel()
            .play()
            .clientbound()
            .add(DisableModS2CPayload.TYPE, DisableModS2CPayload.CODEC, (packet, context) -> DisableModS2CPayload.handle());
    public static Channel<CustomPacketPayload> CHANNEL;


    public ATGForge() {
        CHANNEL = CHANNEL_BUILDER.bidirectional().build();

        PlayerEvent.PlayerLoggedInEvent.BUS.addListener((event) -> {
            boolean dedicated = event.getEntity().level().getServer().isDedicatedServer();
            if(!event.getEntity().level().isClientSide() && dedicated)
                CHANNEL.send(new DisableModS2CPayload(), PacketDistributor.PLAYER.with((ServerPlayer) event.getEntity()));
            else if(!dedicated)
                ATGConfig.IS_DISABLED = false;
        });
    }
}