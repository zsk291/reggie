package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.DishFlavor;
import com.itheima.reggie.entity.Employee;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.DishFlavorService;
import com.itheima.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/dish")
public class DishController {

    @Autowired
    private DishService ds;

    @Autowired
    private CategoryService cs;

    @Autowired
    private DishFlavorService dfs;

    /**
     * 菜品分页查询
     *
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
    public R<Page> selectByPage(int page, int pageSize, String name) {
        log.info("page={}, pageSize={}, name={}", page, pageSize, name);
        //主体对象
        Page<DishDto> pa2 = new Page<>(page, pageSize);

        Page<Dish> pa = new Page<>(page, pageSize);

        LambdaQueryWrapper<Dish> lqw = new LambdaQueryWrapper<>();

        lqw.like(StringUtils.isNotEmpty(name), Dish::getName, name);
        //根据更新时间排序
        lqw.orderByDesc(Dish::getUpdateTime);

        //dish的分页查询
        ds.page(pa, lqw);

        //工具类拷贝,忽略records
        BeanUtils.copyProperties(pa, pa2, "records");

        //专门处理records
        List<DishDto> dishDtoList = pa.getRecords().stream().map((item) -> {
            DishDto dishDto = new DishDto();
            //复制dish的原有属性
            BeanUtils.copyProperties(item, dishDto);

            //根据Id查询出名字
            Category cate = cs.getById(dishDto.getCategoryId());
            if (cate != null) {
                String categoryName = cate.getName();
                dishDto.setCategoryName(categoryName);
            }


            //封装完毕，返回新的对象
            return dishDto;
        }).collect(Collectors.toList());//元素收集

        //封装records
        pa2.setRecords(dishDtoList);
        log.info("用户进行了菜品分页查询");

        //整个pa2对象属性整理完毕，返回对象
        return R.success(pa2);
    }

    /**
     * 保存菜品
     *
     * @param dishDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto) {
        log.info("新增了{}", dishDto.getName());
        ds.saveWithFlavor(dishDto);
        return R.success(null);
    }

    /**
     * 删除某个菜品
     *
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> delete(long[] ids) {
        ds.deleteWithFlavor(ids);
        return R.success("删除成功");
    }

    /**
     * 修改某个菜品
     *
     * @param dishDto
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto) {
        log.info("修改了{}", dishDto.getName());
        ds.updateWithFlavor(dishDto);
        return R.success(null);
    }

    /**
     * 回显用户信息
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<DishDto> echoData(@PathVariable Long id) {
        log.info("回显了{}数据", id);

        DishDto dishDto = new DishDto();

        Dish dish = ds.getById(id);
        BeanUtils.copyProperties(dish, dishDto);

        LambdaQueryWrapper<DishFlavor> lqw = new LambdaQueryWrapper<>();
        lqw.eq(DishFlavor::getDishId, dish.getId());
        List<DishFlavor> list = dfs.list(lqw);

        dishDto.setFlavors(list);

        Category cate = cs.getById(dish.getCategoryId());
        if (cate != null) {
            String categoryName = cate.getName();
            dishDto.setCategoryName(categoryName);
        }

        return R.success(dishDto);
    }


    /**
     * 删除菜品
     *
     * @param ids
     * @return
     */
    @PostMapping("/status/{status}")
    public R<String> saleStop(@PathVariable("status") Integer status, long[] ids) {
        for (long id : ids) {
            Dish dish = ds.getById(id);
            if (dish != null) {
                dish.setStatus(status);
                ds.updateById(dish);

            }
        }
        return R.success("状态修改成功");
    }

    /**
     * 窗口展示某类的菜品
     *
     * @param dish
     * @return
     */
    @GetMapping("/list")
    public R<List<DishDto>> List(Dish dish) {
        LambdaQueryWrapper<Dish> lqw = new LambdaQueryWrapper<>();

        Long categoryId = dish.getCategoryId();
        String name = dish.getName();

        lqw.eq(categoryId != null, Dish::getCategoryId, categoryId);
        //查询在售的菜品
        lqw.eq(dish.getStatus()!=null,Dish::getStatus, 1);
        //查询名字
        lqw.like(name != null, Dish::getName, name);
        //排序
        lqw.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);

        List<Dish> list = ds.list(lqw);

        List<DishDto> dishDtoList = list.stream().map((item) -> {
            //赋值dish
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item, dishDto);


            //赋值categoryName
            Category category = cs.getById(categoryId);
            if (category != null) {
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }

            //赋值flavors
            Long dishId = item.getId();
            LambdaQueryWrapper<DishFlavor> lqw2 = new LambdaQueryWrapper<>();
            lqw2.eq(DishFlavor::getDishId, dishId);
            List<DishFlavor> dishFlavorlist = dfs.list(lqw2);

            dishDto.setFlavors(dishFlavorlist);

            return dishDto;
        }).collect(Collectors.toList());

        log.info("窗口展示某类的菜品");
        return R.success(dishDtoList);
    }


}
