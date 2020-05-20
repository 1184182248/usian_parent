package com.usian.controller;

import com.usian.pojo.TbItemParam;
import com.usian.service.ItemParamService;
import com.usian.utils.PageResult;
import com.usian.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/service/itemParam")
public class ItemParamController {
    @Autowired
    private ItemParamService itemParamService;

    /**
     * 根据商品分类 ID 查询
     * @param itemCatId
     * @return
     */
    @RequestMapping("/selectItemParamByItemCatId")
    public TbItemParam selectItemParamByItemCatId( Long itemCatId){
        return itemParamService.selectItemParamByItemCatId(itemCatId);
    }

    /**
     * 查询所有商品规格模板
     */
    @RequestMapping("/selectItemParamAll")
    public PageResult selectItemParamAll(Integer
                                                 page, Integer rows){
        return this.itemParamService.selectItemParamAll(page,rows);
    }

    @RequestMapping("/insertItemParam")
    public Integer insertItemParam(Long itemCatId,String paramData){
        return itemParamService.insertItemParam(itemCatId,paramData);
    }
    @RequestMapping("/deleteItemParamById")
    public Integer deleteItemParamById(Long id){
        return itemParamService.deleteItemParamById(id);
    }

}
