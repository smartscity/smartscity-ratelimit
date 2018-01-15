package com.smartscity.ratelimit.core.limiter.concurrent;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * <B>文件名称：</B>LimitRule<BR>
 * <B>文件描述：</B>LimitRule is  <BR>
 * <BR>
 * <B>版权声明：</B>(C)2016-2018<BR>
 * <B>公司部门：</B>SMARTSCITY Technology<BR>
 * <B>创建时间：</B>2018/1/12 上午10:30<BR>
 *
 * @author apple  lyl2008dsg@163.com
 * @version 1.0
 **/
public class LimitRule {

    private final int       limit;
    private final long      timeoutMillis;
    private final String    name;
    private final TimeUnit  timeUnit;

    private LimitRule(int limit, long timeout) {
        this(null, limit, timeout);
    }

    public LimitRule(int limit, TimeUnit timeUnit) {
        this(null, limit, timeUnit);
    }

    public LimitRule(String name, int limit, long timeout) {
        this(name, limit, TimeUnit.MILLISECONDS.toMillis(timeout), TimeUnit.MILLISECONDS);
    }

    public LimitRule(String name, int limit, TimeUnit timeUnit) {
        this(name, limit, 1L, timeUnit);
    }

    public LimitRule(String name, int limit, long timeout, TimeUnit timeUnit) {
        this.limit      = limit;
        this.timeoutMillis    = timeUnit.toMillis(timeout);
        this.name       = name;
        this.timeUnit   = timeUnit;
    }

    public static LimitRule of(int limit, TimeUnit timeOutUnit, int timeOut) {
        return new LimitRule(limit, timeOutUnit.toMillis(timeOut));
    }

    public long getTimeoutMillis() {
        return timeoutMillis;
    }

    public int getLimit() {
        return limit;
    }


    public String getName() {
        return name;
    }

    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        LimitRule that = (LimitRule) o;
        return limit == that.limit &&
                timeoutMillis == that.timeoutMillis &&
                Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(limit, timeoutMillis, name);
    }


}
