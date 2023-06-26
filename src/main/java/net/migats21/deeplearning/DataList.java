package net.migats21.deeplearning;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public abstract class DataList<T> {
    final Map<ResourceLocation, Integer> entries;
    final T registryObject;
    public long getEntry(ResourceLocation location) {
        return entries.getOrDefault(location, -1);
    }
    protected DataList(Registry<T> registry, ResourceLocation key) {
        entries = new HashMap<>();
        registryObject = registry.get(key);
    }
    @Nullable
    public T getRegistryObject() {
        return registryObject;
    }

}
