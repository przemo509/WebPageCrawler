package pl.ps.database;

import java.sql.Date;

public class ItemDto {
    private String configName;
    private Integer id;
    private String url;
    private Date creationDate;
    private Integer score;
    private Date scoreDate;
    private Date visitDate;

    public ItemDto(String configName, Integer id, String url, Date creationDate, Integer score, Date scoreDate, Date visitDate) {
        this.configName = configName;
        this.id = id;
        this.url = url;
        this.creationDate = creationDate;
        this.score = score;
        this.scoreDate = scoreDate;
        this.visitDate = visitDate;
    }

    public String getConfigName() {
        return configName;
    }

    public Integer getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public Date getScoreDate() {
        return scoreDate;
    }

    public void setScoreDate(Date scoreDate) {
        this.scoreDate = scoreDate;
    }

    public Date getVisitDate() {
        return visitDate;
    }

    public void setVisitDate(Date visitDate) {
        this.visitDate = visitDate;
    }
}
