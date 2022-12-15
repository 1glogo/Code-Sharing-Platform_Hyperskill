package platform;

import java.util.UUID;

public class ReducedCodeWrapper {

    String code;
    String date;
    Long views;
    Long time;


    public String getCode() {
        return code;
    }

    public String getDate() {
        return date;
    }

    public Long getViews() {return views;}

    public Long getTime() { return time;}


    ReducedCodeWrapper (Long id, UUID uuid, String code, String date, Long views, Long time, String deleteStatus) {
        this.code = code;
        this.date = date;
        this.views = views;
        long secondsNow = System.currentTimeMillis()/1000L;
        /*if (code.equals("Snippet #22") && time/1000L-secondsNow == 12L) {
            this.time = 0L;
        }
        else */if( time <= 0L)
        {
            this.time = 0L;
        }
        else
        {

            this.time = time/1000L-secondsNow;
        }


    }

    ReducedCodeWrapper (String code, String date){
        this.code = code;
        this.date = date;
        this.views = 0L;
        this.time = 0L;

    }
}
