package com.usian.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.usian.mapper.*;
import com.usian.pojo.*;
import com.usian.redis.RedisClient;
import com.usian.utils.IDUtils;
import com.usian.utils.PageResult;
import com.usian.utils.Result;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class ItemServiceImpl implements ItemService{
    @Autowired
    private TbItemMapper tbItemMapper;

    @Autowired
    private TbItemCatMapper tbItemCatMapper;

    @Autowired
    private TbItemDescMapper tbItemDescMapper;

    @Autowired
    private TbItemParamItemMapper tbItemParamItemMapper;

    @Autowired
    private TbOrderItemMapper tbOrderItemMapper;

    @Autowired
    private AmqpTemplate amqpTemplate;

    @Value("${ITEM_INFO}")
    private String ITEM_INFO;

    @Value("${BASE}")
    private String BASE;

    @Value("${DESC}")
    private String DESC;

    @Value("${PARAM}")
    private String PARAM;

    @Value("${ITEM_INFO_EXPIRE}")
    private Integer ITEM_INFO_EXPIRE;

    @Value("${SETNX_BASC_LOCK_KEY}")
    private String SETNX_BASC_LOCK_KEY;

    @Value("${SETNX_DESC_LOCK_KEY}")
    private String SETNX_DESC_LOCK_KEY;

    @Autowired
    private RedisClient redisClient;

    @Override
    public TbItem selectItemInfo(Long itemId) {
        //查询缓存
        TbItem tbItem = (TbItem) redisClient.get(ITEM_INFO + ":" + itemId + ":"+ BASE);
        if(tbItem!=null){
            return tbItem;
        }
        //解决缓存击穿
        if(redisClient.setnx(SETNX_BASC_LOCK_KEY+":"+itemId,itemId,30L)){
            //2、再查询数据库,并把查询结果缓存到redis,并设置失效时间
            tbItem = tbItemMapper.selectByPrimaryKey(itemId);

            //解决缓存穿透
            if(tbItem!=null){
                redisClient.set(ITEM_INFO+":"+itemId+":"+BASE,tbItem);
                redisClient.expire(ITEM_INFO+":"+itemId+":"+BASE,ITEM_INFO_EXPIRE);
            }else{
                redisClient.set(ITEM_INFO+":"+itemId+":"+BASE,null);
                redisClient.expire(ITEM_INFO+":"+itemId+":"+BASE,30L);
            }
            redisClient.del(SETNX_BASC_LOCK_KEY+":"+itemId);
            return tbItem;
        }else{
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return selectItemInfo(itemId);
        }
    }
    /**
     * 根据商品 ID 查询商品描述
     * @param itemId
     * @return
     */
    @Override
    public TbItemDesc selectItemDescByItemId(Long itemId) {
        //查询缓存
        TbItemDesc tbItemDesc = (TbItemDesc) redisClient.get(
                ITEM_INFO + ":" + itemId + ":"+ DESC);
        if(tbItemDesc!=null){
            return tbItemDesc;
        }

        if(redisClient.setnx(SETNX_DESC_LOCK_KEY+":"+itemId,itemId,30L)){
            //2、再查询mysql,并把查询结果缓存到redis,并设置失效时间
            tbItemDesc = tbItemDescMapper.selectByPrimaryKey(itemId);

            if(tbItemDesc!=null){
                redisClient.set(ITEM_INFO + ":" + itemId + ":" + DESC,tbItemDesc);
                redisClient.expire(ITEM_INFO + ":" + itemId + ":" +
                        DESC,ITEM_INFO_EXPIRE);

            }else{
                redisClient.set(ITEM_INFO + ":" + itemId + ":" + DESC,null);
                redisClient.expire(ITEM_INFO + ":" + itemId + ":" + DESC,30L);
            }
            redisClient.del(SETNX_DESC_LOCK_KEY+":"+itemId);
            return tbItemDesc;
        }else{
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return selectItemDescByItemId(itemId);
        }
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
        Integer itemParamItmeNum = tbItemParamItemMapper.insertSelective(tbItemParamItem);

        //添加商品发布消息到mq
        amqpTemplate.convertAndSend("item_exchage","item.add", itemId);
            return tbItemNum + tbitemDescNum + itemParamItmeNum;
    }
    /**
     * 根据id删除商品
     * @param itemId
     * @return
     */
    @Override
    public Integer deleteItemById(Long itemId) {
        //删除redis缓存
        redisClient.del(ITEM_INFO+":"+itemId+":"+BASE);
        redisClient.del(ITEM_INFO+":"+itemId+":"+DESC);
        redisClient.del(ITEM_INFO+":"+itemId+":"+PARAM);

        return tbItemMapper.deleteByPrimaryKey(itemId);

    }


    /**
     * 修改商品回显
     * @param itemId
     * @return
     */
    @Override
    public Map<String, Object> preUpdateItem(Long itemId) {
        Map<String, Object> map = new HashMap<>();
        //根据商品 ID 查询商品
        TbItem item = this.tbItemMapper.selectByPrimaryKey(itemId);
        map.put("item", item);
        //根据商品 ID 查询商品描述
        TbItemDesc itemDesc = this.tbItemDescMapper.selectByPrimaryKey(itemId);
        map.put("itemDesc", itemDesc.getItemDesc());
        //根据商品 ID 查询商品类目
        TbItemCat itemCat = this.tbItemCatMapper.selectByPrimaryKey(item.getCid());
        map.put("itemCat", itemCat.getName());
        //根据商品 ID 查询商品规格参数
        TbItemParamItemExample example = new TbItemParamItemExample();
        TbItemParamItemExample.Criteria criteria = example.createCriteria();
        criteria.andItemIdEqualTo(itemId);
        List<TbItemParamItem> list =
                this.tbItemParamItemMapper.selectByExampleWithBLOBs(example);
        if (list != null && list.size() > 0) {
            map.put("itemParamItem", list.get(0).getParamData());
        }
        return map;
    }

    @Override
    public Integer updateTbItem(TbItem tbItem, String desc, String itemParams) {
        //补齐 Tbitem 数据
        Date date = new Date();
        tbItem.setCreated(date);
        tbItem.setPrice(tbItem.getPrice()*100);
        Integer tbItemNum = tbItemMapper.updateByPrimaryKeySelective(tbItem);

        //补齐商品描述对象
        TbItemDesc tbItemDesc = new TbItemDesc();
        tbItemDesc.setItemId(tbItem.getId());
        tbItemDesc.setItemDesc(desc);
        tbItemDesc.setUpdated(date);
        Integer tbitemDescNum = tbItemDescMapper.updateByPrimaryKeySelective(tbItemDesc);

        //补齐商品规格参数
       TbItemParamItemExample tbItemParamItemExample = new TbItemParamItemExample();
        TbItemParamItemExample.Criteria criteria = tbItemParamItemExample.createCriteria();
        criteria.andItemIdEqualTo(tbItem.getId());
        List<TbItemParamItem> tbItemParamItems=tbItemParamItemMapper.selectByExampleWithBLOBs(tbItemParamItemExample);
        TbItemParamItem tbItemParamItem = tbItemParamItems.get(0);
        tbItemParamItem.setParamData(itemParams);
        tbItemParamItem.setUpdated(date);
        int itemParamItmeNum = tbItemParamItemMapper.updateByPrimaryKeySelective(tbItemParamItem);
        return tbItemNum + tbitemDescNum + itemParamItmeNum;

    }

    /**
     * 修改商品库存数量
     * @param orderId
     * @return
     */
    @Override
    public Integer updateTbItemByOrderId(String orderId) {
        TbOrderItemExample tbOrderItemExample = new TbOrderItemExample();
        TbOrderItemExample.Criteria criteria = tbOrderItemExample.createCriteria();
        criteria.andOrderIdEqualTo(orderId);
        List<TbOrderItem> tbOrderItemList = tbOrderItemMapper.selectByExample(tbOrderItemExample);
        int result = 0;
        for (int i = 0; i < tbOrderItemList.size(); i++) {
            TbOrderItem tbOrderItem =  tbOrderItemList.get(i);
            TbItem tbItem = tbItemMapper.selectByPrimaryKey(Long.valueOf(tbOrderItem.getItemId()));
            tbItem.setNum(tbItem.getNum()-tbOrderItem.getNum());
            result += tbItemMapper.updateByPrimaryKeySelective(tbItem);
        }
        return result;
    }

}
