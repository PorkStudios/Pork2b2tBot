/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove;

import gnu.trove.TByteCollection;
import gnu.trove.TCharCollection;
import gnu.trove.TDoubleCollection;
import gnu.trove.TFloatCollection;
import gnu.trove.TIntCollection;
import gnu.trove.TLongCollection;
import gnu.trove.TShortCollection;
import gnu.trove.impl.sync.TSynchronizedByteByteMap;
import gnu.trove.impl.sync.TSynchronizedByteCharMap;
import gnu.trove.impl.sync.TSynchronizedByteCollection;
import gnu.trove.impl.sync.TSynchronizedByteDoubleMap;
import gnu.trove.impl.sync.TSynchronizedByteFloatMap;
import gnu.trove.impl.sync.TSynchronizedByteIntMap;
import gnu.trove.impl.sync.TSynchronizedByteList;
import gnu.trove.impl.sync.TSynchronizedByteLongMap;
import gnu.trove.impl.sync.TSynchronizedByteObjectMap;
import gnu.trove.impl.sync.TSynchronizedByteSet;
import gnu.trove.impl.sync.TSynchronizedByteShortMap;
import gnu.trove.impl.sync.TSynchronizedCharByteMap;
import gnu.trove.impl.sync.TSynchronizedCharCharMap;
import gnu.trove.impl.sync.TSynchronizedCharCollection;
import gnu.trove.impl.sync.TSynchronizedCharDoubleMap;
import gnu.trove.impl.sync.TSynchronizedCharFloatMap;
import gnu.trove.impl.sync.TSynchronizedCharIntMap;
import gnu.trove.impl.sync.TSynchronizedCharList;
import gnu.trove.impl.sync.TSynchronizedCharLongMap;
import gnu.trove.impl.sync.TSynchronizedCharObjectMap;
import gnu.trove.impl.sync.TSynchronizedCharSet;
import gnu.trove.impl.sync.TSynchronizedCharShortMap;
import gnu.trove.impl.sync.TSynchronizedDoubleByteMap;
import gnu.trove.impl.sync.TSynchronizedDoubleCharMap;
import gnu.trove.impl.sync.TSynchronizedDoubleCollection;
import gnu.trove.impl.sync.TSynchronizedDoubleDoubleMap;
import gnu.trove.impl.sync.TSynchronizedDoubleFloatMap;
import gnu.trove.impl.sync.TSynchronizedDoubleIntMap;
import gnu.trove.impl.sync.TSynchronizedDoubleList;
import gnu.trove.impl.sync.TSynchronizedDoubleLongMap;
import gnu.trove.impl.sync.TSynchronizedDoubleObjectMap;
import gnu.trove.impl.sync.TSynchronizedDoubleSet;
import gnu.trove.impl.sync.TSynchronizedDoubleShortMap;
import gnu.trove.impl.sync.TSynchronizedFloatByteMap;
import gnu.trove.impl.sync.TSynchronizedFloatCharMap;
import gnu.trove.impl.sync.TSynchronizedFloatCollection;
import gnu.trove.impl.sync.TSynchronizedFloatDoubleMap;
import gnu.trove.impl.sync.TSynchronizedFloatFloatMap;
import gnu.trove.impl.sync.TSynchronizedFloatIntMap;
import gnu.trove.impl.sync.TSynchronizedFloatList;
import gnu.trove.impl.sync.TSynchronizedFloatLongMap;
import gnu.trove.impl.sync.TSynchronizedFloatObjectMap;
import gnu.trove.impl.sync.TSynchronizedFloatSet;
import gnu.trove.impl.sync.TSynchronizedFloatShortMap;
import gnu.trove.impl.sync.TSynchronizedIntByteMap;
import gnu.trove.impl.sync.TSynchronizedIntCharMap;
import gnu.trove.impl.sync.TSynchronizedIntCollection;
import gnu.trove.impl.sync.TSynchronizedIntDoubleMap;
import gnu.trove.impl.sync.TSynchronizedIntFloatMap;
import gnu.trove.impl.sync.TSynchronizedIntIntMap;
import gnu.trove.impl.sync.TSynchronizedIntList;
import gnu.trove.impl.sync.TSynchronizedIntLongMap;
import gnu.trove.impl.sync.TSynchronizedIntObjectMap;
import gnu.trove.impl.sync.TSynchronizedIntSet;
import gnu.trove.impl.sync.TSynchronizedIntShortMap;
import gnu.trove.impl.sync.TSynchronizedLongByteMap;
import gnu.trove.impl.sync.TSynchronizedLongCharMap;
import gnu.trove.impl.sync.TSynchronizedLongCollection;
import gnu.trove.impl.sync.TSynchronizedLongDoubleMap;
import gnu.trove.impl.sync.TSynchronizedLongFloatMap;
import gnu.trove.impl.sync.TSynchronizedLongIntMap;
import gnu.trove.impl.sync.TSynchronizedLongList;
import gnu.trove.impl.sync.TSynchronizedLongLongMap;
import gnu.trove.impl.sync.TSynchronizedLongObjectMap;
import gnu.trove.impl.sync.TSynchronizedLongSet;
import gnu.trove.impl.sync.TSynchronizedLongShortMap;
import gnu.trove.impl.sync.TSynchronizedObjectByteMap;
import gnu.trove.impl.sync.TSynchronizedObjectCharMap;
import gnu.trove.impl.sync.TSynchronizedObjectDoubleMap;
import gnu.trove.impl.sync.TSynchronizedObjectFloatMap;
import gnu.trove.impl.sync.TSynchronizedObjectIntMap;
import gnu.trove.impl.sync.TSynchronizedObjectLongMap;
import gnu.trove.impl.sync.TSynchronizedObjectShortMap;
import gnu.trove.impl.sync.TSynchronizedRandomAccessByteList;
import gnu.trove.impl.sync.TSynchronizedRandomAccessCharList;
import gnu.trove.impl.sync.TSynchronizedRandomAccessDoubleList;
import gnu.trove.impl.sync.TSynchronizedRandomAccessFloatList;
import gnu.trove.impl.sync.TSynchronizedRandomAccessIntList;
import gnu.trove.impl.sync.TSynchronizedRandomAccessLongList;
import gnu.trove.impl.sync.TSynchronizedRandomAccessShortList;
import gnu.trove.impl.sync.TSynchronizedShortByteMap;
import gnu.trove.impl.sync.TSynchronizedShortCharMap;
import gnu.trove.impl.sync.TSynchronizedShortCollection;
import gnu.trove.impl.sync.TSynchronizedShortDoubleMap;
import gnu.trove.impl.sync.TSynchronizedShortFloatMap;
import gnu.trove.impl.sync.TSynchronizedShortIntMap;
import gnu.trove.impl.sync.TSynchronizedShortList;
import gnu.trove.impl.sync.TSynchronizedShortLongMap;
import gnu.trove.impl.sync.TSynchronizedShortObjectMap;
import gnu.trove.impl.sync.TSynchronizedShortSet;
import gnu.trove.impl.sync.TSynchronizedShortShortMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableByteByteMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableByteCharMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableByteCollection;
import gnu.trove.impl.unmodifiable.TUnmodifiableByteDoubleMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableByteFloatMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableByteIntMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableByteList;
import gnu.trove.impl.unmodifiable.TUnmodifiableByteLongMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableByteObjectMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableByteSet;
import gnu.trove.impl.unmodifiable.TUnmodifiableByteShortMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableCharByteMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableCharCharMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableCharCollection;
import gnu.trove.impl.unmodifiable.TUnmodifiableCharDoubleMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableCharFloatMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableCharIntMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableCharList;
import gnu.trove.impl.unmodifiable.TUnmodifiableCharLongMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableCharObjectMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableCharSet;
import gnu.trove.impl.unmodifiable.TUnmodifiableCharShortMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableDoubleByteMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableDoubleCharMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableDoubleCollection;
import gnu.trove.impl.unmodifiable.TUnmodifiableDoubleDoubleMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableDoubleFloatMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableDoubleIntMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableDoubleList;
import gnu.trove.impl.unmodifiable.TUnmodifiableDoubleLongMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableDoubleObjectMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableDoubleSet;
import gnu.trove.impl.unmodifiable.TUnmodifiableDoubleShortMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableFloatByteMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableFloatCharMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableFloatCollection;
import gnu.trove.impl.unmodifiable.TUnmodifiableFloatDoubleMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableFloatFloatMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableFloatIntMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableFloatList;
import gnu.trove.impl.unmodifiable.TUnmodifiableFloatLongMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableFloatObjectMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableFloatSet;
import gnu.trove.impl.unmodifiable.TUnmodifiableFloatShortMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableIntByteMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableIntCharMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableIntCollection;
import gnu.trove.impl.unmodifiable.TUnmodifiableIntDoubleMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableIntFloatMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableIntIntMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableIntList;
import gnu.trove.impl.unmodifiable.TUnmodifiableIntLongMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableIntObjectMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableIntSet;
import gnu.trove.impl.unmodifiable.TUnmodifiableIntShortMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableLongByteMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableLongCharMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableLongCollection;
import gnu.trove.impl.unmodifiable.TUnmodifiableLongDoubleMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableLongFloatMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableLongIntMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableLongList;
import gnu.trove.impl.unmodifiable.TUnmodifiableLongLongMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableLongObjectMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableLongSet;
import gnu.trove.impl.unmodifiable.TUnmodifiableLongShortMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableObjectByteMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableObjectCharMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableObjectDoubleMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableObjectFloatMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableObjectIntMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableObjectLongMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableObjectShortMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableRandomAccessByteList;
import gnu.trove.impl.unmodifiable.TUnmodifiableRandomAccessCharList;
import gnu.trove.impl.unmodifiable.TUnmodifiableRandomAccessDoubleList;
import gnu.trove.impl.unmodifiable.TUnmodifiableRandomAccessFloatList;
import gnu.trove.impl.unmodifiable.TUnmodifiableRandomAccessIntList;
import gnu.trove.impl.unmodifiable.TUnmodifiableRandomAccessLongList;
import gnu.trove.impl.unmodifiable.TUnmodifiableRandomAccessShortList;
import gnu.trove.impl.unmodifiable.TUnmodifiableShortByteMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableShortCharMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableShortCollection;
import gnu.trove.impl.unmodifiable.TUnmodifiableShortDoubleMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableShortFloatMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableShortIntMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableShortList;
import gnu.trove.impl.unmodifiable.TUnmodifiableShortLongMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableShortObjectMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableShortSet;
import gnu.trove.impl.unmodifiable.TUnmodifiableShortShortMap;
import gnu.trove.list.TByteList;
import gnu.trove.list.TCharList;
import gnu.trove.list.TDoubleList;
import gnu.trove.list.TFloatList;
import gnu.trove.list.TIntList;
import gnu.trove.list.TLongList;
import gnu.trove.list.TShortList;
import gnu.trove.map.TByteByteMap;
import gnu.trove.map.TByteCharMap;
import gnu.trove.map.TByteDoubleMap;
import gnu.trove.map.TByteFloatMap;
import gnu.trove.map.TByteIntMap;
import gnu.trove.map.TByteLongMap;
import gnu.trove.map.TByteObjectMap;
import gnu.trove.map.TByteShortMap;
import gnu.trove.map.TCharByteMap;
import gnu.trove.map.TCharCharMap;
import gnu.trove.map.TCharDoubleMap;
import gnu.trove.map.TCharFloatMap;
import gnu.trove.map.TCharIntMap;
import gnu.trove.map.TCharLongMap;
import gnu.trove.map.TCharObjectMap;
import gnu.trove.map.TCharShortMap;
import gnu.trove.map.TDoubleByteMap;
import gnu.trove.map.TDoubleCharMap;
import gnu.trove.map.TDoubleDoubleMap;
import gnu.trove.map.TDoubleFloatMap;
import gnu.trove.map.TDoubleIntMap;
import gnu.trove.map.TDoubleLongMap;
import gnu.trove.map.TDoubleObjectMap;
import gnu.trove.map.TDoubleShortMap;
import gnu.trove.map.TFloatByteMap;
import gnu.trove.map.TFloatCharMap;
import gnu.trove.map.TFloatDoubleMap;
import gnu.trove.map.TFloatFloatMap;
import gnu.trove.map.TFloatIntMap;
import gnu.trove.map.TFloatLongMap;
import gnu.trove.map.TFloatObjectMap;
import gnu.trove.map.TFloatShortMap;
import gnu.trove.map.TIntByteMap;
import gnu.trove.map.TIntCharMap;
import gnu.trove.map.TIntDoubleMap;
import gnu.trove.map.TIntFloatMap;
import gnu.trove.map.TIntIntMap;
import gnu.trove.map.TIntLongMap;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.TIntShortMap;
import gnu.trove.map.TLongByteMap;
import gnu.trove.map.TLongCharMap;
import gnu.trove.map.TLongDoubleMap;
import gnu.trove.map.TLongFloatMap;
import gnu.trove.map.TLongIntMap;
import gnu.trove.map.TLongLongMap;
import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.TLongShortMap;
import gnu.trove.map.TObjectByteMap;
import gnu.trove.map.TObjectCharMap;
import gnu.trove.map.TObjectDoubleMap;
import gnu.trove.map.TObjectFloatMap;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.TObjectLongMap;
import gnu.trove.map.TObjectShortMap;
import gnu.trove.map.TShortByteMap;
import gnu.trove.map.TShortCharMap;
import gnu.trove.map.TShortDoubleMap;
import gnu.trove.map.TShortFloatMap;
import gnu.trove.map.TShortIntMap;
import gnu.trove.map.TShortLongMap;
import gnu.trove.map.TShortObjectMap;
import gnu.trove.map.TShortShortMap;
import gnu.trove.set.TByteSet;
import gnu.trove.set.TCharSet;
import gnu.trove.set.TDoubleSet;
import gnu.trove.set.TFloatSet;
import gnu.trove.set.TIntSet;
import gnu.trove.set.TLongSet;
import gnu.trove.set.TShortSet;
import java.util.RandomAccess;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class TCollections {
    private TCollections() {
    }

    public static TDoubleCollection unmodifiableCollection(TDoubleCollection c) {
        return new TUnmodifiableDoubleCollection(c);
    }

    public static TFloatCollection unmodifiableCollection(TFloatCollection c) {
        return new TUnmodifiableFloatCollection(c);
    }

    public static TIntCollection unmodifiableCollection(TIntCollection c) {
        return new TUnmodifiableIntCollection(c);
    }

    public static TLongCollection unmodifiableCollection(TLongCollection c) {
        return new TUnmodifiableLongCollection(c);
    }

    public static TByteCollection unmodifiableCollection(TByteCollection c) {
        return new TUnmodifiableByteCollection(c);
    }

    public static TShortCollection unmodifiableCollection(TShortCollection c) {
        return new TUnmodifiableShortCollection(c);
    }

    public static TCharCollection unmodifiableCollection(TCharCollection c) {
        return new TUnmodifiableCharCollection(c);
    }

    public static TDoubleSet unmodifiableSet(TDoubleSet s) {
        return new TUnmodifiableDoubleSet(s);
    }

    public static TFloatSet unmodifiableSet(TFloatSet s) {
        return new TUnmodifiableFloatSet(s);
    }

    public static TIntSet unmodifiableSet(TIntSet s) {
        return new TUnmodifiableIntSet(s);
    }

    public static TLongSet unmodifiableSet(TLongSet s) {
        return new TUnmodifiableLongSet(s);
    }

    public static TByteSet unmodifiableSet(TByteSet s) {
        return new TUnmodifiableByteSet(s);
    }

    public static TShortSet unmodifiableSet(TShortSet s) {
        return new TUnmodifiableShortSet(s);
    }

    public static TCharSet unmodifiableSet(TCharSet s) {
        return new TUnmodifiableCharSet(s);
    }

    public static TDoubleList unmodifiableList(TDoubleList list) {
        return list instanceof RandomAccess ? new TUnmodifiableRandomAccessDoubleList(list) : new TUnmodifiableDoubleList(list);
    }

    public static TFloatList unmodifiableList(TFloatList list) {
        return list instanceof RandomAccess ? new TUnmodifiableRandomAccessFloatList(list) : new TUnmodifiableFloatList(list);
    }

    public static TIntList unmodifiableList(TIntList list) {
        return list instanceof RandomAccess ? new TUnmodifiableRandomAccessIntList(list) : new TUnmodifiableIntList(list);
    }

    public static TLongList unmodifiableList(TLongList list) {
        return list instanceof RandomAccess ? new TUnmodifiableRandomAccessLongList(list) : new TUnmodifiableLongList(list);
    }

    public static TByteList unmodifiableList(TByteList list) {
        return list instanceof RandomAccess ? new TUnmodifiableRandomAccessByteList(list) : new TUnmodifiableByteList(list);
    }

    public static TShortList unmodifiableList(TShortList list) {
        return list instanceof RandomAccess ? new TUnmodifiableRandomAccessShortList(list) : new TUnmodifiableShortList(list);
    }

    public static TCharList unmodifiableList(TCharList list) {
        return list instanceof RandomAccess ? new TUnmodifiableRandomAccessCharList(list) : new TUnmodifiableCharList(list);
    }

    public static TDoubleDoubleMap unmodifiableMap(TDoubleDoubleMap m) {
        return new TUnmodifiableDoubleDoubleMap(m);
    }

    public static TDoubleFloatMap unmodifiableMap(TDoubleFloatMap m) {
        return new TUnmodifiableDoubleFloatMap(m);
    }

    public static TDoubleIntMap unmodifiableMap(TDoubleIntMap m) {
        return new TUnmodifiableDoubleIntMap(m);
    }

    public static TDoubleLongMap unmodifiableMap(TDoubleLongMap m) {
        return new TUnmodifiableDoubleLongMap(m);
    }

    public static TDoubleByteMap unmodifiableMap(TDoubleByteMap m) {
        return new TUnmodifiableDoubleByteMap(m);
    }

    public static TDoubleShortMap unmodifiableMap(TDoubleShortMap m) {
        return new TUnmodifiableDoubleShortMap(m);
    }

    public static TDoubleCharMap unmodifiableMap(TDoubleCharMap m) {
        return new TUnmodifiableDoubleCharMap(m);
    }

    public static TFloatDoubleMap unmodifiableMap(TFloatDoubleMap m) {
        return new TUnmodifiableFloatDoubleMap(m);
    }

    public static TFloatFloatMap unmodifiableMap(TFloatFloatMap m) {
        return new TUnmodifiableFloatFloatMap(m);
    }

    public static TFloatIntMap unmodifiableMap(TFloatIntMap m) {
        return new TUnmodifiableFloatIntMap(m);
    }

    public static TFloatLongMap unmodifiableMap(TFloatLongMap m) {
        return new TUnmodifiableFloatLongMap(m);
    }

    public static TFloatByteMap unmodifiableMap(TFloatByteMap m) {
        return new TUnmodifiableFloatByteMap(m);
    }

    public static TFloatShortMap unmodifiableMap(TFloatShortMap m) {
        return new TUnmodifiableFloatShortMap(m);
    }

    public static TFloatCharMap unmodifiableMap(TFloatCharMap m) {
        return new TUnmodifiableFloatCharMap(m);
    }

    public static TIntDoubleMap unmodifiableMap(TIntDoubleMap m) {
        return new TUnmodifiableIntDoubleMap(m);
    }

    public static TIntFloatMap unmodifiableMap(TIntFloatMap m) {
        return new TUnmodifiableIntFloatMap(m);
    }

    public static TIntIntMap unmodifiableMap(TIntIntMap m) {
        return new TUnmodifiableIntIntMap(m);
    }

    public static TIntLongMap unmodifiableMap(TIntLongMap m) {
        return new TUnmodifiableIntLongMap(m);
    }

    public static TIntByteMap unmodifiableMap(TIntByteMap m) {
        return new TUnmodifiableIntByteMap(m);
    }

    public static TIntShortMap unmodifiableMap(TIntShortMap m) {
        return new TUnmodifiableIntShortMap(m);
    }

    public static TIntCharMap unmodifiableMap(TIntCharMap m) {
        return new TUnmodifiableIntCharMap(m);
    }

    public static TLongDoubleMap unmodifiableMap(TLongDoubleMap m) {
        return new TUnmodifiableLongDoubleMap(m);
    }

    public static TLongFloatMap unmodifiableMap(TLongFloatMap m) {
        return new TUnmodifiableLongFloatMap(m);
    }

    public static TLongIntMap unmodifiableMap(TLongIntMap m) {
        return new TUnmodifiableLongIntMap(m);
    }

    public static TLongLongMap unmodifiableMap(TLongLongMap m) {
        return new TUnmodifiableLongLongMap(m);
    }

    public static TLongByteMap unmodifiableMap(TLongByteMap m) {
        return new TUnmodifiableLongByteMap(m);
    }

    public static TLongShortMap unmodifiableMap(TLongShortMap m) {
        return new TUnmodifiableLongShortMap(m);
    }

    public static TLongCharMap unmodifiableMap(TLongCharMap m) {
        return new TUnmodifiableLongCharMap(m);
    }

    public static TByteDoubleMap unmodifiableMap(TByteDoubleMap m) {
        return new TUnmodifiableByteDoubleMap(m);
    }

    public static TByteFloatMap unmodifiableMap(TByteFloatMap m) {
        return new TUnmodifiableByteFloatMap(m);
    }

    public static TByteIntMap unmodifiableMap(TByteIntMap m) {
        return new TUnmodifiableByteIntMap(m);
    }

    public static TByteLongMap unmodifiableMap(TByteLongMap m) {
        return new TUnmodifiableByteLongMap(m);
    }

    public static TByteByteMap unmodifiableMap(TByteByteMap m) {
        return new TUnmodifiableByteByteMap(m);
    }

    public static TByteShortMap unmodifiableMap(TByteShortMap m) {
        return new TUnmodifiableByteShortMap(m);
    }

    public static TByteCharMap unmodifiableMap(TByteCharMap m) {
        return new TUnmodifiableByteCharMap(m);
    }

    public static TShortDoubleMap unmodifiableMap(TShortDoubleMap m) {
        return new TUnmodifiableShortDoubleMap(m);
    }

    public static TShortFloatMap unmodifiableMap(TShortFloatMap m) {
        return new TUnmodifiableShortFloatMap(m);
    }

    public static TShortIntMap unmodifiableMap(TShortIntMap m) {
        return new TUnmodifiableShortIntMap(m);
    }

    public static TShortLongMap unmodifiableMap(TShortLongMap m) {
        return new TUnmodifiableShortLongMap(m);
    }

    public static TShortByteMap unmodifiableMap(TShortByteMap m) {
        return new TUnmodifiableShortByteMap(m);
    }

    public static TShortShortMap unmodifiableMap(TShortShortMap m) {
        return new TUnmodifiableShortShortMap(m);
    }

    public static TShortCharMap unmodifiableMap(TShortCharMap m) {
        return new TUnmodifiableShortCharMap(m);
    }

    public static TCharDoubleMap unmodifiableMap(TCharDoubleMap m) {
        return new TUnmodifiableCharDoubleMap(m);
    }

    public static TCharFloatMap unmodifiableMap(TCharFloatMap m) {
        return new TUnmodifiableCharFloatMap(m);
    }

    public static TCharIntMap unmodifiableMap(TCharIntMap m) {
        return new TUnmodifiableCharIntMap(m);
    }

    public static TCharLongMap unmodifiableMap(TCharLongMap m) {
        return new TUnmodifiableCharLongMap(m);
    }

    public static TCharByteMap unmodifiableMap(TCharByteMap m) {
        return new TUnmodifiableCharByteMap(m);
    }

    public static TCharShortMap unmodifiableMap(TCharShortMap m) {
        return new TUnmodifiableCharShortMap(m);
    }

    public static TCharCharMap unmodifiableMap(TCharCharMap m) {
        return new TUnmodifiableCharCharMap(m);
    }

    public static <V> TDoubleObjectMap<V> unmodifiableMap(TDoubleObjectMap<V> m) {
        return new TUnmodifiableDoubleObjectMap<V>(m);
    }

    public static <V> TFloatObjectMap<V> unmodifiableMap(TFloatObjectMap<V> m) {
        return new TUnmodifiableFloatObjectMap<V>(m);
    }

    public static <V> TIntObjectMap<V> unmodifiableMap(TIntObjectMap<V> m) {
        return new TUnmodifiableIntObjectMap<V>(m);
    }

    public static <V> TLongObjectMap<V> unmodifiableMap(TLongObjectMap<V> m) {
        return new TUnmodifiableLongObjectMap<V>(m);
    }

    public static <V> TByteObjectMap<V> unmodifiableMap(TByteObjectMap<V> m) {
        return new TUnmodifiableByteObjectMap<V>(m);
    }

    public static <V> TShortObjectMap<V> unmodifiableMap(TShortObjectMap<V> m) {
        return new TUnmodifiableShortObjectMap<V>(m);
    }

    public static <V> TCharObjectMap<V> unmodifiableMap(TCharObjectMap<V> m) {
        return new TUnmodifiableCharObjectMap<V>(m);
    }

    public static <K> TObjectDoubleMap<K> unmodifiableMap(TObjectDoubleMap<K> m) {
        return new TUnmodifiableObjectDoubleMap<K>(m);
    }

    public static <K> TObjectFloatMap<K> unmodifiableMap(TObjectFloatMap<K> m) {
        return new TUnmodifiableObjectFloatMap<K>(m);
    }

    public static <K> TObjectIntMap<K> unmodifiableMap(TObjectIntMap<K> m) {
        return new TUnmodifiableObjectIntMap<K>(m);
    }

    public static <K> TObjectLongMap<K> unmodifiableMap(TObjectLongMap<K> m) {
        return new TUnmodifiableObjectLongMap<K>(m);
    }

    public static <K> TObjectByteMap<K> unmodifiableMap(TObjectByteMap<K> m) {
        return new TUnmodifiableObjectByteMap<K>(m);
    }

    public static <K> TObjectShortMap<K> unmodifiableMap(TObjectShortMap<K> m) {
        return new TUnmodifiableObjectShortMap<K>(m);
    }

    public static <K> TObjectCharMap<K> unmodifiableMap(TObjectCharMap<K> m) {
        return new TUnmodifiableObjectCharMap<K>(m);
    }

    public static TDoubleCollection synchronizedCollection(TDoubleCollection c) {
        return new TSynchronizedDoubleCollection(c);
    }

    static TDoubleCollection synchronizedCollection(TDoubleCollection c, Object mutex) {
        return new TSynchronizedDoubleCollection(c, mutex);
    }

    public static TFloatCollection synchronizedCollection(TFloatCollection c) {
        return new TSynchronizedFloatCollection(c);
    }

    static TFloatCollection synchronizedCollection(TFloatCollection c, Object mutex) {
        return new TSynchronizedFloatCollection(c, mutex);
    }

    public static TIntCollection synchronizedCollection(TIntCollection c) {
        return new TSynchronizedIntCollection(c);
    }

    static TIntCollection synchronizedCollection(TIntCollection c, Object mutex) {
        return new TSynchronizedIntCollection(c, mutex);
    }

    public static TLongCollection synchronizedCollection(TLongCollection c) {
        return new TSynchronizedLongCollection(c);
    }

    static TLongCollection synchronizedCollection(TLongCollection c, Object mutex) {
        return new TSynchronizedLongCollection(c, mutex);
    }

    public static TByteCollection synchronizedCollection(TByteCollection c) {
        return new TSynchronizedByteCollection(c);
    }

    static TByteCollection synchronizedCollection(TByteCollection c, Object mutex) {
        return new TSynchronizedByteCollection(c, mutex);
    }

    public static TShortCollection synchronizedCollection(TShortCollection c) {
        return new TSynchronizedShortCollection(c);
    }

    static TShortCollection synchronizedCollection(TShortCollection c, Object mutex) {
        return new TSynchronizedShortCollection(c, mutex);
    }

    public static TCharCollection synchronizedCollection(TCharCollection c) {
        return new TSynchronizedCharCollection(c);
    }

    static TCharCollection synchronizedCollection(TCharCollection c, Object mutex) {
        return new TSynchronizedCharCollection(c, mutex);
    }

    public static TDoubleSet synchronizedSet(TDoubleSet s) {
        return new TSynchronizedDoubleSet(s);
    }

    static TDoubleSet synchronizedSet(TDoubleSet s, Object mutex) {
        return new TSynchronizedDoubleSet(s, mutex);
    }

    public static TFloatSet synchronizedSet(TFloatSet s) {
        return new TSynchronizedFloatSet(s);
    }

    static TFloatSet synchronizedSet(TFloatSet s, Object mutex) {
        return new TSynchronizedFloatSet(s, mutex);
    }

    public static TIntSet synchronizedSet(TIntSet s) {
        return new TSynchronizedIntSet(s);
    }

    static TIntSet synchronizedSet(TIntSet s, Object mutex) {
        return new TSynchronizedIntSet(s, mutex);
    }

    public static TLongSet synchronizedSet(TLongSet s) {
        return new TSynchronizedLongSet(s);
    }

    static TLongSet synchronizedSet(TLongSet s, Object mutex) {
        return new TSynchronizedLongSet(s, mutex);
    }

    public static TByteSet synchronizedSet(TByteSet s) {
        return new TSynchronizedByteSet(s);
    }

    static TByteSet synchronizedSet(TByteSet s, Object mutex) {
        return new TSynchronizedByteSet(s, mutex);
    }

    public static TShortSet synchronizedSet(TShortSet s) {
        return new TSynchronizedShortSet(s);
    }

    static TShortSet synchronizedSet(TShortSet s, Object mutex) {
        return new TSynchronizedShortSet(s, mutex);
    }

    public static TCharSet synchronizedSet(TCharSet s) {
        return new TSynchronizedCharSet(s);
    }

    static TCharSet synchronizedSet(TCharSet s, Object mutex) {
        return new TSynchronizedCharSet(s, mutex);
    }

    public static TDoubleList synchronizedList(TDoubleList list) {
        return list instanceof RandomAccess ? new TSynchronizedRandomAccessDoubleList(list) : new TSynchronizedDoubleList(list);
    }

    static TDoubleList synchronizedList(TDoubleList list, Object mutex) {
        return list instanceof RandomAccess ? new TSynchronizedRandomAccessDoubleList(list, mutex) : new TSynchronizedDoubleList(list, mutex);
    }

    public static TFloatList synchronizedList(TFloatList list) {
        return list instanceof RandomAccess ? new TSynchronizedRandomAccessFloatList(list) : new TSynchronizedFloatList(list);
    }

    static TFloatList synchronizedList(TFloatList list, Object mutex) {
        return list instanceof RandomAccess ? new TSynchronizedRandomAccessFloatList(list, mutex) : new TSynchronizedFloatList(list, mutex);
    }

    public static TIntList synchronizedList(TIntList list) {
        return list instanceof RandomAccess ? new TSynchronizedRandomAccessIntList(list) : new TSynchronizedIntList(list);
    }

    static TIntList synchronizedList(TIntList list, Object mutex) {
        return list instanceof RandomAccess ? new TSynchronizedRandomAccessIntList(list, mutex) : new TSynchronizedIntList(list, mutex);
    }

    public static TLongList synchronizedList(TLongList list) {
        return list instanceof RandomAccess ? new TSynchronizedRandomAccessLongList(list) : new TSynchronizedLongList(list);
    }

    static TLongList synchronizedList(TLongList list, Object mutex) {
        return list instanceof RandomAccess ? new TSynchronizedRandomAccessLongList(list, mutex) : new TSynchronizedLongList(list, mutex);
    }

    public static TByteList synchronizedList(TByteList list) {
        return list instanceof RandomAccess ? new TSynchronizedRandomAccessByteList(list) : new TSynchronizedByteList(list);
    }

    static TByteList synchronizedList(TByteList list, Object mutex) {
        return list instanceof RandomAccess ? new TSynchronizedRandomAccessByteList(list, mutex) : new TSynchronizedByteList(list, mutex);
    }

    public static TShortList synchronizedList(TShortList list) {
        return list instanceof RandomAccess ? new TSynchronizedRandomAccessShortList(list) : new TSynchronizedShortList(list);
    }

    static TShortList synchronizedList(TShortList list, Object mutex) {
        return list instanceof RandomAccess ? new TSynchronizedRandomAccessShortList(list, mutex) : new TSynchronizedShortList(list, mutex);
    }

    public static TCharList synchronizedList(TCharList list) {
        return list instanceof RandomAccess ? new TSynchronizedRandomAccessCharList(list) : new TSynchronizedCharList(list);
    }

    static TCharList synchronizedList(TCharList list, Object mutex) {
        return list instanceof RandomAccess ? new TSynchronizedRandomAccessCharList(list, mutex) : new TSynchronizedCharList(list, mutex);
    }

    public static TDoubleDoubleMap synchronizedMap(TDoubleDoubleMap m) {
        return new TSynchronizedDoubleDoubleMap(m);
    }

    public static TDoubleFloatMap synchronizedMap(TDoubleFloatMap m) {
        return new TSynchronizedDoubleFloatMap(m);
    }

    public static TDoubleIntMap synchronizedMap(TDoubleIntMap m) {
        return new TSynchronizedDoubleIntMap(m);
    }

    public static TDoubleLongMap synchronizedMap(TDoubleLongMap m) {
        return new TSynchronizedDoubleLongMap(m);
    }

    public static TDoubleByteMap synchronizedMap(TDoubleByteMap m) {
        return new TSynchronizedDoubleByteMap(m);
    }

    public static TDoubleShortMap synchronizedMap(TDoubleShortMap m) {
        return new TSynchronizedDoubleShortMap(m);
    }

    public static TDoubleCharMap synchronizedMap(TDoubleCharMap m) {
        return new TSynchronizedDoubleCharMap(m);
    }

    public static TFloatDoubleMap synchronizedMap(TFloatDoubleMap m) {
        return new TSynchronizedFloatDoubleMap(m);
    }

    public static TFloatFloatMap synchronizedMap(TFloatFloatMap m) {
        return new TSynchronizedFloatFloatMap(m);
    }

    public static TFloatIntMap synchronizedMap(TFloatIntMap m) {
        return new TSynchronizedFloatIntMap(m);
    }

    public static TFloatLongMap synchronizedMap(TFloatLongMap m) {
        return new TSynchronizedFloatLongMap(m);
    }

    public static TFloatByteMap synchronizedMap(TFloatByteMap m) {
        return new TSynchronizedFloatByteMap(m);
    }

    public static TFloatShortMap synchronizedMap(TFloatShortMap m) {
        return new TSynchronizedFloatShortMap(m);
    }

    public static TFloatCharMap synchronizedMap(TFloatCharMap m) {
        return new TSynchronizedFloatCharMap(m);
    }

    public static TIntDoubleMap synchronizedMap(TIntDoubleMap m) {
        return new TSynchronizedIntDoubleMap(m);
    }

    public static TIntFloatMap synchronizedMap(TIntFloatMap m) {
        return new TSynchronizedIntFloatMap(m);
    }

    public static TIntIntMap synchronizedMap(TIntIntMap m) {
        return new TSynchronizedIntIntMap(m);
    }

    public static TIntLongMap synchronizedMap(TIntLongMap m) {
        return new TSynchronizedIntLongMap(m);
    }

    public static TIntByteMap synchronizedMap(TIntByteMap m) {
        return new TSynchronizedIntByteMap(m);
    }

    public static TIntShortMap synchronizedMap(TIntShortMap m) {
        return new TSynchronizedIntShortMap(m);
    }

    public static TIntCharMap synchronizedMap(TIntCharMap m) {
        return new TSynchronizedIntCharMap(m);
    }

    public static TLongDoubleMap synchronizedMap(TLongDoubleMap m) {
        return new TSynchronizedLongDoubleMap(m);
    }

    public static TLongFloatMap synchronizedMap(TLongFloatMap m) {
        return new TSynchronizedLongFloatMap(m);
    }

    public static TLongIntMap synchronizedMap(TLongIntMap m) {
        return new TSynchronizedLongIntMap(m);
    }

    public static TLongLongMap synchronizedMap(TLongLongMap m) {
        return new TSynchronizedLongLongMap(m);
    }

    public static TLongByteMap synchronizedMap(TLongByteMap m) {
        return new TSynchronizedLongByteMap(m);
    }

    public static TLongShortMap synchronizedMap(TLongShortMap m) {
        return new TSynchronizedLongShortMap(m);
    }

    public static TLongCharMap synchronizedMap(TLongCharMap m) {
        return new TSynchronizedLongCharMap(m);
    }

    public static TByteDoubleMap synchronizedMap(TByteDoubleMap m) {
        return new TSynchronizedByteDoubleMap(m);
    }

    public static TByteFloatMap synchronizedMap(TByteFloatMap m) {
        return new TSynchronizedByteFloatMap(m);
    }

    public static TByteIntMap synchronizedMap(TByteIntMap m) {
        return new TSynchronizedByteIntMap(m);
    }

    public static TByteLongMap synchronizedMap(TByteLongMap m) {
        return new TSynchronizedByteLongMap(m);
    }

    public static TByteByteMap synchronizedMap(TByteByteMap m) {
        return new TSynchronizedByteByteMap(m);
    }

    public static TByteShortMap synchronizedMap(TByteShortMap m) {
        return new TSynchronizedByteShortMap(m);
    }

    public static TByteCharMap synchronizedMap(TByteCharMap m) {
        return new TSynchronizedByteCharMap(m);
    }

    public static TShortDoubleMap synchronizedMap(TShortDoubleMap m) {
        return new TSynchronizedShortDoubleMap(m);
    }

    public static TShortFloatMap synchronizedMap(TShortFloatMap m) {
        return new TSynchronizedShortFloatMap(m);
    }

    public static TShortIntMap synchronizedMap(TShortIntMap m) {
        return new TSynchronizedShortIntMap(m);
    }

    public static TShortLongMap synchronizedMap(TShortLongMap m) {
        return new TSynchronizedShortLongMap(m);
    }

    public static TShortByteMap synchronizedMap(TShortByteMap m) {
        return new TSynchronizedShortByteMap(m);
    }

    public static TShortShortMap synchronizedMap(TShortShortMap m) {
        return new TSynchronizedShortShortMap(m);
    }

    public static TShortCharMap synchronizedMap(TShortCharMap m) {
        return new TSynchronizedShortCharMap(m);
    }

    public static TCharDoubleMap synchronizedMap(TCharDoubleMap m) {
        return new TSynchronizedCharDoubleMap(m);
    }

    public static TCharFloatMap synchronizedMap(TCharFloatMap m) {
        return new TSynchronizedCharFloatMap(m);
    }

    public static TCharIntMap synchronizedMap(TCharIntMap m) {
        return new TSynchronizedCharIntMap(m);
    }

    public static TCharLongMap synchronizedMap(TCharLongMap m) {
        return new TSynchronizedCharLongMap(m);
    }

    public static TCharByteMap synchronizedMap(TCharByteMap m) {
        return new TSynchronizedCharByteMap(m);
    }

    public static TCharShortMap synchronizedMap(TCharShortMap m) {
        return new TSynchronizedCharShortMap(m);
    }

    public static TCharCharMap synchronizedMap(TCharCharMap m) {
        return new TSynchronizedCharCharMap(m);
    }

    public static <V> TDoubleObjectMap<V> synchronizedMap(TDoubleObjectMap<V> m) {
        return new TSynchronizedDoubleObjectMap<V>(m);
    }

    public static <V> TFloatObjectMap<V> synchronizedMap(TFloatObjectMap<V> m) {
        return new TSynchronizedFloatObjectMap<V>(m);
    }

    public static <V> TIntObjectMap<V> synchronizedMap(TIntObjectMap<V> m) {
        return new TSynchronizedIntObjectMap<V>(m);
    }

    public static <V> TLongObjectMap<V> synchronizedMap(TLongObjectMap<V> m) {
        return new TSynchronizedLongObjectMap<V>(m);
    }

    public static <V> TByteObjectMap<V> synchronizedMap(TByteObjectMap<V> m) {
        return new TSynchronizedByteObjectMap<V>(m);
    }

    public static <V> TShortObjectMap<V> synchronizedMap(TShortObjectMap<V> m) {
        return new TSynchronizedShortObjectMap<V>(m);
    }

    public static <V> TCharObjectMap<V> synchronizedMap(TCharObjectMap<V> m) {
        return new TSynchronizedCharObjectMap<V>(m);
    }

    public static <K> TObjectDoubleMap<K> synchronizedMap(TObjectDoubleMap<K> m) {
        return new TSynchronizedObjectDoubleMap<K>(m);
    }

    public static <K> TObjectFloatMap<K> synchronizedMap(TObjectFloatMap<K> m) {
        return new TSynchronizedObjectFloatMap<K>(m);
    }

    public static <K> TObjectIntMap<K> synchronizedMap(TObjectIntMap<K> m) {
        return new TSynchronizedObjectIntMap<K>(m);
    }

    public static <K> TObjectLongMap<K> synchronizedMap(TObjectLongMap<K> m) {
        return new TSynchronizedObjectLongMap<K>(m);
    }

    public static <K> TObjectByteMap<K> synchronizedMap(TObjectByteMap<K> m) {
        return new TSynchronizedObjectByteMap<K>(m);
    }

    public static <K> TObjectShortMap<K> synchronizedMap(TObjectShortMap<K> m) {
        return new TSynchronizedObjectShortMap<K>(m);
    }

    public static <K> TObjectCharMap<K> synchronizedMap(TObjectCharMap<K> m) {
        return new TSynchronizedObjectCharMap<K>(m);
    }
}

