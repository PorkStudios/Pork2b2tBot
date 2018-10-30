/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.ints.IntComparator;
import it.unimi.dsi.fastutil.ints.IntPriorityQueue;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Comparator;

public final class IntPriorityQueues {
    private IntPriorityQueues() {
    }

    public static IntPriorityQueue synchronize(IntPriorityQueue q) {
        return new SynchronizedPriorityQueue(q);
    }

    public static IntPriorityQueue synchronize(IntPriorityQueue q, Object sync) {
        return new SynchronizedPriorityQueue(q, sync);
    }

    public static class SynchronizedPriorityQueue
    implements IntPriorityQueue {
        protected final IntPriorityQueue q;
        protected final Object sync;

        protected SynchronizedPriorityQueue(IntPriorityQueue q, Object sync) {
            this.q = q;
            this.sync = sync;
        }

        protected SynchronizedPriorityQueue(IntPriorityQueue q) {
            this.q = q;
            this.sync = this;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void enqueue(int x) {
            Object object = this.sync;
            synchronized (object) {
                this.q.enqueue(x);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public int dequeueInt() {
            Object object = this.sync;
            synchronized (object) {
                return this.q.dequeueInt();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public int firstInt() {
            Object object = this.sync;
            synchronized (object) {
                return this.q.firstInt();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public int lastInt() {
            Object object = this.sync;
            synchronized (object) {
                return this.q.lastInt();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean isEmpty() {
            Object object = this.sync;
            synchronized (object) {
                return this.q.isEmpty();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public int size() {
            Object object = this.sync;
            synchronized (object) {
                return this.q.size();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void clear() {
            Object object = this.sync;
            synchronized (object) {
                this.q.clear();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void changed() {
            Object object = this.sync;
            synchronized (object) {
                this.q.changed();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public IntComparator comparator() {
            Object object = this.sync;
            synchronized (object) {
                return this.q.comparator();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Deprecated
        @Override
        public void enqueue(Integer x) {
            Object object = this.sync;
            synchronized (object) {
                this.q.enqueue(x);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Deprecated
        @Override
        public Integer dequeue() {
            Object object = this.sync;
            synchronized (object) {
                return this.q.dequeue();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Deprecated
        @Override
        public Integer first() {
            Object object = this.sync;
            synchronized (object) {
                return this.q.first();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Deprecated
        @Override
        public Integer last() {
            Object object = this.sync;
            synchronized (object) {
                return this.q.last();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public int hashCode() {
            Object object = this.sync;
            synchronized (object) {
                return this.q.hashCode();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            Object object = this.sync;
            synchronized (object) {
                return this.q.equals(o);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        private void writeObject(ObjectOutputStream s) throws IOException {
            Object object = this.sync;
            synchronized (object) {
                s.defaultWriteObject();
            }
        }
    }

}

