package mc.mian.attackthroughgrass.forge;

import mc.mian.attackthroughgrass.common.networking.DisableModS2CPayload;
import mc.mian.attackthroughgrass.common.util.ATGConfig;
import mc.mian.attackthroughgrass.common.util.ATGConstants;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.bus.BusGroup;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.*;
import net.minecraftforge.network.payload.PayloadProtocol;

import java.lang.invoke.MethodHandles;

@Mod(ATGConstants.MOD_ID)
public class ATGForge {
    private static final int PROTOCOL_VERSION = 0;
    public static final PayloadProtocol<RegistryFriendlyByteBuf, CustomPacketPayload> CHANNEL_BUILDER = ChannelBuilder
            .named(ResourceLocation.fromNamespaceAndPath(ATGConstants.MOD_ID, "main"))
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
            boolean dedicated = event.getEntity().getServer().isDedicatedServer();
            if(!event.getEntity().level().isClientSide() && dedicated)
                CHANNEL.send(new DisableModS2CPayload(), PacketDistributor.PLAYER.with((ServerPlayer) event.getEntity()));
            else if(!dedicated)
                ATGConfig.IS_DISABLED = false;
        });
    }
}