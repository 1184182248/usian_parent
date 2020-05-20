package com.usian.controller;

import com.usian.pojo.TbItemCat;
import com.usian.service.ItemCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/service/itemCategory")
public class ItemCategoryController {

    @Autowired
    private ItemCategoryService itemCategoryService;

    /**
     * 根据父节点查询子节点
     */
    @RequestMapping("/selectItemCategoryByParentId")
    public List<TbItemCat> selectItemCategoryByParentId(@RequestParam Long
                                                                id) {
        return itemCategoryService.selectItemCategoryByParentId(id);
    }
}
