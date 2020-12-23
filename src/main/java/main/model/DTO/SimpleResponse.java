package main.model.DTO;

public class SimpleResponse {

    private boolean result;

    public SimpleResponse(boolean result) {
        this.result = result;
    }

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }
}
