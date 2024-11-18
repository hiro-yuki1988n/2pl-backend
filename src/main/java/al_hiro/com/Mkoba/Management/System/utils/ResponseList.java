package al_hiro.com.Mkoba.Management.System.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ResponseList<T> {

	private ResponseStatus status = ResponseStatus.Success;

	private List<T> data;

	private String message = "Success";

	public ResponseList(List<T> data) {

		this.data = data;
	}

	public ResponseList(String message) {
		this.status = ResponseStatus.Error;
		this.message = message;
	}

	public ResponseList(Exception e){
		status = ResponseStatus.Error;
		message = Utils.getExceptionMessage(e);
	}

    public static <T>ResponseList<T> error(String string) {
		return new ResponseList<>(string);
    }

}
