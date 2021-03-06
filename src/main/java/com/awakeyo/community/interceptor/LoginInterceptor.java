package com.awakeyo.community.interceptor;

import com.awakeyo.community.mapper.UserMapper;
import com.awakeyo.community.pojo.User;
import com.awakeyo.community.service.NotificationService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author awakeyoyoyo
 * @className LoginInterceptor
 * @description TODO
 * @date 2019-12-10 16:55
 */
@Component
public class LoginInterceptor implements HandlerInterceptor  {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private NotificationService notificationService;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        User obj;
        obj=(User)request.getSession().getAttribute("user");
        UsernamePasswordToken shirotoken;
        if (obj==null) {
            Cookie[] cookies = request.getCookies();
            if (cookies!=null) {
                for (Cookie c : cookies) {
                    if (c.getName().equals("token")) {
                        String token = c.getValue();
                        if (token != null&&!token.isEmpty()) {
                            obj = userMapper.findByToken(token);
                            if (obj==null){
                                break;
                            }
                            Subject subject= SecurityUtils.getSubject();
                            //封装登陆
                            //
                            obj.getPassword();
                            shirotoken=new UsernamePasswordToken(obj.getAccountId(),obj.getPassword());
                            subject.login(shirotoken);
                            if (obj != null) {
                                request.getSession().setAttribute("user", obj);
                            }
                            break;
                        }
                    }
                }
            }
        }
        if (obj!=null){
            Long unreadCount=notificationService.getUnreadCount(obj.getId());
            //获取当前用户
//            Subject subject= SecurityUtils.getSubject();
            //封装登陆
//            shirotoken=new UsernamePasswordToken(obj.getAccountId(),obj.getPassword());
//            subject.login(shirotoken);
            request.getSession().setAttribute("unreadCount",unreadCount);
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
