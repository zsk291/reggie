package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.Employee;
import com.itheima.reggie.entity.PageForSel;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/category")
@Slf4j
public class CateGoryController {
    @Autowired
    private CategoryService cs;
    @Autowired
    private DishService ds;

    /**
     * 新增菜品分类
     * @param category
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody Category category) {
        log.info("新增了{}", category.getName());
        cs.save(category);
        return R.success(null);
    }

    /**
     * 菜品分类查询
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
    public R<Page> selectByPage(int page, int pageSize) {
        log.info("page={}, pageSize={}", page, pageSize);
        Page<Category> pa = new Page<>(page, pageSize);
        LambdaQueryWrapper<Category> lqw = new LambdaQueryWrapper<>();
        lqw.orderByDesc(Category::getSort);
        cs.page(pa, lqw);

        log.info("进行了菜品分类查询");
        return R.success(pa);
    }

    /**
     * 删除菜品的某个类
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> delete(Long ids) {
        log.info("删除了{}", ids);
        cs.remove(ids);
        return R.success(null);
    }

    /**
     * 修改菜品某个类
     * @param category
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody Category category) {
        log.info("修改了{}", category.getName());
        cs.updateById(category);
        return R.success(null);
    }

    @GetMapping("/list")
    public R<List<Category>> getList(Category category) {
        LambdaQueryWrapper<Category> lqw = new LambdaQueryWrapper<>();
        lqw.eq(category.getType()!=null,Category::getType,category.getType());

        lqw.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);
        List<Category> list = cs.list(lqw);

        log.info("用户进行了分页查询");
        return R.success(list);
    }

}
