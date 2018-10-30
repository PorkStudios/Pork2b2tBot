/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.map;

import gnu.trove.function.TObjectFunction;
import gnu.trove.procedure.TObjectObjectProcedure;
import gnu.trove.procedure.TObjectProcedure;
import java.util.Map;

public interface TMap<K, V>
extends Map<K, V> {
    @Override
    public V putIfAbsent(K var1, V var2);

    public boolean forEachKey(TObjectProcedure<? super K> var1);

    public boolean forEachValue(TObjectProcedure<? super V> var1);

    public boolean forEachEntry(TObjectObjectProcedure<? super K, ? super V> var1);

    public boolean retainEntries(TObjectObjectProcedure<? super K, ? super V> var1);

    public void transformValues(TObjectFunction<V, V> var1);
}

