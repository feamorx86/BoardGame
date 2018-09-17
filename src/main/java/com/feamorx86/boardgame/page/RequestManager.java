package com.feamorx86.boardgame.page;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.condition.ProducesRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Home on 25.08.2017.
 */
@Component
public class RequestManager {

    @Qualifier("requestMappingHandlerMapping")
    @Autowired
    RequestMappingHandlerMapping handlerMapping;

    @Autowired
    private WebApplicationContext context;
    @Autowired
    private Environment environment;
    private String serverLocationPrefix;

    public String getServerLocationPrefix() {
        return serverLocationPrefix;
    }

    public static class MappingInfo {
        public BasePage controller;
        public Integer dataId;
        public String path;
        public String bean;
        public String method;
        public RequestMappingInfo mappingInfo;
    }

    private HashMap<String, MappingInfo> mappings = new HashMap<String, MappingInfo>();


    @PostConstruct
    public void initialize() throws NoSuchMethodException {
        Map<String, BasePage> controllers = context.getBeansOfType(BasePage.class);
        for (Map.Entry<String, BasePage> controller : controllers.entrySet()) {
            String bean =  controller.getKey();
            String path =  controller.getValue().getFullName();
            if (bean != null) {
                if (path.equalsIgnoreCase("index")) {
                    registerMapping("*", bean, controller.getValue(), "handleRequest", controller.getValue().getContentType());
                } else {
                    registerMapping(path, bean, controller.getValue(), "handleRequest", controller.getValue().getContentType());
                }
            }
        }
        serverLocationPrefix = environment.getProperty("server.locationPrefix", "");
    }

    public MappingInfo registerMapping(String path, String bean, BasePage controller, String method, String produces) throws NoSuchMethodException {
        if (produces == null) produces = "text/html; charset=utf-8";
        RequestMappingInfo requestMappingInfo = new RequestMappingInfo(bean+"_mapping", new PatternsRequestCondition(path), null,
                null, null, null,
                new ProducesRequestCondition(produces), null);
        MappingInfo info = new MappingInfo();
        info.controller = controller;
        info.path = path;
        info.bean = bean;
        info.method = method;
        info.mappingInfo = requestMappingInfo;
        mappings.put(path, info);
        handlerMapping.registerMapping(requestMappingInfo, controller, controller.getClass().getMethod(method, HttpServletRequest.class, HttpServletResponse.class));
        return info;
    }

    public MappingInfo getMappingWithId(int dataId) {
        for(MappingInfo info : mappings.values()) {
            if (info.dataId != null && info.dataId == dataId) {
                return  info;
            }
        }
        return  null;
    }

    public boolean unregister(String path) {
        MappingInfo info = mappings.remove(path);
        if (info != null) {
            handlerMapping.unregisterMapping(info.mappingInfo);
            return true;
        }else {
            return  false;
        }
    }

    public HashMap<String, MappingInfo> getMappings() {
        return mappings;
    }

}
