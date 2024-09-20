package com.nuhlowl;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.resource.JsonDataLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourcePack;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;

import java.util.Map;

public class LootTableResourceListener extends JsonDataLoader implements IdentifiableResourceReloadListener {
    public LootTableResourceListener() {
        super(new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().setLenient().create(), "loot_table");
    }

    @Override
    public Identifier getFabricId() {
        return Identifier.of(MiningMagic.MOD_ID, "resource_loader");
    }

    @Override
    protected void apply(Map<Identifier, JsonElement> prepared, ResourceManager manager, Profiler profiler) {
        for (ResourcePack pack : manager.streamResourcePacks().toList()) {
            MiningMagic.LOGGER.info("Checking pack {}", pack.getId());
//            try {
//                for (VillagerProfession villager : Registries.VILLAGER_PROFESSION.stream().collect(Collectors.toList())) {
//                    loadAndMergeTradeOffers(pack, villager);
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
        }
    }
}
