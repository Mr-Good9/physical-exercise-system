// 日志切面
package com.good.physicalexercisesystem.aspect;

import com.good.physicalexercisesystem.annotation.Log;
import com.good.physicalexercisesystem.service.SysLogService;
import com.good.physicalexercisesystem.utils.UserUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Arrays;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class LogAspect {

    private final SysLogService sysLogService;

    @Pointcut("@annotation(com.good.physicalexercisesystem.annotation.Log)")
    public void logPointCut() {
    }

    /**
     * 日志功能
     * 在方法执行后记录日志
     * 1. 在方法中使用@Log注解
     * 2. 执行到这个方法时，会获取当前请求的信息和用户信息，记录到数据库中日志表
     */
    @AfterReturning(pointcut = "logPointCut()")
    public void doAfterReturning(JoinPoint joinPoint) {
        try {
            // 获取用户名
            String username = UserUtils.getCurrentUsername();

            // 获取请求信息
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpServletRequest request = attributes.getRequest();
            String browser = request.getHeader("User-Agent");

            // 获取注解信息
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            Method method = signature.getMethod();
            Log logAnnotation = method.getAnnotation(Log.class);

            // 获取方法参数
            String params = Arrays.toString(joinPoint.getArgs());

            // 记录日志
            sysLogService.addLog(
                "operation",
                logAnnotation.level(),
                logAnnotation.value(),
                username,
                browser,
                params
            );
        } catch (Exception e) {
            log.error("记录操作日志失败", e);
        }
    }
}
