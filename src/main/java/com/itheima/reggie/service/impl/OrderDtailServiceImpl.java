package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.entity.Employee;
import com.itheima.reggie.entity.OrderDetail;
import com.itheima.reggie.mapper.EmployeeMapper;
import com.itheima.reggie.mapper.OrderDetaiMapper;
import com.itheima.reggie.service.EmployeeService;
import com.itheima.reggie.service.OrderDetailService;
import org.springframework.stereotype.Service;

@Service
public class OrderDtailServiceImpl extends ServiceImpl<OrderDetaiMapper, OrderDetail> implements OrderDetailService {
}
