/*
 * Decompiled with CFR 0_132.
 */
package org.yaml.snakeyaml.events;

import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.events.CollectionEndEvent;
import org.yaml.snakeyaml.events.Event;

public final class SequenceEndEvent
extends CollectionEndEvent {
    public SequenceEndEvent(Mark startMark, Mark endMark) {
        super(startMark, endMark);
    }

    @Override
    public boolean is(Event.ID id) {
        return Event.ID.SequenceEnd == id;
    }
}

