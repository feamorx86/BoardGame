package com.feamorx86.boardgame.controller;

import com.feamorx86.boardgame.utils.SimpleTagProcessor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by Home on 09.10.2017.
 */
@Component
public class Templates {

    public static final String basePath = "./src/main/resources/web/";

    private boolean useCache = false;
    private HashMap<String, SimpleTagProcessor> templates = new HashMap<>();

    public void setUseCache(boolean useCache) {
        this.useCache = useCache;
    }

    public boolean isUseCache() {
        return useCache;
    }

    public SimpleTagProcessor getTemplate(String name) {
        SimpleTagProcessor template = templates.get(name.toLowerCase());
        if (template == null) {
            try {
                template = new SimpleTagProcessor(name);
                template.prepare(basePath + name + ".html");
                if (useCache) {
                    templates.put(name, template);
                }
            } catch (IOException ex) {
                ex.printStackTrace();
                template = null;
            }
        }
        return template;
    }

    public void clear() {
        templates.clear();
    }
}
