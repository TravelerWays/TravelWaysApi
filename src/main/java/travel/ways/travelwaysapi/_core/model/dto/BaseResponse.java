package travel.ways.travelwaysapi._core.model.dto;

import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.lang.Nullable;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BaseResponse {
    @NotNull
    private boolean isSuccess;
    @Nullable
    private String message;

    public static BaseResponse success() {
        return new BaseResponse(
                true,
                null
        );
    }

    public static BaseResponse success(String message) {
        return new BaseResponse(
                true,
                message
        );
    }

    public static BaseResponse fail(String message) {
        return new BaseResponse(
                false,
                message
        );
    }

    public static BaseResponse fail() {
        return new BaseResponse(
                false,
                null
        );
    }
}
