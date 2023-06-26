package net.migats21.deeplearning;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;

public class EntityDataList extends DataList<EntityType<?>> {
    public EntityDataList(ResourceLocation location) {
        super(Registry.ENTITY_TYPE, location);
    }
}
