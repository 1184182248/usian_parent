package com.usian.service;

import com.usian.pojo.TbItem;
import com.usian.utils.PageResult;

public interface ItemService {
    TbItem selectItemInfo(Long itemId);


    PageResult selectTbItemAllByPage(Integer page, Long rows);

    Integer insertTbItem(TbItem tbItem, String desc, String itemParams);
}
