package mc.mian.attackthroughgrass.common.networking;

import mc.mian.attackthroughgrass.common.util.ATGConfig;
import mc.mian.attackthroughgrass.common.util.ATGConstants;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record DisableModS2CPayload() implements CustomPacketPayload {
    public static final Identifier DISABLE_MOD_PAYLOAD_ID = Identifier.fromNamespaceAndPath(ATGConstants.MOD_ID, "disable_attack_through_grass");
    public static final CustomPacketPayload.Type<DisableModS2CPayload> TYPE = new CustomPacketPayload.Type<>(DISABLE_MOD_PAYLOAD_ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, DisableModS2CPayload> CODEC = StreamCodec.ofMember((packet, buf) -> {}, (buf) -> new DisableModS2CPayload());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(){
        ATGConstants.LOGGER.info("Server has Attack Through Grass installed! Disabling on client...");

        ATGConfig.IS_DISABLED = true;
    }
}
