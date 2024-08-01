package com.atguigu.lease.web.admin.service.impl;

import com.atguigu.lease.common.constant.RedisConstant;
import com.atguigu.lease.common.exception.LeaseException;
import com.atguigu.lease.common.result.ResultCodeEnum;
import com.atguigu.lease.common.utils.JwtUtil;
import com.atguigu.lease.model.entity.SystemUser;
import com.atguigu.lease.model.entity.UserInfo;
import com.atguigu.lease.model.enums.BaseStatus;
import com.atguigu.lease.web.admin.mapper.SystemUserMapper;
import com.atguigu.lease.web.admin.service.LoginService;
import com.atguigu.lease.web.admin.service.UserInfoService;
import com.atguigu.lease.web.admin.vo.login.CaptchaVo;
import com.atguigu.lease.web.admin.vo.login.LoginVo;
import com.atguigu.lease.web.admin.vo.system.user.SystemUserInfoVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wf.captcha.SpecCaptcha;
import com.wf.captcha.base.Captcha;
import jakarta.annotation.Resource;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class LoginServiceImpl implements LoginService {
    @Resource(name = "stringRedisTemplate")
    private StringRedisTemplate redisTemplate;

    @Resource
    private SystemUserMapper userMapper;

    @Override
    public CaptchaVo getCaptcha() {
        SpecCaptcha specCaptcha = new SpecCaptcha(130,48,5); // 定义图片的大小以及验证码的长度
        specCaptcha.setCharType(Captcha.TYPE_DEFAULT);

        // 获取验证码,这样可以不管用户输入的大写还是小写，统一转换成小写，再与redis里面的数据进行比对
        String text = specCaptcha.text().toLowerCase();

        // 命名该验证码的key，也就是项目名+功能模块名+UUID
        String key = RedisConstant.ADMIN_LOGIN_PREFIX + UUID.randomUUID();

        // 将图片以base64的计算方式，进行存储
        String image = specCaptcha.toBase64();

        // 存到redis里面，并设置60秒的时间
        redisTemplate.opsForValue().set(key,text,RedisConstant.ADMIN_LOGIN_CAPTCHA_TTL_SEC, TimeUnit.SECONDS);

        // 返回CaptchaVo对象
        return new CaptchaVo(image,key);
    }

    @Override
    public String login(LoginVo loginVo) {
        // 先判断验证码是否为空
        if (loginVo.getCaptchaCode()== null || loginVo.getCaptchaCode().isEmpty()){
            throw new LeaseException(ResultCodeEnum.APP_LOGIN_CODE_EMPTY);
        }

        // 再判断验证码是否过期，也就是能否从redis中拿到验证码
        String code = redisTemplate.opsForValue().get(loginVo.getCaptchaKey());
        if (Objects.isNull(code)){
            throw new LeaseException(ResultCodeEnum.APP_LOGIN_CODE_EXPIRED);
        }

        //如果能拿到验证码，就先比较验证码是否正确
        if (!Objects.equals(loginVo.getCaptchaCode().toLowerCase(),code)){
            throw new LeaseException(ResultCodeEnum.APP_LOGIN_CODE_ERROR);
        }

        // 验证码正确的话，就开始核对账号信息

        // 现根据用户名查询账号是否存在，因为我们之前在设置SystemUser中的password字段时，选择了select=false，所以直接用mapper方法是查询不到password字段的
        SystemUser user = userMapper.selectOneUserInfo(loginVo.getUsername());

        if (user != null){
            // 验证账号状态
            if (user.getStatus() != BaseStatus.DISABLE){
                // 验证密码
                if (Objects.equals(user.getPassword(), DigestUtils.md5Hex(loginVo.getPassword()))){
                    // 返回token
                    return JwtUtil.createToken(user.getId(),user.getUsername());
                }
                throw new LeaseException(ResultCodeEnum.ADMIN_ACCOUNT_ERROR);

            }
            throw new LeaseException(ResultCodeEnum.ADMIN_ACCOUNT_DISABLED_ERROR);
        }
        throw new LeaseException(ResultCodeEnum.ADMIN_ACCOUNT_ERROR);
    }

    @Override
    public SystemUserInfoVo getUserById(Long userId) {
        SystemUser user = userMapper.selectById(userId);
        SystemUserInfoVo systemUserInfoVo = new SystemUserInfoVo();
        systemUserInfoVo.setName(user.getName());
        systemUserInfoVo.setAvatarUrl(user.getAvatarUrl());
        return systemUserInfoVo;
    }
}
