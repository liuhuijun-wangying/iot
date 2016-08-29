package com.iot.basesvr;

import com.iot.basesvr.annotation.Cmd;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by zc on 16-8-26.
 */
public class ControllerScanner {

    private static Map<Integer,CtrlMethod> map = new ConcurrentHashMap<>();

    //scan methods od all controllers
    //only methods with Cmd annotation, param count = 1, param type is byte[] and return type is byte[] are legal
    static void scan(ApplicationContext ctx){
        Map<String, Object> controllers = ctx.getBeansWithAnnotation(Controller.class);
        for(Map.Entry<String,Object> entry: controllers.entrySet()){
            Class clazz = entry.getValue().getClass();
            Method[] methods = clazz.getDeclaredMethods();
            for(Method method: methods){
                Cmd cmd = method.getAnnotation(Cmd.class);
                if (cmd!=null && cmd.value()!=-1 && method.getParameterCount()==1
                        && (method.getParameters()[0]).getType().getSimpleName().equals("byte[]")
                        && method.getReturnType().getSimpleName().equals("byte[]")){
                    method.setAccessible(true);
                    map.put(cmd.value(),new CtrlMethod(method,entry.getValue()));
                }
            }
        }
    }

    public static class CtrlMethod{
        public Method m;
        public Object obj;

        public CtrlMethod(Method m, Object obj) {
            this.m = m;
            this.obj = obj;
        }

        public Object invoke(Object... args) throws Exception {
            return this.m.invoke(this.obj,args);
        }
    }

    public static CtrlMethod getMethod(int cmd){
        return map.get(cmd);
    }
}
