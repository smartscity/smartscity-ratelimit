package com.smartscity.ratelimit.core.limiter.concurrent;

/**
 * <B>文件名称：</B>RequestLimiter<BR>
 * <B>文件描述：</B><BR>
 * <BR>
 * <B>版权声明：</B>(C)2016-2018<BR>
 * <B>公司部门：</B>SMARTSCITY Technology<BR>
 * <B>创建时间：</B>2018/1/12 上午10:55<BR>
 *
 * @author apple  lyl2008dsg@163.com
 * @version 1.0
 **/


public interface RequestLimiter {

    Baton acquire(String key);

    Baton acquire(String key, int weight);

}
