package mc.mian.attackthroughgrass.fabric;

import mc.mian.attackthroughgrass.common.networking.DisableModS2CPayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class ATGFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(DisableModS2CPayload.TYPE, (payload, context) -> DisableModS2CPayload.handle());
    }
}
