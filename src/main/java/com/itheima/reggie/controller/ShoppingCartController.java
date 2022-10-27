package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.AddressBook;
import com.itheima.reggie.entity.ShoppingCart;
import com.itheima.reggie.service.AddressBookService;
import com.itheima.reggie.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;


@Slf4j
@RestController
@RequestMapping("/shoppingCart")
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService scs;

    @GetMapping("/list")
    public R<List<ShoppingCart>> list() {
        LambdaQueryWrapper<ShoppingCart> lqw = new LambdaQueryWrapper<>();
        lqw.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());
        lqw.orderByAsc(ShoppingCart::getCreateTime);
        List<ShoppingCart> list = scs.list(lqw);
        return R.success(list);
    }

    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart) {
        //存userId
        shoppingCart.setUserId(BaseContext.getCurrentId());

        LambdaQueryWrapper<ShoppingCart> lqw = new LambdaQueryWrapper<>();
        //判断是菜还是套餐
        if (shoppingCart.getDishId() != null) {
            lqw.eq(ShoppingCart::getDishId, shoppingCart.getDishId());
        } else {
            lqw.eq(ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
        }

        ShoppingCart one = scs.getOne(lqw);


        if (one != null) {
            one.setNumber(one.getNumber() + 1);
            scs.updateById(one);
        } else {
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            scs.save(shoppingCart);
            one = shoppingCart;
        }
        return R.success(one);
    }

    @PostMapping("/sub")
    public R<String> sub(@RequestBody ShoppingCart shoppingCart) {

        LambdaQueryWrapper<ShoppingCart> lqw = new LambdaQueryWrapper<>();
        //判断是菜还是套餐
        if (shoppingCart.getDishId() != null) {
            lqw.eq(ShoppingCart::getDishId, shoppingCart.getDishId());
        } else {
            lqw.eq(ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
        }
        ShoppingCart one = scs.getOne(lqw);

        if (one.getNumber()>1){
            one.setNumber(one.getNumber() - 1);
            scs.updateById(one);
        }else {
            scs.removeById(one);
        }

        return R.success("成功");
    }

    @DeleteMapping("/clean")
    public R<String> clean() {

        LambdaQueryWrapper<ShoppingCart> lqw = new LambdaQueryWrapper<>();
        lqw.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());

        scs.remove(lqw);
        return R.success("成功");
    }
}
