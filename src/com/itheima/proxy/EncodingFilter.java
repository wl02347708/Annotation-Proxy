package com.itheima.proxy;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

public class EncodingFilter implements Filter{

	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		
		HttpServletRequest req = (HttpServletRequest) request;
		
		// 使用動態代理完成全局編碼
		HttpServletRequest enhanceRequest = (HttpServletRequest) Proxy.newProxyInstance(
				req.getClass().getClassLoader(), 
				req.getClass().getInterfaces(), 
				new InvocationHandler() {
					@Override
					public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
						// 對 getParameter 方法進行增強
						String name = method.getName();
						if("getParameter".equals(name)){
							String invoke = (String) method.invoke(req, args);
							invoke = new String(invoke.getBytes("iso8859-1"), "UTF-8");
							return invoke;
						}
						return method.invoke(req, args);
					}
				});
		
		chain.doFilter(enhanceRequest, response);
		
	}

	@Override
	public void destroy() {
		
	}
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		
	}

}
