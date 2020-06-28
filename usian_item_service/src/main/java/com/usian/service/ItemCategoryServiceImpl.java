package com.usian.service;

import com.usian.mapper.TbItemCatMapper;
import com.usian.pojo.TbItemCat;
import com.usian.pojo.TbItemCatExample;
import com.usian.redis.RedisClient;
import com.usian.utils.CatNode;
import com.usian.utils.CatResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ItemCategoryServiceImpl implements ItemCategoryService{
    @Autowired
    private TbItemCatMapper tbItemCatMapper;

    @Autowired
    private RedisClient redisClient;

    @Value("${portal_catresult_redis_key}")
    private String portal_catresult_redis_key;

    /**
     * 根据分类ID查询子节点
     * @param id
     * @return
     */
    @Override
    public List<TbItemCat> selectItemCategoryByParentId(Long id) {
        TbItemCatExample example = new TbItemCatExample();
        TbItemCatExample.Criteria criteria = example.createCriteria();
        criteria.andParentIdEqualTo(id);
        criteria.andStatusEqualTo(1);
        List<TbItemCat> list = tbItemCatMapper.selectByExample(example);
        if (id!=0){
            int a = 6 / 0;
        }
        return list;
    }
    /**
     * 查询首页商品分类
     * @return
     */
    @Override
    public CatResult selectItemCategoryAll() {
        //先查询缓存
        CatResult catResultRedis = (CatResult)redisClient.get(portal_catresult_redis_key);
        if(catResultRedis!=null){
            //如果redis有，直接return
            System.out.println("=====从Redis获取=====");
            return catResultRedis;
        }

        //如果redis没有，则查询数据库并把结果放到redis中
        List catlist = getCatList(0L);
        CatResult catResult = new CatResult();
        catResult.setData(catlist);
        redisClient.set(portal_catresult_redis_key,catResult);
        System.out.println("=====从后台查询====");
        return catResult;
    }

    /**
     * 私有方法，查询商品分类
     */
    private List<?> getCatList(Long parentId){
        //创建查询条件
        TbItemCatExample example = new TbItemCatExample();
        TbItemCatExample.Criteria criteria = example.createCriteria();
        criteria.andParentIdEqualTo(parentId);
        List<TbItemCat> list = this.tbItemCatMapper.selectByExample(example);
        List resultList = new ArrayList();
        int count = 0;
        for(int i = 0;i<list.size();i++){
            TbItemCat tbItemCat = list.get(i);
            //判断是否是父节点
            if(tbItemCat.getIsParent()){
                CatNode catNode = new CatNode();
                catNode.setName(tbItemCat.getName());
                catNode.setItem(getCatList(tbItemCat.getId()));
                resultList.add(catNode);
                count++;

                if (count == 18){
                    break;
                }
            }else{
                //若没有子节点
                resultList.add(tbItemCat.getName());
            }
        }
        return resultList;
    }
}
