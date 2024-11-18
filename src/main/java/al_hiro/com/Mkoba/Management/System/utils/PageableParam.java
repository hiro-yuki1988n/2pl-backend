package al_hiro.com.Mkoba.Management.System.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageableParam {
    private String sortBy;
    private Sort.Direction sortDirection;
    private Integer size;
    private Integer page;
    private String searchParam;
    private Boolean isActive = true;

    public Pageable getPageable(Boolean sorted) {
        return pageable(sorted, false);
    }

    public Pageable getNativePageable(Boolean sorted) {
        return pageable(sorted, true);
    }

    private Pageable pageable(Boolean sorted, Boolean nativeQuery) {
        if(sortBy==null) sortBy = "createdAt";
        Sort sort = Sort.by(sortDirection != null ? sortDirection : Sort.Direction.DESC,
                nativeQuery ? Utils.camelCaseToSnakeCase(sortBy) : sortBy);
        return PageRequest.of(page == null || page < 0 ? 0 : page, size == null || size < 1 ? ResponsePage.DEFAULT_PAGE_SIZE : size, sorted?sort:Sort.unsorted());
    }

    public void setSort(String sortBy, Sort.Direction sortDirection){
        this.sortBy = sortBy;
        this.sortDirection = sortDirection;
    }

    public Boolean getIsActive(){
        return isActive == null || isActive;
    }

    public String key() {
        return searchParam != null ? searchParam.toLowerCase() : "";
    }

}