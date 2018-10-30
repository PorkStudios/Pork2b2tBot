/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.bytes.ByteComparator;
import it.unimi.dsi.fastutil.bytes.BytePriorityQueue;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Comparator;

public final class BytePriorityQueues {
    private BytePriorityQueues() {
    }

    public static BytePriorityQueue synchronize(BytePriorityQueue q) {
        return new SynchronizedPriorityQueue(q);
    }

    public static BytePriorityQueue synchronize(BytePriorityQueue q, Object sync) {
        return new SynchronizedPriorityQueue(q, sync);
    }

    public static class SynchronizedPriorityQueue
    implements BytePriorityQueue {
        protected final BytePriorityQueue q;
        protected final Object sync;

        protected SynchronizedPriorityQueue(BytePriorityQueue q, Object sync) {
            this.q = q;
            this.sync = sync;
        }

        protected SynchronizedPriorityQueue(BytePriorityQueue q) {
            this.q = q;
            this.sync = this;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void enqueue(byte x) {
            Object object = this.sync;
            synchronized (object) {
                this.q.enqueue(x);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public byte dequeueByte() {
            Object object = this.sync;
            synchronized (object) {
                return this.q.dequeueByte();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public byte firstByte() {
            Object object = this.sync;
            synchronized (object) {
                return this.q.firstByte();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public byte lastByte() {
            Object object = this.sync;
            synchronized (object) {
                return this.q.lastByte();
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
        public ByteComparator comparator() {
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
        public void enqueue(Byte x) {
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
        public Byte dequeue() {
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
        public Byte first() {
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
        public Byte last() {
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

