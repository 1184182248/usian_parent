package com.usian.controller;

import com.usian.pojo.SearchItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.usian.feign.SearchItemFeign;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/frontend/searchItem")
public class SearchItemController {

    @Autowired
    private SearchItemFeign searchItemFeign;

    @RequestMapping("/importAll")
    public boolean importAll(){
        return searchItemFeign.importAll();
    }

    @RequestMapping("/list")
    public List<SearchItem> selectByq(String q, @RequestParam(defaultValue = "1") Long page,
                                      @RequestParam(defaultValue = "20") Integer pageSize) throws IOException {
        return searchItemFeign.selectByq(q,page,pageSize);
    }
}
