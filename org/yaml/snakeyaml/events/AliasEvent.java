/*
 * Decompiled with CFR 0_132.
 */
package org.yaml.snakeyaml.events;

import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.events.Event;
import org.yaml.snakeyaml.events.NodeEvent;

public final class AliasEvent
extends NodeEvent {
    public AliasEvent(String anchor, Mark startMark, Mark endMark) {
        super(anchor, startMark, endMark);
    }

    @Override
    public boolean is(Event.ID id) {
        return Event.ID.Alias == id;
    }
}

