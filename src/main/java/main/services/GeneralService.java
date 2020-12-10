package main.services;

import main.model.DTO.CalendarDTO;
import main.model.repositories.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GeneralService {


    private final PostRepository postRepository;

    @Autowired
    public GeneralService(PostRepository postRepository) {

        this.postRepository = postRepository;
    }

    public CalendarDTO getPostCalendar(Integer year) {
        CalendarDTO calendar = new CalendarDTO();


        List<String> dates = postRepository.findDatePostOfYear(year);
        List<Integer> counts = postRepository.findPostCountOfYear(year);
        Map<String, Integer> posts = new HashMap<>();

        int size = dates.size();

        for (int i = 0; i < size; i++) {

            posts.put(dates.get(i), counts.get(i));
        }
        calendar.setYears(postRepository.findYears());
        calendar.setPosts(posts);

        return calendar;
    }
}
