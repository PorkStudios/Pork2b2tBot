/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.shorts.ShortComparator;
import it.unimi.dsi.fastutil.shorts.ShortPriorityQueue;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Comparator;

public final class ShortPriorityQueues {
    private ShortPriorityQueues() {
    }

    public static ShortPriorityQueue synchronize(ShortPriorityQueue q) {
        return new SynchronizedPriorityQueue(q);
    }

    public static ShortPriorityQueue synchronize(ShortPriorityQueue q, Object sync) {
        return new SynchronizedPriorityQueue(q, sync);
    }

    public static class SynchronizedPriorityQueue
    implements ShortPriorityQueue {
        protected final ShortPriorityQueue q;
        protected final Object sync;

        protected SynchronizedPriorityQueue(ShortPriorityQueue q, Object sync) {
            this.q = q;
            this.sync = sync;
        }

        protected SynchronizedPriorityQueue(ShortPriorityQueue q) {
            this.q = q;
            this.sync = this;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void enqueue(short x) {
            Object object = this.sync;
            synchronized (object) {
                this.q.enqueue(x);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public short dequeueShort() {
            Object object = this.sync;
            synchronized (object) {
                return this.q.dequeueShort();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public short firstShort() {
            Object object = this.sync;
            synchronized (object) {
                return this.q.firstShort();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public short lastShort() {
            Object object = this.sync;
            synchronized (object) {
                return this.q.lastShort();
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
        public ShortComparator comparator() {
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
        public void enqueue(Short x) {
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
        public Short dequeue() {
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
        public Short first() {
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
        public Short last() {
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

