package com.usian.service;

import com.usian.mapper.TbUserMapper;
import com.usian.pojo.TbUser;
import com.usian.pojo.TbUserExample;
import com.usian.redis.RedisClient;
import com.usian.utils.MD5Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 用户注册与登录业务层
 */
@Service
@Transactional
public class SSOServiceImpl implements SSOService {
    @Autowired
    private TbUserMapper tbUserMapper;

    @Autowired
    private RedisClient redisClient;

    @Value("${USER_INFO}")
    private String USER_INFO;

    @Value("${SESSION_EXPIRE}")
    private Long SESSION_EXPIRE;

    /**
     * 对用户的注册信息做校验
     * @param checkValue
     * @param checkFlag
     * @return
     */
    @Override
    public boolean checkUserInfo(String checkValue, Integer checkFlag) {
        TbUserExample example = new TbUserExample();
        TbUserExample.Criteria criteria = example.createCriteria();
        // 查询条件根据参数动态生成
        if (checkFlag == 1) {
            criteria.andUsernameEqualTo(checkValue);
        } else if (checkFlag == 2) {
            criteria.andPhoneEqualTo(checkValue);
        }
        // 2、从tb_user表中查询数据
        List<TbUser> list = tbUserMapper.selectByExample(example);
        // 3、判断查询结果，如果查询到数据返回false。
        if (list == null || list.size() == 0) {
            return true;
        }
        return false;
    }

    /**
     * 用户注册
     * @param user
     * @return
     */
    @Override
    public Integer userRegister(TbUser user) {
        //密码加密
        String pwd = MD5Utils.digest(user.getPassword());
        user.setPassword(pwd);
        user.setCreated(new Date());
        user.setUpdated(new Date());

        return tbUserMapper.insert(user);
    }

    /**
     * 用户登录
     * @param username
     * @param password
     * @return
     */
    @Override
    public Map userLogin(String username, String password) {
        //判断用户名密码是否正确。
        String pwd = MD5Utils.digest(password);
        TbUserExample example = new TbUserExample();
        TbUserExample.Criteria criteria = example.createCriteria();
        criteria.andUsernameEqualTo(username);
        criteria.andPasswordEqualTo(pwd);
        List<TbUser> userList = this.tbUserMapper.selectByExample(example);
        if(userList == null || userList.size() <= 0){
            return null;
        }
        TbUser tbUser = userList.get(0);
        // 登录成功后生成token
        String token = UUID.randomUUID().toString();
        // 把用户信息保存到redis
        tbUser.setPassword(null);
        redisClient.set(USER_INFO + ":" + token, tbUser);
        // 设置过期时间
        redisClient.expire(USER_INFO + ":" + token, SESSION_EXPIRE);

        Map<String,String> map = new HashMap<String,String>();
        map.put("token",token);
        map.put("userid",tbUser.getId().toString());
        map.put("username",tbUser.getUsername());
        return map;
    }

    /**
     * 查询用户登录是否过期
     * @param token
     * @return
     */
    @Override
    public TbUser getUserByToken(String token) {
        TbUser tbUser = (TbUser) redisClient.get(USER_INFO + ":" + token);
        if (tbUser!=null){
            redisClient.expire(USER_INFO + ":" + token,SESSION_EXPIRE);
            return tbUser;
        }
        return null;
    }

    /**
     * 退出登录
     * @param token
     * @return
     */
    @Override
    public Boolean logOut(String token) {
        return redisClient.del(USER_INFO + ":" + token);
    }


}
