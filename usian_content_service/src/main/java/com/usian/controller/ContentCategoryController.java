package com.usian.controller;

import com.usian.pojo.TbContentCategory;
import com.usian.service.ContentCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/service/contentCategory")
public class ContentCategoryController {
    @Autowired
    private ContentCategoryService contentCategoryService;

    /**
     * 根据父节点 ID 查询子节点
     */
    @RequestMapping("/selectContentCategoryByParentId")
    public List<TbContentCategory>
    selectContentCategoryByParentId(@RequestParam Long parentId) {
        return this.contentCategoryService.selectContentCategoryByParentId(parentId);
    }
}
