package us.dit.consentimientos.service;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;

import us.dit.consentimientos.model.Signal;
import us.dit.consentimientos.service.controllers.SignalsController;
import org.springframework.beans.factory.annotation.Autowired;
import javax.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.SessionAttribute;
import us.dit.consentimientos.service.services.kie.KieUtilService;


@RestController
public class weblistener {

    @Autowired
	private KieUtilService kie;

    @PostMapping("/endpoint2")
    public ResponseEntity<String> handleEndpoint(@RequestBody String json) { /*Importar bundle de hapifhir*/
        System.out.println("Received JSON:");
        System.out.println(json);

        //-----
        /* nombre: uri plandefinition  Message: uri ServiceRequest */
        Signal signal = new Signal();
        signal.setName("signal1");
        signal.setMessage(json);

        kie.sendSignal(signal.getName(), signal.getMessage());
        //-----

        return ResponseEntity.ok("Received JSON");
    }
}
