package platform;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
public class Service {


    public final CodeRepository codeRepository;
    @Autowired
    public Service(CodeRepository codeRepository){
        this.codeRepository = codeRepository;
    }

    //Code Repository function called up
    public CodeWrapper save(CodeWrapper toSave) {
        return codeRepository.save(toSave);
    }

    public CodeWrapper findCodeByUuid (UUID uuid) {
        return codeRepository.findCodeWrapperByUuid(uuid);
    }

    public void deleteCodeEntity (CodeWrapper codeWrapper) {
        codeRepository.delete(codeWrapper);
    }
    
    public List<CodeWrapper> findAllCode() {
        return codeRepository.findAll();
    }

    public List<CodeWrapper> findUnrestrictedCode(Boolean condition){
        return codeRepository.findCodeWrapperByUnrestricted(condition);
    }
    
    //Reduced Code Wrapper is the output required by the client
    //Database stores the CodeWrapper with full information
    //The CodeWrapper is reduced to ReducedCodeWrapper in order to meet client's requirements
    public ReducedCodeWrapper codeRequest (CodeWrapper currentCode) {
        long viewsRem = currentCode.getViews();
        
        //reviewing the time left for the CodeWrapper to be available to client
        long time = currentCode.getTime();
        long milliSecondsNow = System.currentTimeMillis();
        long timeRem = currentCode.getTime()-milliSecondsNow;

        //CodeWrapper having an NA delete status there are not restrictions
        String deleteStatus = currentCode.getDeleteStatus();
        if (deleteStatus.equals("NA")){
            return new ReducedCodeWrapper(currentCode);
        }
        //CodeWrapper with positive views and positive timeRem or 0 makes it available
        else if (viewsRem > 0L && (timeRem >= 0L || time == 0L))
        {
            //Option 1 for viewsRem over 1, to reduce the remaining viewsRem
            if (viewsRem > 1) {
                currentCode.setViews(viewsRem - 1);
                save(currentCode);
            } 
            //Option 2: change the restriction status, delete status to "yes" and views to 0
            //delete status "yes" are not to be deleted yet, this is an intermediary level between tes & delete in accordance
            //with JetBrains Tests
            else {
                currentCode.setViews(0L);
                currentCode.setUnrestricted(false);
                currentCode.setDeleteStatus("yes");
                save(currentCode);
            }
            //Updated CodeWrapper is changed ot Reduced version and issued to client
            return new ReducedCodeWrapper(currentCode);
        } 
        //Deleting of any elements having a "delete" status
        else if (deleteStatus.equals("delete")) {
            deleteCodeEntity(currentCode);
            return null;
        } 
        //Changing of deleteStatus as to delete
        else if (deleteStatus.equals("yes")) {
            currentCode.setDeleteStatus("delete");
            save(currentCode);
            return null;
        }
        //Positive timeRem & 0 viewsRem means that the CodeWrapper is available
        //NOTE that 0L in this case is 0L set at the set up of the wrapper as unlimited
        else if (timeRem>0L && viewsRem == 0L) {
            return new ReducedCodeWrapper(currentCode);
        }
        //CodeWrapper where the time has run out are being forwarded to a "yes" deleteStatus
        else if (timeRem<=0L && viewsRem == 0L) {
            currentCode.setDeleteStatus("yes");
            currentCode.setUnrestricted(false);
            save(currentCode);
            return null;
        } else{
            return null;
        }
    }

    public void unrestrictedStatusUpdateAll () {

        List<CodeWrapper> allCode = findAllCode();

        for (CodeWrapper i:allCode){
            long time = i.getTime();
            Boolean unrestrictedStatus = i.getUnrestricted();

            if (unrestrictedStatus && time < 0L) {
                i.setUnrestricted(false);
                save(i);
            }
        }
    }

    //Below function is only provided to produce Status 404 in @GetMapping("/code/{uuid}")
    //for one of the options specified by JetBrains Test
    @ResponseStatus(code = HttpStatus.NOT_FOUND, reason= "not found")
    public String setStatus404(){
        return "";
    }
    
    //GET request for CodeWrapper from the DB based on UUID and AIP "getCode"
    //Using apache freemarker, js script is provided to produce the AIP request
    //Layout is found at Code Sharing Platform/task/src/resources/templates
    @GetMapping("/code/{uuid}")
    private String displayCode(Model model, @PathVariable UUID uuid) {
        CodeWrapper codeItem = codeRepository.findCodeWrapperByUuid(uuid);
        ReducedCodeWrapper currentCode = codeRequest(codeItem);
        
        if (currentCode != null) {
            //Information to be passed on to freemarker html layouts
            model.addAttribute("date", currentCode.getDate());
            model.addAttribute("code", currentCode.getCode());
            model.addAttribute("views", currentCode.getViews());
            model.addAttribute("time", currentCode.getTime());
            // various layouts have been provided with minor amendments just to agree with
            //jetbrains tests
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

    //API to get CodeWraper base on UUID
    @ResponseBody
    @GetMapping("/api/code/{uuid}")
    private ResponseEntity <ReducedCodeWrapper> getCode(@PathVariable UUID uuid) {
        CodeWrapper currentCode = findCodeByUuid(uuid);
        ReducedCodeWrapper resultingCode = codeRequest(currentCode);
        if (resultingCode != null){
            return new ResponseEntity<>(resultingCode, HttpStatus.OK);
        } 
        //Null returns Not_Found in database
        else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @ResponseBody
    @PostMapping("/api/code/new")
    private Map<String, String> getNewCodeAPI(@RequestBody CodeWrapper newCode) {
        //Time input updated to CodeWrapper time in accordance with service logic
       long timeInput = newCode.getTime();
       long time;
       //negative or 0 timeInput means that the time is not a factor on the snippet
       if (timeInput <= 0L)
       {
           time = 0L;
       } else {
           time = timeInput;
       }
       //All args constructor used and time is provided as per the above
       CodeWrapper createdCodeWrapper = save(new
               CodeWrapper(newCode.getId(), newCode.getUuid(), newCode.getCode(),
               newCode.getDate(), newCode.getViews(), newCode.getUnrestricted(), time, newCode.getDeleteStatus()));
       return Map.of("id", createdCodeWrapper.getUuid().toString());
   }

    //API "getNewCodeAPI" is called by the GET request below following the users input
    //Layout is found at Code Sharing Platform/task/src/resources/templates
    @GetMapping("/code/new")
    private Object getNewCode() {
        return "create";
    }

    //API "getLatestCodes" is called by the GET request below following the users input
    //Layout is found at Code Sharing Platform/task/src/resources/templates
    @GetMapping("/code/latest")
    private String displayLatestCodes(Model model) {
        List <ReducedCodeWrapper> reverseWrappers = new ArrayList<>();
        //For loop is used for reversing the loop. Stream can be used alternatively
        for (int i = getLatestCodes().size() - 1; i >= 0; i--)
        {
            reverseWrappers.add(getLatestCodes().get(i));
        }

        model.addAttribute("wrappers",reverseWrappers);
        return "latest";
    }

    @ResponseBody
    @GetMapping("/api/code/latest")
    private List<ReducedCodeWrapper> getLatestCodes() {
        //List of wrappers
        List<ReducedCodeWrapper> wrappers = new ArrayList<>();

        //updating the unrestrictedStatus for all CodeWrappers
        unrestrictedStatusUpdateAll();

        //Creating list of all Unrestricted Code Wrappers
        int dataSize = this.findUnrestrictedCode(true).size();

        //Loop to add the latest 10 unrestricted codeWrappers to wrappers list ot be issued
        for (int i = dataSize - 1; i > (dataSize > 10 ? dataSize - 11 : -1); i--) {
            CodeWrapper currentCode = findUnrestrictedCode(true).get(i);
            wrappers.add(codeRequest(currentCode));
        }

        //CodeWrappers udpated to match client reqs
        //As long as the wrapper contains elements, a for loop adds all elements to a List of
        //reducedCodeWrappers
        //If wrapper is empty an emptyList is returned
        List<ReducedCodeWrapper> wrappersNoNull;
        if (wrappers.size()>0){
            wrappersNoNull = new ArrayList<>(wrappers);
        } else {
            wrappersNoNull = Collections.emptyList();
        }
        return wrappersNoNull;
    }

}