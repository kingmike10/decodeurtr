package com.uqtr.decodeurtr.controller;

import com.uqtr.decodeurtr.dto.AdminClientDecodeursDTO;
import com.uqtr.decodeurtr.dto.CreateClientRequestDTO;
import com.uqtr.decodeurtr.service.client.ClientService;
import com.uqtr.decodeurtr.service.decodeur.DecodeurService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/admin/clients")
@CrossOrigin(origins = "*")
public class AdminClientController {

    private final ClientService clientService;
    private  final DecodeurService decodeurService;

    public AdminClientController(ClientService clientService, DecodeurService decodeurService) {
        this.clientService = clientService;
        this.decodeurService = decodeurService;
    }

    @PostMapping("/create")
    public ResponseEntity createClient(@RequestBody CreateClientRequestDTO request) {
        return ResponseEntity.ok( clientService.createClient(request));

    }

    @GetMapping("/{idClient}/decodeurs")
    public AdminClientDecodeursDTO getDecodeursByClient(@PathVariable Long idClient) {
        return decodeurService.getDecodeursByClient(idClient);
    }


    @GetMapping("/all")
    public ResponseEntity getAllClients() {
        return ResponseEntity.ok(clientService.getAllClients());
    }

        @DeleteMapping("/delete/{idClient}")
    public ResponseEntity deleteClient(@PathVariable Long idClient) {
            return ResponseEntity.ok(clientService.deleteClient(idClient));
        }
}
