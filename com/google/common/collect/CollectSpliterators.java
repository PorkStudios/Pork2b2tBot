/*
 * Decompiled with CFR 0_132.
 */
package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import java.util.Comparator;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import javax.annotation.Nullable;

@GwtCompatible
final class CollectSpliterators {
    private CollectSpliterators() {
    }

    static <T> Spliterator<T> indexed(int size, int extraCharacteristics, IntFunction<T> function) {
        return CollectSpliterators.indexed(size, extraCharacteristics, function, null);
    }

    static <T> Spliterator<T> indexed(int size, int extraCharacteristics, IntFunction<T> function, Comparator<? super T> comparator) {
        if (comparator != null) {
            Preconditions.checkArgument((extraCharacteristics & 4) != 0);
        }
        class WithCharacteristics
        implements Spliterator<T> {
            private final Spliterator<T> delegate;
            final /* synthetic */ int val$extraCharacteristics;
            final /* synthetic */ Comparator val$comparator;

            WithCharacteristics(Spliterator<T> delegate) {
                this.val$extraCharacteristics = n;
                this.val$comparator = var3_3;
                this.delegate = delegate;
            }

            @Override
            public boolean tryAdvance(Consumer<? super T> action) {
                return this.delegate.tryAdvance(action);
            }

            @Override
            public void forEachRemaining(Consumer<? super T> action) {
                this.delegate.forEachRemaining(action);
            }

            @Nullable
            @Override
            public Spliterator<T> trySplit() {
                Spliterator<T> split = this.delegate.trySplit();
                return split == null ? null : new WithCharacteristics(split, this.val$extraCharacteristics, this.val$comparator);
            }

            @Override
            public long estimateSize() {
                return this.delegate.estimateSize();
            }

            @Override
            public int characteristics() {
                return this.delegate.characteristics() | this.val$extraCharacteristics;
            }

            @Override
            public Comparator<? super T> getComparator() {
                if (this.hasCharacteristics(4)) {
                    return this.val$comparator;
                }
                throw new IllegalStateException();
            }
        }
        return new WithCharacteristics(IntStream.range(0, size).mapToObj(function).spliterator(), extraCharacteristics, comparator);
    }

    static <F, T> Spliterator<T> map(final Spliterator<F> fromSpliterator, final Function<? super F, ? extends T> function) {
        Preconditions.checkNotNull(fromSpliterator);
        Preconditions.checkNotNull(function);
        return new Spliterator<T>(){

            @Override
            public boolean tryAdvance(Consumer<? super T> action) {
                return fromSpliterator.tryAdvance(arg_0 -> .lambda$tryAdvance$0(action, function, arg_0));
            }

            @Override
            public void forEachRemaining(Consumer<? super T> action) {
                fromSpliterator.forEachRemaining(arg_0 -> .lambda$forEachRemaining$1(action, function, arg_0));
            }

            @Override
            public Spliterator<T> trySplit() {
                Spliterator fromSplit = fromSpliterator.trySplit();
                return fromSplit != null ? CollectSpliterators.map(fromSplit, function) : null;
            }

            @Override
            public long estimateSize() {
                return fromSpliterator.estimateSize();
            }

            @Override
            public int characteristics() {
                return fromSpliterator.characteristics() & -262;
            }

            private static /* synthetic */ void lambda$forEachRemaining$1(Consumer action, Function function2, Object fromElement) {
                action.accept(function2.apply(fromElement));
            }

            private static /* synthetic */ void lambda$tryAdvance$0(Consumer action, Function function2, Object fromElement) {
                action.accept(function2.apply(fromElement));
            }
        };
    }

    static <T> Spliterator<T> filter(Spliterator<T> fromSpliterator, final Predicate<? super T> predicate) {
        Preconditions.checkNotNull(fromSpliterator);
        Preconditions.checkNotNull(predicate);
        class Splitr
        implements Spliterator<T>,
        Consumer<T> {
            T holder = null;

            Splitr() {
            }

            @Override
            public void accept(T t) {
                this.holder = t;
            }

            @Override
            public boolean tryAdvance(Consumer<? super T> action) {
                while (Spliterator.this.tryAdvance(this)) {
                    try {
                        if (!predicate.test(this.holder)) continue;
                        action.accept(this.holder);
                        boolean bl = true;
                        return bl;
                    }
                    finally {
                        this.holder = null;
                    }
                }
                return false;
            }

            @Override
            public Spliterator<T> trySplit() {
                Spliterator fromSplit = Spliterator.this.trySplit();
                return fromSplit == null ? null : CollectSpliterators.filter(fromSplit, predicate);
            }

            @Override
            public long estimateSize() {
                return Spliterator.this.estimateSize() / 2L;
            }

            @Override
            public Comparator<? super T> getComparator() {
                return Spliterator.this.getComparator();
            }

            @Override
            public int characteristics() {
                return Spliterator.this.characteristics() & 277;
            }
        }
        return fromSpliterator.new Splitr();
    }

    static <F, T> Spliterator<T> flatMap(Spliterator<F> fromSpliterator, Function<? super F, Spliterator<T>> function, int topCharacteristics, long topSize) {
        Preconditions.checkArgument((topCharacteristics & 16384) == 0, "flatMap does not support SUBSIZED characteristic");
        Preconditions.checkArgument((topCharacteristics & 4) == 0, "flatMap does not support SORTED characteristic");
        Preconditions.checkNotNull(fromSpliterator);
        Preconditions.checkNotNull(function);
        class FlatMapSpliterator
        implements Spliterator<T> {
            @Nullable
            Spliterator<T> prefix;
            final Spliterator<F> from;
            int characteristics;
            long estimatedSize;
            final /* synthetic */ Function val$function;

            FlatMapSpliterator(Spliterator<T> prefix, Spliterator<F> from, int characteristics, long estimatedSize) {
                this.val$function = var6_5;
                this.prefix = prefix;
                this.from = from;
                this.characteristics = characteristics;
                this.estimatedSize = estimatedSize;
            }

            @Override
            public boolean tryAdvance(Consumer<? super T> action) {
                do {
                    if (this.prefix != null && this.prefix.tryAdvance(action)) {
                        if (this.estimatedSize != Long.MAX_VALUE) {
                            --this.estimatedSize;
                        }
                        return true;
                    }
                    this.prefix = null;
                } while (this.from.tryAdvance(arg_0 -> this.lambda$tryAdvance$0(this.val$function, arg_0)));
                return false;
            }

            @Override
            public void forEachRemaining(Consumer<? super T> action) {
                if (this.prefix != null) {
                    this.prefix.forEachRemaining(action);
                    this.prefix = null;
                }
                this.from.forEachRemaining(arg_0 -> FlatMapSpliterator.lambda$forEachRemaining$1(this.val$function, action, arg_0));
                this.estimatedSize = 0L;
            }

            @Override
            public Spliterator<T> trySplit() {
                Spliterator<F> fromSplit = this.from.trySplit();
                if (fromSplit != null) {
                    int splitCharacteristics = this.characteristics & -65;
                    long estSplitSize = this.estimateSize();
                    if (estSplitSize < Long.MAX_VALUE) {
                        this.estimatedSize -= (estSplitSize /= 2L);
                        this.characteristics = splitCharacteristics;
                    }
                    FlatMapSpliterator result = new FlatMapSpliterator(this.prefix, fromSplit, splitCharacteristics, estSplitSize, this.val$function);
                    this.prefix = null;
                    return result;
                }
                if (this.prefix != null) {
                    Spliterator<T> result = this.prefix;
                    this.prefix = null;
                    return result;
                }
                return null;
            }

            @Override
            public long estimateSize() {
                if (this.prefix != null) {
                    this.estimatedSize = Math.max(this.estimatedSize, this.prefix.estimateSize());
                }
                return Math.max(this.estimatedSize, 0L);
            }

            @Override
            public int characteristics() {
                return this.characteristics;
            }

            private static /* synthetic */ void lambda$forEachRemaining$1(Function function, Consumer action, Object fromElement) {
                ((Spliterator)function.apply(fromElement)).forEachRemaining(action);
            }

            private /* synthetic */ void lambda$tryAdvance$0(Function function, Object fromElement) {
                this.prefix = (Spliterator)function.apply(fromElement);
            }
        }
        return new FlatMapSpliterator(null, fromSpliterator, topCharacteristics, topSize, function);
    }

}

