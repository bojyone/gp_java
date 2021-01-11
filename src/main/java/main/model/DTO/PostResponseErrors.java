package main.model.DTO;

import java.util.Map;

public class PostResponseErrors {
    private Boolean result;
    private Map<String, String> errors;

    public PostResponseErrors() {}

    public PostResponseErrors(Boolean result, Map<String, String> errors) {
        this.result = result;
        this.errors = errors;
    }
    public Boolean getResult() {
        return result;
    }

    public void setResult(Boolean result) {
        this.result = result;
    }

    public Map<String, String> getErrors() {
        return errors;
    }

    public void setErrors(Map<String, String> errors) {
        this.errors = errors;
    }
}
