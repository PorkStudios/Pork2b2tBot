/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.dns;

import io.netty.handler.codec.dns.DnsMessage;
import io.netty.handler.codec.dns.DnsOpCode;
import io.netty.handler.codec.dns.DnsQuery;
import io.netty.handler.codec.dns.DnsQuestion;
import io.netty.handler.codec.dns.DnsRecord;
import io.netty.handler.codec.dns.DnsSection;
import io.netty.util.AbstractReferenceCounted;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.ReferenceCounted;
import io.netty.util.ResourceLeakDetector;
import io.netty.util.ResourceLeakDetectorFactory;
import io.netty.util.ResourceLeakTracker;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.StringUtil;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractDnsMessage
extends AbstractReferenceCounted
implements DnsMessage {
    private static final ResourceLeakDetector<DnsMessage> leakDetector = ResourceLeakDetectorFactory.instance().newResourceLeakDetector(DnsMessage.class);
    private static final int SECTION_QUESTION = DnsSection.QUESTION.ordinal();
    private static final int SECTION_COUNT = 4;
    private final ResourceLeakTracker<DnsMessage> leak = leakDetector.track(this);
    private short id;
    private DnsOpCode opCode;
    private boolean recursionDesired;
    private byte z;
    private Object questions;
    private Object answers;
    private Object authorities;
    private Object additionals;

    protected AbstractDnsMessage(int id) {
        this(id, DnsOpCode.QUERY);
    }

    protected AbstractDnsMessage(int id, DnsOpCode opCode) {
        this.setId(id);
        this.setOpCode(opCode);
    }

    @Override
    public int id() {
        return this.id & 65535;
    }

    @Override
    public DnsMessage setId(int id) {
        this.id = (short)id;
        return this;
    }

    @Override
    public DnsOpCode opCode() {
        return this.opCode;
    }

    @Override
    public DnsMessage setOpCode(DnsOpCode opCode) {
        this.opCode = ObjectUtil.checkNotNull(opCode, "opCode");
        return this;
    }

    @Override
    public boolean isRecursionDesired() {
        return this.recursionDesired;
    }

    @Override
    public DnsMessage setRecursionDesired(boolean recursionDesired) {
        this.recursionDesired = recursionDesired;
        return this;
    }

    @Override
    public int z() {
        return this.z;
    }

    @Override
    public DnsMessage setZ(int z) {
        this.z = (byte)(z & 7);
        return this;
    }

    @Override
    public int count(DnsSection section) {
        return this.count(AbstractDnsMessage.sectionOrdinal(section));
    }

    private int count(int section) {
        Object records = this.sectionAt(section);
        if (records == null) {
            return 0;
        }
        if (records instanceof DnsRecord) {
            return 1;
        }
        List recordList = (List)records;
        return recordList.size();
    }

    @Override
    public int count() {
        int count = 0;
        for (int i = 0; i < 4; ++i) {
            count += this.count(i);
        }
        return count;
    }

    @Override
    public <T extends DnsRecord> T recordAt(DnsSection section) {
        return this.recordAt(AbstractDnsMessage.sectionOrdinal(section));
    }

    private <T extends DnsRecord> T recordAt(int section) {
        Object records = this.sectionAt(section);
        if (records == null) {
            return null;
        }
        if (records instanceof DnsRecord) {
            return AbstractDnsMessage.castRecord(records);
        }
        List recordList = (List)records;
        if (recordList.isEmpty()) {
            return null;
        }
        return AbstractDnsMessage.castRecord(recordList.get(0));
    }

    @Override
    public <T extends DnsRecord> T recordAt(DnsSection section, int index) {
        return this.recordAt(AbstractDnsMessage.sectionOrdinal(section), index);
    }

    private <T extends DnsRecord> T recordAt(int section, int index) {
        Object records = this.sectionAt(section);
        if (records == null) {
            throw new IndexOutOfBoundsException("index: " + index + " (expected: none)");
        }
        if (records instanceof DnsRecord) {
            if (index == 0) {
                return AbstractDnsMessage.castRecord(records);
            }
            throw new IndexOutOfBoundsException("index: " + index + "' (expected: 0)");
        }
        List recordList = (List)records;
        return AbstractDnsMessage.castRecord(recordList.get(index));
    }

    @Override
    public DnsMessage setRecord(DnsSection section, DnsRecord record) {
        this.setRecord(AbstractDnsMessage.sectionOrdinal(section), record);
        return this;
    }

    private void setRecord(int section, DnsRecord record) {
        this.clear(section);
        this.setSection(section, AbstractDnsMessage.checkQuestion(section, record));
    }

    @Override
    public <T extends DnsRecord> T setRecord(DnsSection section, int index, DnsRecord record) {
        return this.setRecord(AbstractDnsMessage.sectionOrdinal(section), index, record);
    }

    private <T extends DnsRecord> T setRecord(int section, int index, DnsRecord record) {
        AbstractDnsMessage.checkQuestion(section, record);
        Object records = this.sectionAt(section);
        if (records == null) {
            throw new IndexOutOfBoundsException("index: " + index + " (expected: none)");
        }
        if (records instanceof DnsRecord) {
            if (index == 0) {
                this.setSection(section, record);
                return AbstractDnsMessage.castRecord(records);
            }
            throw new IndexOutOfBoundsException("index: " + index + " (expected: 0)");
        }
        List recordList = (List)records;
        return AbstractDnsMessage.castRecord(recordList.set(index, record));
    }

    @Override
    public DnsMessage addRecord(DnsSection section, DnsRecord record) {
        this.addRecord(AbstractDnsMessage.sectionOrdinal(section), record);
        return this;
    }

    private void addRecord(int section, DnsRecord record) {
        AbstractDnsMessage.checkQuestion(section, record);
        Object records = this.sectionAt(section);
        if (records == null) {
            this.setSection(section, record);
            return;
        }
        if (records instanceof DnsRecord) {
            ArrayList<DnsRecord> recordList = AbstractDnsMessage.newRecordList();
            recordList.add((DnsRecord)AbstractDnsMessage.castRecord(records));
            recordList.add(record);
            this.setSection(section, recordList);
            return;
        }
        List recordList = (List)records;
        recordList.add(record);
    }

    @Override
    public DnsMessage addRecord(DnsSection section, int index, DnsRecord record) {
        this.addRecord(AbstractDnsMessage.sectionOrdinal(section), index, record);
        return this;
    }

    private void addRecord(int section, int index, DnsRecord record) {
        AbstractDnsMessage.checkQuestion(section, record);
        Object records = this.sectionAt(section);
        if (records == null) {
            if (index != 0) {
                throw new IndexOutOfBoundsException("index: " + index + " (expected: 0)");
            }
            this.setSection(section, record);
            return;
        }
        if (records instanceof DnsRecord) {
            ArrayList<DnsRecord> recordList;
            if (index == 0) {
                recordList = AbstractDnsMessage.newRecordList();
                recordList.add(record);
                recordList.add((DnsRecord)AbstractDnsMessage.castRecord(records));
            } else if (index == 1) {
                recordList = AbstractDnsMessage.newRecordList();
                recordList.add((DnsRecord)AbstractDnsMessage.castRecord(records));
                recordList.add(record);
            } else {
                throw new IndexOutOfBoundsException("index: " + index + " (expected: 0 or 1)");
            }
            this.setSection(section, recordList);
            return;
        }
        List recordList = (List)records;
        recordList.add(index, record);
    }

    @Override
    public <T extends DnsRecord> T removeRecord(DnsSection section, int index) {
        return this.removeRecord(AbstractDnsMessage.sectionOrdinal(section), index);
    }

    private <T extends DnsRecord> T removeRecord(int section, int index) {
        Object records = this.sectionAt(section);
        if (records == null) {
            throw new IndexOutOfBoundsException("index: " + index + " (expected: none)");
        }
        if (records instanceof DnsRecord) {
            if (index != 0) {
                throw new IndexOutOfBoundsException("index: " + index + " (expected: 0)");
            }
            T record = AbstractDnsMessage.castRecord(records);
            this.setSection(section, null);
            return record;
        }
        List recordList = (List)records;
        return AbstractDnsMessage.castRecord(recordList.remove(index));
    }

    @Override
    public DnsMessage clear(DnsSection section) {
        this.clear(AbstractDnsMessage.sectionOrdinal(section));
        return this;
    }

    @Override
    public DnsMessage clear() {
        for (int i = 0; i < 4; ++i) {
            this.clear(i);
        }
        return this;
    }

    private void clear(int section) {
        List list;
        Object recordOrList = this.sectionAt(section);
        this.setSection(section, null);
        if (recordOrList instanceof ReferenceCounted) {
            ((ReferenceCounted)recordOrList).release();
        } else if (recordOrList instanceof List && !(list = (List)recordOrList).isEmpty()) {
            for (Object r : list) {
                ReferenceCountUtil.release(r);
            }
        }
    }

    @Override
    public DnsMessage touch() {
        return (DnsMessage)super.touch();
    }

    @Override
    public DnsMessage touch(Object hint) {
        if (this.leak != null) {
            this.leak.record(hint);
        }
        return this;
    }

    @Override
    public DnsMessage retain() {
        return (DnsMessage)super.retain();
    }

    @Override
    public DnsMessage retain(int increment) {
        return (DnsMessage)super.retain(increment);
    }

    @Override
    protected void deallocate() {
        this.clear();
        ResourceLeakTracker<DnsMessage> leak = this.leak;
        if (leak != null) {
            boolean closed = leak.close(this);
            assert (closed);
        }
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof DnsMessage)) {
            return false;
        }
        DnsMessage that = (DnsMessage)obj;
        if (this.id() != that.id()) {
            return false;
        }
        if (this instanceof DnsQuery ? !(that instanceof DnsQuery) : that instanceof DnsQuery) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        return this.id() * 31 + (this instanceof DnsQuery ? 0 : 1);
    }

    private Object sectionAt(int section) {
        switch (section) {
            case 0: {
                return this.questions;
            }
            case 1: {
                return this.answers;
            }
            case 2: {
                return this.authorities;
            }
            case 3: {
                return this.additionals;
            }
        }
        throw new Error();
    }

    private void setSection(int section, Object value) {
        switch (section) {
            case 0: {
                this.questions = value;
                return;
            }
            case 1: {
                this.answers = value;
                return;
            }
            case 2: {
                this.authorities = value;
                return;
            }
            case 3: {
                this.additionals = value;
                return;
            }
        }
        throw new Error();
    }

    private static int sectionOrdinal(DnsSection section) {
        return ObjectUtil.checkNotNull(section, "section").ordinal();
    }

    private static DnsRecord checkQuestion(int section, DnsRecord record) {
        if (section == SECTION_QUESTION && !(ObjectUtil.checkNotNull(record, "record") instanceof DnsQuestion)) {
            throw new IllegalArgumentException("record: " + record + " (expected: " + StringUtil.simpleClassName(DnsQuestion.class) + ')');
        }
        return record;
    }

    private static <T extends DnsRecord> T castRecord(Object record) {
        return (T)((DnsRecord)record);
    }

    private static ArrayList<DnsRecord> newRecordList() {
        return new ArrayList<DnsRecord>(2);
    }
}

