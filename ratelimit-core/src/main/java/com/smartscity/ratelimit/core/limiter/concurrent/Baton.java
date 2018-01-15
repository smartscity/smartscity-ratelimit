package com.smartscity.ratelimit.core.limiter.concurrent;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * <B>文件名称：</B><BR>
 * <B>文件描述：</B>The Baton is the command of multithreading scheduling<BR>
 * <BR>
 * <B>版权声明：</B>(C)2016-2018<BR>
 * <B>公司部门：</B>SMARTSCITY Technology<BR>
 * <B>创建时间：</B>2018/1/12<BR>
 *
 * @author 李云龙  lyl2008dsg@163.com
 * @version 1.0
 **/


public interface Baton {

    <T> Optional<T> get(Supplier<T> action);

    void release();

    void doAction(Runnable action);

    boolean hasAcquired();
}
