package auth.sm.reactive.model.respModel;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ReqRespModel<T> implements ReqRespModelInterface<T>{
    private T data;
    private String message;
    @Override
    public T getData() {
        return this.data;
    }

    @Override
    public String getMessage() {
        return this.message;
    }
}
