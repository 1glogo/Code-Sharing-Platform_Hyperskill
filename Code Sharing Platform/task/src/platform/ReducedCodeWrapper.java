package platform;

import lombok.Data;

import java.util.UUID;

// purpose of this ReducedCodeWrapper is to simplify the CodeWrapper for the client's use
@Data
public class ReducedCodeWrapper {

    String code;
    String date;
    Long views;
    Long time;

    ReducedCodeWrapper (CodeWrapper codeWrapper) {
        this.code = codeWrapper.getCode();
        this.date = codeWrapper.getDate();
        this.views = codeWrapper.getViews();
        // time is set as remaining time from the request time
        long secondsNow = System.currentTimeMillis()/1000L;
        Long timeIn = codeWrapper.getTime();
        if( timeIn <= 0L)
        {
            this.time = 0L;
        }
        else
        {
            this.time = timeIn/1000L-secondsNow;
        }
    }
    ReducedCodeWrapper (String code, String date){
        this.code = code;
        this.date = date;
        this.views = 0L;
        this.time = 0L;

    }
}
