package com.usian.service;

import com.usian.pojo.TbItemCat;

import java.util.List;

public interface ItemCategoryService {

    List<TbItemCat> selectItemCategoryByParentId(Long id);
}
