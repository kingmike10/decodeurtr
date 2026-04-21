package com.uqtr.decodeurtr.controller;


import com.uqtr.decodeurtr.dto.ChaineRequestDTO;
import com.uqtr.decodeurtr.dto.AssignDecoderResponseDTO;
import com.uqtr.decodeurtr.service.decodeur.DecodeurService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("*")
@RequestMapping("api/decoder")
public class DecodeurController {

    public DecodeurService decodeurService;

    public DecodeurController(DecodeurService decodeurService) {
        this.decodeurService = decodeurService;
    }

    @GetMapping("/assigned")
    public ResponseEntity getAllAssignedDecoders() {
        return ResponseEntity.ok(decodeurService.getAllAssignedDecoders());
    }

    @GetMapping("/available")
    public ResponseEntity getAllUnassignedDecoders() {
        return ResponseEntity.ok(decodeurService.getAllAvailableDecoders());
    }

    @PutMapping("/assign/{decodeurId}/assigner/{clientId}")
    public ResponseEntity<AssignDecoderResponseDTO> assignerDecodeur(
            @PathVariable Long decodeurId,
            @PathVariable Long clientId) {

        return ResponseEntity.ok(
                decodeurService.assignDecoderToClient(clientId, decodeurId)
        );
    }

    @GetMapping("/client/{clientId}")
    public ResponseEntity getDecoderByClient(@PathVariable Long clientId) {
        return ResponseEntity.ok(decodeurService.getAllDecodersByClient(clientId));
    }

    @PutMapping("/retirer/{idDecodeur}")
    public ResponseEntity retirerDecodeur(@PathVariable Long idDecodeur) {
        return ResponseEntity.ok(decodeurService.removeDecoder(idDecodeur));
    }

    @GetMapping("/getEtat/{idDecodeur}")
    public ResponseEntity getEtatDecodeur(@PathVariable Long idDecodeur) {
        return ResponseEntity.ok(decodeurService.getEtatDecodeur(idDecodeur));
    }

    @PutMapping("/restart/{idDecodeur}")
    public ResponseEntity restartDecodeur(@PathVariable Long idDecodeur) {
        return ResponseEntity.ok(decodeurService.restartDecoder(idDecodeur));
    }

    @PostMapping("/ajouterChaine")
    public ResponseEntity ajouterChaine(@RequestBody ChaineRequestDTO request) {
        return ResponseEntity.ok(decodeurService.ajouterChaine(request));
    }

        @GetMapping("/getChaines/{idDecodeur}")
    public ResponseEntity getChaines(@PathVariable Long idDecodeur) {
            return ResponseEntity.ok(decodeurService.getChaines(idDecodeur));
        }

    @PostMapping("/retirerChaine")
    public ResponseEntity retirerChaine(@RequestBody ChaineRequestDTO request) {
        return ResponseEntity.ok(decodeurService.retirerChaine(request));
    }


}
