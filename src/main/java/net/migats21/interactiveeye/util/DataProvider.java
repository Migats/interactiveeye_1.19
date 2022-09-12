package net.migats21.interactiveeye.util;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Stream;

public abstract class DataProvider {
    private static Stream<DataProperty<?>> stream;
    private static Set<DataProperty<?>> tempSet = Collections.emptySortedSet();
    public static DataProperty<?> registerData(DataProperty<?> property) {
        tempSet.add(property);
        return property;
    }
    public static void registerData() {
        stream = tempSet.stream();
        tempSet = null;
    }
}

