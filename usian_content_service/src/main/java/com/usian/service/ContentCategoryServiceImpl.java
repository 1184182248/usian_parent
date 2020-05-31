package com.usian.service;

import com.usian.mapper.TbContentCategoryMapper;
import com.usian.pojo.TbContentCategory;
import com.usian.pojo.TbContentCategoryExample;
import com.usian.pojo.TbItemParamExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 内容分类业务层
 */
@Service
public class ContentCategoryServiceImpl implements ContentCategoryService {
    @Autowired
    private TbContentCategoryMapper tbContentCategoryMapper;

    /**
     * 根据父节点 Id 查询子节点
     *
     * @param parentId
     * @return
     */
    @Override
    public List<TbContentCategory> selectContentCategoryByParentId(Long parentId) {
        TbContentCategoryExample tbContentCategoryExample = new TbContentCategoryExample();
        TbContentCategoryExample.Criteria criteria = tbContentCategoryExample.createCriteria();
        criteria.andParentIdEqualTo(parentId);
        List<TbContentCategory> list =
                	this.tbContentCategoryMapper.selectByExample(tbContentCategoryExample);
        return list;
    }
    /**
     * 添加内容分类
     *
     * @param tbContentCategory
     * @return
     */
    @Override
    public Integer insertContentCategory(TbContentCategory tbContentCategory) {
        //添加内容分类
        tbContentCategory.setUpdated(new Date());
        tbContentCategory.setCreated(new Date());
        tbContentCategory.setIsParent(false);
        tbContentCategory.setSortOrder(1);
        tbContentCategory.setStatus(1);
        int contentCategoryNum = tbContentCategoryMapper.insert(tbContentCategory);

        //查询当前新节点的父节点
        TbContentCategory contentCategory = tbContentCategoryMapper.selectByPrimaryKey(tbContentCategory.getParentId());
        if (!contentCategory.getIsParent()){
            contentCategory.setIsParent(true);
            contentCategory.setUpdated(new Date());
            tbContentCategoryMapper.updateByPrimaryKey(contentCategory);
        }
        return contentCategoryNum;
    }
    /**
     * 删除内容分类
     * @param categoryId
     * @return
     */
    @Override
    public Integer deleteContentCategoryById(Long categoryId) {
        //查询当前节点
        TbContentCategory tbContentCategory = tbContentCategoryMapper.selectByPrimaryKey(categoryId);

        //不删除父节点
        if (tbContentCategory.getIsParent()==true){
            return 0;
        }
        //不是父节点
        tbContentCategoryMapper.deleteByPrimaryKey(categoryId);

        //不是当前节点
        TbContentCategoryExample tbContentCategoryExample = new TbContentCategoryExample();
        TbContentCategoryExample.Criteria criteria = tbContentCategoryExample.createCriteria();
        criteria.andParentIdEqualTo(tbContentCategory.getParentId());
        List<TbContentCategory> tbContentCategoryList = tbContentCategoryMapper.selectByExample(tbContentCategoryExample);

        //删除之后如果父节点没有子节点，则修改isParent为false
        if (tbContentCategoryList.size()==0){
            TbContentCategory tbContentCategory1 = new TbContentCategory();
            tbContentCategory1.setId(tbContentCategory.getParentId());
            tbContentCategory1.setUpdated(new Date());
            tbContentCategory1.setIsParent(false);
            tbContentCategoryMapper.updateByPrimaryKeySelective(tbContentCategory1);
        }
        return 200;
    }

    /**
     * 修改内容分类
     * @param tbContentCategory
     * @return
     */
    @Override
    public Integer updateContentCategory(TbContentCategory tbContentCategory) {
        tbContentCategory.setUpdated(new Date());
        return tbContentCategoryMapper.updateByPrimaryKeySelective(tbContentCategory);
    }
}
