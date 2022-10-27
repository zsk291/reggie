package com.itheima.reggie.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Employee;
import com.itheima.reggie.entity.PageForSel;
import com.itheima.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService es;

    /**
     * 员工登录
     *
     * @param req
     * @param employee
     * @return
     */
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest req, @RequestBody Employee employee) {
        //先得到密码并加密
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes(StandardCharsets.UTF_8));

        //得到用户名并查询对象是否存在

        LambdaQueryWrapper<Employee> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Employee::getUsername, employee.getUsername());
        Employee emp = es.getOne(lqw);
        if (null == emp) {
            return R.error("未找到用户！");
        }

        if (!password.equals(emp.getPassword())) {
            return R.error("未找到用户密码！");
        }

        if (0 == emp.getStatus()) {
            return R.error("该账号处于禁用状态！");
        }

        //将员工id存入Session
        req.getSession().setAttribute("employee", emp.getId());
        return R.success(emp);
    }

    /**
     * 退出管理端
     *
     * @param req
     * @return
     */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest req) {
        req.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }

    /**
     * 新增管理
     *
     * @param req
     * @param employee
     * @return
     */
    @PostMapping
    public R<String> save(HttpServletRequest req, @RequestBody Employee employee) {
        log.info("新增员工");
        //设置密码，并采取MD5加密
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes(StandardCharsets.UTF_8)));

        long empId = (Long) req.getSession().getAttribute("employee");

        es.save(employee);
        return R.success("新增员工成功");
    }


    /**
     * 用户信息分页
     *
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> selectByPage(int page, int pageSize, String name) {
        log.info("page={}, pageSize={}, name={}", page, pageSize, name);
        Page<Employee> pa = new Page<>(page, pageSize);
        LambdaQueryWrapper<Employee> lqw = new LambdaQueryWrapper<>();
        //添加姓名相似过滤
        lqw.like(StringUtils.isNotEmpty(name), Employee::getName, name);
        //根据更新时间排序
        lqw.orderByDesc(Employee::getUpdateTime);
        es.page(pa, lqw);

        log.info("用户进行了分页查询");
        return R.success(pa);
    }

    /**
     * 管理员管理用户状态
     *
     * @param req
     * @param employee
     * @return
     */
    @PutMapping
    public R<String> modifyStatus(HttpServletRequest req, @RequestBody Employee employee) {
        //拆包
        Long id = employee.getId();
        Integer status = employee.getStatus();
        //记录日志
        log.info("admin修改{}员工的{}状态", id, status);

        //根据员工更改状态
        es.updateById(employee);

        return R.success("员工信息修改成功");
    }

    /**
     * 回显用户信息
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<Employee> echoData(@PathVariable Long id) {
        log.info("回显了{}数据", id);
        Employee emp = es.getById(id);
        return R.success(emp);
    }
}
