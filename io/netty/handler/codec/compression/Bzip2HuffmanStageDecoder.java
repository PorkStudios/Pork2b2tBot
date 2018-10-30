/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.compression;

import io.netty.handler.codec.compression.Bzip2BitReader;
import io.netty.handler.codec.compression.Bzip2MoveToFrontTable;
import io.netty.handler.codec.compression.DecompressionException;

final class Bzip2HuffmanStageDecoder {
    private final Bzip2BitReader reader;
    byte[] selectors;
    private final int[] minimumLengths;
    private final int[][] codeBases;
    private final int[][] codeLimits;
    private final int[][] codeSymbols;
    private int currentTable;
    private int groupIndex = -1;
    private int groupPosition = -1;
    final int totalTables;
    final int alphabetSize;
    final Bzip2MoveToFrontTable tableMTF = new Bzip2MoveToFrontTable();
    int currentSelector;
    final byte[][] tableCodeLengths;
    int currentGroup;
    int currentLength = -1;
    int currentAlpha;
    boolean modifyLength;

    Bzip2HuffmanStageDecoder(Bzip2BitReader reader, int totalTables, int alphabetSize) {
        this.reader = reader;
        this.totalTables = totalTables;
        this.alphabetSize = alphabetSize;
        this.minimumLengths = new int[totalTables];
        this.codeBases = new int[totalTables][25];
        this.codeLimits = new int[totalTables][24];
        this.codeSymbols = new int[totalTables][258];
        this.tableCodeLengths = new byte[totalTables][258];
    }

    void createHuffmanDecodingTables() {
        int alphabetSize = this.alphabetSize;
        for (int table = 0; table < this.tableCodeLengths.length; ++table) {
            int i;
            int[] tableBases = this.codeBases[table];
            int[] tableLimits = this.codeLimits[table];
            int[] tableSymbols = this.codeSymbols[table];
            byte[] codeLengths = this.tableCodeLengths[table];
            int minimumLength = 23;
            int maximumLength = 0;
            for (i = 0; i < alphabetSize; ++i) {
                byte currLength = codeLengths[i];
                maximumLength = Math.max(currLength, maximumLength);
                minimumLength = Math.min(currLength, minimumLength);
            }
            this.minimumLengths[table] = minimumLength;
            for (i = 0; i < alphabetSize; ++i) {
                int[] arrn = tableBases;
                int n = codeLengths[i] + 1;
                arrn[n] = arrn[n] + 1;
            }
            int b = tableBases[0];
            for (i = 1; i < 25; ++i) {
                tableBases[i] = b += tableBases[i];
            }
            int code = 0;
            for (i = minimumLength; i <= maximumLength; ++i) {
                int base = code;
                tableBases[i] = base - tableBases[i];
                tableLimits[i] = (code += tableBases[i + 1] - tableBases[i]) - 1;
                code <<= 1;
            }
            int codeIndex = 0;
            for (int bitLength = minimumLength; bitLength <= maximumLength; ++bitLength) {
                for (int symbol = 0; symbol < alphabetSize; ++symbol) {
                    if (codeLengths[symbol] != bitLength) continue;
                    tableSymbols[codeIndex++] = symbol;
                }
            }
        }
        this.currentTable = this.selectors[0];
    }

    int nextSymbol() {
        int codeLength;
        if (++this.groupPosition % 50 == 0) {
            ++this.groupIndex;
            if (this.groupIndex == this.selectors.length) {
                throw new DecompressionException("error decoding block");
            }
            this.currentTable = this.selectors[this.groupIndex] & 255;
        }
        Bzip2BitReader reader = this.reader;
        int currentTable = this.currentTable;
        int[] tableLimits = this.codeLimits[currentTable];
        int[] tableBases = this.codeBases[currentTable];
        int[] tableSymbols = this.codeSymbols[currentTable];
        int codeBits = reader.readBits(codeLength);
        for (codeLength = this.minimumLengths[currentTable]; codeLength <= 23; ++codeLength) {
            if (codeBits <= tableLimits[codeLength]) {
                return tableSymbols[codeBits - tableBases[codeLength]];
            }
            codeBits = codeBits << 1 | reader.readBits(1);
        }
        throw new DecompressionException("a valid code was not recognised");
    }
}

