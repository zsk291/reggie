package com.itheima.reggie.common;

import com.itheima.reggie.exception.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 全局异常处理器
 */
@ControllerAdvice(annotations = {RestController.class, Controller.class})
@Slf4j
@ResponseBody//返回json数据
public class GlobalExceptionHandler{

    /**
     * 抛SQLIntegrityConstraintViolationException.class异常处理方法
     * @return
     */
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public R<String> exceptionHander(SQLIntegrityConstraintViolationException ex){
        log.error(ex.getMessage());
        if (ex.getMessage().contains("Duplicate entry")){
            String[] strings = ex.getMessage().split(" ");
            return R.error(strings[2]+"已存在");
        }
        return R.error("未知错误");
    }
    @ExceptionHandler(CustomException.class)
    public R<String> CustomExceptionHander(CustomException ex){
        log.error(ex.getMessage());
        return R.error(ex.getMessage());
    }

}

