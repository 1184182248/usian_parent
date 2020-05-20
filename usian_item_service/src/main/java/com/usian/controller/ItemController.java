package com.usian.controller;

import com.usian.pojo.TbItem;
import com.usian.service.ItemService;
import com.usian.utils.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/service/item")
public class ItemController {
    @Autowired
    private ItemService itemService;


    @RequestMapping("/selectItemInfo")
    public TbItem selectItemInfo(Long itemId){
        return this.itemService.selectItemInfo(itemId);
    }

    /**
     * 商品列表查询
     * @param page
     * @param rows
     * @return
     */
    @RequestMapping(value = "/selectTbItemAllByPage", method = RequestMethod.GET)
    public PageResult selectTbItemAllByPage(Integer page,Long rows){
        return itemService.selectTbItemAllByPage(page,rows);
    }

    /**
     * 商品的添加
     */
    @RequestMapping("/insertTbItem")
    public Integer insertTbItem(@RequestBody TbItem tbItem,String desc,
                                String itemParams){
        return this.itemService.insertTbItem(tbItem,desc,itemParams);
    }
}
