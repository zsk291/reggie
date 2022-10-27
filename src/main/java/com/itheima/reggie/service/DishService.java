package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Dish;


public interface DishService extends IService<Dish> {

    //新增菜品
    public void saveWithFlavor(DishDto dishDto);

    public void updateWithFlavor(DishDto dishDto);

    public void deleteWithFlavor(long[] ids);
}
