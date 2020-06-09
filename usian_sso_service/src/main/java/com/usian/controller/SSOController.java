package com.usian.controller;

import com.usian.pojo.TbUser;
import com.usian.service.SSOService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@RequestMapping("/service/sso")
public class SSOController {
    @Autowired
    private SSOService ssoService;

    /**
     * 用户注册信息校验
     * @param checkValue
     * @param checkFlag
     * @return
     */
    @RequestMapping("/checkUserInfo/{checkValue}/{checkFlag}")
    public boolean checkUserInfo(@PathVariable String checkValue, @PathVariable Integer checkFlag) {
        return ssoService.checkUserInfo(checkValue, checkFlag);
    }

    /**
     * 用户注册
     * @param user
     * @return
     */
    @RequestMapping("/userRegister")
    public Integer userRegister(@RequestBody TbUser user) {
        return ssoService.userRegister(user);
    }

    /**
     * 用户登录
     * @param username
     * @param password
     * @return
     */
    @RequestMapping("/userLogin")
    public Map userLogin(String username, String password) {
        return this.ssoService.userLogin(username, password);
    }

    /**
     * 查询用户登录是否过期
     * @param token
     * @return
     */
    @RequestMapping("/getUserByToken/{token}")
    @ResponseBody
    public TbUser getUserByToken(@PathVariable String token) {
        return ssoService.getUserByToken(token);
    }

    @RequestMapping("/logOut")
    public Boolean logOut(String token){
        return ssoService.logOut(token);
    }
}
