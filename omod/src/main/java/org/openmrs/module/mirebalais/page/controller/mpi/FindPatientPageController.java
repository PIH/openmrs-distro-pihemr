package org.openmrs.module.mirebalais.page.controller.mpi;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.emr.EmrConstants;
import org.openmrs.module.emr.utils.GeneralUtils;
import org.openmrs.module.importpatientfromws.RemotePatient;
import org.openmrs.module.importpatientfromws.api.ImportPatientFromWebService;
import org.openmrs.module.mirebalais.MirebalaisConstants;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FindPatientPageController {

    private final Log log = LogFactory.getLog(getClass());

    private void saveToCache(List<RemotePatient> remotePatients, HttpSession session) {
        Map<String, RemotePatient> mpiSearchResults = (Map<String, RemotePatient>) session.getAttribute(MirebalaisConstants.MPI_SEARCH_RESULTS);
        if (mpiSearchResults == null) {
            mpiSearchResults = new HashMap<String, RemotePatient>();
            session.setAttribute(MirebalaisConstants.MPI_SEARCH_RESULTS, mpiSearchResults);
        }
        if(remotePatients!=null && remotePatients.size()>0){
            for(RemotePatient remotePatient : remotePatients){
                mpiSearchResults.put(MirebalaisConstants.MPI_REMOTE_SERVER + ":" + remotePatient.getRemoteUuid(), remotePatient);
            }
        }
    }

    private RemotePatient getFromCache(String uuid, HttpSession session){

        RemotePatient remotePatient = null;
        Map<String, RemotePatient> mpiSearchResults = (Map<String, RemotePatient>) session.getAttribute(MirebalaisConstants.MPI_SEARCH_RESULTS);
        if(mpiSearchResults!=null && mpiSearchResults.size()>0){
            remotePatient = mpiSearchResults.get(MirebalaisConstants.MPI_REMOTE_SERVER + ":" + uuid);
        }
        return remotePatient;
    }

    private RemotePatient removeFromCache(String uuid, HttpSession session){

        RemotePatient remotePatient = null;
        Map<String, RemotePatient> mpiSearchResults = (Map<String, RemotePatient>) session.getAttribute(MirebalaisConstants.MPI_SEARCH_RESULTS);
        if(mpiSearchResults!=null && mpiSearchResults.size()>0){
            String key = MirebalaisConstants.MPI_REMOTE_SERVER + ":" + uuid;
            remotePatient = mpiSearchResults.get(key);
            if(remotePatient!=null){
                mpiSearchResults.remove(key);
            }
        }
        return remotePatient;
    }

    public void get(PageModel model,
                    @SpringBean ImportPatientFromWebService webService,
                    @RequestParam(required = false, value="id") String id,
                    @RequestParam(required = false, value = "name") String name,
                    @RequestParam(required = false, value = "gender") String gender,
                    HttpSession session) {

        List<RemotePatient> results = null;
        try {
            if (StringUtils.isNotBlank(id)) {
                results = webService.searchRemoteServer(MirebalaisConstants.MPI_REMOTE_SERVER, id);
            }else if(StringUtils.isNotBlank(name)){
                results = webService.searchRemoteServer(MirebalaisConstants.MPI_REMOTE_SERVER, name, gender, null);
            }
            if(results!=null && results.size()>0){
                saveToCache(results, session);
                model.addAttribute("addressHierarchyLevels", GeneralUtils.getAddressHierarchyLevels());
            }
        } catch (Exception e) {
            log.error("Error communicating with remote server", e);
            session.setAttribute(EmrConstants.SESSION_ATTRIBUTE_ERROR_MESSAGE, "Error communicating with remote server. (See log for details.)");
        }

        model.addAttribute("results", results);
    }

    public String post(@RequestParam(required=false, value="remoteServer") String remoteServer,
                       @RequestParam("remoteUuid") String remoteUuid,
                       UiUtils ui,
                       HttpServletRequest request
                       ) {

        if(StringUtils.isNotBlank(remoteUuid)){
            HttpSession session = request.getSession();
            RemotePatient remotePatient = getFromCache(remoteUuid, session);
            if(remotePatient!=null){
                //import the patient
                try{
                    Patient patient = Context.getPatientService().savePatient(remotePatient.getPatient());
                    if(patient!=null){
                        removeFromCache(remoteUuid, session);
                        return "redirect:" + ui.pageLink("emr", "patient?patientId=" + patient.getId().toString());
                    }
                }catch(Exception e){
                    log.error("failed to import patient", e);
                    request.getSession().setAttribute(EmrConstants.SESSION_ATTRIBUTE_ERROR_MESSAGE, e);
                }

            }
        }
        return "redirect:" + ui.pageLink("mirebalais/mpi", "findPatient");

    }
}
