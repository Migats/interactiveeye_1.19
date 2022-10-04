package net.migats21.deeplearning;

import net.minecraft.resources.ResourceLocation;
import org.apache.http.util.ByteArrayBuffer;

import javax.swing.text.html.parser.Entity;
import java.awt.image.DataBufferByte;
import java.util.function.BiPredicate;

public abstract class DataPropertyType<T> {
    public DataPropertyType(ResourceLocation key) {
        this.keyname = key;
    }
    protected final ResourceLocation keyname;
    public abstract byte getValueOf(T tval);
    public abstract void applyValue(T tval, byte value);
    public static abstract class DataPropertyValue<V extends DataPropertyType> {
    }

    public static class EntityPropertyType extends DataPropertyType<Entity> {

        public EntityPropertyType(ResourceLocation key) {
            super(key);
        }

        @Override
        public byte getValueOf(Entity tval) {
            return -1;
        }

        @Override
        public void applyValue(Entity tval, byte value) {

        }
    }
}
