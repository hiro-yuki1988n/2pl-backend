package al_hiro.com.Mkoba.Management.System.utils;

import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class Response<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Builder.Default
    private ResponseStatus status = ResponseStatus.Success;

    private T data;

    @Builder.Default
    private String message = ResponseStatus.Success.toString();

    @Builder.Default
    private List<String> warnings = new ArrayList<>();

//    public  static <T> Response<T> audit(T data, ModuleEnums module, String auditTitle, String auditDesc) {
//        AuditTrailRepository auditTrailRepository = SpringContext.getBean(AuditTrailRepository.class);
//        if(auditTrailRepository!=null)
//            try {
//                auditTrailRepository.save(new AuditTrail(module.toString(), auditTitle, auditDesc));
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        return new Response<T>(data);
//    }

    public Response(T data) {
        this.data = data;
        this.status = ResponseStatus.Success;
        this.message = status.toString();
    }

    public Response (Exception e){
        this.status = ResponseStatus.Error;
        message = Utils.getExceptionMessage(e);
    }

    public Response (String message) {
        this.status = ResponseStatus.Error;
        this.message = message;
    }

    public static <T> Response<T> success(T data){
        return new Response<T>(ResponseStatus.Success, data, "Success",null);
    }

    public static <T> Response<T> warning(T data, String message){
        return new Response<T>(ResponseStatus.Warning, data, message, null);
    }

    public static <T> Response<T> warning(T data, List<String> warnings){
        return new Response<T>(ResponseStatus.Warning, data, "Warning", warnings);
    }

    public static <T> Response<T> warning(T data, String message, List<String> warnings){
        return new Response<T>(ResponseStatus.Warning, data, message, warnings);
    }

    public static <T> Response<T> error(String message){
        return new Response<T>(ResponseStatus.Error, null, message,null);
    }

    public Boolean success(){
        return status.equals(ResponseStatus.Success);
    }
}
