package org.dataspread.sheetanalyzer.dependency.util;

import java.util.Objects;

public class Offset {

    public static final Offset noOffset = new Offset(0, 0);
    private final int rowOffset;
    private final int colOffset;

    public Offset(int rowOffset, int colOffset) {
        this.rowOffset = rowOffset;
        this.colOffset = colOffset;
    }

    public int getColOffset() {
        return this.colOffset;
    }

    public int getRowOffset() {
        return this.rowOffset;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Offset)) {
            return false;
        }
        Offset offset = (Offset) o;
        return this.rowOffset == offset.rowOffset
                && this.colOffset == offset.colOffset;
    }

    @Override
    public int hashCode() {
        return Objects.hash(rowOffset, colOffset);
    }
}
