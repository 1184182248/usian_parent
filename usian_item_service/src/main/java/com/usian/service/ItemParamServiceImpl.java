package com.usian.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.usian.mapper.TbItemParamMapper;
import com.usian.pojo.TbItem;
import com.usian.pojo.TbItemParam;
import com.usian.pojo.TbItemParamExample;
import com.usian.utils.PageResult;
import com.usian.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
@Service
public class ItemParamServiceImpl implements ItemParamService {
    @Autowired
    private TbItemParamMapper tbItemParamMapper;

    @Override
    public TbItemParam selectItemParamByItemCatId(Long itemCatId){
        TbItemParamExample example = new TbItemParamExample();
        TbItemParamExample.Criteria criteria = example.createCriteria();
        criteria.andItemCatIdEqualTo(itemCatId);
        List<TbItemParam> list = tbItemParamMapper.selectByExampleWithBLOBs(example);
        if (list != null && list.size() >0){
            return list.get(0);
        }
            return null;
    }
    /**
     * 查询所有商品规格模板
     * @param page
     * @param rows
     * @return
     */
    @Override
    public PageResult selectItemParamAll(Integer page, Integer rows) {
        PageHelper.startPage (page,rows);
        TbItemParamExample example = new TbItemParamExample();
        example.setOrderByClause("updated DESC");
        List<TbItemParam> list =
                this.tbItemParamMapper.selectByExampleWithBLOBs(example);
        PageInfo<TbItemParam> pageInfo = new PageInfo<>(list);
        PageResult pageResult = new PageResult();
        pageResult.setPageIndex(page);
        pageResult.setResult(pageInfo.getList());
        pageResult.setTotalPage(Long.valueOf(pageInfo.getPages()));
        return pageResult;
    }

    @Override
    public Integer insertItemParam(Long itemCatId, String paramData) {
        //判断该类别的商品是否有规格模板
        TbItemParamExample tbItemParamExample  = new TbItemParamExample();
        TbItemParamExample.Criteria criteria = tbItemParamExample.createCriteria();
        criteria.andItemCatIdEqualTo(itemCatId);
        List<TbItemParam> itemParamList = tbItemParamMapper.selectByExample(tbItemParamExample);
        if (itemParamList.size()>0){
            return 0;
        }

        //保存规格模板
        Date date = new Date();
        TbItemParam tbItemParam = new TbItemParam();
        tbItemParam.setItemCatId(itemCatId);
        tbItemParam.setParamData(paramData);
        tbItemParam.setUpdated(date);
        tbItemParam.setCreated(date);
        return tbItemParamMapper.insertSelective(tbItemParam);
    }

    @Override
    public Integer deleteItemParamById(Long id) {
        return  tbItemParamMapper.deleteByPrimaryKey(id);

    }
}
