package main.services;

import main.model.DTO.*;
import main.model.entities.GlobalSetting;
import main.model.entities.Post;
import main.model.entities.User;
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

    public Map<String, List<TagWeightInterface>> getTagWeight(String query) {
        Map<String, List<TagWeightInterface>> tags = new HashMap<>();

        if (query == null) {
            tags.put("tags", tagRepository.findAllTagWeight());
        }
        else {
            tags.put("tags", tagRepository.findTagWeight(query));
        }
        return tags;
    }


    public SettingDTO getSettings() {
        Iterable<GlobalSetting> settings = setRepository.findAll();
        SettingDTO globalSets = new SettingDTO();

        for (GlobalSetting set : settings) {
            if (set.getCode().equals("MULTIUSER_MODE")) {
                globalSets.setMultiUserMode(false);
                if (set.getValue().equals("YES")) {
                    globalSets.setMultiUserMode(true);
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


    public void editGlobalSettings(SettingDTO settings) {

        Iterable<GlobalSetting> currentSettings = setRepository.findAll();

        for (GlobalSetting set : currentSettings) {
            if (set.getCode().equals("MULTIUSER_MODE")) {
                if (settings.isMultiUserMode() && set.getValue().equals("NO")) {
                    set.setValue("YES");
                }
                else if (!settings.isMultiUserMode() && set.getValue().equals("YES")) {
                    set.setValue("NO");
                }
            }
            else if (set.getCode().equals("POST_PREMODERATION")) {
                if (settings.isPostPremoderation() && set.getValue().equals("NO")) {
                    set.setValue("YES");
                }
                else if (!settings.isPostPremoderation() && set.getValue().equals("YES")) {
                    set.setValue("NO");
                }
                else {
                    if (settings.isStatisticsIsPublic() && set.getValue().equals("NO")) {
                        set.setValue("YES");
                    }
                    else if (!settings.isStatisticsIsPublic() && set.getValue().equals("YES")) {
                        set.setValue("NO");
                    }
                }
            }
        }
    }


    public StatisticInterface getAllStatistics(User user) {

        if (setRepository.findPermissions().equals("YES") || (user != null && user.getIsModerator() == 1)) {
            return postRepository.findAllStatistics();
        }
        return null;
    }


    public StatisticInterface getUserStatistics(User user) {

        return postRepository.findUserStatistics(user.getId());
    }


    public SimpleResponse moderatorAct(ModeratorAction action, User moderator) {

        Post post = postRepository.findDetailPost(action.getPostId());
        post.setModerationStatus(action.getDecision().equals("accept") ? "ACCEPTED" : "DECLINED");
        post.setModerator(moderator.getId());
        return new SimpleResponse(true);
    }
}
