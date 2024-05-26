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

import org.hl7.fhir.r5.model.Bundle;
import org.hl7.fhir.r5.model.Bundle.BundleEntryComponent;
import org.hl7.fhir.r5.model.PlanDefinition;
import org.hl7.fhir.r5.model.ServiceRequest;
import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;

@RestController
public class listener_V2 {

    @Autowired
	private KieUtilService kie;

    @PostMapping("/endpoint")
    public ResponseEntity<String> handleEndpoint(@RequestBody String json) {
        System.out.println("Received JSON:");
        //System.out.println(json);

        // Pasamos JSON a FHIR Bundle
        FhirContext ctx = FhirContext.forR5();
        IParser parser = ctx.newJsonParser();
        Bundle bundle = parser.parseResource(Bundle.class, json);

        //Extraemos las URIs
        String serviceRequestUri = null;
        String planDefinitionUri = null;

        for (BundleEntryComponent entry : bundle.getEntry()) {
            if (entry.getResource() instanceof ServiceRequest) {
                serviceRequestUri = entry.getFullUrl();
            } else if (entry.getResource() instanceof PlanDefinition) {
                planDefinitionUri = entry.getFullUrl();
            }
        }
        System.out.println("ServiceRequest URI: " + serviceRequestUri);
        System.out.println("PlanDefinition URI: " + planDefinitionUri);

        //-----
        /* nombre: uri plandefinition  Message: uri ServiceRequest */
        if (planDefinitionUri != null && serviceRequestUri != null) {
            //cambiamos "/" por "_" ya que jbpm no acepta como nombre de la señal el caracter "/"
            String signalName = planDefinitionUri.replace("/", "_");

            Signal signal = new Signal();
            signal.setName(signalName);
            signal.setMessage(serviceRequestUri);

            kie.sendSignal(signal.getName(), signal.getMessage());
        } else {
            System.err.println("No se han encontrado las URI necesarias.");
        }
        //-----

        return ResponseEntity.ok("Received JSON");
    }
}

