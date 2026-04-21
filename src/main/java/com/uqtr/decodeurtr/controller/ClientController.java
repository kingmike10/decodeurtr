package com.uqtr.decodeurtr.controller;
import com.uqtr.decodeurtr.dto.ClientDashboardDTO;
import com.uqtr.decodeurtr.service.client.ClientService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/clients")
public class ClientController {

    private final ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @GetMapping("/dashboard/{identifiant}")
    public ResponseEntity<ClientDashboardDTO> getDashboard(@PathVariable String identifiant) {
        try {
            ClientDashboardDTO dashboard = clientService.getDashboardData(identifiant);
            return ResponseEntity.ok(dashboard);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
