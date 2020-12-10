package main.model.DTO;

import java.util.List;
import java.util.Map;

public class CalendarDTO {

    private List<Integer> years;
    private Map<String, Integer> posts;

    public List<Integer> getYears() {
        return years;
    }

    public void setYears(List<Integer> years) {
        this.years = years;
    }

    public Map<String, Integer> getPosts() {
        return posts;
    }

    public void setPosts(Map<String, Integer> posts) {
        this.posts = posts;
    }
}
