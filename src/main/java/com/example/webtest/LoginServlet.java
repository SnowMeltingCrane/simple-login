package com.example.webtest;

import com.example.entity.User;
import com.example.mapper.UserMapper;
import jakarta.servlet.Servlet;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebInitParam;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import javax.print.attribute.standard.Severity;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@WebServlet(value = "/login",loadOnStartup = 1,initParams = {
        @WebInitParam(name="test",value = "我是初始化参数")
})
public class LoginServlet extends HttpServlet {

    SqlSessionFactory factory;
    @SneakyThrows
    @Override
    public void init() throws ServletException {
        System.out.println(getInitParameter("test"));
        factory = new SqlSessionFactoryBuilder().build(Resources.getResourceAsReader("mybatis-config.xml"));
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Cookie[] cookies = req.getCookies();
        if (cookies != null) {
            String username = null;
            String password = null;
            for (Cookie cookie : cookies) {
                if ("username".equals(cookie.getName())) username = cookie.getValue();
                if ("password".equals(cookie.getName())) password = cookie.getValue();
            }
            if (username != null && password != null) {
                try(SqlSession session = factory.openSession()){
                    UserMapper mapper = session.getMapper(UserMapper.class);
                    User user = mapper.getUserByNameAndPwd(username, password);
                    if (user != null) {
                        HttpSession httpSession = req.getSession();
                        httpSession.setAttribute("user",user);
                        resp.sendRedirect("time");
                        return;
                    }
                }
            }else {
                Cookie cookie_username = new Cookie("username",username);
                Cookie cookie_password = new Cookie("password",password);
                resp.addCookie(cookie_username);
                resp.addCookie(cookie_password);
                cookie_username.setMaxAge(0);
                cookie_password.setMaxAge(0);
            }
        }
        req.getRequestDispatcher("/").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html;charset=utf-8");
        Map<String,String[]> map =req.getParameterMap();

        if(map.containsKey("username") && map.containsKey("password")){
            String username = req.getParameter("username");
            String password = req.getParameter("password");

            try (SqlSession session = factory.openSession()) {
                UserMapper userMapper = session.getMapper(UserMapper.class);
                User user = userMapper.getUserByNameAndPwd(username, password);

                if(user != null){
                    if(map.containsKey("remember-me")){
                        Cookie cookie_username = new Cookie("username",username);
                        cookie_username.setMaxAge(60*60*24*7);
                        Cookie cookie_password = new Cookie("password",password);
                        cookie_password.setMaxAge(60*60*24*7);
                        resp.addCookie(cookie_username);
                        resp.addCookie(cookie_password);
                    }else {
                        Cookie cookie_username = new Cookie("username",username);
                        cookie_username.setMaxAge(0);
                        Cookie cookie_password = new Cookie("password",password);
                        cookie_password.setMaxAge(0);
                        resp.addCookie(cookie_username);
                        resp.addCookie(cookie_password);
                    }
                    HttpSession httpSession = req.getSession();
                    httpSession.setAttribute("user",user);
                    resp.sendRedirect("time");
                }else{
                    resp.getWriter().write("你所登录的用户密码不正确，或用户不存在");
                }
            }
        } else {
            resp.getWriter().write("<h1>错误，表单数据不完整</h1>");
        }
    }
}
