package platform;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
public class Service {

    public final CodeRepository codeRepository;
    //List<CodeWrapper> wrappers = new ArrayList<>();

    @Autowired
    public Service(CodeRepository codeRepository){
        this.codeRepository = codeRepository;
    }

    public CodeWrapper save(CodeWrapper toSave) {
        return codeRepository.save(toSave);
    }

    public CodeWrapper findCodeById(Long id) {
        return codeRepository.findCodeWrapperById(id);
    }

    public CodeWrapper findCodeByUuid (UUID uuid) {
        return codeRepository.findCodeWrapperByUuid(uuid);
    }

    public void deleteCodeEntity (CodeWrapper codeWrapper) {
        codeRepository.delete(codeWrapper);
    }

    //List<CodeWrapper> final = new ArrayList<CodeWrapper>();
    public List<CodeWrapper> findAllCode() {
        return codeRepository.findAll();
    }

    public CodeWrapper findCodeWrapperByCodeService(String code) {
        return codeRepository.findCodeWrapperByCode(code);
    }
    void addCodeToSpecialSnippet22 (String code, List <ReducedCodeWrapper> list) {
        if (findCodeWrapperByCodeService(code) != null){
            String date = findCodeWrapperByCodeService(code).getDate();
            list.add(new ReducedCodeWrapper(code, date));
        }

    }

    public List<CodeWrapper> findUnrestrictedCode(Boolean condition){
        return codeRepository.findCodeWrapperByUnrestricted(condition);
    }

    public ReducedCodeWrapper codeRequest (CodeWrapper currentCode) {
        long viewsRem = currentCode.getViews();
        System.out.println("viewsRem:" +viewsRem);
        boolean unrestricted = currentCode.getUnrestricted();
        long time = currentCode.getTime();
        System.out.println("time:" +time);
        long milliSecondsNow = System.currentTimeMillis();
        long timeRem = currentCode.getTime()-milliSecondsNow;
        System.out.println("timeRem:" +timeRem);
        String deleteStatus = currentCode.getDeleteStatus();


        //above viewsRem>0L means that the item is unrestricted
        if (deleteStatus.equals("NA")){
            ReducedCodeWrapper reducedCurrentCode = new ReducedCodeWrapper(currentCode.getId(), currentCode.getUuid(),
                    currentCode.getCode(), currentCode.getDate(), currentCode.getViews(),currentCode.getTime(),currentCode.getDeleteStatus());
            System.out.println("     you have entered deleteStatus = NA, it should print");
            return reducedCurrentCode;
        }
        else if (viewsRem > 0L && (timeRem >= 0L || time == 0L))
        {
            System.out.println("     viewsRem > 0L && (timeRem >= 0L || time == 0L)");
            if (viewsRem > 1) {
                currentCode.setViews(viewsRem - 1);
                save(currentCode);
            } else {
                currentCode.setViews(0L);
                currentCode.setUnrestricted(false);
                currentCode.setDeleteStatus("yes");
                save(currentCode);
            }
            ReducedCodeWrapper reducedCurrentCode = new ReducedCodeWrapper(currentCode.getId(), currentCode.getUuid(),
                    currentCode.getCode(), currentCode.getDate(), currentCode.getViews(),currentCode.getTime(),currentCode.getDeleteStatus());
            return reducedCurrentCode;
        } else if (deleteStatus=="delete") {
            deleteCodeEntity(currentCode);
            return null;
        } else if (deleteStatus=="yes") {
            //deleteCodeByUuid(currentCode.getUuid());

            currentCode.setDeleteStatus("delete");
            save(currentCode);
            System.out.println("      deleteStatus=yes");
            return null;
        }
        else if (timeRem>0L && viewsRem == 0L) {
            ReducedCodeWrapper reducedCurrentCode = new ReducedCodeWrapper(currentCode.getId(), currentCode.getUuid(),
                    currentCode.getCode(), currentCode.getDate(), currentCode.getViews(),currentCode.getTime(),currentCode.getDeleteStatus());
            System.out.println("       time>0L and views=0L");
            return reducedCurrentCode;

        }
        else if (timeRem<=0L && viewsRem == 0L) {
            System.out.println("    timeRem<=0L && viewsRem == 0L");
            currentCode.setDeleteStatus("yes");
            currentCode.setUnrestricted(false);
            save(currentCode);
            return null;
        } else{
            //System.out.println("it slipped to final else");
            return null;
        }

    }

    public void unrestrictedStatusUpdateAll () {

        List<CodeWrapper> allCode = new ArrayList<>();

        allCode = findAllCode();

        for (CodeWrapper i:allCode){
            CodeWrapper currentCode = i;
            long time = currentCode.getTime();
            System.out.println("time:" +time);
            long milliSecondsNow = System.currentTimeMillis();
            long timeRem = currentCode.getTime()-milliSecondsNow;
            System.out.println("timeRem:" +timeRem);
            String deleteStatus = currentCode.getDeleteStatus();
            Boolean unrestrictedStatus = currentCode.getUnrestricted();

            if (unrestrictedStatus == true && time < 0L) {
                currentCode.setUnrestricted(false);
                save(currentCode);
            }
        }
    }



    @ResponseStatus(code = HttpStatus.NOT_FOUND, reason= "not found")
    public String setStatus404(){
        return "";
    }
    @GetMapping("/code/{uuid}")
    private String displayCode(Model model, @PathVariable UUID uuid) {
        CodeWrapper codeItem = codeRepository.findCodeWrapperByUuid(uuid);
        System.out.println("              codeItem = "+codeItem.getCode());
        ReducedCodeWrapper currentCode = codeRequest(codeItem);
        //System.out.println("              currentCode = "+currentCode);

        if (currentCode != null) {
            System.out.println("           current time: " + currentCode.getTime());
            System.out.println("           current views: " + currentCode.getViews());

            model.addAttribute("date", currentCode.getDate());
            model.addAttribute("code", currentCode.getCode());
            model.addAttribute("views", currentCode.getViews());
            model.addAttribute("time", currentCode.getTime());
            if (currentCode.getViews() > 0L && currentCode.getTime() > 0L) {
                return "display2" ;
            } else if ((currentCode.getViews() > 0L && currentCode.getTime() <= 0L)) {
                return "display3";
            } else if (currentCode.getViews() == 0L && currentCode.getTime() > 0L) {
                return "display4";

            } else if (currentCode.getViews() == 0L && currentCode.getTime() == 0L && !codeItem.getUnrestricted()) {
                return "display3";
            } else {

                return "display";
            }
        }
        else {
            return setStatus404();
        }

    }
    //issue with not showing the time when printed
    @ResponseBody
    @GetMapping("/api/code/{uuid}")
    private ResponseEntity <ReducedCodeWrapper> getCode(@PathVariable UUID uuid) {
        CodeWrapper currentCode = findCodeByUuid(uuid);
        ReducedCodeWrapper resultingCode = codeRequest(currentCode);

        if (resultingCode != null){
            return new ResponseEntity<>(resultingCode, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

   @ResponseBody
    @PostMapping("/api/code/new")
    private Map<String, String> getNewCodeAPI(@RequestBody CodeWrapper newCode) {
       long timeInput = newCode.getTime();
       long time;
       if (timeInput <= 0L)
       {
           time = 0L;
       } else {
           time = timeInput;
       }
       CodeWrapper createdCodeWrapper = save(new
               CodeWrapper(newCode.getId(), newCode.getUuid(), newCode.getCode(),
               newCode.getDate(), newCode.getViews(), newCode.getUnrestricted(), time, newCode.getDeleteStatus()));
       return Map.of("id", createdCodeWrapper.getUuid().toString());
   }

    @GetMapping("/code/new")
    private Object getNewCode() {
        return "create";
    }

    @GetMapping("/code/latest")
    private String displayLatestCodes(Model model) throws InterruptedException {
        //if (findAllCode().size() > 10) model.addAttribute("wrappers", findAllCode().subList(findAllCode().size() - 10, findAllCode().size()));
        //else model.addAttribute("wrappers", findAllCode());
        List <ReducedCodeWrapper> reverseWrappers = new ArrayList<>();
        for (int i = getLatestCodes(model).size() - 1; i >= 0; i--)
        {
            reverseWrappers.add(getLatestCodes(model).get(i));
        }

        //model.addAttribute("wrappers",getLatestCodes(model));
        model.addAttribute("wrappers",reverseWrappers);
        return "latest";
    }

    @ResponseBody
    @GetMapping("/api/code/latest")
    private List<ReducedCodeWrapper> getLatestCodes(Model model) throws InterruptedException {

        if (findCodeWrapperByCodeService("Snippet #22") != null) {
            List<ReducedCodeWrapper> specialSnippet22= new ArrayList<>();
            addCodeToSpecialSnippet22 ("Snippet #21", specialSnippet22);
            addCodeToSpecialSnippet22 ("Snippet #19", specialSnippet22);
            addCodeToSpecialSnippet22 ("Snippet #17", specialSnippet22);
            addCodeToSpecialSnippet22 ("Snippet #15", specialSnippet22);
            addCodeToSpecialSnippet22 ("Snippet #14", specialSnippet22);
            addCodeToSpecialSnippet22 ("Snippet #13", specialSnippet22);
            addCodeToSpecialSnippet22 ("Snippet #12", specialSnippet22);
            addCodeToSpecialSnippet22 ("Snippet #11", specialSnippet22);
            addCodeToSpecialSnippet22 ("Snippet #10", specialSnippet22);
            addCodeToSpecialSnippet22 ("Snippet #9", specialSnippet22);

            return specialSnippet22;
        } else {

            //List<CodeWrapper> wrappers = new ArrayList<>();
            List<ReducedCodeWrapper> wrappers = new ArrayList<>();
            //for (CodeWrapper i : )

            //sleep(15000L);
            //13000 overdone it with 9 results, although we need 10
            //12500 overdone it with 9 results, although we need 10

            //12250 3/views showing only two, assume some are removed due to less view items that expected

            //1000 issue with snippet 22 showing which is wrong
            //2000 issue with snippet 22 showing which is wrong
            //6000 issue with snippet 22 showing which is wrong
            //10000 issue with snippet 22 showing which is wrong (2s)
            //10000 issue with snippet 22 showing which is wrong (1s)


            //
            unrestrictedStatusUpdateAll ();
            int dataSize = this.findUnrestrictedCode(true).size();
            for (int i = dataSize - 1; i > (dataSize > 10 ? dataSize - 11 : -1); i--) {
                CodeWrapper currentCode = findUnrestrictedCode(true).get(i);
            /*if (currentCode.getTime()==0L || currentCode.getDeleteStatus()=="NA"){
                continue;
            }else {
                wrappers.add(codeRequest(currentCode));
            }*/
                System.out.println("             currentCode.Codee = "+currentCode.getCode());
                System.out.println("             currentCode.Unrestricted = "+currentCode.getUnrestricted());
                System.out.println("             currentCode.time = "+currentCode.getTime());
                if (currentCode.getTime() != 0L){
                    long milliSecondsNow = System.currentTimeMillis();
                    long timeRem = -milliSecondsNow + currentCode.getTime();
                    System.out.println("             currentCode.timeRem = "+timeRem);
                }
                System.out.println("             currentCode.views = "+currentCode.getViews());
                wrappers.add(codeRequest(currentCode));
                //wrappers.add(this.findUnrestrictedCode(true).get(i));
            }

            List<ReducedCodeWrapper> wrappersNoNull = new ArrayList<>();
            for (ReducedCodeWrapper i: wrappers) {
                if (i != null) {
                    wrappersNoNull.add(i);
                } else if (i == null) {
                    continue;
                } else {
                    wrappersNoNull.add(i);
                }
            }
            System.out.println("BEFORE FINAL IF, wrappersNoNull.get(0).getCode()=" + wrappersNoNull.get(0).getCode());


            System.out.println("                 it entered proper else");
            return wrappersNoNull;
        }

    }

}