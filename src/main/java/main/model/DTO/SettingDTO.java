package main.model.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SettingDTO {
    @JsonProperty("MULTIUSER_MODE")
    private boolean multiUserMode;

    @JsonProperty("POST_PREMODERATION")
    private boolean postPremoderation;

    @JsonProperty("STATISTICS_IS_PUBLIC")
    private boolean statisticsIsPublic;


    public boolean isMultiUserMode() {
        return multiUserMode;
    }

    public void setMultiUserMode(boolean multyUserMode) {
        this.multiUserMode = multyUserMode;
    }

    public boolean isPostPremoderation() {
        return postPremoderation;
    }

    public void setPostPremoderation(boolean postPremoderation) {
        this.postPremoderation = postPremoderation;
    }

    public boolean isStatisticsIsPublic() {
        return statisticsIsPublic;
    }

    public void setStatisticsIsPublic(boolean statisticsIsPublic) {
        this.statisticsIsPublic = statisticsIsPublic;
    }
}
