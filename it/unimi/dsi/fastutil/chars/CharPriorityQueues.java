/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.chars.CharComparator;
import it.unimi.dsi.fastutil.chars.CharPriorityQueue;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Comparator;

public final class CharPriorityQueues {
    private CharPriorityQueues() {
    }

    public static CharPriorityQueue synchronize(CharPriorityQueue q) {
        return new SynchronizedPriorityQueue(q);
    }

    public static CharPriorityQueue synchronize(CharPriorityQueue q, Object sync) {
        return new SynchronizedPriorityQueue(q, sync);
    }

    public static class SynchronizedPriorityQueue
    implements CharPriorityQueue {
        protected final CharPriorityQueue q;
        protected final Object sync;

        protected SynchronizedPriorityQueue(CharPriorityQueue q, Object sync) {
            this.q = q;
            this.sync = sync;
        }

        protected SynchronizedPriorityQueue(CharPriorityQueue q) {
            this.q = q;
            this.sync = this;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void enqueue(char x) {
            Object object = this.sync;
            synchronized (object) {
                this.q.enqueue(x);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public char dequeueChar() {
            Object object = this.sync;
            synchronized (object) {
                return this.q.dequeueChar();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public char firstChar() {
            Object object = this.sync;
            synchronized (object) {
                return this.q.firstChar();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public char lastChar() {
            Object object = this.sync;
            synchronized (object) {
                return this.q.lastChar();
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
        public CharComparator comparator() {
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
        public void enqueue(Character x) {
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
        public Character dequeue() {
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
        public Character first() {
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
        public Character last() {
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

