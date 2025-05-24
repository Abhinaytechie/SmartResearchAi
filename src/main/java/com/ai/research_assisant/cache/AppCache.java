package com.ai.research_assisant.cache;

import org.springframework.stereotype.Component;
import java.util.Map;
@Component
public class AppCache {

    private Map<String,String> appcache;

    public void init(){
        appcache=null;
    }
}

