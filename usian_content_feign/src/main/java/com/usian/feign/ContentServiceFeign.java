package com.usian.feign;

import com.usian.pojo.TbContentCategory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(value = "usian-content-service")
public interface ContentServiceFeign {

    @PostMapping("/service/contentCategory/selectContentCategoryByParentId")
    List<TbContentCategory> selectContentCategoryByParentId(@RequestParam("parentId") 
                                                            			Long parentId);
}
