package com.usian.controller;

import com.usian.feign.ContentServiceFeign;
import com.usian.pojo.TbContentCategory;
import com.usian.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/backend/content")
public class ContentCategoryController {

    @Autowired
    private ContentServiceFeign contentServiceFeign;

    /**
     * 根据当前节点 ID 查询子节点
     */
    @RequestMapping("/selectContentCategoryByParentId")
    public Result selectContentCategoryByParentId(@RequestParam(defaultValue = "0")
                                                          Long id) {
        List<TbContentCategory> list =
                contentServiceFeign.selectContentCategoryByParentId(id);
        if (list != null && list.size() > 0) {
            return Result.ok(list);
        }
        return Result.error("查无结果");
    }

    /**
     * 添加内容分类
     */
    @RequestMapping("/insertContentCategory")
    public Result insertContentCategory(TbContentCategory tbContentCategory){
        Integer contentCategoryNum =
                contentServiceFeign.insertContentCategory(tbContentCategory);
        if(contentCategoryNum==1){
            return Result.ok();
        }
        return Result.error("添加失败");
    }

    /**
     * 删除内容分类
     *
     */
    @RequestMapping("/deleteContentCategoryById")
    public Result deleteContentCategoryById(Long categoryId){
        Integer status = contentServiceFeign.deleteContentCategoryById(categoryId);
        if(status == 200){
            return Result.ok();
        }
        return Result.error("删除失败");
    }
    /**
     * 修改内容分类
     */
    @RequestMapping("/updateContentCategory")
    public Result updateContentCategory(TbContentCategory tbContentCategory){
        Integer updateContentCategory =
                contentServiceFeign.updateContentCategory(tbContentCategory);
        if(updateContentCategory==1){
            return Result.ok();
        }
        return Result.error("修改失败");
    }
}
