package com.weifei.dispatcher;

import com.weifei.anotation.WFController;
import com.weifei.anotation.WFRequestMapping;
import com.weifei.anotation.WFService;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

public class WFDispatcherServlet extends HttpServlet {
    private Map<String, Object> mapping = new ConcurrentHashMap<>();
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doDispatch(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doDispatch(req, resp);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doDispatch(req, resp);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doDispatch(req, resp);
    }

    /**
     * 分发请求
     * @param request
     * @param response
     */
    private void doDispatch(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String url = request.getRequestURI();
        String context = request.getContextPath();
        url = url.replace(context, "").replace("/+", "/");
        System.out.println("第一步拿到请求路径URL: " + url + " CONTEXT: " + context);
        System.out.println("mapping" + mapping.toString());
        // 判断是否是有效URL
        if (!mapping.containsKey(url)) {
            response.getWriter().write("404 NOT FOUND PAGE");
            return;
        }
        Method method = (Method) this.mapping.get(url);
        try {
            method.invoke(mapping.get(method.getDeclaringClass().getName())
                    , new Object[]{request, response});
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

    }


    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init();
        System.out.println("SpringMVC init start...");
        Properties configContext = new Properties();
        InputStream input = null;
        // 可能有多个配置文件，需要修改逻辑
        String configPath = config.getInitParameter("contextConfigLocation")
                .replace("classpath", "")
                .replace(":", "").replaceAll(" ", "");
        input = this.getClass()
                .getClassLoader()
                .getResourceAsStream(configPath);
        try {
            configContext.load(input);
            String scanPackage = configContext.getProperty("scanPackage");
            System.out.println("scanPackage = " + scanPackage);
            //收集需要扫描的类
            this.doScanner(scanPackage);
            System.out.println("mapping = " + mapping.toString());

            for (String clazzName : mapping.keySet()) {
                if (!clazzName.contains(".")) {
                    return;
                }
                Class clazz = Class.forName(clazzName);
                // 控制类
                if (clazz.isAnnotationPresent(WFController.class)) {
                    mapping.put(clazzName, clazz.newInstance());
                    String baseUrl = "";
                    if (clazz.isAnnotationPresent(WFRequestMapping.class)) {
                        WFRequestMapping requestMapping =  (WFRequestMapping)clazz.getAnnotation(WFRequestMapping.class);
                        baseUrl = requestMapping.value();
                        System.out.println(baseUrl);
                        Method[] methods = clazz.getMethods();
                        for (Method method : methods) {
                            if (!method.isAnnotationPresent(WFRequestMapping.class)) {
                                continue;
                            }
                            WFRequestMapping wfRequestMapping = method.getAnnotation(WFRequestMapping.class);
                            String url = (baseUrl + "/" + wfRequestMapping.value()).replaceAll("/+","/");
                            mapping.put(url, method);
                        }
                    }
                }
                // 服务类
                else if (clazz.isAnnotationPresent(WFService.class)) {
                    mapping.put(clazzName, clazz.newInstance());
                    WFService wfService = (WFService) clazz.getAnnotation(WFService.class);
                    String beanName = wfService.value();
                    if ("".equals(beanName)) {
                        beanName = clazz.getName();
                    }
                    Object instance = clazz.newInstance();
                    mapping.put(beanName, instance);
                    for (Class c : clazz.getInterfaces()) {
                        mapping.put(c.getName(), instance);
                    }
                }
            }
            System.out.println("mapping = " + mapping.toString());
        } catch (IOException
                | ClassNotFoundException
                | InstantiationException
                | IllegalAccessException e) {
            e.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    /**
     * 扫描路径下的注解，生产ioc容器
     * @param scanPath
     */
    private void doScanner(String scanPath) {
        URL url = this.getClass()
                .getClassLoader()
                .getResource("/"
                        + scanPath.replaceAll("\\.", "/"));
        File classDir = new File(url.getFile());
        for (File file : classDir.listFiles()) {
            if (file.isDirectory()) {
                doScanner(scanPath + "." + file.getName());
            }else {
                if (!file.getName().endsWith(".class")){
                    continue;
                }
                String clazzName = scanPath + "."
                        + file.getName().replace(".class", "");
                mapping.put(clazzName, new Object());
            }
        }
    }

}
