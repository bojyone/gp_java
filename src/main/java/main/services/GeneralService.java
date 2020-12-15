package main.services;

import main.model.DTO.CalendarDTO;
import main.model.DTO.PostCountInterface;
import main.model.DTO.SettingDTO;
import main.model.DTO.TagWeightInterface;
import main.model.entities.GlobalSetting;
import main.model.repositories.GlobalSettingRepository;
import main.model.repositories.PostRepository;
import main.model.repositories.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GeneralService {


    private final PostRepository postRepository;
    private final TagRepository tagRepository;
    private final GlobalSettingRepository setRepository;

    @Autowired
    public GeneralService(PostRepository postRepository, TagRepository tagRepository,
                          GlobalSettingRepository setRepository) {

        this.postRepository = postRepository;
        this.tagRepository = tagRepository;
        this.setRepository = setRepository;
    }

    public CalendarDTO getPostCalendar(Integer year) {
        CalendarDTO calendar = new CalendarDTO();

        List<PostCountInterface> counts = postRepository.findPostCountOfYear(year);
        Map<String, Integer> posts = new HashMap<>();

        for (PostCountInterface postCountDTO : counts) {
            posts.put(postCountDTO.getDate(), postCountDTO.getCount());
        }
        calendar.setYears(postRepository.findYears());
        calendar.setPosts(posts);

        return calendar;
    }

    public Map<String, List<TagWeightInterface>> getTagWeight() {
        Map<String, List<TagWeightInterface>> tags = new HashMap<>();
        tags.put("tags", tagRepository.findAllTagWeight());
        return tags;
    }


    public SettingDTO getSettings() {
        Iterable<GlobalSetting> settings = setRepository.findAll();
        SettingDTO globalSets = new SettingDTO();

        for (GlobalSetting set : settings) {
            if (set.getCode().equals("MULTIUSER_MODE")) {
                globalSets.setMultiuserMode(false);
                if (set.getValue().equals("YES")) {
                    globalSets.setMultiuserMode(true);
                }
            }
            else if (set.getCode().equals("POST_PREMODERATION")) {
                globalSets.setPostPremoderation(false);
                if (set.getValue().equals("YES")) {
                    globalSets.setPostPremoderation(true);
                }
            }
            else {
                globalSets.setStatisticsIsPublic(false);
                if (set.getValue().equals("YES")) {
                    globalSets.setStatisticsIsPublic(true);
                }
            }
        }

        return globalSets;
    }
}
