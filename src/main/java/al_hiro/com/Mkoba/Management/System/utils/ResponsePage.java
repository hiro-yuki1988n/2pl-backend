package al_hiro.com.Mkoba.Management.System.utils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.leangen.graphql.annotations.GraphQLIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ResponsePage<T> extends Response<List<T>> {

    @GraphQLIgnore
    @JsonIgnore
    public static final Integer DEFAULT_PAGE_SIZE = 10;

    private Integer elements; // Total number of elements
    private Integer size = DEFAULT_PAGE_SIZE; // Elements in one page
    private Integer pages = 0; // Number of pages
    private Integer page = 1; // Current page

//    public  static <T> ResponsePage<T> audit(Page<T> _page, ModuleEnums module, String auditTitle, String auditDesc) {
//        AuditTrailRepository auditTrailRepository = SpringContext.getBean(AuditTrailRepository.class);
//        if(auditTrailRepository!=null)
//            try {
//                auditTrailRepository.save(new AuditTrail(module.toString(), auditTitle, auditDesc));
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        return new ResponsePage<T>(_page);
//    }

    public ResponsePage(Page<T> _page) {
        super(_page.getContent());
        setStatus(ResponseStatus.Success);
        setMessage(ResponseStatus.Success.toString());
        pages = _page.getTotalPages();
        elements = (int) _page.getTotalElements();
        size = _page.getSize();
        page = _page.getNumber() + 1;
    }

    public ResponsePage (Exception e){
        setStatus(ResponseStatus.Error);
        setMessage(Utils.getExceptionMessage(e));
    }

    public ResponsePage(String message) {
        setStatus(ResponseStatus.Error);
        setMessage(message);
    }

    public ResponsePage(Page<T> _page, String message) {
        super(_page.getContent());
        setMessage(message);
        setStatus(ResponseStatus.Warning);
        pages = _page.getTotalPages();
        elements = (int) _page.getTotalElements();
        size = _page.getSize();
        page = _page.getNumber() + 1;
    }

    public Page<T> convertListToPage(List<T> list, Pageable pageable) {
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), list.size());
        return new PageImpl<>(list.subList(start, end), pageable, list.size());
    }
}
