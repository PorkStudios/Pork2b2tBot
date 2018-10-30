/*
 * Decompiled with CFR 0_132.
 */
package org.yaml.snakeyaml.events;

import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.events.CollectionEndEvent;
import org.yaml.snakeyaml.events.Event;

public final class MappingEndEvent
extends CollectionEndEvent {
    public MappingEndEvent(Mark startMark, Mark endMark) {
        super(startMark, endMark);
    }

    @Override
    public boolean is(Event.ID id) {
        return Event.ID.MappingEnd == id;
    }
}

