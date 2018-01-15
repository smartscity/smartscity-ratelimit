package com.smartscity.ratelimit.core.limiter.request;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * <B>文件名称：</B>RequestLimitRule<BR>
 * <B>文件描述：</B><BR>
 * <BR>
 * <B>版权声明：</B>(C)2016-2018<BR>
 * <B>公司部门：</B>SMARTSCITY Technology<BR>
 * <B>创建时间：</B>2018/1/12 上午10:57<BR>
 *
 * @author apple  lyl2008dsg@163.com
 * @version 1.0
 **/


public class RequestLimitRule {

    private final int durationSeconds;
    private final long limit;
    private final int precision;
    private final String name;

    private RequestLimitRule(int durationSeconds, long limit, int precision) {
        this(durationSeconds, limit, precision, null);
    }

    private RequestLimitRule(int durationSeconds, long limit, int precision, String name) {
        this.durationSeconds = durationSeconds;
        this.limit = limit;
        this.precision = precision;
        this.name = name;
    }

    public static RequestLimitRule of(int duration, TimeUnit timeUnit, int limit) {
        int durationSeconds = (int) timeUnit.toSeconds(duration);
        return new RequestLimitRule(durationSeconds, limit, durationSeconds);
    }

    public int getDurationSeconds() {
        return durationSeconds;
    }

    public long getLimit() {
        return limit;
    }

    public int getPrecision() {
        return precision;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RequestLimitRule that = (RequestLimitRule) o;
        return durationSeconds == that.durationSeconds
                && limit == that.limit
                && Objects.equals(precision, that.precision)
                && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(durationSeconds, limit, precision, name);
    }


    public RequestLimitRule withPrecision(int precision) {
        return new RequestLimitRule(this.durationSeconds, this.limit, precision, this.name);
    }

    public RequestLimitRule withName(String name) {
        return new RequestLimitRule(this.durationSeconds, this.limit, this.precision, name);
    }
}
