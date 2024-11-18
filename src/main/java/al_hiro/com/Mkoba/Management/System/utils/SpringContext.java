package al_hiro.com.Mkoba.Management.System.utils;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class SpringContext {

    private static ApplicationContext context;

    public SpringContext(ApplicationContext applicationContext) {
        if(context==null)
            context = applicationContext;
    }

    public static <T> T getBean(Class<T> clazz) {
        if (context == null)
            throw new IllegalStateException("SpringContext not initialized (context not available)");
        return context.getBean(clazz);
    }

	public void log(String message){
        System.out.println(message);
	}
}
