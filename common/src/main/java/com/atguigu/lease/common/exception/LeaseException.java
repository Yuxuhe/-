package com.atguigu.lease.common.exception;

import com.atguigu.lease.common.result.ResultCodeEnum;
import lombok.Data;

/**
 * ClassName: LeaseException
 * PackageName: com.atguigu.lease.common.exception
 * Create: 2024/7/26-15:13
 * Description: 当公寓里面有房间，但用户执行删除公寓操作时，抛出此异常
 */
@Data
public class LeaseException extends RuntimeException{
    private Integer code;

    public LeaseException(Integer code,String message){
        super(message);
        this.code = code;
    }

    public LeaseException(ResultCodeEnum codeEnum){
        super(codeEnum.getMessage());
        this.code = codeEnum.getCode();
    }
}
