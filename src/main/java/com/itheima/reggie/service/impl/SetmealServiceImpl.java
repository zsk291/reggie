package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.DishFlavor;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.entity.SetmealDish;
import com.itheima.reggie.exception.CustomException;
import com.itheima.reggie.mapper.CategoryMapper;
import com.itheima.reggie.mapper.SetmealMapper;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.SetmealDishService;
import com.itheima.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    @Autowired
    private SetmealDishService sds;

    @Transactional
    @Override
    public void saveWithSetmealDishs(SetmealDto setmealDto) {
        //封装对应属性到setmeal表
        this.save(setmealDto);

        Long setmealId = setmealDto.getId();
        //封装属性到setmeal_dish
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes = setmealDishes.stream().map((item)->{
            item.setSetmealId(setmealId);
            return item;
        }).collect(Collectors.toList());

        sds.saveBatch(setmealDishes);

    }


    @Transactional
    @Override
    public void updateWithSetmealDishs(SetmealDto setmealDto){
        //封装对应属性到setmeal
        this.updateById(setmealDto);

        //清除原套餐菜品
        LambdaQueryWrapper<SetmealDish> lqw = new LambdaQueryWrapper<>();
        lqw.eq(setmealDto.getId()!=null,SetmealDish::getSetmealId,setmealDto.getId());
        sds.remove(lqw);

        //添加selmealDto中list
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes = setmealDishes.stream().map((item)->{
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());
        sds.saveBatch(setmealDishes);
    }

    @Transactional
    @Override
    public void deleteWithSetmealDishs(List<Long> ids){
//        for (long id : ids) {
//            //删除setmeal
//            this.removeById(id);
//
//            //删除setmealDishs
//            LambdaQueryWrapper<SetmealDish> lqw = new LambdaQueryWrapper<>();
//            lqw.eq(SetmealDish::getSetmealId,id);
//            sds.remove(lqw);
//        }
        //排除售卖中的
        LambdaQueryWrapper<Setmeal> lqw = new LambdaQueryWrapper<>();
        lqw.in(Setmeal::getId,ids).eq(Setmeal::getStatus,1);
        int count = this.count(lqw);
        if (count > 0) {
            throw new CustomException("套餐正在售卖中，不能删除");
        }
        this.removeByIds(ids);

        LambdaQueryWrapper<SetmealDish> lqw2 = new LambdaQueryWrapper<>();
        lqw2.in(SetmealDish::getSetmealId,ids);
        sds.remove(lqw2);
    }
}
