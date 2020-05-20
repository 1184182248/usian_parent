package com.usian.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.usian.mapper.TbItemDescMapper;
import com.usian.mapper.TbItemMapper;
import com.usian.mapper.TbItemParamItemMapper;
import com.usian.pojo.TbItem;
import com.usian.pojo.TbItemDesc;
import com.usian.pojo.TbItemExample;
import com.usian.pojo.TbItemParamItem;
import com.usian.utils.IDUtils;
import com.usian.utils.PageResult;
import com.usian.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@Transactional
public class ItemServiceImpl implements ItemService{
    @Autowired
    private TbItemMapper tbItemMapper;

    @Autowired
    private TbItemDescMapper tbItemDescMapper;

    @Autowired
    private TbItemParamItemMapper tbItemParamItemMapper;


    @Override
    public TbItem selectItemInfo(Long itemId) {
        return tbItemMapper.selectByPrimaryKey(itemId);
    }

    /**
     * 展示商品并分页
     * @param page
     * @param rows
     * @return
     */
    @Override
    public PageResult selectTbItemAllByPage(Integer page, Long rows){
        PageHelper.startPage(page,rows.intValue());
        TbItemExample example = new TbItemExample();
        example.setOrderByClause("updated DESC");
        TbItemExample.Criteria criteria = example.createCriteria();
        criteria.andStatusEqualTo((byte)1);
        List<TbItem> list = tbItemMapper.selectByExample(example);

        PageInfo<TbItem> pageInfo = new PageInfo<TbItem>(list);
        PageResult result = new PageResult();
        result.setPageIndex(page);
        result.setTotalPage(pageInfo.getTotal());
        result.setResult(list);
        return result;
    }
    @Override
    public Integer insertTbItem(TbItem tbItem, String desc, String itemParams){

        //补齐 Tbitem 数据
        Long itemId = IDUtils.genItemId();
        Date date = new Date();
        tbItem.setId(itemId);
        tbItem.setStatus((byte)1);
        tbItem.setUpdated(date);
        tbItem.setCreated(date);
        Integer tbItemNum = tbItemMapper.insertSelective(tbItem);

        //补齐商品描述对象
        TbItemDesc tbItemDesc = new TbItemDesc();
        tbItemDesc.setItemId(itemId);
        tbItemDesc.setItemDesc(desc);
        tbItemDesc.setCreated(date);
        tbItemDesc.setUpdated(date);
        Integer tbitemDescNum = tbItemDescMapper.insertSelective(tbItemDesc);

        //补齐商品规格参数
        TbItemParamItem tbItemParamItem = new TbItemParamItem();
        tbItemParamItem.setItemId(itemId);
        tbItemParamItem.setParamData(itemParams);
        tbItemParamItem.setUpdated(date);
        tbItemParamItem.setCreated(date);
        Integer itemParamItmeNum =
                tbItemParamItemMapper.insertSelective(tbItemParamItem);

            return tbItemNum + tbitemDescNum + itemParamItmeNum;
    }
}
