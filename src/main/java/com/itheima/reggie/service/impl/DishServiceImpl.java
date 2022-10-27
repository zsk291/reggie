package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.DishFlavor;
import com.itheima.reggie.mapper.DishMapper;
import com.itheima.reggie.service.DishFlavorService;
import com.itheima.reggie.service.DishService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Autowired
    private DishFlavorService dfs;

    @Transactional//涉及多张表同步操作，需要开启事务
    //新增菜品，同时操作两张表  dish，dish_flavor
    @Override
    public void saveWithFlavor(DishDto dishDto) {
        //封装对应属性到dish表
        this.save(dishDto);

        Long dishId = dishDto.getId();

        //封装属性到dish_flavor
        List<DishFlavor> flavors = dishDto.getFlavors();

        //遍历存储
        for (DishFlavor flavor : flavors) {
            flavor.setDishId(dishId);
            dfs.save(flavor);
        }
    }

    @Override
    @Transactional
    public void updateWithFlavor(DishDto dishDto) {
        //封装对应属性到dish表
        this.updateById(dishDto);

        Long dishId = dishDto.getId();

        //不便修改数组里的内容
        //1.先删除原先的，2.再添加新的
        LambdaQueryWrapper<DishFlavor> lqw = new LambdaQueryWrapper<>();
        lqw.eq(DishFlavor::getDishId, dishId);
        dfs.remove(lqw);

        //为没有dishId的flavor添加dishId
        List<DishFlavor> list = dishDto.getFlavors().stream().map((item) -> {
            item.setDishId(dishId);
            return item;
        }).collect(Collectors.toList());

        dfs.saveBatch(list);

    }

    @Override
    @Transactional
    public void deleteWithFlavor(long[] ids) {
        for (long id : ids) {
            //删除套餐
            this.removeById(id);

            //删除口味
            LambdaQueryWrapper<DishFlavor> lqw = new LambdaQueryWrapper<>();
            lqw.eq(DishFlavor::getDishId,id);
            List<DishFlavor> list = dfs.list(lqw);
            if (list!=null){
                for (DishFlavor dishFlavor : list) {
                    dfs.removeById(dishFlavor);
                }
            }
        }

    }


}

