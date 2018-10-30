/*
 * Decompiled with CFR 0_132.
 */
package com.google.common.util.concurrent;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.AggregateFuture;
import com.google.common.util.concurrent.ListenableFuture;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;

@GwtCompatible(emulated=true)
abstract class CollectionFuture<V, C>
extends AggregateFuture<V, C> {
    CollectionFuture() {
    }

    static final class ListFuture<V>
    extends CollectionFuture<V, List<V>> {
        ListFuture(ImmutableCollection<? extends ListenableFuture<? extends V>> futures, boolean allMustSucceed) {
            this.init((AggregateFuture.RunningState)((Object)new ListFutureRunningState(futures, allMustSucceed)));
        }

        private final class ListFutureRunningState
        extends CollectionFuture<V, List<V>> {
            ListFutureRunningState(ImmutableCollection<? extends ListenableFuture<? extends V>> futures, boolean allMustSucceed) {
                super(futures, allMustSucceed);
            }

            public List<V> combine(List<Optional<V>> values) {
                ArrayList<Object> result = Lists.newArrayListWithCapacity(values.size());
                for (Optional<V> element : values) {
                    result.add(element != null ? (Object)element.orNull() : null);
                }
                return Collections.unmodifiableList(result);
            }
        }

    }

    abstract class CollectionFutureRunningState
    extends AggregateFuture<V, C> {
        private List<Optional<V>> values;

        CollectionFutureRunningState(ImmutableCollection<? extends ListenableFuture<? extends V>> futures, boolean allMustSucceed) {
            super(futures, allMustSucceed, true);
            this.values = futures.isEmpty() ? ImmutableList.of() : Lists.newArrayListWithCapacity(futures.size());
            for (int i = 0; i < futures.size(); ++i) {
                this.values.add(null);
            }
        }

        final void collectOneValue(boolean allMustSucceed, int index, @Nullable V returnValue) {
            List<Optional<V>> localValues = this.values;
            if (localValues != null) {
                localValues.set(index, Optional.fromNullable(returnValue));
            } else {
                Preconditions.checkState(allMustSucceed || CollectionFuture.this.isCancelled(), "Future was done before all dependencies completed");
            }
        }

        final void handleAllCompleted() {
            List<Optional<V>> localValues = this.values;
            if (localValues != null) {
                CollectionFuture.this.set(this.combine(localValues));
            } else {
                Preconditions.checkState(CollectionFuture.this.isDone());
            }
        }

        void releaseResourcesAfterFailure() {
            AggregateFuture.RunningState.super.releaseResourcesAfterFailure();
            this.values = null;
        }

        abstract C combine(List<Optional<V>> var1);
    }

}

