package com.usian.service;

import com.usian.pojo.OrderInfo;
import com.usian.pojo.TbOrder;

import java.util.List;

public interface OrderService {

    Long insertOrder(OrderInfo orderInfo);
    //关闭超时订单
    void updateOverTimeTbOrder(TbOrder tbOrder);

    void updateTbItemByOrderId(String orderId);
    //查询超时订单
    List<TbOrder> selectOverTimeOrder();

}
