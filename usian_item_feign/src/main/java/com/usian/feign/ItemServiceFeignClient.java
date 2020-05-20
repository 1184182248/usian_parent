package com.usian.feign;

import com.usian.pojo.TbItem;
import com.usian.pojo.TbItemCat;
import com.usian.pojo.TbItemParam;
import com.usian.utils.PageResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(value = "usian-item-service")
public interface ItemServiceFeignClient {
    @RequestMapping("/service/item/selectItemInfo")
    TbItem selectItemInfo(@RequestParam("itemId") Long itemId);

    @GetMapping("/service/item/selectTbItemAllByPage")
    PageResult selectTbItemAllByPage(@RequestParam("page") Integer page,
                                     @RequestParam("rows") Long rows);

    @RequestMapping("/service/itemCategory/selectItemCategoryByParentId")
    List<TbItemCat> selectItemCategoryByParentId(@RequestParam Long id);

    @PostMapping("/service/itemParam/selectItemParamByItemCatId")
    TbItemParam selectItemParamByItemCatId(@RequestParam("itemCatId") Long itemCatId);

    @PostMapping("/service/item/insertTbItem")
    Integer insertTbItem(@RequestBody TbItem tbItem, @RequestParam String desc,
                                @RequestParam String itemParams);
    @RequestMapping("/service/itemParam/selectItemParamAll")
    PageResult selectItemParamAll(@RequestParam Integer page, @RequestParam Integer rows);

    @RequestMapping("/service/itemParam/insertItemParam")
    Integer insertItemParam(@RequestParam Long itemCatId, @RequestParam String paramData);

    @RequestMapping("/service/itemParam/deleteItemParamById")
    Integer deleteItemParamById(@RequestParam Long id);
}

