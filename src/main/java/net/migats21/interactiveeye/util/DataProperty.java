package net.migats21.interactiveeye.util;

import net.minecraft.util.Identifier;

import java.util.Map;

public abstract class DataProperty<T> {
    private Identifier key;
    private T value;
    private Map<String, String> additionalData;
    public DataProperty(String keyName, T value, Map<String, String> additionalData) {
        this.key = new Identifier(keyName);
        this.value = value;
        this.additionalData = additionalData;
    }

    public T getValue() {
        return value;
    }

    public abstract String getStringValue();

    public static class StringDataProperty extends DataProperty<String> {
        public StringDataProperty(String keyName, String value, Map<String, String> additionalData) {
            super(keyName, value, additionalData);
        }
        @Override
        public String getStringValue() {
            return String.valueOf(getValue());
        }
    }
    public static class FloatDataProperty extends DataProperty<Float> {
        public FloatDataProperty(String keyName, Float value, Map<String, String> additionalData) {
            super(keyName, value, additionalData);
        }

        @Override
        public String getStringValue() {
            return String.format("%.02f", getValue());
        }
    }
    public static class IntDataProperty extends DataProperty<Integer> {
        public IntDataProperty(String keyName, Integer value, Map<String, String> additionalData) {
            super(keyName, value, additionalData);
        }

        @Override
        public String getStringValue() {
            return null;
        }
    }
}
