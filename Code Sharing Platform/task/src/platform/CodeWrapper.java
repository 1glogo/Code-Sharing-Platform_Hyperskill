package platform;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Entity
@Table(name="code")
public class CodeWrapper {
    @Id
    @GeneratedValue
    private Long id;

    @Column
    private UUID uuid;
    @Column
    private String code;
    @Column
    private String date;
    @Column
    private Long views;
    @Column
    private Boolean unrestricted;
    //Time is stored as the current time of input
    @Column
    private Long time;
    @Column
    //"NA": when 0 time and 0 views, "yes": it should be picked up and set to "delete", "no":currently ok, "delete: remove
    private String deleteStatus;

    //Default Constructor
    public CodeWrapper() {
        this.code = "";
        this.date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"));
        this.uuid = UUID.randomUUID();
        this.views = 0L;
        this.unrestricted = true;
        this.time=0L;
        this.deleteStatus= "NA";
    }

    //All arguments constructor
    public CodeWrapper(Long id, UUID uuid, String code, String date,
                       Long views, Boolean unrestricted, Long time, String deleteStatus) {
        this.id = id;
        this.uuid = uuid;
        this.code=code;
        this.date= date;

        //The restricted Status is subject to limits set for views and time
        //0 views and 0 time means an unrestricted file with no delete option
        if((views==0L) && (time == 0L)){
            this.views =0L;
            this.unrestricted = true;
            this.deleteStatus = "NA";
            this.time = 0L;
        }
        // 0 views and posistive time sets it as unrestricted
        // and not to be deleted at this moment in time until time runs out
        else if((views==0L) && time>0L)
        {
            this.views =0L;
            this.unrestricted = true;
            this.deleteStatus = "no";
            long milliSecondsNow = System.currentTimeMillis();
            this.time = milliSecondsNow + time*1000L;
        }
        // + views and 0 time sets it as unrestricted
        // and not to be deleted at this moment in time until views run out
        else if (views > 0L && (time == 0L))
        {
            this.views =views;
            this.unrestricted = true;
            this.deleteStatus = "no";
            this.time = 0L;
        }
        //default option for any other option is set below
        else
        {
            this.views = views;
            this.unrestricted = unrestricted;
            this.deleteStatus = "no";
            long milliSecondsNow = System.currentTimeMillis();
            this.time = milliSecondsNow + time*1000L;
        }
    }

    //Getters and setters provided due to severe performance and memory consumption issues when used with JPA


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Long getViews() {
        return views;
    }

    public void setViews(Long views) {
        this.views = views;
    }

    public Boolean getUnrestricted() {
        return unrestricted;
    }

    public void setUnrestricted(Boolean unrestricted) {
        this.unrestricted = unrestricted;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public String getDeleteStatus() {
        return deleteStatus;
    }

    public void setDeleteStatus(String deleteStatus) {
        this.deleteStatus = deleteStatus;
    }
}