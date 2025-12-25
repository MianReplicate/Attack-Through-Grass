package mc.mian.attackthroughgrass.neoforge;

import mc.mian.attackthroughgrass.common.networking.DisableModS2CPayload;
import mc.mian.attackthroughgrass.common.util.ATGConstants;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.javafmlmod.FMLModContainer;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@Mod(value = ATGConstants.MOD_ID, dist = Dist.CLIENT)
public class ATGNeoForgeClient {
    public ATGNeoForgeClient(FMLModContainer container, IEventBus modBus, Dist dist){
        modBus.register(ATGNeoForgeClient.class);
    }

    @SubscribeEvent // on the mod event bus
    public static void register(RegisterPayloadHandlersEvent event) {
        // Sets the current network version
        final PayloadRegistrar registrar = event.registrar(ATGConstants.MOD_ID);
        registrar.optional().playToClient(DisableModS2CPayload.TYPE, DisableModS2CPayload.CODEC, (payload, context) -> DisableModS2CPayload.handle());
    }
}
