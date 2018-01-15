package com.smartscity.ratelimit.inmemory.concurrent;

import com.smartscity.ratelimit.core.limiter.concurrent.Baton;
import com.smartscity.ratelimit.core.limiter.concurrent.LimitRule;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * <B>文件名称：</B>InMemoryRequestRateLimiterTest<BR>
 * <B>文件描述：</B><BR>
 * <BR>
 * <B>版权声明：</B>(C)2016-2018<BR>
 * <B>公司部门：</B>SMARTSCITY Technology<BR>
 * <B>创建时间：</B>2018/1/12 上午11:38<BR>
 *
 * @author apple  lyl2008dsg@163.com
 * @version 1.0
 **/


public class InMemoryConcurrentSyncRequestRateLimiterTest {

    @Test
    public void shouldPreventConcurrentRequests() {
        InMemoryRequestRateLimiter limiter = new InMemoryRequestRateLimiter( LimitRule.of(2, TimeUnit.MINUTES, 1) );

        assertThat(limiter.acquire("key").hasAcquired()).isTrue();
        Baton baton1 = limiter.acquire("key");
        Baton baton2 = limiter.acquire("key");

        assertThat(baton1.hasAcquired()).isTrue();
        assertThat(baton2.hasAcquired()).isFalse();
        baton1.release();

        Baton baton3 = limiter.acquire("key");
        assertThat(baton3.hasAcquired()).isTrue();

        Baton baton4 = limiter.acquire("key");
        assertThat(baton4.hasAcquired()).isFalse();
    }

    @Test
    public void shouldPreventConcurrentRequestsWithWeight() {
        InMemoryRequestRateLimiter limiter = new InMemoryRequestRateLimiter(
                LimitRule.of(2, TimeUnit.MINUTES, 1));

        assertThat(limiter.acquire("key", 2).hasAcquired()).isTrue();
        assertThat(limiter.acquire("key").hasAcquired()).isFalse();
    }

    @Test
    public void shouldTimeOutUnclosedBaton() throws Exception {
        InMemoryRequestRateLimiter limiter = new InMemoryRequestRateLimiter(
                LimitRule.of(1, TimeUnit.MILLISECONDS, 500));

        assertThat(limiter.acquire("key").hasAcquired()).isTrue();
        assertThat(limiter.acquire("key").hasAcquired()).isFalse();

        Thread.sleep(1000);

        assertThat(limiter.acquire("key").hasAcquired()).isTrue();
        assertThat(limiter.acquire("key").hasAcquired()).isFalse();
    }

    @Test
    public void shouldDoWork() {
        InMemoryRequestRateLimiter limiter = new InMemoryRequestRateLimiter(
                LimitRule.of(1, TimeUnit.MINUTES, 1));

        Integer result = limiter.acquire("key")
                .get(this::executeSomeMethod)
                .orElseThrow(() -> new RuntimeException("concurrent limit exceeded"));

        assertThat(result).isEqualTo(1);

        assertThat(limiter.acquire("key").hasAcquired()).isTrue();
        assertThat(limiter.acquire("key").hasAcquired()).isFalse();


        limiter.acquire("key").doAction(this::executeSomeMethod);
    }

    @SuppressWarnings("SameReturnValue")
    private Integer executeSomeMethod() {
        return 1;
    }



}
