package com.itheima.reggie.filter;

import com.alibaba.fastjson.JSON;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Employee;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@Slf4j
@WebFilter(filterName = "LoginCheckFilter", urlPatterns = "/*")
public class LoginCheckFilter implements Filter {

    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpServletResponse res = (HttpServletResponse) servletResponse;
        //1.先接路径uri
        String uri = req.getRequestURI();
        log.info("拦截到路径"+uri);


        //排除不拦截的路径
        String[] uris = {
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/common/**",
                "/user/sendMsg",
                "/user/login"
        };
        boolean check = check(uris, uri);
        //匹配成功放行
        if (check) {
            log.info("本次请求{}不需要处理",uri);
            filterChain.doFilter(req,res);
            return;
        }

        //employee匹配成功
        if (null!=req.getSession().getAttribute("employee")){
            Long empId = (Long) req.getSession().getAttribute("employee");
            BaseContext.setCurrentId(empId);
            log.info("用户已登录，用户id为：{}",empId);
            filterChain.doFilter(req,res);
            return;
        }

        //user匹配成功
        if (null!=req.getSession().getAttribute("user")){
            Long userId = (Long) req.getSession().getAttribute("user");
            BaseContext.setCurrentId(userId);
            log.info("用户已登录，用户id为：{}",userId);
            filterChain.doFilter(req,res);
            return;
        }

        log.info("用户未登录");
        //匹配不成功，则判断登录状态，登录成功（id存在）则随便访问页面
        res.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;
    }

    /**
     * 路径匹配，看是否要放行
     * @param uris
     * @param uri
     * @return
     */
    public boolean check(String[] uris,String uri){
        for (String s : uris) {
            boolean match = PATH_MATCHER.match(s, uri);
            if (match) return match;
        }
        return false;
    }
}
