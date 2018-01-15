package com.smartscity.ratelimit.core.limiter.request;

/**
 * <B>文件名称：</B>RequestRateLimiter<BR>
 * <B>文件描述：</B>RequestRateLimiter is  <BR>
 * <BR>
 * <B>版权声明：</B>(C)2016-2018<BR>
 * <B>公司部门：</B>SMARTSCITY Technology<BR>
 * <B>创建时间：</B>2018/1/12 上午10:30<BR>
 *
 * @author apple  lyl2008dsg@163.com
 * @version 1.0
 **/
public interface RequestRateLimiter {

    boolean overLimit(String key);
    boolean overLimit(String key, int weight);

    boolean lessLimit(String key);

    boolean lessLimit(String key, int weight);

    boolean resetLimit(String key);
}
