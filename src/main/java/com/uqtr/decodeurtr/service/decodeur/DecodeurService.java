package com.uqtr.decodeurtr.service.decodeur;

import com.uqtr.decodeurtr.dto.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface DecodeurService {

    OperationDecodeurResponseDTO retirerChaine(ChaineRequestDTO request);
    OperationDecodeurResponseDTO ajouterChaine(ChaineRequestDTO request);
    List<AllDecodersByClientResponseDTO> getAllDecodersByClient(Long idClient);
    AssignDecoderResponseDTO assignDecoderToClient(Long clientId,  Long decodeurId);
    List<DecoderAssignedResponseDTO> getAllAssignedDecoders();
    List<DecoderAvailableResponseDTO> getAllAvailableDecoders();
    RemoveDecoderResponseDTO removeDecoder(Long decoderId);
    EtatDecodeurResponseDTO getEtatDecodeur(Long decodeurId);
    ResetDecodeurResponseDTO resetDecoder(Long decodeurId);
    GetChainesResponseDTO getChaines(Long idDecodeur);

}
