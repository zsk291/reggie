package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.entity.*;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.DishService;
import com.itheima.reggie.service.SetmealDishService;
import com.itheima.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/setmeal")
@Slf4j
public class SetmealController {

    @Autowired
    private SetmealService ss;
    @Autowired
    private SetmealDishService sds;
    @Autowired
    private CategoryService cs;


    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto) {
        ss.saveWithSetmealDishs(setmealDto);
        return R.success("保存成功");
    }

    /**
     * 菜品分页查询
     *
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
    public R<Page> selectByPage(int page, int pageSize, String name) {
        Page<Setmeal> setmealPage = new Page<>();
        Page<SetmealDto> setmealDtoPage = new Page<>();

        LambdaQueryWrapper<Setmeal> lqw = new LambdaQueryWrapper<>();
        lqw.like(StringUtils.isNotEmpty(name), Setmeal::getName, name);
        lqw.orderByDesc(Setmeal::getUpdateTime);

        ss.page(setmealPage, lqw);

        BeanUtils.copyProperties(setmealPage, setmealDtoPage, "records");

        List<SetmealDto> list = setmealPage.getRecords().stream().map((item) -> {
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(item, setmealDto);

            Category category = cs.getById(item.getCategoryId());
            if (category != null) {
                String categoryName = category.getName();
                setmealDto.setCategoryName(categoryName);
            }
            return setmealDto;
        }).collect(Collectors.toList());

        setmealDtoPage.setRecords(list);

        //封装完毕
        return R.success(setmealDtoPage);
    }

    /**
     * 回显套餐信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<SetmealDto> echoData(@PathVariable Long id) {
        log.info("回显了{}数据", id);

        SetmealDto setmealDto = new SetmealDto();
        Setmeal setmeal = ss.getById(id);

        //封装原属性
        BeanUtils.copyProperties(setmeal,setmealDto);

        //封装list<SetmealDish>属性
        LambdaQueryWrapper<SetmealDish> lqw = new LambdaQueryWrapper<>();
        lqw.eq(SetmealDish::getSetmealId,id);
        List<SetmealDish> list = sds.list(lqw);
        setmealDto.setSetmealDishes(list);

        //封装categoryName属性
        Category category = cs.getById(setmeal);
        if (category!=null){
            String categoryName = category.getName();
            setmealDto.setCategoryName(categoryName);
        }

        //封装完成返回setmealDto对象
        return R.success(setmealDto);
    }

    @PutMapping
    public R<String> updateWithSetmealDish(@RequestBody SetmealDto setmealDto){
        ss.updateWithSetmealDishs(setmealDto);
        return R.success("修改成功");
    }

    @DeleteMapping
    public R<String> deleteWithSetmealDish(@RequestParam List<Long> ids){
        ss.deleteWithSetmealDishs(ids);
        return R.success("删除成功");
    }

    @PostMapping("/status/{status}")
    public R<String> updateMulStatus(@PathVariable Integer status, Long[] ids){
        List<Long> list = Arrays.asList(ids);
        LambdaUpdateWrapper<Setmeal> lqw = new LambdaUpdateWrapper<>();
        lqw.in(Setmeal::getId,list).set(Setmeal::getStatus,status);
        ss.update(lqw);

        return R.success("成功");

    }

    @GetMapping("/list")
    public R<List<SetmealDto>> List(Setmeal setmeal) {
        LambdaQueryWrapper<Setmeal> lqw = new LambdaQueryWrapper<>();

        Long categoryId = setmeal.getCategoryId();
        String name = setmeal.getName();

        lqw.eq(categoryId != null, Setmeal::getCategoryId, categoryId);
        //查询在售的菜品
        lqw.eq(setmeal.getStatus()!=null,Setmeal::getStatus, 1);
        //查询名字
        lqw.like(name != null, Setmeal::getName, name);
        //排序
        lqw.orderByDesc(Setmeal::getUpdateTime);

        List<Setmeal> list = ss.list(lqw);

        List<SetmealDto> setmealDtoList = list.stream().map((item) -> {
            //赋值Setmeal
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(item, setmealDto);


            //赋值categoryName
            Category category = cs.getById(categoryId);
            if (category != null) {
                String categoryName = category.getName();
                setmealDto.setCategoryName(categoryName);
            }

            //赋值flavors
            Long SetmealId = item.getId();
            LambdaQueryWrapper<SetmealDish> lqw2 = new LambdaQueryWrapper<>();
            lqw2.eq(SetmealDish::getSetmealId, SetmealId);
            List<SetmealDish> setmealdishlist = sds.list(lqw2);

            setmealDto.setSetmealDishes(setmealdishlist);

            return setmealDto;
        }).collect(Collectors.toList());

        log.info("窗口展示某类的菜品");
        return R.success(setmealDtoList);
    }
}
