/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.floats.FloatComparator;
import it.unimi.dsi.fastutil.floats.FloatPriorityQueue;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Comparator;

public final class FloatPriorityQueues {
    private FloatPriorityQueues() {
    }

    public static FloatPriorityQueue synchronize(FloatPriorityQueue q) {
        return new SynchronizedPriorityQueue(q);
    }

    public static FloatPriorityQueue synchronize(FloatPriorityQueue q, Object sync) {
        return new SynchronizedPriorityQueue(q, sync);
    }

    public static class SynchronizedPriorityQueue
    implements FloatPriorityQueue {
        protected final FloatPriorityQueue q;
        protected final Object sync;

        protected SynchronizedPriorityQueue(FloatPriorityQueue q, Object sync) {
            this.q = q;
            this.sync = sync;
        }

        protected SynchronizedPriorityQueue(FloatPriorityQueue q) {
            this.q = q;
            this.sync = this;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void enqueue(float x) {
            Object object = this.sync;
            synchronized (object) {
                this.q.enqueue(x);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public float dequeueFloat() {
            Object object = this.sync;
            synchronized (object) {
                return this.q.dequeueFloat();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public float firstFloat() {
            Object object = this.sync;
            synchronized (object) {
                return this.q.firstFloat();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public float lastFloat() {
            Object object = this.sync;
            synchronized (object) {
                return this.q.lastFloat();
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
        public FloatComparator comparator() {
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
        public void enqueue(Float x) {
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
        public Float dequeue() {
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
        public Float first() {
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
        public Float last() {
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

