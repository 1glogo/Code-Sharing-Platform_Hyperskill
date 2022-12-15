package platform;

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
    @Column
    private Long time;
    @Column
    //"NA": when 0 time and 0 views, "yes": it should be picked up and set to "delete", "no":currently ok, "delete: remove
    private String deleteStatus;

    public CodeWrapper() {
        this.code = "";
        this.date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"));
        this.uuid = UUID.randomUUID();
        this.views = 0L;
        this.unrestricted = true;
        this.time=0L;
        this.deleteStatus= "NA";
    }

   /* public CodeWrapper(String code, Long time, Long views) {
        this.code = code;
        this.date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"));
        this.uuid = UUID.randomUUID();
        if((views<=0L || views == null) && (time <= 0L || time ==null)){
            this.views =0L;
            this.unrestricted = true;
            this.deleteStatus = "NA";
            this.time = 0L;
        }
        else if((views<=0L || views == null) && time>0L)
        {
            this.views =0L;
            this.unrestricted = true;
            this.deleteStatus = "no";
            long secondsNow = System.currentTimeMillis() / 1000L;
            this.time = secondsNow + time;
        }
        else if (views>0L && (time <= 0L || time ==null))
        {
            this.views =views;
            this.unrestricted = true;
            this.deleteStatus = "no";
            this.time = 0L;
        }
        else
        {
            this.views = views;
            this.unrestricted = true;
            this.deleteStatus = "no";
            long secondsNow = System.currentTimeMillis() / 1000L;
            this.time = secondsNow + time;
        }
        System.out.println("3 constructor used");
    }
*/
    public CodeWrapper(Long id, UUID uuid, String code, String date,
                       Long views, Boolean unrestricted, Long time, String deleteStatus) {
        this.id = id;
        this.uuid = uuid;
        this.code=code;
        this.date= date;
        if((views==0L || views == null) && (time == 0L || time ==null)){
            this.views =0L;
            this.unrestricted = true;
            this.deleteStatus = "NA";
            this.time = 0L;
        }
        else if((views==0L || views == null) && time>0L)
        {
            this.views =0L;
            this.unrestricted = true;
            this.deleteStatus = "no";
            long milliSecondsNow = System.currentTimeMillis();
            this.time = milliSecondsNow + time*1000L;
        }
        else if (views > 0L && (time == 0L || time ==null))
        {
            this.views =views;
            this.unrestricted = true;
            this.deleteStatus = "no";
            this.time = 0L;
        }
        else
        {
            this.views = views;
            this.unrestricted = unrestricted;
            this.deleteStatus = "no";
            long milliSecondsNow = System.currentTimeMillis();
            this.time = milliSecondsNow + time*1000L;
        }

        System.out.println("full constructor used");
        System.out.println("     codeNameInput = " + code);
        System.out.println("     timeInput = " + time);
        System.out.println("     viewsInput = " + views);
    }


    public String getDeleteStatus() {
        return deleteStatus;
    }

    public void setDeleteStatus(String deleteStatus) {
        this.deleteStatus = deleteStatus;
    }

    public Long getTime() {
        return time;
    }

    public void setUnrestricted(Boolean unrestricted) {
        this.unrestricted = unrestricted;
    }
    public Boolean getUnrestricted() {
        return unrestricted;
    }

    public void setViews(Long views) {
        this.views = views;
    }

    public Long getViews() {
        return views;
    }

    /*    public void setCode(String code) {
        this.code = code;
        this.date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"));
    }*/

    public Long getId() {
        return id;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getCode() { return code; }
    public String getDate() { return date; }
}