/*
 * Decompiled with CFR 0_132.
 */
package org.yaml.snakeyaml.events;

import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.events.CollectionStartEvent;
import org.yaml.snakeyaml.events.Event;

public final class SequenceStartEvent
extends CollectionStartEvent {
    public SequenceStartEvent(String anchor, String tag, boolean implicit, Mark startMark, Mark endMark, Boolean flowStyle) {
        super(anchor, tag, implicit, startMark, endMark, flowStyle);
    }

    @Override
    public boolean is(Event.ID id) {
        return Event.ID.SequenceStart == id;
    }
}

