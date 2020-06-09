package com.usian.feign;

import com.usian.pojo.TbUser;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient("usian-sso-service")
public interface SSOServiceFeign {

    //用户注册信息校验
    @RequestMapping("/service/sso/checkUserInfo/{checkValue}/{checkFlag}")
    public boolean checkUserInfo(@PathVariable String checkValue, @PathVariable Integer checkFlag);

    //用户注册
    @RequestMapping("/service/sso/userRegister")
    Integer userRegister(@RequestBody TbUser user);

    //用户登录
    @RequestMapping("/service/sso/userLogin")
    Map userLogin(@RequestParam String username, @RequestParam String password);

    //查询用户token
    @RequestMapping("/service/sso/getUserByToken/{token}")
    TbUser getUserByToken(@PathVariable String token);

    @RequestMapping("/service/sso/logOut")
    Boolean logOut(@RequestParam String token);
}
