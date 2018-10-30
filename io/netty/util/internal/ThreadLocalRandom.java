/*
 * Decompiled with CFR 0_132.
 */
package io.netty.util.internal;

import io.netty.util.internal.InternalThreadLocalMap;
import io.netty.util.internal.SystemPropertyUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.security.SecureRandom;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public final class ThreadLocalRandom
extends Random {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(ThreadLocalRandom.class);
    private static final AtomicLong seedUniquifier = new AtomicLong();
    private static volatile long initialSeedUniquifier = SystemPropertyUtil.getLong("io.netty.initialSeedUniquifier", 0L);
    private static final Thread seedGeneratorThread;
    private static final BlockingQueue<Long> seedQueue;
    private static final long seedGeneratorStartTime;
    private static volatile long seedGeneratorEndTime;
    private static final long multiplier = 25214903917L;
    private static final long addend = 11L;
    private static final long mask = 0xFFFFFFFFFFFFL;
    private long rnd;
    boolean initialized = true;
    private long pad0;
    private long pad1;
    private long pad2;
    private long pad3;
    private long pad4;
    private long pad5;
    private long pad6;
    private long pad7;
    private static final long serialVersionUID = -5851777807851030925L;

    public static void setInitialSeedUniquifier(long initialSeedUniquifier) {
        ThreadLocalRandom.initialSeedUniquifier = initialSeedUniquifier;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static long getInitialSeedUniquifier() {
        long initialSeedUniquifier = ThreadLocalRandom.initialSeedUniquifier;
        if (initialSeedUniquifier != 0L) {
            return initialSeedUniquifier;
        }
        Class<ThreadLocalRandom> class_ = ThreadLocalRandom.class;
        synchronized (ThreadLocalRandom.class) {
            boolean interrupted;
            block10 : {
                long waitTime;
                initialSeedUniquifier = ThreadLocalRandom.initialSeedUniquifier;
                if (initialSeedUniquifier != 0L) {
                    // ** MonitorExit[var2_1] (shouldn't be in output)
                    return initialSeedUniquifier;
                }
                long timeoutSeconds = 3L;
                long deadLine = seedGeneratorStartTime + TimeUnit.SECONDS.toNanos(3L);
                interrupted = false;
                do {
                    waitTime = deadLine - System.nanoTime();
                    try {
                        Long seed = waitTime <= 0L ? seedQueue.poll() : seedQueue.poll(waitTime, TimeUnit.NANOSECONDS);
                        if (seed == null) continue;
                        initialSeedUniquifier = seed;
                    }
                    catch (InterruptedException e) {
                        interrupted = true;
                        logger.warn("Failed to generate a seed from SecureRandom due to an InterruptedException.");
                    }
                    break block10;
                } while (waitTime > 0L);
                seedGeneratorThread.interrupt();
                logger.warn("Failed to generate a seed from SecureRandom within {} seconds. Not enough entropy?", (Object)3L);
            }
            initialSeedUniquifier ^= 3627065505421648153L;
            ThreadLocalRandom.initialSeedUniquifier = initialSeedUniquifier ^= Long.reverse(System.nanoTime());
            if (interrupted) {
                Thread.currentThread().interrupt();
                seedGeneratorThread.interrupt();
            }
            if (seedGeneratorEndTime == 0L) {
                seedGeneratorEndTime = System.nanoTime();
            }
            // ** MonitorExit[var2_1] (shouldn't be in output)
            return initialSeedUniquifier;
        }
    }

    private static long newSeed() {
        long actualCurrent;
        long current;
        long next;
        while (!seedUniquifier.compareAndSet(current, next = (actualCurrent = (current = seedUniquifier.get()) != 0L ? current : ThreadLocalRandom.getInitialSeedUniquifier()) * 181783497276652981L)) {
        }
        if (current == 0L && logger.isDebugEnabled()) {
            if (seedGeneratorEndTime != 0L) {
                logger.debug(String.format("-Dio.netty.initialSeedUniquifier: 0x%016x (took %d ms)", actualCurrent, TimeUnit.NANOSECONDS.toMillis(seedGeneratorEndTime - seedGeneratorStartTime)));
            } else {
                logger.debug(String.format("-Dio.netty.initialSeedUniquifier: 0x%016x", actualCurrent));
            }
        }
        return next ^ System.nanoTime();
    }

    private static long mix64(long z) {
        z = (z ^ z >>> 33) * -49064778989728563L;
        z = (z ^ z >>> 33) * -4265267296055464877L;
        return z ^ z >>> 33;
    }

    ThreadLocalRandom() {
        super(ThreadLocalRandom.newSeed());
    }

    public static ThreadLocalRandom current() {
        return InternalThreadLocalMap.get().random();
    }

    @Override
    public void setSeed(long seed) {
        if (this.initialized) {
            throw new UnsupportedOperationException();
        }
        this.rnd = (seed ^ 25214903917L) & 0xFFFFFFFFFFFFL;
    }

    @Override
    protected int next(int bits) {
        this.rnd = this.rnd * 25214903917L + 11L & 0xFFFFFFFFFFFFL;
        return (int)(this.rnd >>> 48 - bits);
    }

    public int nextInt(int least, int bound) {
        if (least >= bound) {
            throw new IllegalArgumentException();
        }
        return this.nextInt(bound - least) + least;
    }

    public long nextLong(long n) {
        if (n <= 0L) {
            throw new IllegalArgumentException("n must be positive");
        }
        long offset = 0L;
        while (n >= Integer.MAX_VALUE) {
            long nextn;
            int bits = this.next(2);
            long half = n >>> 1;
            long l = nextn = (bits & 2) == 0 ? half : n - half;
            if ((bits & 1) == 0) {
                offset += n - nextn;
            }
            n = nextn;
        }
        return offset + (long)this.nextInt((int)n);
    }

    public long nextLong(long least, long bound) {
        if (least >= bound) {
            throw new IllegalArgumentException();
        }
        return this.nextLong(bound - least) + least;
    }

    public double nextDouble(double n) {
        if (n <= 0.0) {
            throw new IllegalArgumentException("n must be positive");
        }
        return this.nextDouble() * n;
    }

    public double nextDouble(double least, double bound) {
        if (least >= bound) {
            throw new IllegalArgumentException();
        }
        return this.nextDouble() * (bound - least) + least;
    }

    static {
        if (initialSeedUniquifier == 0L) {
            boolean secureRandom = SystemPropertyUtil.getBoolean("java.util.secureRandomSeed", false);
            if (secureRandom) {
                seedQueue = new LinkedBlockingQueue<Long>();
                seedGeneratorStartTime = System.nanoTime();
                seedGeneratorThread = new Thread("initialSeedUniquifierGenerator"){

                    @Override
                    public void run() {
                        SecureRandom random = new SecureRandom();
                        byte[] seed = random.generateSeed(8);
                        seedGeneratorEndTime = System.nanoTime();
                        long s = ((long)seed[0] & 255L) << 56 | ((long)seed[1] & 255L) << 48 | ((long)seed[2] & 255L) << 40 | ((long)seed[3] & 255L) << 32 | ((long)seed[4] & 255L) << 24 | ((long)seed[5] & 255L) << 16 | ((long)seed[6] & 255L) << 8 | (long)seed[7] & 255L;
                        seedQueue.add(s);
                    }
                };
                seedGeneratorThread.setDaemon(true);
                seedGeneratorThread.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler(){

                    @Override
                    public void uncaughtException(Thread t, Throwable e) {
                        logger.debug("An exception has been raised by {}", (Object)t.getName(), (Object)e);
                    }
                });
                seedGeneratorThread.start();
            } else {
                initialSeedUniquifier = ThreadLocalRandom.mix64(System.currentTimeMillis()) ^ ThreadLocalRandom.mix64(System.nanoTime());
                seedGeneratorThread = null;
                seedQueue = null;
                seedGeneratorStartTime = 0L;
            }
        } else {
            seedGeneratorThread = null;
            seedQueue = null;
            seedGeneratorStartTime = 0L;
        }
    }

}

