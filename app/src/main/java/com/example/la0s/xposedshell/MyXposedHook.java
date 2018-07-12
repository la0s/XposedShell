package com.example.la0s.xposedshell;

import java.lang.reflect.Member;
import java.util.regex.Pattern;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;

/**
 * Hook's type: trace
 * Usage:
 * in XposedModule
 * new MyXposedHook.hook(Method);
 * or	new MyXposedHook.hook(Class,MethodRegx);
 */
public class MyXposedHook extends XC_MethodHook {
    public Member method;            //被Hook的方法
    public Object thisObject;        //方法被调用时的this对象
    public Object[] args;            //方法被调用时的参数
    private Object result = null;    //方法被调用后的返回结果

    @Override
    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
        gatherInfo(param);
        //Write your code here.
        log("<" + method.getDeclaringClass() + " method=" + MethodDescription(param).toString() + ">");
        try {
            for (int i = 0; i < args.length; i++) {
                log("<Arg index=" + i + ">" + translate(args[i]) + "</Arg>");
            }
        } catch (Throwable e) {
            log("<Error>" + e.getLocalizedMessage() + "</Error>");
        } finally {
        }
    }

    @Override
    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
        gatherInfo(param);
        result = param.getResult();
        //Write your code here.

        try {
            log("<Result>" + translate(result) + "</Result>");
        } catch (Throwable e) {
            log("<Error>" + e.getLocalizedMessage() + "</Error>");
        } finally {
            log("</" + method.getDeclaringClass() + " method=" + MethodDescription(param).toString() + ">");
        }

        //You can replace it's result by uncomment this
        //param.setResult(result);


    }

    private void log(String log) {
        //You can add your own logger here.
        //e.g filelogger like Xlog.log(log);
        XposedBridge.log(log);
    }

    private String MethodDescription(MethodHookParam param) {
        StringBuilder sb = new StringBuilder();
        sb.append(method.getName().toString());
        sb.append("(");
        for (Object arg : args) {
            if (arg == null) sb.append("UnknownType");
            else if (arg.getClass().isPrimitive()) sb.append(arg.getClass().getSimpleName());
            else sb.append(arg.getClass().getName());
            sb.append(",");
        }
        sb.append(")");
        return sb.toString();
    }

    private String translate(Object obj) {
        //Write your translator here.
        return obj.toString();
    }

    public void hook(Member method) {
        XposedBridge.hookMethod(method, this);
    }

    public void hook(Class clz, String methodRegEx) {
        Pattern pattern = Pattern.compile(methodRegEx);
        for (Member method : clz.getDeclaredMethods()) {
            if (pattern.matcher(method.getName()).matches()) hook(method);
        }
    }

    private void gatherInfo(MethodHookParam param) {
        method = param.method;
        thisObject = param.thisObject;
        args = param.args;
    }
}
