package main.model.DTO;

public class SettingDTO {
    private boolean MULTIUSER_MODE;
    private boolean POST_PREMODERATION;
    private boolean STATISTICS_IS_PUBLIC;

    public boolean getMultiuserMode() {
        return MULTIUSER_MODE;
    }

    public void setMultiuserMode(boolean MULTIUSER_MODE) {
        this.MULTIUSER_MODE = MULTIUSER_MODE;
    }

    public boolean getPostPremoderation() {
        return POST_PREMODERATION;
    }

    public void setPostPremoderation(boolean POST_PREMODERATION) {
        this.POST_PREMODERATION = POST_PREMODERATION;
    }

    public boolean getStatisticsIsPublic() {
        return STATISTICS_IS_PUBLIC;
    }

    public void setStatisticsIsPublic(boolean STATISTICS_IS_PUBLIC) {
        this.STATISTICS_IS_PUBLIC = STATISTICS_IS_PUBLIC;
    }
}
