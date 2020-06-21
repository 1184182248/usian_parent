package com.usian.quartz;

import com.usian.mapper.LocalMessageMapper;
import com.usian.mq.MQSender;
import com.usian.pojo.LocalMessage;
import com.usian.pojo.LocalMessageExample;
import com.usian.pojo.TbOrder;
import com.usian.pojo.TbOrderItem;
import com.usian.redis.RedisClient;
import com.usian.service.OrderService;
import jdk.internal.org.objectweb.asm.commons.TryCatchBlockSorter;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.List;

public class OrderQuartz implements Job {

	@Autowired
	private OrderService orderService;

    @Autowired
    private RedisClient redisClient;

    @Autowired
    private LocalMessageMapper localMessageMapper;

    @Autowired
    private MQSender mQSender;

    /**
     * 关闭超时订单
     */
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        //查询超时订单
        List<TbOrder> tbOrderList = orderService.selectOverTimeOrder();

        String ip = null;
        try{
            ip = InetAddress.getLocalHost().getHostAddress();
        }catch (UnknownHostException e){
            e.printStackTrace();
        }

        //解决quartz集群任务重复执行
        if(redisClient.setnx("SETNX_LOCK_ORDER_KEY",ip,30)) {
            // 关闭超时订单业务
            System.out.println("执行检查本地消息表的任务...." + new Date());
            LocalMessageExample localMessageExample = new LocalMessageExample();
            LocalMessageExample.Criteria criteria = localMessageExample.createCriteria();
            criteria.andStateEqualTo(0);
            List<LocalMessage> localMessageList = localMessageMapper.selectByExample(localMessageExample);
            for (LocalMessage localMessage : localMessageList) {
                mQSender.sendMsg(localMessage);
            }
            redisClient.del("SETNX_LOCK_ORDER_KEY"+ip);
        }else{
            System.out.println(
                    "============机器："+ip+" 占用分布式锁，任务正在执行=======================");
        }

    }
}
