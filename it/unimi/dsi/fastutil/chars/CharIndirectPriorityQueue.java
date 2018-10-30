/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.IndirectPriorityQueue;
import it.unimi.dsi.fastutil.chars.CharComparator;
import java.util.Comparator;

public interface CharIndirectPriorityQueue
extends IndirectPriorityQueue<Character> {
    public CharComparator comparator();
}

