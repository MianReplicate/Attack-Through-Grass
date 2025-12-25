package mc.mian.attackthroughgrass.neoforge;

import mc.mian.attackthroughgrass.common.networking.DisableModS2CPayload;
import mc.mian.attackthroughgrass.common.util.ATGConfig;
import mc.mian.attackthroughgrass.common.util.ATGConstants;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@Mod(value = ATGConstants.MOD_ID, dist = Dist.DEDICATED_SERVER)
public class ATGNeoForge {
    public ATGNeoForge(IEventBus modBus, ModContainer container) {
        NeoForge.EVENT_BUS.register(ATGNeoForge.class);
        modBus.register(ModBusEvents.class);
    }

    @SubscribeEvent
    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event){
        boolean dedicated = event.getEntity().getServer().isDedicatedServer();
        if(!event.getEntity().level().isClientSide() && dedicated)
            PacketDistributor.sendToPlayer((ServerPlayer) event.getEntity(), new DisableModS2CPayload());
        else if(!dedicated)
            ATGConfig.IS_DISABLED = false;
    }

    public static class ModBusEvents {
        @SubscribeEvent // on the mod event bus
        public static void register(RegisterPayloadHandlersEvent event) {
            final PayloadRegistrar registrar = event.registrar(ATGConstants.MOD_ID);
            registrar.optional().playToClient(DisableModS2CPayload.TYPE, DisableModS2CPayload.CODEC, (payload, context) -> DisableModS2CPayload.handle());
        }
    }
}