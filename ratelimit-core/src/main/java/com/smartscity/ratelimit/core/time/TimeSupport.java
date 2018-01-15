package com.smartscity.ratelimit.core.time;

import reactor.core.publisher.Mono;

import java.util.concurrent.CompletionStage;

/**
 * <B>文件名称：</B>LimitRule<BR>
 * <B>文件描述：</B>LimitRule is  <BR>
 * <BR>
 * <B>版权声明：</B>(C)2016-2018<BR>
 * <B>公司部门：</B>SMARTSCITY Technology<BR>
 * <B>创建时间：</B>2018/1/12 上午10:30<BR>
 *
 * @author liyunlong  lyl2008dsg@163.com
 * @version 1.0
 **/
public interface TimeSupport {

    long get();

    Mono<Long> getReactive();

}
