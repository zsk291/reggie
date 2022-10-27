package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Setmeal;

import java.util.List;


public interface SetmealService extends IService<Setmeal> {
    /**
     * 保存
     * @param setmealDto
     */
    public void saveWithSetmealDishs(SetmealDto setmealDto);

    /**
     * 修改
     * @param setmealDto
     */
    public void updateWithSetmealDishs(SetmealDto setmealDto);

    /**
     * 删除
     * @param ids
     */
    public void deleteWithSetmealDishs(List<Long> ids);
}
