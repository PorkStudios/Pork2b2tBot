/*
 * Decompiled with CFR 0_132.
 */
package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableTable;
import com.google.common.collect.Maps;
import com.google.common.collect.RegularImmutableTable;
import com.google.common.collect.Table;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import javax.annotation.concurrent.Immutable;

@GwtCompatible
@Immutable
final class SparseImmutableTable<R, C, V>
extends RegularImmutableTable<R, C, V> {
    static final ImmutableTable<Object, Object, Object> EMPTY = new SparseImmutableTable<Object, Object, Object>(ImmutableList.of(), ImmutableSet.of(), ImmutableSet.of());
    private final ImmutableMap<R, Map<C, V>> rowMap;
    private final ImmutableMap<C, Map<R, V>> columnMap;
    private final int[] cellRowIndices;
    private final int[] cellColumnInRowIndices;

    SparseImmutableTable(ImmutableList<Table.Cell<R, C, V>> cellList, ImmutableSet<R> rowSpace, ImmutableSet<C> columnSpace) {
        ImmutableMap<R, Integer> rowIndex = Maps.indexMap(rowSpace);
        LinkedHashMap rows = Maps.newLinkedHashMap();
        for (Object row : rowSpace) {
            rows.put(row, new LinkedHashMap());
        }
        LinkedHashMap columns = Maps.newLinkedHashMap();
        for (Object col : columnSpace) {
            columns.put(col, new LinkedHashMap());
        }
        int[] cellRowIndices = new int[cellList.size()];
        int[] cellColumnInRowIndices = new int[cellList.size()];
        for (int i = 0; i < cellList.size(); ++i) {
            Table.Cell<R, C, V> cell = cellList.get(i);
            R rowKey = cell.getRowKey();
            C columnKey = cell.getColumnKey();
            V value = cell.getValue();
            cellRowIndices[i] = rowIndex.get(rowKey);
            Map thisRow = (Map)rows.get(rowKey);
            cellColumnInRowIndices[i] = thisRow.size();
            V oldValue = thisRow.put(columnKey, value);
            if (oldValue != null) {
                throw new IllegalArgumentException("Duplicate value for row=" + rowKey + ", column=" + columnKey + ": " + value + ", " + oldValue);
            }
            ((Map)columns.get(columnKey)).put(rowKey, value);
        }
        this.cellRowIndices = cellRowIndices;
        this.cellColumnInRowIndices = cellColumnInRowIndices;
        ImmutableMap.Builder rowBuilder = new ImmutableMap.Builder(rows.size());
        for (Map.Entry row : rows.entrySet()) {
            rowBuilder.put(row.getKey(), ImmutableMap.copyOf((Map)row.getValue()));
        }
        this.rowMap = rowBuilder.build();
        ImmutableMap.Builder columnBuilder = new ImmutableMap.Builder(columns.size());
        for (Map.Entry col : columns.entrySet()) {
            columnBuilder.put(col.getKey(), ImmutableMap.copyOf((Map)col.getValue()));
        }
        this.columnMap = columnBuilder.build();
    }

    @Override
    public ImmutableMap<C, Map<R, V>> columnMap() {
        return this.columnMap;
    }

    @Override
    public ImmutableMap<R, Map<C, V>> rowMap() {
        return this.rowMap;
    }

    @Override
    public int size() {
        return this.cellRowIndices.length;
    }

    @Override
    Table.Cell<R, C, V> getCell(int index) {
        int rowIndex = this.cellRowIndices[index];
        Map.Entry rowEntry = (Map.Entry)this.rowMap.entrySet().asList().get(rowIndex);
        ImmutableMap row = (ImmutableMap)rowEntry.getValue();
        int columnIndex = this.cellColumnInRowIndices[index];
        Map.Entry colEntry = (Map.Entry)row.entrySet().asList().get(columnIndex);
        return SparseImmutableTable.cellOf(rowEntry.getKey(), colEntry.getKey(), colEntry.getValue());
    }

    @Override
    V getValue(int index) {
        int rowIndex = this.cellRowIndices[index];
        ImmutableMap row = (ImmutableMap)this.rowMap.values().asList().get(rowIndex);
        int columnIndex = this.cellColumnInRowIndices[index];
        return (V)row.values().asList().get(columnIndex);
    }

    @Override
    ImmutableTable.SerializedForm createSerializedForm() {
        ImmutableMap columnKeyToIndex = Maps.indexMap(this.columnKeySet());
        int[] cellColumnIndices = new int[this.cellSet().size()];
        int i = 0;
        for (Table.Cell cell : this.cellSet()) {
            cellColumnIndices[i++] = columnKeyToIndex.get(cell.getColumnKey());
        }
        return ImmutableTable.SerializedForm.create(this, this.cellRowIndices, cellColumnIndices);
    }
}

