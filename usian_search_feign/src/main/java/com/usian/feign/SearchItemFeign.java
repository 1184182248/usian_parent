package com.usian.feign;

import com.usian.pojo.SearchItem;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient("usian-search-service")
public interface SearchItemFeign {
    //商品导入
    @RequestMapping("/service/searchItem/importAll")
    Boolean importAll();
    //商品展示
    @RequestMapping("/service/searchItem/list")
    List<SearchItem> selectByq(@RequestParam String q, @RequestParam Long page, @RequestParam Integer pageSize);
}
