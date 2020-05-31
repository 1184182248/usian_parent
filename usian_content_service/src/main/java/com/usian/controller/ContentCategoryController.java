package com.usian.controller;

import com.usian.pojo.TbContentCategory;
import com.usian.service.ContentCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
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
    /**
     * 添加内容分类
     */
    @RequestMapping("/insertContentCategory")
    public Integer insertContentCategory(@RequestBody TbContentCategory
                                                 tbContentCategory){
        return this.contentCategoryService.insertContentCategory(tbContentCategory);
    }
    /**
     * 删除内容分类
     */
    @RequestMapping("/deleteContentCategoryById")
    public Integer deleteContentCategoryById(Long categoryId){
        return this.contentCategoryService.deleteContentCategoryById(categoryId);
    }
    /**
     * 修改内容分类
     */
    @RequestMapping("/updateContentCategory")
    public Integer updateContentCategory(@RequestBody TbContentCategory
                                                 tbContentCategory){
        return this.contentCategoryService.updateContentCategory(tbContentCategory);
    }
}
