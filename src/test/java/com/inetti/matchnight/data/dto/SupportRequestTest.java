package com.inetti.matchnight.data.dto;

import com.inetti.matchnight.data.model.SupportRequest;
import org.junit.Assert;
import org.junit.Test;

public class SupportRequestTest {

    @Test
    public void of() {
        SupportRequest request = SupportRequest.of("projectId", null, SupportRequest.Location.ON_CALL, SupportRequest.ResponseTime.BUSINESS_HOURS,
                SupportRequest.Duration.H4, null);
        Assert.assertNotNull(request);

    }

    @Test(expected = NullPointerException.class)
    public void testNullProjectId() {
        SupportRequest.of(null, null, SupportRequest.Location.ON_CALL, SupportRequest.ResponseTime.BUSINESS_HOURS, SupportRequest.Duration.H4, null);
    }

    @Test(expected = NullPointerException.class)
    public void testNullLocation() {
        SupportRequest.of("projectId", null, null, SupportRequest.ResponseTime.BUSINESS_HOURS, SupportRequest.Duration.H4, null);
    }

    @Test(expected = NullPointerException.class)
    public void testNullResponseTime() {
        SupportRequest.of("projectId", null, SupportRequest.Location.ON_CALL, null, SupportRequest.Duration.H4, null);
    }

    @Test(expected = NullPointerException.class)
    public void testNullDuration(){
        SupportRequest.of("projectId", null, SupportRequest.Location.ON_CALL, SupportRequest.ResponseTime.BUSINESS_HOURS, null, null);
    }



    @Test
    public void testDurationConverter() {
        Assert.assertNull(SupportRequest.Duration.withCode(null));
        Assert.assertEquals(SupportRequest.Duration.H4, SupportRequest.Duration.withCode("h4"));
        Assert.assertEquals(SupportRequest.Duration.H8, SupportRequest.Duration.withCode("h8"));
        Assert.assertEquals(SupportRequest.Duration.H16, SupportRequest.Duration.withCode("h16"));
        Assert.assertEquals(SupportRequest.Duration.H24, SupportRequest.Duration.withCode("h24"));

    }

    @Test
    public void testResponseTimeConverter() {
        Assert.assertNull(SupportRequest.ResponseTime.of(null));
        Assert.assertEquals(SupportRequest.ResponseTime.IMMEDIATE, SupportRequest.ResponseTime.of(0));
        Assert.assertEquals(SupportRequest.ResponseTime.ONE_HOUR, SupportRequest.ResponseTime.of(1));
        Assert.assertEquals(SupportRequest.ResponseTime.TWO_HOURS, SupportRequest.ResponseTime.of(2));
        Assert.assertEquals(SupportRequest.ResponseTime.BUSINESS_HOURS, SupportRequest.ResponseTime.of(3));

    }

    @Test
    public void testLocationConverter() {
        Assert.assertNull(SupportRequest.Location.of(null));
        Assert.assertEquals(SupportRequest.Location.ON_CALL, SupportRequest.Location.of(1));
        Assert.assertEquals(SupportRequest.Location.ON_SITE, SupportRequest.Location.of(2));

    }
}