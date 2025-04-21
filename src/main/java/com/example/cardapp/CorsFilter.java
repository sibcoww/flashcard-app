package com.example.cardapp;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter("/*") // применить фильтр ко всем URL
public class CorsFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) {}

    @Override
    public void destroy() {}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletResponse res = (HttpServletResponse) response;

        res.setHeader("Access-Control-Allow-Origin", "*");
        res.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        res.setHeader("Access-Control-Allow-Headers", "Content-Type");

        chain.doFilter(request, response);
    }
}
