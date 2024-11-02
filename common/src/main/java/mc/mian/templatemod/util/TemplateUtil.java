package mc.mian.templatemod.util;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.storage.loot.LootTable;

public class TemplateUtil {
    public static ResourceLocation modLoc(String name) {
        return new ResourceLocation(TemplateConstants.MOD_ID, name);
    }
    private static ResourceKey<PlacedFeature> createPlacedFeature(String domain, String name){
        return ResourceKey.create(Registries.PLACED_FEATURE, new ResourceLocation(domain, name));
    }
    private static ResourceKey<ConfiguredFeature<?, ?>> createConfiguredFeature(String domain, String name){
        return ResourceKey.create(Registries.CONFIGURED_FEATURE, new ResourceLocation(domain, name));
    }

    public static ResourceKey<StructureTemplatePool> createTemplatePool(String domain, String name) {
        return ResourceKey.create(Registries.TEMPLATE_POOL, new ResourceLocation(domain, name));
    }

    public static ResourceKey<StructureSet> createStructureSet(String domain, String name) {
        return ResourceKey.create(Registries.STRUCTURE_SET, new ResourceLocation(domain, name));
    }

    public static ResourceKey<Structure> createStructure(String domain, String name) {
        return ResourceKey.create(Registries.STRUCTURE, new ResourceLocation(domain, name));
    }
}
