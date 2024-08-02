package com.atguigu.lease.common.exception;

import com.atguigu.lease.common.result.Result;
import com.atguigu.lease.common.result.ResultCodeEnum;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * ClassName: GlobalExceptionHandler
 * PackageName: com.atguigu.lease.common.com.atguigu.lease.common.exception
 * Create: 2024/7/25-11:13
 * Description: 配置全局异常处理器
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Result error(Exception e) {
        e.printStackTrace();
        return Result.fail();
    }

    @ExceptionHandler(LeaseException.class)
    @ResponseBody
    public Result error(LeaseException leaseException){
        return Result.fail(leaseException.getCode(), leaseException.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public Result error(MethodArgumentNotValidException ex){
        // 从异常中获取错误信息字段
        FieldError fieldError = ex.getBindingResult().getFieldError();
        if (fieldError != null) {
            String message = fieldError.getDefaultMessage();
            return Result.fail(202,message);
        }
        return Result.fail(200,"请求异常");
    }
}
