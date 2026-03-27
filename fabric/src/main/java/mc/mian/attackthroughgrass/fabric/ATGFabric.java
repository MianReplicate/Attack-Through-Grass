package mc.mian.attackthroughgrass.fabric;

import mc.mian.attackthroughgrass.common.networking.DisableModS2CPayload;
import mc.mian.attackthroughgrass.common.util.ATGConfig;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

public class ATGFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        PayloadTypeRegistry.clientboundPlay().register(DisableModS2CPayload.TYPE, DisableModS2CPayload.CODEC);
        ServerPlayerEvents.JOIN.register((player) -> {
            if(ServerPlayNetworking.canSend(player, DisableModS2CPayload.TYPE)){
                if(player.level().getServer().isDedicatedServer())
                    ServerPlayNetworking.send(player, new DisableModS2CPayload());
                else
                    ATGConfig.IS_DISABLED = false;
            }
        });
    }
}
