package com.uqtr.decodeurtr.controller;

import com.uqtr.decodeurtr.dto.CreateClientRequestDTO;
import com.uqtr.decodeurtr.dto.CreateClientResponseDTO;
import com.uqtr.decodeurtr.service.client.ClientService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/admin/clients")
@CrossOrigin(origins = "*")
public class AdminClientController {

    private final ClientService clientService;

    public AdminClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @PostMapping("/create")
    public ResponseEntity<CreateClientRequestDTO> createClient(@RequestBody CreateClientRequestDTO request) {
        CreateClientResponseDTO response = clientService.createClient(request);
        return new ResponseEntity(response, HttpStatus.CREATED);
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
