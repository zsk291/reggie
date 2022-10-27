package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.exception.CustomException;
import com.itheima.reggie.mapper.CategoryMapper;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.DishService;
import com.itheima.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper,Category> implements CategoryService {

    @Autowired
    private DishService ds;
    @Autowired
    private SetmealService ss;

    @Override
    public void remove(Long id) {
        LambdaQueryWrapper<Dish> dishlqw = new LambdaQueryWrapper<>();
        dishlqw.eq(Dish::getCategoryId,id);
        int count1 = ds.count(dishlqw);
        if (count1>0){
            //关联了菜品，不能删除
            //抛业务异常
            throw new CustomException("关联了分类，不能删除");
        }

        LambdaQueryWrapper<Setmeal> setmeallqw = new LambdaQueryWrapper<>();
        setmeallqw.eq(Setmeal::getCategoryId,id);
        int count2 = ss.count(setmeallqw);
        if (count2>0){
            //关联了菜品，不能删除
            throw new CustomException("关联了套餐，不能删除");
        }

        //都不关联则执行删除方法
        super.removeById(id);
    }
}
