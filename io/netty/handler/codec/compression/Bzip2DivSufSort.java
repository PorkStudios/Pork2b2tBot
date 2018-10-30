/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.compression;

final class Bzip2DivSufSort {
    private static final int STACK_SIZE = 64;
    private static final int BUCKET_A_SIZE = 256;
    private static final int BUCKET_B_SIZE = 65536;
    private static final int SS_BLOCKSIZE = 1024;
    private static final int INSERTIONSORT_THRESHOLD = 8;
    private static final int[] LOG_2_TABLE = new int[]{-1, 0, 1, 1, 2, 2, 2, 2, 3, 3, 3, 3, 3, 3, 3, 3, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7};
    private final int[] SA;
    private final byte[] T;
    private final int n;

    Bzip2DivSufSort(byte[] block, int[] bwtBlock, int blockLength) {
        this.T = block;
        this.SA = bwtBlock;
        this.n = blockLength;
    }

    private static void swapElements(int[] array1, int idx1, int[] array2, int idx2) {
        int temp = array1[idx1];
        array1[idx1] = array2[idx2];
        array2[idx2] = temp;
    }

    private int ssCompare(int p1, int p2, int depth) {
        int U2;
        int[] SA = this.SA;
        byte[] T = this.T;
        int U1n = SA[p1 + 1] + 2;
        int U2n = SA[p2 + 1] + 2;
        int U1 = depth + SA[p1];
        for (U2 = depth + SA[p2]; U1 < U1n && U2 < U2n && T[U1] == T[U2]; ++U1, ++U2) {
        }
        return U1 < U1n ? (U2 < U2n ? (T[U1] & 255) - (T[U2] & 255) : 1) : (U2 < U2n ? -1 : 0);
    }

    private int ssCompareLast(int pa, int p1, int p2, int depth, int size) {
        int U2;
        int[] SA = this.SA;
        byte[] T = this.T;
        int U1 = depth + SA[p1];
        int U1n = size;
        int U2n = SA[p2 + 1] + 2;
        for (U2 = depth + SA[p2]; U1 < U1n && U2 < U2n && T[U1] == T[U2]; ++U1, ++U2) {
        }
        if (U1 < U1n) {
            return U2 < U2n ? (T[U1] & 255) - (T[U2] & 255) : 1;
        }
        if (U2 == U2n) {
            return 1;
        }
        U1 %= size;
        U1n = SA[pa] + 2;
        while (U1 < U1n && U2 < U2n && T[U1] == T[U2]) {
            ++U1;
            ++U2;
        }
        return U1 < U1n ? (U2 < U2n ? (T[U1] & 255) - (T[U2] & 255) : 1) : (U2 < U2n ? -1 : 0);
    }

    private void ssInsertionSort(int pa, int first, int last, int depth) {
        int[] SA = this.SA;
        for (int i = last - 2; first <= i; --i) {
            int r;
            int t = SA[i];
            int j = i + 1;
            while (0 < (r = this.ssCompare(pa + t, pa + SA[j], depth))) {
                do {
                    SA[j - 1] = SA[j];
                } while (++j < last && SA[j] < 0);
                if (last > j) continue;
            }
            if (r == 0) {
                SA[j] = ~ SA[j];
            }
            SA[j - 1] = t;
        }
    }

    private void ssFixdown(int td, int pa, int sa, int i, int size) {
        int j;
        int[] SA = this.SA;
        byte[] T = this.T;
        int v = SA[sa + i];
        int c = T[td + SA[pa + v]] & 255;
        while ((j = 2 * i + 1) < size) {
            int e;
            int d;
            int k;
            if ((d = T[td + SA[pa + SA[sa + (k = j++)]]] & 255) < (e = T[td + SA[pa + SA[sa + j]]] & 255)) {
                k = j;
                d = e;
            }
            if (d <= c) break;
            SA[sa + i] = SA[sa + k];
            i = k;
        }
        SA[sa + i] = v;
    }

    private void ssHeapSort(int td, int pa, int sa, int size) {
        int i;
        int[] SA = this.SA;
        byte[] T = this.T;
        int m = size;
        if (size % 2 == 0 && (T[td + SA[pa + SA[sa + --m / 2]]] & 255) < (T[td + SA[pa + SA[sa + m]]] & 255)) {
            Bzip2DivSufSort.swapElements(SA, sa + m, SA, sa + m / 2);
        }
        for (i = m / 2 - 1; 0 <= i; --i) {
            this.ssFixdown(td, pa, sa, i, m);
        }
        if (size % 2 == 0) {
            Bzip2DivSufSort.swapElements(SA, sa, SA, sa + m);
            this.ssFixdown(td, pa, sa, 0, m);
        }
        for (i = m - 1; 0 < i; --i) {
            int t = SA[sa];
            SA[sa] = SA[sa + i];
            this.ssFixdown(td, pa, sa, 0, i);
            SA[sa + i] = t;
        }
    }

    private int ssMedian3(int td, int pa, int v1, int v2, int v3) {
        int[] SA = this.SA;
        byte[] T = this.T;
        int T_v1 = T[td + SA[pa + SA[v1]]] & 255;
        int T_v2 = T[td + SA[pa + SA[v2]]] & 255;
        int T_v3 = T[td + SA[pa + SA[v3]]] & 255;
        if (T_v1 > T_v2) {
            int temp = v1;
            v1 = v2;
            v2 = temp;
            int T_vtemp = T_v1;
            T_v1 = T_v2;
            T_v2 = T_vtemp;
        }
        if (T_v2 > T_v3) {
            if (T_v1 > T_v3) {
                return v1;
            }
            return v3;
        }
        return v2;
    }

    private int ssMedian5(int td, int pa, int v1, int v2, int v3, int v4, int v5) {
        int temp;
        int T_vtemp;
        int[] SA = this.SA;
        byte[] T = this.T;
        int T_v1 = T[td + SA[pa + SA[v1]]] & 255;
        int T_v2 = T[td + SA[pa + SA[v2]]] & 255;
        int T_v3 = T[td + SA[pa + SA[v3]]] & 255;
        int T_v4 = T[td + SA[pa + SA[v4]]] & 255;
        int T_v5 = T[td + SA[pa + SA[v5]]] & 255;
        if (T_v2 > T_v3) {
            temp = v2;
            v2 = v3;
            v3 = temp;
            T_vtemp = T_v2;
            T_v2 = T_v3;
            T_v3 = T_vtemp;
        }
        if (T_v4 > T_v5) {
            temp = v4;
            v4 = v5;
            v5 = temp;
            T_vtemp = T_v4;
            T_v4 = T_v5;
            T_v5 = T_vtemp;
        }
        if (T_v2 > T_v4) {
            v4 = temp = v2;
            T_v4 = T_vtemp = T_v2;
            temp = v3;
            v3 = v5;
            v5 = temp;
            T_vtemp = T_v3;
            T_v3 = T_v5;
            T_v5 = T_vtemp;
        }
        if (T_v1 > T_v3) {
            temp = v1;
            v1 = v3;
            v3 = temp;
            T_vtemp = T_v1;
            T_v1 = T_v3;
            T_v3 = T_vtemp;
        }
        if (T_v1 > T_v4) {
            v4 = temp = v1;
            T_v4 = T_vtemp = T_v1;
            v3 = v5;
            T_v3 = T_v5;
        }
        if (T_v3 > T_v4) {
            return v4;
        }
        return v3;
    }

    private int ssPivot(int td, int pa, int first, int last) {
        int t = last - first;
        int middle = first + t / 2;
        if (t <= 512) {
            if (t <= 32) {
                return this.ssMedian3(td, pa, first, middle, last - 1);
            }
            return this.ssMedian5(td, pa, first, first + t, middle, last - 1 - (t >>= 2), last - 1);
        }
        return this.ssMedian3(td, pa, this.ssMedian3(td, pa, first, first + t, first + (t << 1)), this.ssMedian3(td, pa, middle - t, middle, middle + t), this.ssMedian3(td, pa, last - 1 - (t << 1), last - 1 - (t >>= 3), last - 1));
    }

    private static int ssLog(int n) {
        return (n & 65280) != 0 ? 8 + LOG_2_TABLE[n >> 8 & 255] : LOG_2_TABLE[n & 255];
    }

    private int ssSubstringPartition(int pa, int first, int last, int depth) {
        int[] SA = this.SA;
        int a = first - 1;
        int b = last;
        do {
            if (++a < b && SA[pa + SA[a]] + depth >= SA[pa + SA[a] + 1] + 1) {
                SA[a] = ~ SA[a];
                continue;
            }
            --b;
            while (a < b && SA[pa + SA[b]] + depth < SA[pa + SA[b] + 1] + 1) {
                --b;
            }
            if (b <= a) break;
            int t = ~ SA[b];
            SA[b] = SA[a];
            SA[a] = t;
        } while (true);
        if (first < a) {
            SA[first] = ~ SA[first];
        }
        return a;
    }

    private void ssMultiKeyIntroSort(int pa, int first, int last, int depth) {
        int[] SA = this.SA;
        byte[] T = this.T;
        StackEntry[] stack = new StackEntry[64];
        int x = 0;
        int ssize = 0;
        int limit = Bzip2DivSufSort.ssLog(last - first);
        do {
            int c;
            int a;
            int b;
            int v;
            if (last - first <= 8) {
                if (1 < last - first) {
                    this.ssInsertionSort(pa, first, last, depth);
                }
                if (ssize == 0) {
                    return;
                }
                StackEntry entry = stack[--ssize];
                first = entry.a;
                last = entry.b;
                depth = entry.c;
                limit = entry.d;
                continue;
            }
            int Td = depth;
            if (limit-- == 0) {
                this.ssHeapSort(Td, pa, first, last - first);
            }
            if (limit < 0) {
                v = T[Td + SA[pa + SA[first]]] & 255;
                for (a = first + 1; a < last; ++a) {
                    x = T[Td + SA[pa + SA[a]]] & 255;
                    if (x == v) continue;
                    if (1 < a - first) break;
                    v = x;
                    first = a;
                }
                if ((T[Td + SA[pa + SA[first]] - 1] & 255) < v) {
                    first = this.ssSubstringPartition(pa, first, a, depth);
                }
                if (a - first <= last - a) {
                    if (1 < a - first) {
                        stack[ssize++] = new StackEntry(a, last, depth, -1);
                        last = a;
                        ++depth;
                        limit = Bzip2DivSufSort.ssLog(a - first);
                        continue;
                    }
                    first = a;
                    limit = -1;
                    continue;
                }
                if (1 < last - a) {
                    stack[ssize++] = new StackEntry(first, a, depth + 1, Bzip2DivSufSort.ssLog(a - first));
                    first = a;
                    limit = -1;
                    continue;
                }
                last = a;
                ++depth;
                limit = Bzip2DivSufSort.ssLog(a - first);
                continue;
            }
            a = this.ssPivot(Td, pa, first, last);
            v = T[Td + SA[pa + SA[a]]] & 255;
            Bzip2DivSufSort.swapElements(SA, first, SA, a);
            for (b = first + 1; b < last && (x = T[Td + SA[pa + SA[b]]] & 255) == v; ++b) {
            }
            a = b;
            if (a < last && x < v) {
                while (++b < last && (x = T[Td + SA[pa + SA[b]]] & 255) <= v) {
                    if (x != v) continue;
                    Bzip2DivSufSort.swapElements(SA, b, SA, a);
                    ++a;
                }
            }
            for (c = last - 1; b < c && (x = T[Td + SA[pa + SA[c]]] & 255) == v; --c) {
            }
            int d = c;
            if (b < d && x > v) {
                while (b < --c && (x = T[Td + SA[pa + SA[c]]] & 255) >= v) {
                    if (x != v) continue;
                    Bzip2DivSufSort.swapElements(SA, c, SA, d);
                    --d;
                }
            }
            while (b < c) {
                Bzip2DivSufSort.swapElements(SA, b, SA, c);
                while (++b < c && (x = T[Td + SA[pa + SA[b]]] & 255) <= v) {
                    if (x != v) continue;
                    Bzip2DivSufSort.swapElements(SA, b, SA, a);
                    ++a;
                }
                while (b < --c && (x = T[Td + SA[pa + SA[c]]] & 255) >= v) {
                    if (x != v) continue;
                    Bzip2DivSufSort.swapElements(SA, c, SA, d);
                    --d;
                }
            }
            if (a <= d) {
                c = b - 1;
                int s = a - first;
                int t = b - a;
                if (s > t) {
                    s = t;
                }
                int e = first;
                int f = b - s;
                while (0 < s) {
                    Bzip2DivSufSort.swapElements(SA, e, SA, f);
                    --s;
                    ++e;
                    ++f;
                }
                s = d - c;
                t = last - d - 1;
                if (s > t) {
                    s = t;
                }
                e = b;
                f = last - s;
                while (0 < s) {
                    Bzip2DivSufSort.swapElements(SA, e, SA, f);
                    --s;
                    ++e;
                    ++f;
                }
                a = first + (b - a);
                c = last - (d - c);
                int n = b = v <= (T[Td + SA[pa + SA[a]] - 1] & 255) ? a : this.ssSubstringPartition(pa, a, c, depth);
                if (a - first <= last - c) {
                    if (last - c <= c - b) {
                        stack[ssize++] = new StackEntry(b, c, depth + 1, Bzip2DivSufSort.ssLog(c - b));
                        stack[ssize++] = new StackEntry(c, last, depth, limit);
                        last = a;
                        continue;
                    }
                    if (a - first <= c - b) {
                        stack[ssize++] = new StackEntry(c, last, depth, limit);
                        stack[ssize++] = new StackEntry(b, c, depth + 1, Bzip2DivSufSort.ssLog(c - b));
                        last = a;
                        continue;
                    }
                    stack[ssize++] = new StackEntry(c, last, depth, limit);
                    stack[ssize++] = new StackEntry(first, a, depth, limit);
                    first = b;
                    last = c;
                    ++depth;
                    limit = Bzip2DivSufSort.ssLog(c - b);
                    continue;
                }
                if (a - first <= c - b) {
                    stack[ssize++] = new StackEntry(b, c, depth + 1, Bzip2DivSufSort.ssLog(c - b));
                    stack[ssize++] = new StackEntry(first, a, depth, limit);
                    first = c;
                    continue;
                }
                if (last - c <= c - b) {
                    stack[ssize++] = new StackEntry(first, a, depth, limit);
                    stack[ssize++] = new StackEntry(b, c, depth + 1, Bzip2DivSufSort.ssLog(c - b));
                    first = c;
                    continue;
                }
                stack[ssize++] = new StackEntry(first, a, depth, limit);
                stack[ssize++] = new StackEntry(c, last, depth, limit);
                first = b;
                last = c;
                ++depth;
                limit = Bzip2DivSufSort.ssLog(c - b);
                continue;
            }
            ++limit;
            if ((T[Td + SA[pa + SA[first]] - 1] & 255) < v) {
                first = this.ssSubstringPartition(pa, first, last, depth);
                limit = Bzip2DivSufSort.ssLog(last - first);
            }
            ++depth;
        } while (true);
    }

    private static void ssBlockSwap(int[] array1, int first1, int[] array2, int first2, int size) {
        int i = size;
        int a = first1;
        int b = first2;
        while (0 < i) {
            Bzip2DivSufSort.swapElements(array1, a, array2, b);
            --i;
            ++a;
            ++b;
        }
    }

    private void ssMergeForward(int pa, int[] buf, int bufoffset, int first, int middle, int last, int depth) {
        int[] SA = this.SA;
        int bufend = bufoffset + (middle - first) - 1;
        Bzip2DivSufSort.ssBlockSwap(buf, bufoffset, SA, first, middle - first);
        int t = SA[first];
        int i = first;
        int j = bufoffset;
        int k = middle;
        do {
            int r;
            if ((r = this.ssCompare(pa + buf[j], pa + SA[k], depth)) < 0) {
                do {
                    SA[i++] = buf[j];
                    if (bufend <= j) {
                        buf[j] = t;
                        return;
                    }
                    buf[j++] = SA[i];
                } while (buf[j] < 0);
                continue;
            }
            if (r > 0) {
                do {
                    SA[i++] = SA[k];
                    SA[k++] = SA[i];
                    if (last > k) continue;
                    while (j < bufend) {
                        SA[i++] = buf[j];
                        buf[j++] = SA[i];
                    }
                    SA[i] = buf[j];
                    buf[j] = t;
                    return;
                } while (SA[k] < 0);
                continue;
            }
            SA[k] = ~ SA[k];
            do {
                SA[i++] = buf[j];
                if (bufend <= j) {
                    buf[j] = t;
                    return;
                }
                buf[j++] = SA[i];
            } while (buf[j] < 0);
            do {
                SA[i++] = SA[k];
                SA[k++] = SA[i];
                if (last > k) continue;
                while (j < bufend) {
                    SA[i++] = buf[j];
                    buf[j++] = SA[i];
                }
                SA[i] = buf[j];
                buf[j] = t;
                return;
            } while (SA[k] < 0);
        } while (true);
    }

    private void ssMergeBackward(int pa, int[] buf, int bufoffset, int first, int middle, int last, int depth) {
        int p1;
        int p2;
        int[] SA = this.SA;
        int bufend = bufoffset + (last - middle);
        Bzip2DivSufSort.ssBlockSwap(buf, bufoffset, SA, middle, last - middle);
        int x = 0;
        if (buf[bufend - 1] < 0) {
            x |= true;
            p1 = pa + ~ buf[bufend - 1];
        } else {
            p1 = pa + buf[bufend - 1];
        }
        if (SA[middle - 1] < 0) {
            x |= 2;
            p2 = pa + ~ SA[middle - 1];
        } else {
            p2 = pa + SA[middle - 1];
        }
        int t = SA[last - 1];
        int i = last - 1;
        int j = bufend - 1;
        int k = middle - 1;
        do {
            int r;
            if ((r = this.ssCompare(p1, p2, depth)) > 0) {
                if ((x & 1) != 0) {
                    do {
                        SA[i--] = buf[j];
                        buf[j--] = SA[i];
                    } while (buf[j] < 0);
                    x ^= 1;
                }
                SA[i--] = buf[j];
                if (j <= bufoffset) {
                    buf[j] = t;
                    return;
                }
                buf[j--] = SA[i];
                if (buf[j] < 0) {
                    x |= 1;
                    p1 = pa + ~ buf[j];
                    continue;
                }
                p1 = pa + buf[j];
                continue;
            }
            if (r < 0) {
                if ((x & 2) != 0) {
                    do {
                        SA[i--] = SA[k];
                        SA[k--] = SA[i];
                    } while (SA[k] < 0);
                    x ^= 2;
                }
                SA[i--] = SA[k];
                SA[k--] = SA[i];
                if (k < first) {
                    while (bufoffset < j) {
                        SA[i--] = buf[j];
                        buf[j--] = SA[i];
                    }
                    SA[i] = buf[j];
                    buf[j] = t;
                    return;
                }
                if (SA[k] < 0) {
                    x |= 2;
                    p2 = pa + ~ SA[k];
                    continue;
                }
                p2 = pa + SA[k];
                continue;
            }
            if ((x & 1) != 0) {
                do {
                    SA[i--] = buf[j];
                    buf[j--] = SA[i];
                } while (buf[j] < 0);
                x ^= 1;
            }
            SA[i--] = ~ buf[j];
            if (j <= bufoffset) {
                buf[j] = t;
                return;
            }
            buf[j--] = SA[i];
            if ((x & 2) != 0) {
                do {
                    SA[i--] = SA[k];
                    SA[k--] = SA[i];
                } while (SA[k] < 0);
                x ^= 2;
            }
            SA[i--] = SA[k];
            SA[k--] = SA[i];
            if (k < first) {
                while (bufoffset < j) {
                    SA[i--] = buf[j];
                    buf[j--] = SA[i];
                }
                SA[i] = buf[j];
                buf[j] = t;
                return;
            }
            if (buf[j] < 0) {
                x |= 1;
                p1 = pa + ~ buf[j];
            } else {
                p1 = pa + buf[j];
            }
            if (SA[k] < 0) {
                x |= 2;
                p2 = pa + ~ SA[k];
                continue;
            }
            p2 = pa + SA[k];
        } while (true);
    }

    private static int getIDX(int a) {
        return 0 <= a ? a : ~ a;
    }

    private void ssMergeCheckEqual(int pa, int depth, int a) {
        int[] SA = this.SA;
        if (0 <= SA[a] && this.ssCompare(pa + Bzip2DivSufSort.getIDX(SA[a - 1]), pa + SA[a], depth) == 0) {
            SA[a] = ~ SA[a];
        }
    }

    private void ssMerge(int pa, int first, int middle, int last, int[] buf, int bufoffset, int bufsize, int depth) {
        int[] SA = this.SA;
        StackEntry[] stack = new StackEntry[64];
        int check = 0;
        int ssize = 0;
        do {
            StackEntry entry;
            if (last - middle <= bufsize) {
                if (first < middle && middle < last) {
                    this.ssMergeBackward(pa, buf, bufoffset, first, middle, last, depth);
                }
                if (check & true) {
                    this.ssMergeCheckEqual(pa, depth, first);
                }
                if ((check & 2) != 0) {
                    this.ssMergeCheckEqual(pa, depth, last);
                }
                if (ssize == 0) {
                    return;
                }
                entry = stack[--ssize];
                first = entry.a;
                middle = entry.b;
                last = entry.c;
                check = entry.d;
                continue;
            }
            if (middle - first <= bufsize) {
                if (first < middle) {
                    this.ssMergeForward(pa, buf, bufoffset, first, middle, last, depth);
                }
                if ((check & 1) != 0) {
                    this.ssMergeCheckEqual(pa, depth, first);
                }
                if ((check & 2) != 0) {
                    this.ssMergeCheckEqual(pa, depth, last);
                }
                if (ssize == 0) {
                    return;
                }
                entry = stack[--ssize];
                first = entry.a;
                middle = entry.b;
                last = entry.c;
                check = entry.d;
                continue;
            }
            int m = 0;
            int len = Math.min(middle - first, last - middle);
            int half = len >> 1;
            while (0 < len) {
                if (this.ssCompare(pa + Bzip2DivSufSort.getIDX(SA[middle + m + half]), pa + Bzip2DivSufSort.getIDX(SA[middle - m - half - 1]), depth) < 0) {
                    m += half + 1;
                    half -= len & 1 ^ 1;
                }
                len = half;
                half >>= 1;
            }
            if (0 < m) {
                int j;
                Bzip2DivSufSort.ssBlockSwap(SA, middle - m, SA, middle, m);
                int i = j = middle;
                int next = 0;
                if (middle + m < last) {
                    if (SA[middle + m] < 0) {
                        while (SA[i - 1] < 0) {
                            --i;
                        }
                        SA[middle + m] = ~ SA[middle + m];
                    }
                    j = middle;
                    while (SA[j] < 0) {
                        ++j;
                    }
                    next = 1;
                }
                if (i - first <= last - j) {
                    stack[ssize++] = new StackEntry(j, middle + m, last, check & 2 | next & 1);
                    middle -= m;
                    last = i;
                    check &= 1;
                    continue;
                }
                if (i == middle && middle == j) {
                    next <<= 1;
                }
                stack[ssize++] = new StackEntry(first, middle - m, i, check & 1 | next & 2);
                first = j;
                middle += m;
                check = check & 2 | next & 1;
                continue;
            }
            if ((check & 1) != 0) {
                this.ssMergeCheckEqual(pa, depth, first);
            }
            this.ssMergeCheckEqual(pa, depth, middle);
            if ((check & 2) != 0) {
                this.ssMergeCheckEqual(pa, depth, last);
            }
            if (ssize == 0) {
                return;
            }
            entry = stack[--ssize];
            first = entry.a;
            middle = entry.b;
            last = entry.c;
            check = entry.d;
        } while (true);
    }

    private void subStringSort(int pa, int first, int last, int[] buf, int bufoffset, int bufsize, int depth, boolean lastsuffix, int size) {
        int k;
        int[] SA = this.SA;
        if (lastsuffix) {
            ++first;
        }
        int a = first;
        int i = 0;
        while (a + 1024 < last) {
            this.ssMultiKeyIntroSort(pa, a, a + 1024, depth);
            int[] curbuf = SA;
            int curbufoffset = a + 1024;
            int curbufsize = last - (a + 1024);
            if (curbufsize <= bufsize) {
                curbufsize = bufsize;
                curbuf = buf;
                curbufoffset = bufoffset;
            }
            int b = a;
            k = 1024;
            int j = i;
            while ((j & 1) != 0) {
                this.ssMerge(pa, b - k, b, b + k, curbuf, curbufoffset, curbufsize, depth);
                b -= k;
                k <<= 1;
                j >>>= 1;
            }
            a += 1024;
            ++i;
        }
        this.ssMultiKeyIntroSort(pa, a, last, depth);
        k = 1024;
        while (i != 0) {
            if (i & true) {
                this.ssMerge(pa, a - k, a, last, buf, bufoffset, bufsize, depth);
                a -= k;
            }
            k <<= 1;
            i >>= 1;
        }
        if (lastsuffix) {
            i = SA[first - 1];
            int r = 1;
            for (a = first; a < last && (SA[a] < 0 || 0 < (r = this.ssCompareLast(pa, pa + i, pa + SA[a], depth, size))); ++a) {
                SA[a - 1] = SA[a];
            }
            if (r == 0) {
                SA[a] = ~ SA[a];
            }
            SA[a - 1] = i;
        }
    }

    private int trGetC(int isa, int isaD, int isaN, int p) {
        return isaD + p < isaN ? this.SA[isaD + p] : this.SA[isa + (isaD - isa + p) % (isaN - isa)];
    }

    private void trFixdown(int isa, int isaD, int isaN, int sa, int i, int size) {
        int j;
        int[] SA = this.SA;
        int v = SA[sa + i];
        int c = this.trGetC(isa, isaD, isaN, v);
        while ((j = 2 * i + 1) < size) {
            int e;
            int d;
            int k;
            if ((d = this.trGetC(isa, isaD, isaN, SA[sa + (k = j++)])) < (e = this.trGetC(isa, isaD, isaN, SA[sa + j]))) {
                k = j;
                d = e;
            }
            if (d <= c) break;
            SA[sa + i] = SA[sa + k];
            i = k;
        }
        SA[sa + i] = v;
    }

    private void trHeapSort(int isa, int isaD, int isaN, int sa, int size) {
        int i;
        int[] SA = this.SA;
        int m = size;
        if (size % 2 == 0 && this.trGetC(isa, isaD, isaN, SA[sa + --m / 2]) < this.trGetC(isa, isaD, isaN, SA[sa + m])) {
            Bzip2DivSufSort.swapElements(SA, sa + m, SA, sa + m / 2);
        }
        for (i = m / 2 - 1; 0 <= i; --i) {
            this.trFixdown(isa, isaD, isaN, sa, i, m);
        }
        if (size % 2 == 0) {
            Bzip2DivSufSort.swapElements(SA, sa, SA, sa + m);
            this.trFixdown(isa, isaD, isaN, sa, 0, m);
        }
        for (i = m - 1; 0 < i; --i) {
            int t = SA[sa];
            SA[sa] = SA[sa + i];
            this.trFixdown(isa, isaD, isaN, sa, 0, i);
            SA[sa + i] = t;
        }
    }

    private void trInsertionSort(int isa, int isaD, int isaN, int first, int last) {
        int[] SA = this.SA;
        for (int a = first + 1; a < last; ++a) {
            int r;
            int t = SA[a];
            int b = a - 1;
            while (0 > (r = this.trGetC(isa, isaD, isaN, t) - this.trGetC(isa, isaD, isaN, SA[b]))) {
                do {
                    SA[b + 1] = SA[b];
                } while (first <= --b && SA[b] < 0);
                if (b >= first) continue;
            }
            if (r == 0) {
                SA[b] = ~ SA[b];
            }
            SA[b + 1] = t;
        }
    }

    private static int trLog(int n) {
        return (n & -65536) != 0 ? ((n & -16777216) != 0 ? 24 + LOG_2_TABLE[n >> 24 & 255] : LOG_2_TABLE[n >> 16 & 271]) : ((n & 65280) != 0 ? 8 + LOG_2_TABLE[n >> 8 & 255] : LOG_2_TABLE[n & 255]);
    }

    private int trMedian3(int isa, int isaD, int isaN, int v1, int v2, int v3) {
        int[] SA = this.SA;
        int SA_v1 = this.trGetC(isa, isaD, isaN, SA[v1]);
        int SA_v2 = this.trGetC(isa, isaD, isaN, SA[v2]);
        int SA_v3 = this.trGetC(isa, isaD, isaN, SA[v3]);
        if (SA_v1 > SA_v2) {
            int temp = v1;
            v1 = v2;
            v2 = temp;
            int SA_vtemp = SA_v1;
            SA_v1 = SA_v2;
            SA_v2 = SA_vtemp;
        }
        if (SA_v2 > SA_v3) {
            if (SA_v1 > SA_v3) {
                return v1;
            }
            return v3;
        }
        return v2;
    }

    private int trMedian5(int isa, int isaD, int isaN, int v1, int v2, int v3, int v4, int v5) {
        int SA_vtemp;
        int temp;
        int[] SA = this.SA;
        int SA_v1 = this.trGetC(isa, isaD, isaN, SA[v1]);
        int SA_v2 = this.trGetC(isa, isaD, isaN, SA[v2]);
        int SA_v3 = this.trGetC(isa, isaD, isaN, SA[v3]);
        int SA_v4 = this.trGetC(isa, isaD, isaN, SA[v4]);
        int SA_v5 = this.trGetC(isa, isaD, isaN, SA[v5]);
        if (SA_v2 > SA_v3) {
            temp = v2;
            v2 = v3;
            v3 = temp;
            SA_vtemp = SA_v2;
            SA_v2 = SA_v3;
            SA_v3 = SA_vtemp;
        }
        if (SA_v4 > SA_v5) {
            temp = v4;
            v4 = v5;
            v5 = temp;
            SA_vtemp = SA_v4;
            SA_v4 = SA_v5;
            SA_v5 = SA_vtemp;
        }
        if (SA_v2 > SA_v4) {
            v4 = temp = v2;
            SA_v4 = SA_vtemp = SA_v2;
            temp = v3;
            v3 = v5;
            v5 = temp;
            SA_vtemp = SA_v3;
            SA_v3 = SA_v5;
            SA_v5 = SA_vtemp;
        }
        if (SA_v1 > SA_v3) {
            temp = v1;
            v1 = v3;
            v3 = temp;
            SA_vtemp = SA_v1;
            SA_v1 = SA_v3;
            SA_v3 = SA_vtemp;
        }
        if (SA_v1 > SA_v4) {
            v4 = temp = v1;
            SA_v4 = SA_vtemp = SA_v1;
            v3 = v5;
            SA_v3 = SA_v5;
        }
        if (SA_v3 > SA_v4) {
            return v4;
        }
        return v3;
    }

    private int trPivot(int isa, int isaD, int isaN, int first, int last) {
        int t = last - first;
        int middle = first + t / 2;
        if (t <= 512) {
            if (t <= 32) {
                return this.trMedian3(isa, isaD, isaN, first, middle, last - 1);
            }
            return this.trMedian5(isa, isaD, isaN, first, first + t, middle, last - 1 - (t >>= 2), last - 1);
        }
        return this.trMedian3(isa, isaD, isaN, this.trMedian3(isa, isaD, isaN, first, first + t, first + (t << 1)), this.trMedian3(isa, isaD, isaN, middle - t, middle, middle + t), this.trMedian3(isa, isaD, isaN, last - 1 - (t << 1), last - 1 - (t >>= 3), last - 1));
    }

    private void lsUpdateGroup(int isa, int first, int last) {
        int[] SA = this.SA;
        for (int a = first; a < last; ++a) {
            int b;
            if (0 <= SA[a]) {
                b = a;
                do {
                    SA[isa + SA[a]] = a++;
                } while (a < last && 0 <= SA[a]);
                SA[b] = b - a;
                if (last <= a) break;
            }
            b = a;
            do {
                SA[a] = ~ SA[a];
            } while (SA[++a] < 0);
            int t = a;
            do {
                SA[isa + SA[b]] = t;
            } while (++b <= a);
        }
    }

    private void lsIntroSort(int isa, int isaD, int isaN, int first, int last) {
        int[] SA = this.SA;
        StackEntry[] stack = new StackEntry[64];
        int x = 0;
        int ssize = 0;
        int limit = Bzip2DivSufSort.trLog(last - first);
        do {
            int b;
            int a;
            StackEntry entry;
            int c;
            if (last - first <= 8) {
                if (1 < last - first) {
                    this.trInsertionSort(isa, isaD, isaN, first, last);
                    this.lsUpdateGroup(isa, first, last);
                } else if (last - first == 1) {
                    SA[first] = -1;
                }
                if (ssize == 0) {
                    return;
                }
                entry = stack[--ssize];
                first = entry.a;
                last = entry.b;
                limit = entry.c;
                continue;
            }
            if (limit-- == 0) {
                this.trHeapSort(isa, isaD, isaN, first, last - first);
                a = last - 1;
                while (first < a) {
                    x = this.trGetC(isa, isaD, isaN, SA[a]);
                    for (b = a - 1; first <= b && this.trGetC(isa, isaD, isaN, SA[b]) == x; --b) {
                        SA[b] = ~ SA[b];
                    }
                    a = b;
                }
                this.lsUpdateGroup(isa, first, last);
                if (ssize == 0) {
                    return;
                }
                entry = stack[--ssize];
                first = entry.a;
                last = entry.b;
                limit = entry.c;
                continue;
            }
            a = this.trPivot(isa, isaD, isaN, first, last);
            Bzip2DivSufSort.swapElements(SA, first, SA, a);
            int v = this.trGetC(isa, isaD, isaN, SA[first]);
            for (b = first + 1; b < last && (x = this.trGetC(isa, isaD, isaN, SA[b])) == v; ++b) {
            }
            a = b;
            if (a < last && x < v) {
                while (++b < last && (x = this.trGetC(isa, isaD, isaN, SA[b])) <= v) {
                    if (x != v) continue;
                    Bzip2DivSufSort.swapElements(SA, b, SA, a);
                    ++a;
                }
            }
            for (c = last - 1; b < c && (x = this.trGetC(isa, isaD, isaN, SA[c])) == v; --c) {
            }
            int d = c;
            if (b < d && x > v) {
                while (b < --c && (x = this.trGetC(isa, isaD, isaN, SA[c])) >= v) {
                    if (x != v) continue;
                    Bzip2DivSufSort.swapElements(SA, c, SA, d);
                    --d;
                }
            }
            while (b < c) {
                Bzip2DivSufSort.swapElements(SA, b, SA, c);
                while (++b < c && (x = this.trGetC(isa, isaD, isaN, SA[b])) <= v) {
                    if (x != v) continue;
                    Bzip2DivSufSort.swapElements(SA, b, SA, a);
                    ++a;
                }
                while (b < --c && (x = this.trGetC(isa, isaD, isaN, SA[c])) >= v) {
                    if (x != v) continue;
                    Bzip2DivSufSort.swapElements(SA, c, SA, d);
                    --d;
                }
            }
            if (a <= d) {
                c = b - 1;
                int s = a - first;
                int t = b - a;
                if (s > t) {
                    s = t;
                }
                int e = first;
                int f = b - s;
                while (0 < s) {
                    Bzip2DivSufSort.swapElements(SA, e, SA, f);
                    --s;
                    ++e;
                    ++f;
                }
                s = d - c;
                t = last - d - 1;
                if (s > t) {
                    s = t;
                }
                e = b;
                f = last - s;
                while (0 < s) {
                    Bzip2DivSufSort.swapElements(SA, e, SA, f);
                    --s;
                    ++e;
                    ++f;
                }
                a = first + (b - a);
                b = last - (d - c);
                v = a - 1;
                for (c = first; c < a; ++c) {
                    SA[isa + SA[c]] = v;
                }
                if (b < last) {
                    v = b - 1;
                    for (c = a; c < b; ++c) {
                        SA[isa + SA[c]] = v;
                    }
                }
                if (b - a == 1) {
                    SA[a] = -1;
                }
                if (a - first <= last - b) {
                    if (first < a) {
                        stack[ssize++] = new StackEntry(b, last, limit, 0);
                        last = a;
                        continue;
                    }
                    first = b;
                    continue;
                }
                if (b < last) {
                    stack[ssize++] = new StackEntry(first, a, limit, 0);
                    first = b;
                    continue;
                }
                last = a;
                continue;
            }
            if (ssize == 0) {
                return;
            }
            entry = stack[--ssize];
            first = entry.a;
            last = entry.b;
            limit = entry.c;
        } while (true);
    }

    private void lsSort(int isa, int n, int depth) {
        int[] SA = this.SA;
        int isaD = isa + depth;
        while (- n < SA[0]) {
            int last;
            int t;
            int first = 0;
            int skip = 0;
            do {
                if ((t = SA[first]) < 0) {
                    first -= t;
                    skip += t;
                    continue;
                }
                if (skip != 0) {
                    SA[first + skip] = skip;
                    skip = 0;
                }
                last = SA[isa + t] + 1;
                this.lsIntroSort(isa, isaD, isa + n, first, last);
                first = last;
            } while (first < n);
            if (skip != 0) {
                SA[first + skip] = skip;
            }
            if (n < isaD - isa) {
                first = 0;
                do {
                    if ((t = SA[first]) < 0) {
                        first -= t;
                        continue;
                    }
                    last = SA[isa + t] + 1;
                    int i = first;
                    while (i < last) {
                        SA[isa + SA[i]] = i++;
                    }
                    first = last;
                } while (first < n);
                break;
            }
            isaD += isaD - isa;
        }
    }

    private PartitionResult trPartition(int isa, int isaD, int isaN, int first, int last, int v) {
        int b;
        int c;
        int[] SA = this.SA;
        int x = 0;
        for (b = first; b < last && (x = this.trGetC(isa, isaD, isaN, SA[b])) == v; ++b) {
        }
        int a = b;
        if (a < last && x < v) {
            while (++b < last && (x = this.trGetC(isa, isaD, isaN, SA[b])) <= v) {
                if (x != v) continue;
                Bzip2DivSufSort.swapElements(SA, b, SA, a);
                ++a;
            }
        }
        for (c = last - 1; b < c && (x = this.trGetC(isa, isaD, isaN, SA[c])) == v; --c) {
        }
        int d = c;
        if (b < d && x > v) {
            while (b < --c && (x = this.trGetC(isa, isaD, isaN, SA[c])) >= v) {
                if (x != v) continue;
                Bzip2DivSufSort.swapElements(SA, c, SA, d);
                --d;
            }
        }
        while (b < c) {
            Bzip2DivSufSort.swapElements(SA, b, SA, c);
            while (++b < c && (x = this.trGetC(isa, isaD, isaN, SA[b])) <= v) {
                if (x != v) continue;
                Bzip2DivSufSort.swapElements(SA, b, SA, a);
                ++a;
            }
            while (b < --c && (x = this.trGetC(isa, isaD, isaN, SA[c])) >= v) {
                if (x != v) continue;
                Bzip2DivSufSort.swapElements(SA, c, SA, d);
                --d;
            }
        }
        if (a <= d) {
            c = b - 1;
            int s = a - first;
            int t = b - a;
            if (s > t) {
                s = t;
            }
            int e = first;
            int f = b - s;
            while (0 < s) {
                Bzip2DivSufSort.swapElements(SA, e, SA, f);
                --s;
                ++e;
                ++f;
            }
            s = d - c;
            t = last - d - 1;
            if (s > t) {
                s = t;
            }
            e = b;
            f = last - s;
            while (0 < s) {
                Bzip2DivSufSort.swapElements(SA, e, SA, f);
                --s;
                ++e;
                ++f;
            }
            first += b - a;
            last -= d - c;
        }
        return new PartitionResult(first, last);
    }

    private void trCopy(int isa, int isaN, int first, int a, int b, int last, int depth) {
        int s;
        int c;
        int[] SA = this.SA;
        int v = b - 1;
        int d = a - 1;
        for (c = first; c <= d; ++c) {
            s = SA[c] - depth;
            if (s < 0) {
                s += isaN - isa;
            }
            if (SA[isa + s] != v) continue;
            SA[++d] = s;
            SA[isa + s] = d;
        }
        c = last - 1;
        int e = d + 1;
        d = b;
        while (e < d) {
            s = SA[c] - depth;
            if (s < 0) {
                s += isaN - isa;
            }
            if (SA[isa + s] == v) {
                SA[--d] = s;
                SA[isa + s] = d;
            }
            --c;
        }
    }

    private void trIntroSort(int isa, int isaD, int isaN, int first, int last, TRBudget budget, int size) {
        int s;
        int[] SA = this.SA;
        StackEntry[] stack = new StackEntry[64];
        int x = 0;
        int ssize = 0;
        int limit = Bzip2DivSufSort.trLog(last - first);
        do {
            int b;
            int c;
            StackEntry entry;
            int a;
            int next;
            int v;
            if (limit < 0) {
                if (limit == -1) {
                    StackEntry entry2;
                    if (!budget.update(size, last - first)) break;
                    PartitionResult result = this.trPartition(isa, isaD - 1, isaN, first, last, last - 1);
                    a = result.first;
                    b = result.last;
                    if (first < a || b < last) {
                        if (a < last) {
                            v = a - 1;
                            for (c = first; c < a; ++c) {
                                SA[isa + SA[c]] = v;
                            }
                        }
                        if (b < last) {
                            v = b - 1;
                            for (c = a; c < b; ++c) {
                                SA[isa + SA[c]] = v;
                            }
                        }
                        stack[ssize++] = new StackEntry(0, a, b, 0);
                        stack[ssize++] = new StackEntry(isaD - 1, first, last, -2);
                        if (a - first <= last - b) {
                            if (1 < a - first) {
                                stack[ssize++] = new StackEntry(isaD, b, last, Bzip2DivSufSort.trLog(last - b));
                                last = a;
                                limit = Bzip2DivSufSort.trLog(a - first);
                                continue;
                            }
                            if (1 < last - b) {
                                first = b;
                                limit = Bzip2DivSufSort.trLog(last - b);
                                continue;
                            }
                            if (ssize == 0) {
                                return;
                            }
                            entry2 = stack[--ssize];
                            isaD = entry2.a;
                            first = entry2.b;
                            last = entry2.c;
                            limit = entry2.d;
                            continue;
                        }
                        if (1 < last - b) {
                            stack[ssize++] = new StackEntry(isaD, first, a, Bzip2DivSufSort.trLog(a - first));
                            first = b;
                            limit = Bzip2DivSufSort.trLog(last - b);
                            continue;
                        }
                        if (1 < a - first) {
                            last = a;
                            limit = Bzip2DivSufSort.trLog(a - first);
                            continue;
                        }
                        if (ssize == 0) {
                            return;
                        }
                        entry2 = stack[--ssize];
                        isaD = entry2.a;
                        first = entry2.b;
                        last = entry2.c;
                        limit = entry2.d;
                        continue;
                    }
                    c = first;
                    while (c < last) {
                        SA[isa + SA[c]] = c++;
                    }
                    if (ssize == 0) {
                        return;
                    }
                    entry2 = stack[--ssize];
                    isaD = entry2.a;
                    first = entry2.b;
                    last = entry2.c;
                    limit = entry2.d;
                    continue;
                }
                if (limit == -2) {
                    a = stack[--ssize].b;
                    b = stack[ssize].c;
                    this.trCopy(isa, isaN, first, a, b, last, isaD - isa);
                    if (ssize == 0) {
                        return;
                    }
                    entry = stack[--ssize];
                    isaD = entry.a;
                    first = entry.b;
                    last = entry.c;
                    limit = entry.d;
                    continue;
                }
                if (0 <= SA[first]) {
                    a = first;
                    do {
                        SA[isa + SA[a]] = a++;
                    } while (a < last && 0 <= SA[a]);
                    first = a;
                }
                if (first < last) {
                    a = first;
                    do {
                        SA[a] = ~ SA[a];
                    } while (SA[++a] < 0);
                    int n = next = SA[isa + SA[a]] != SA[isaD + SA[a]] ? Bzip2DivSufSort.trLog(a - first + 1) : -1;
                    if (++a < last) {
                        v = a - 1;
                        for (b = first; b < a; ++b) {
                            SA[isa + SA[b]] = v;
                        }
                    }
                    if (a - first <= last - a) {
                        stack[ssize++] = new StackEntry(isaD, a, last, -3);
                        ++isaD;
                        last = a;
                        limit = next;
                        continue;
                    }
                    if (1 < last - a) {
                        stack[ssize++] = new StackEntry(isaD + 1, first, a, next);
                        first = a;
                        limit = -3;
                        continue;
                    }
                    ++isaD;
                    last = a;
                    limit = next;
                    continue;
                }
                if (ssize == 0) {
                    return;
                }
                entry = stack[--ssize];
                isaD = entry.a;
                first = entry.b;
                last = entry.c;
                limit = entry.d;
                continue;
            }
            if (last - first <= 8) {
                if (!budget.update(size, last - first)) break;
                this.trInsertionSort(isa, isaD, isaN, first, last);
                limit = -3;
                continue;
            }
            if (limit-- == 0) {
                if (!budget.update(size, last - first)) break;
                this.trHeapSort(isa, isaD, isaN, first, last - first);
                a = last - 1;
                while (first < a) {
                    x = this.trGetC(isa, isaD, isaN, SA[a]);
                    for (b = a - 1; first <= b && this.trGetC(isa, isaD, isaN, SA[b]) == x; --b) {
                        SA[b] = ~ SA[b];
                    }
                    a = b;
                }
                limit = -3;
                continue;
            }
            a = this.trPivot(isa, isaD, isaN, first, last);
            Bzip2DivSufSort.swapElements(SA, first, SA, a);
            v = this.trGetC(isa, isaD, isaN, SA[first]);
            for (b = first + 1; b < last && (x = this.trGetC(isa, isaD, isaN, SA[b])) == v; ++b) {
            }
            a = b;
            if (a < last && x < v) {
                while (++b < last && (x = this.trGetC(isa, isaD, isaN, SA[b])) <= v) {
                    if (x != v) continue;
                    Bzip2DivSufSort.swapElements(SA, b, SA, a);
                    ++a;
                }
            }
            for (c = last - 1; b < c && (x = this.trGetC(isa, isaD, isaN, SA[c])) == v; --c) {
            }
            int d = c;
            if (b < d && x > v) {
                while (b < --c && (x = this.trGetC(isa, isaD, isaN, SA[c])) >= v) {
                    if (x != v) continue;
                    Bzip2DivSufSort.swapElements(SA, c, SA, d);
                    --d;
                }
            }
            while (b < c) {
                Bzip2DivSufSort.swapElements(SA, b, SA, c);
                while (++b < c && (x = this.trGetC(isa, isaD, isaN, SA[b])) <= v) {
                    if (x != v) continue;
                    Bzip2DivSufSort.swapElements(SA, b, SA, a);
                    ++a;
                }
                while (b < --c && (x = this.trGetC(isa, isaD, isaN, SA[c])) >= v) {
                    if (x != v) continue;
                    Bzip2DivSufSort.swapElements(SA, c, SA, d);
                    --d;
                }
            }
            if (a <= d) {
                c = b - 1;
                s = a - first;
                int t = b - a;
                if (s > t) {
                    s = t;
                }
                int e = first;
                int f = b - s;
                while (0 < s) {
                    Bzip2DivSufSort.swapElements(SA, e, SA, f);
                    --s;
                    ++e;
                    ++f;
                }
                s = d - c;
                t = last - d - 1;
                if (s > t) {
                    s = t;
                }
                e = b;
                f = last - s;
                while (0 < s) {
                    Bzip2DivSufSort.swapElements(SA, e, SA, f);
                    --s;
                    ++e;
                    ++f;
                }
                a = first + (b - a);
                b = last - (d - c);
                next = SA[isa + SA[a]] != v ? Bzip2DivSufSort.trLog(b - a) : -1;
                v = a - 1;
                for (c = first; c < a; ++c) {
                    SA[isa + SA[c]] = v;
                }
                if (b < last) {
                    v = b - 1;
                    for (c = a; c < b; ++c) {
                        SA[isa + SA[c]] = v;
                    }
                }
                if (a - first <= last - b) {
                    if (last - b <= b - a) {
                        if (1 < a - first) {
                            stack[ssize++] = new StackEntry(isaD + 1, a, b, next);
                            stack[ssize++] = new StackEntry(isaD, b, last, limit);
                            last = a;
                            continue;
                        }
                        if (1 < last - b) {
                            stack[ssize++] = new StackEntry(isaD + 1, a, b, next);
                            first = b;
                            continue;
                        }
                        if (1 < b - a) {
                            ++isaD;
                            first = a;
                            last = b;
                            limit = next;
                            continue;
                        }
                        if (ssize == 0) {
                            return;
                        }
                        entry = stack[--ssize];
                        isaD = entry.a;
                        first = entry.b;
                        last = entry.c;
                        limit = entry.d;
                        continue;
                    }
                    if (a - first <= b - a) {
                        if (1 < a - first) {
                            stack[ssize++] = new StackEntry(isaD, b, last, limit);
                            stack[ssize++] = new StackEntry(isaD + 1, a, b, next);
                            last = a;
                            continue;
                        }
                        if (1 < b - a) {
                            stack[ssize++] = new StackEntry(isaD, b, last, limit);
                            ++isaD;
                            first = a;
                            last = b;
                            limit = next;
                            continue;
                        }
                        first = b;
                        continue;
                    }
                    if (1 < b - a) {
                        stack[ssize++] = new StackEntry(isaD, b, last, limit);
                        stack[ssize++] = new StackEntry(isaD, first, a, limit);
                        ++isaD;
                        first = a;
                        last = b;
                        limit = next;
                        continue;
                    }
                    stack[ssize++] = new StackEntry(isaD, b, last, limit);
                    last = a;
                    continue;
                }
                if (a - first <= b - a) {
                    if (1 < last - b) {
                        stack[ssize++] = new StackEntry(isaD + 1, a, b, next);
                        stack[ssize++] = new StackEntry(isaD, first, a, limit);
                        first = b;
                        continue;
                    }
                    if (1 < a - first) {
                        stack[ssize++] = new StackEntry(isaD + 1, a, b, next);
                        last = a;
                        continue;
                    }
                    if (1 < b - a) {
                        ++isaD;
                        first = a;
                        last = b;
                        limit = next;
                        continue;
                    }
                    stack[ssize++] = new StackEntry(isaD, first, last, limit);
                    continue;
                }
                if (last - b <= b - a) {
                    if (1 < last - b) {
                        stack[ssize++] = new StackEntry(isaD, first, a, limit);
                        stack[ssize++] = new StackEntry(isaD + 1, a, b, next);
                        first = b;
                        continue;
                    }
                    if (1 < b - a) {
                        stack[ssize++] = new StackEntry(isaD, first, a, limit);
                        ++isaD;
                        first = a;
                        last = b;
                        limit = next;
                        continue;
                    }
                    last = a;
                    continue;
                }
                if (1 < b - a) {
                    stack[ssize++] = new StackEntry(isaD, first, a, limit);
                    stack[ssize++] = new StackEntry(isaD, b, last, limit);
                    ++isaD;
                    first = a;
                    last = b;
                    limit = next;
                    continue;
                }
                stack[ssize++] = new StackEntry(isaD, first, a, limit);
                first = b;
                continue;
            }
            if (!budget.update(size, last - first)) break;
            ++limit;
            ++isaD;
        } while (true);
        for (s = 0; s < ssize; ++s) {
            if (stack[s].d != -3) continue;
            this.lsUpdateGroup(isa, stack[s].b, stack[s].c);
        }
    }

    private void trSort(int isa, int n, int depth) {
        int[] SA = this.SA;
        int first = 0;
        if (- n < SA[0]) {
            TRBudget budget = new TRBudget(n, Bzip2DivSufSort.trLog(n) * 2 / 3 + 1);
            do {
                int t;
                if ((t = SA[first]) < 0) {
                    first -= t;
                    continue;
                }
                int last = SA[isa + t] + 1;
                if (1 < last - first) {
                    this.trIntroSort(isa, isa + depth, isa + n, first, last, budget, n);
                    if (budget.chance == 0) {
                        if (0 < first) {
                            SA[0] = - first;
                        }
                        this.lsSort(isa, n, depth);
                        break;
                    }
                }
                first = last;
            } while (first < n);
        }
    }

    private static int BUCKET_B(int c0, int c1) {
        return c1 << 8 | c0;
    }

    private static int BUCKET_BSTAR(int c0, int c1) {
        return c0 << 8 | c1;
    }

    private int sortTypeBstar(int[] bucketA, int[] bucketB) {
        int ti1;
        int c1;
        int c0;
        int t;
        int i;
        byte[] T = this.T;
        int[] SA = this.SA;
        int n = this.n;
        int[] tempbuf = new int[256];
        boolean flag = true;
        for (i = 1; i < n; ++i) {
            if (T[i - 1] == T[i]) continue;
            if ((T[i - 1] & 255) <= (T[i] & 255)) break;
            flag = false;
            break;
        }
        i = n - 1;
        int m = n;
        int ti = T[i] & 255;
        int t0 = T[0] & 255;
        if (ti < t0 || T[i] == T[0] && flag) {
            if (!flag) {
                int[] arrn = bucketB;
                int n2 = Bzip2DivSufSort.BUCKET_BSTAR(ti, t0);
                arrn[n2] = arrn[n2] + 1;
                SA[--m] = i;
            } else {
                int[] arrn = bucketB;
                int n3 = Bzip2DivSufSort.BUCKET_B(ti, t0);
                arrn[n3] = arrn[n3] + 1;
            }
            --i;
            while (0 <= i && (ti = T[i] & 255) <= (ti1 = T[i + 1] & 255)) {
                int[] arrn = bucketB;
                int n4 = Bzip2DivSufSort.BUCKET_B(ti, ti1);
                arrn[n4] = arrn[n4] + 1;
                --i;
            }
        }
        while (0 <= i) {
            do {
                int[] arrn = bucketA;
                int n5 = T[i] & 255;
                arrn[n5] = arrn[n5] + 1;
            } while (0 <= --i && (T[i] & 255) >= (T[i + 1] & 255));
            if (0 > i) continue;
            int[] arrn = bucketB;
            int n6 = Bzip2DivSufSort.BUCKET_BSTAR(T[i] & 255, T[i + 1] & 255);
            arrn[n6] = arrn[n6] + 1;
            SA[--m] = i--;
            while (0 <= i && (ti = T[i] & 255) <= (ti1 = T[i + 1] & 255)) {
                int[] arrn2 = bucketB;
                int n7 = Bzip2DivSufSort.BUCKET_B(ti, ti1);
                arrn2[n7] = arrn2[n7] + 1;
                --i;
            }
        }
        if ((m = n - m) == 0) {
            i = 0;
            while (i < n) {
                SA[i] = i++;
            }
            return 0;
        }
        i = -1;
        int j = 0;
        for (c0 = 0; c0 < 256; ++c0) {
            t = i + bucketA[c0];
            bucketA[c0] = i + j;
            i = t + bucketB[Bzip2DivSufSort.BUCKET_B(c0, c0)];
            for (c1 = c0 + 1; c1 < 256; ++c1) {
                bucketB[c0 << 8 | c1] = j += bucketB[Bzip2DivSufSort.BUCKET_BSTAR(c0, c1)];
                i += bucketB[Bzip2DivSufSort.BUCKET_B(c0, c1)];
            }
        }
        int PAb = n - m;
        int ISAb = m;
        i = m - 2;
        while (0 <= i) {
            t = SA[PAb + i];
            c0 = T[t] & 255;
            c1 = T[t + 1] & 255;
            int[] arrn = bucketB;
            int n8 = Bzip2DivSufSort.BUCKET_BSTAR(c0, c1);
            int n9 = arrn[n8] - 1;
            arrn[n8] = n9;
            SA[n9] = i--;
        }
        t = SA[PAb + m - 1];
        c0 = T[t] & 255;
        c1 = T[t + 1] & 255;
        int[] arrn = bucketB;
        int n10 = Bzip2DivSufSort.BUCKET_BSTAR(c0, c1);
        int n11 = arrn[n10] - 1;
        arrn[n10] = n11;
        SA[n11] = m - 1;
        int[] buf = SA;
        int bufoffset = m;
        int bufsize = n - 2 * m;
        if (bufsize <= 256) {
            buf = tempbuf;
            bufoffset = 0;
            bufsize = 256;
        }
        c0 = 255;
        j = m;
        while (0 < j) {
            for (c1 = 255; c0 < c1; --c1) {
                i = bucketB[Bzip2DivSufSort.BUCKET_BSTAR(c0, c1)];
                if (1 < j - i) {
                    this.subStringSort(PAb, i, j, buf, bufoffset, bufsize, 2, SA[i] == m - 1, n);
                }
                j = i;
            }
            --c0;
        }
        for (i = m - 1; 0 <= i; --i) {
            if (0 <= SA[i]) {
                j = i;
                do {
                    SA[ISAb + SA[i]] = i--;
                } while (0 <= i && 0 <= SA[i]);
                SA[i + 1] = i - j;
                if (i <= 0) break;
            }
            j = i;
            do {
                SA[i] = ~ SA[i];
                SA[ISAb + SA[i]] = j;
            } while (SA[--i] < 0);
            SA[ISAb + SA[i]] = j;
        }
        this.trSort(ISAb, m, 1);
        i = n - 1;
        j = m;
        if ((T[i] & 255) < (T[0] & 255) || T[i] == T[0] && flag) {
            if (!flag) {
                SA[SA[ISAb + --j]] = i;
            }
            --i;
            while (0 <= i && (T[i] & 255) <= (T[i + 1] & 255)) {
                --i;
            }
        }
        while (0 <= i) {
            --i;
            while (0 <= i && (T[i] & 255) >= (T[i + 1] & 255)) {
                --i;
            }
            if (0 > i) continue;
            SA[SA[ISAb + --j]] = i--;
            while (0 <= i && (T[i] & 255) <= (T[i + 1] & 255)) {
                --i;
            }
        }
        i = n - 1;
        int k = m - 1;
        for (c0 = 255; 0 <= c0; --c0) {
            for (c1 = 255; c0 < c1; --c1) {
                t = i - bucketB[Bzip2DivSufSort.BUCKET_B(c0, c1)];
                bucketB[Bzip2DivSufSort.BUCKET_B((int)c0, (int)c1)] = i + 1;
                i = t;
                j = bucketB[Bzip2DivSufSort.BUCKET_BSTAR(c0, c1)];
                while (j <= k) {
                    SA[i] = SA[k];
                    --i;
                    --k;
                }
            }
            t = i - bucketB[Bzip2DivSufSort.BUCKET_B(c0, c0)];
            bucketB[Bzip2DivSufSort.BUCKET_B((int)c0, (int)c0)] = i + 1;
            if (c0 < 255) {
                bucketB[Bzip2DivSufSort.BUCKET_BSTAR((int)c0, (int)(c0 + 1))] = t + 1;
            }
            i = bucketA[c0];
        }
        return m;
    }

    private int constructBWT(int[] bucketA, int[] bucketB) {
        int s;
        int s1;
        int c0;
        int i;
        byte[] T = this.T;
        int[] SA = this.SA;
        int n = this.n;
        int t = 0;
        int c2 = 0;
        int orig = -1;
        for (int c1 = 254; 0 <= c1; --c1) {
            i = bucketB[Bzip2DivSufSort.BUCKET_BSTAR(c1, c1 + 1)];
            t = 0;
            c2 = -1;
            for (int j = bucketA[c1 + 1]; i <= j; --j) {
                s1 = s = SA[j];
                if (0 <= s) {
                    if (--s < 0) {
                        s = n - 1;
                    }
                    if ((c0 = T[s] & 255) > c1) continue;
                    SA[j] = ~ s1;
                    if (0 < s && (T[s - 1] & 255) > c0) {
                        s ^= -1;
                    }
                    if (c2 == c0) {
                        SA[--t] = s;
                        continue;
                    }
                    if (0 <= c2) {
                        bucketB[Bzip2DivSufSort.BUCKET_B((int)c2, (int)c1)] = t;
                    }
                    c2 = c0;
                    t = bucketB[Bzip2DivSufSort.BUCKET_B(c2, c1)] - 1;
                    SA[t] = s;
                    continue;
                }
                SA[j] = ~ s;
            }
        }
        for (i = 0; i < n; ++i) {
            s1 = s = SA[i];
            if (0 <= s) {
                if (--s < 0) {
                    s = n - 1;
                }
                if ((c0 = T[s] & 255) >= (T[s + 1] & 255)) {
                    if (0 < s && (T[s - 1] & 255) < c0) {
                        s ^= -1;
                    }
                    if (c0 == c2) {
                        SA[++t] = s;
                    } else {
                        if (c2 != -1) {
                            bucketA[c2] = t;
                        }
                        c2 = c0;
                        t = bucketA[c2] + 1;
                        SA[t] = s;
                    }
                }
            } else {
                s1 ^= -1;
            }
            if (s1 == 0) {
                SA[i] = T[n - 1];
                orig = i;
                continue;
            }
            SA[i] = T[s1 - 1];
        }
        return orig;
    }

    public int bwt() {
        int[] SA = this.SA;
        byte[] T = this.T;
        int n = this.n;
        int[] bucketA = new int[256];
        int[] bucketB = new int[65536];
        if (n == 0) {
            return 0;
        }
        if (n == 1) {
            SA[0] = T[0];
            return 0;
        }
        int m = this.sortTypeBstar(bucketA, bucketB);
        if (0 < m) {
            return this.constructBWT(bucketA, bucketB);
        }
        return 0;
    }

    private static class TRBudget {
        int budget;
        int chance;

        TRBudget(int budget, int chance) {
            this.budget = budget;
            this.chance = chance;
        }

        boolean update(int size, int n) {
            this.budget -= n;
            if (this.budget <= 0) {
                if (--this.chance == 0) {
                    return false;
                }
                this.budget += size;
            }
            return true;
        }
    }

    private static class PartitionResult {
        final int first;
        final int last;

        PartitionResult(int first, int last) {
            this.first = first;
            this.last = last;
        }
    }

    private static class StackEntry {
        final int a;
        final int b;
        final int c;
        final int d;

        StackEntry(int a, int b, int c, int d) {
            this.a = a;
            this.b = b;
            this.c = c;
            this.d = d;
        }
    }

}

