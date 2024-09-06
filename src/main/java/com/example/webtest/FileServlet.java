package com.example.webtest;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import org.apache.commons.io.IOUtils;
import org.apache.ibatis.io.Resources;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

@MultipartConfig
@WebServlet("/file")
public class FileServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("image/jpg");
        OutputStream out = resp.getOutputStream();
        InputStream in = Resources.getResourceAsStream("鬼针草.jpg");
        IOUtils.copy(in, out);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try(FileOutputStream outputStream = new FileOutputStream("D:\\study\\webTest\\src\\main\\resources\\test.png")) {
            Part part = req.getPart("test-file");
            IOUtils.copy(part.getInputStream(), outputStream);
            resp.setContentType("text/html;charset=utf-8");
            resp.getWriter().write("<h1>文件上传成功</h1>");
        }
    }
}
