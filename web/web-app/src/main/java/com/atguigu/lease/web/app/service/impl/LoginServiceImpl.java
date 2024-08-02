package com.atguigu.lease.web.app.service.impl;

import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.dysmsapi20170525.models.SendSmsRequest;
import com.atguigu.lease.common.constant.RedisConstant;
import com.atguigu.lease.common.exception.LeaseException;
import com.atguigu.lease.common.result.ResultCodeEnum;
import com.atguigu.lease.common.utils.JwtUtil;
import com.atguigu.lease.common.utils.RandomCodeUtil;
import com.atguigu.lease.model.entity.SystemUser;
import com.atguigu.lease.model.entity.UserInfo;
import com.atguigu.lease.model.enums.BaseStatus;
import com.atguigu.lease.web.app.mapper.UserInfoMapper;
import com.atguigu.lease.web.app.service.LoginService;
import com.atguigu.lease.web.app.service.SmsService;
import com.atguigu.lease.web.app.vo.user.LoginVo;
import com.atguigu.lease.web.app.vo.user.UserInfoVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import jakarta.annotation.Resource;
import org.apache.catalina.User;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Service
public class LoginServiceImpl implements LoginService {
    @Resource
    private SmsService smsService;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private UserInfoMapper userInfoMapper;

    @Override
    public void getCode(String phone) {
        String code = RandomCodeUtil.codeGenerate(6);
        String key = RedisConstant.APP_LOGIN_PREFIX + phone;

        // 设置用户只能在一分钟内请求一次这个接口
        if (stringRedisTemplate.hasKey(key)){
            Long ttl = stringRedisTemplate.getExpire(key,TimeUnit.SECONDS);
            if (RedisConstant.APP_LOGIN_CODE_TTL_SEC - ttl < RedisConstant.APP_LOGIN_CODE_RESEND_TIME_SEC){
               throw new LeaseException(ResultCodeEnum.APP_SEND_SMS_TOO_OFTEN);
            }
        }
        smsService.sendCode(phone,code);
        stringRedisTemplate.opsForValue().set(key,code,RedisConstant.APP_LOGIN_CODE_TTL_SEC, TimeUnit.SECONDS);


    }

    @Override
    public String login(LoginVo loginVo) {
        // 验证验证码的正确性
        String code = stringRedisTemplate.opsForValue().get(RedisConstant.APP_LOGIN_PREFIX + loginVo.getPhone());
        if (code == null){
            throw new LeaseException(ResultCodeEnum.APP_LOGIN_CODE_EXPIRED);
        }
        if (Objects.equals(loginVo.getCode(),code)){
            LambdaQueryWrapper<UserInfo> userInfoQueryWrapper = new LambdaQueryWrapper<>();
            userInfoQueryWrapper.eq(UserInfo::getPhone,loginVo.getPhone());
            UserInfo user = userInfoMapper.selectOne(userInfoQueryWrapper);

            if (user == null){
                user = new UserInfo();
                user.setPhone(loginVo.getPhone());
                user.setStatus(BaseStatus.ENABLE);
                user.setNickname("用户-"+loginVo.getPhone().substring(6));
                userInfoMapper.insert(user);
            }
            if (user.getStatus() == BaseStatus.DISABLE){
                throw new LeaseException(ResultCodeEnum.APP_ACCOUNT_DISABLED_ERROR);
            }

            // 返回token
            return JwtUtil.createToken(user.getId(), user.getPhone());

        }
        throw new LeaseException(ResultCodeEnum.APP_LOGIN_CODE_ERROR);
    }

    @Override
    public UserInfoVo info(Long userId) {
        UserInfo userInfo = userInfoMapper.selectById(userId);
        UserInfoVo userInfoVo = new UserInfoVo(userInfo.getNickname(), userInfo.getAvatarUrl());
        return userInfoVo;
    }
}
