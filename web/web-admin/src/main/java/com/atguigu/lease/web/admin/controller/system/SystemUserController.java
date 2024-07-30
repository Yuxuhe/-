package com.atguigu.lease.web.admin.controller.system;


import com.atguigu.lease.common.result.Result;
import com.atguigu.lease.model.entity.SystemPost;
import com.atguigu.lease.model.entity.SystemUser;
import com.atguigu.lease.model.enums.BaseStatus;
import com.atguigu.lease.web.admin.service.SystemPostService;
import com.atguigu.lease.web.admin.service.SystemUserService;
import com.atguigu.lease.web.admin.vo.system.user.SystemUserItemVo;
import com.atguigu.lease.web.admin.vo.system.user.SystemUserQueryVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;


@Tag(name = "后台用户信息管理")
@RestController
@RequestMapping("/admin/system/user")
public class SystemUserController {
    @Resource
    private SystemUserService userService;
    @Resource
    private SystemPostService postService;

    @Operation(summary = "根据条件分页查询后台用户列表")
    @GetMapping("page")
    public Result<IPage<SystemUserItemVo>> page(@RequestParam long current, @RequestParam long size, SystemUserQueryVo queryVo) {
        Page<SystemUserItemVo> systemUserItemVoPage = new Page<>(current, size);
        IPage<SystemUserItemVo> page = userService.pageItem(systemUserItemVoPage, queryVo);
        return Result.ok(page);
    }

    @Operation(summary = "根据ID查询后台用户信息")
    @GetMapping("getById")
    public Result<SystemUserItemVo> getById(@RequestParam Long id) {
        SystemUser systemUser = userService.getById(id);
        SystemPost systemPost = postService.getById(systemUser.getPostId());
        SystemUserItemVo itemVo = new SystemUserItemVo();
        BeanUtils.copyProperties(systemUser, itemVo);
        itemVo.setPostName(systemPost.getName());
        return Result.ok(itemVo);
    }

    @Operation(summary = "保存或更新后台用户信息")
    @PostMapping("saveOrUpdate")
    public Result saveOrUpdate(@RequestBody SystemUser systemUser) {
        if (systemUser.getPassword() != null) {
            // 证明修改了后台用户的密码，数据库中对应的密文密码需要更新
            systemUser.setPassword(DigestUtils.md5Hex(systemUser.getPassword()));
        }
        userService.saveOrUpdate(systemUser);
        return Result.ok();
    }

    @Operation(summary = "判断后台用户名是否可用")
    @GetMapping("isUserNameAvailable")
    public Result<Boolean> isUsernameExists(@RequestParam String username) {
        // 相当于用userName查询数据库中有没有与之重复的
        LambdaQueryWrapper<SystemUser> userQueryWrapper = new LambdaQueryWrapper<>();
        userQueryWrapper.eq(SystemUser::getUsername, username);
        long count = userService.count(userQueryWrapper);
        return Result.ok(count == 0);
    }

    @DeleteMapping("deleteById")
    @Operation(summary = "根据ID删除后台用户信息")
    public Result removeById(@RequestParam Long id) {
        userService.removeById(id);
        return Result.ok();
    }

    @Operation(summary = "根据ID修改后台用户状态")
    @PostMapping("updateStatusByUserId")
    public Result updateStatusByUserId(@RequestParam Long id, @RequestParam BaseStatus status) {
        LambdaUpdateWrapper<SystemUser> userUpdateWrapper = new LambdaUpdateWrapper<>();
        userUpdateWrapper.eq(SystemUser::getId, id).set(SystemUser::getStatus, status);
        userService.update(userUpdateWrapper);
        return Result.ok();
    }
}
