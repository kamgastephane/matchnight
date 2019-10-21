package com.inetti.matchnight.data;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Objects;
import java.util.Optional;

public class OffsetLimitRequest implements Pageable {

    private static final Integer LIMIT_DEFAULT = 20;
    private static final Integer LIMIT_MAX = 200;

    private final Long offset;
    private final Integer limit;
    private final Sort sort;

    private OffsetLimitRequest(Long offset, Integer limit, Sort sort) {

        this.offset = Optional.ofNullable(offset).orElse(0L);
        this.limit = Math.min(Optional.ofNullable(limit).orElse(LIMIT_DEFAULT), LIMIT_MAX);
        this.sort = sort;

    }

    public static OffsetLimitRequest of(Sort sort) {
        return new OffsetLimitRequest(null, null, Objects.requireNonNull(sort));
    }

    public static OffsetLimitRequest of(Long offset, Integer limit, Sort sort) {
        return new OffsetLimitRequest(offset, limit, Objects.requireNonNull(sort));
    }

    @Override
    public int getPageNumber() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getPageSize() {
        return limit;
    }

    @Override
    public long getOffset() {
        return offset;
    }

    @Override
    public Sort getSort() {
        return sort;
    }

    @Override
    public Pageable next() {
        return new OffsetLimitRequest(offset + limit, limit, sort);
    }

    @Override
    public Pageable previousOrFirst() {
        if (this.offset - this.limit <= 0) {
            return new OffsetLimitRequest(0L, this.limit, this.sort);
        } else {
            return new OffsetLimitRequest(this.offset - this.limit, this.limit, this.sort);
        }
    }

    @Override
    public Pageable first() {
        return new OffsetLimitRequest(0L, limit, sort);
    }

    @Override
    public boolean hasPrevious() {
        return offset > 0;
    }

    @Override
    public String toString() {
        return "OffsetLimitRequest{" +
                "offset=" + offset +
                ", limit=" + limit +
                ", sort=" + sort +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OffsetLimitRequest that = (OffsetLimitRequest) o;
        return Objects.equals(offset, that.offset) &&
                Objects.equals(limit, that.limit) &&
                Objects.equals(sort, that.sort);
    }

    @Override
    public int hashCode() {
        return Objects.hash(offset, limit, sort);
    }
}
