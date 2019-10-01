package com.inetti.matchnight.data;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import static org.junit.Assert.*;

public class OffsetLimitRequestTest {


    @Test
    public void test() {
        Pageable page = OffsetLimitRequest.of(Sort.by(Sort.Direction.DESC, "name")).first();
        Assert.assertEquals( 0L, page.getOffset());
        Assert.assertEquals(20, page.getPageSize());
        Assert.assertEquals(Sort.by(Sort.Direction.DESC, "name"), page.getSort());

        Pageable p = OffsetLimitRequest.of(23L, 10, Sort.by(Sort.Direction.DESC, "name"));
        Assert.assertEquals(23, p.getOffset());
        Assert.assertEquals(10, p.getPageSize());
        Assert.assertEquals(13, p.previousOrFirst().getOffset());
        Assert.assertEquals(33, p.next().getOffset());
    }

}