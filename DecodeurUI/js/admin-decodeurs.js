let selectedDecodeurId = null;
let clientsCache = [];

function isDecoderOnline(etat) {
    return etat === "EN_LIGNE";
}

function getStatusLabel(etat) {
    return isDecoderOnline(etat) ? "En ligne" : "Hors ligne";
}

function getStatusClass(etat) {
    return isDecoderOnline(etat) ? "online" : "offline";
}

async function fetchAssignedDecoders() {
    const response = await fetch("http://localhost:8080/api/decoder/assigned", {
        method: "GET",
        headers: {
            "Content-Type": "application/json"
        }
    });

    if (!response.ok) {
        throw new Error("Impossible de charger les décodeurs attribués");
    }

    return await response.json();
}

async function fetchAvailableDecoders() {
    const response = await fetch("http://localhost:8080/api/decoder/available", {
        method: "GET",
        headers: {
            "Content-Type": "application/json"
        }
    });

    if (!response.ok) {
        throw new Error("Impossible de charger les décodeurs disponibles");
    }

    return await response.json();
}

async function fetchClients() {
    const response = await fetch("http://localhost:8080/api/admin/clients/all", {
        method: "GET",
        headers: {
            "Content-Type": "application/json"
        }
    });

    if (!response.ok) {
        throw new Error("Impossible de charger les clients");
    }

    return await response.json();
}

function renderAssignedDecoders(decodeurs) {
    const grid = document.getElementById("assigned-grid");
    const count = document.getElementById("assigned-count");

    if (!grid || !count) return;

    count.textContent = decodeurs.length;
    grid.innerHTML = "";

    if (!decodeurs.length) {
        grid.innerHTML = `<div class="empty-state">Aucun décodeur attribué.</div>`;
        return;
    }

    decodeurs.forEach(dec => {
        const statusLabel = getStatusLabel(dec.etat);
        const statusClass = getStatusClass(dec.etat);

        const card = `
            <div class="decoder-card">
                <div class="decoder-top">
                    <div class="decoder-left">
                        <div class="decoder-icon">
                            <i class="fas fa-tv"></i>
                        </div>
                        <div>
                            <p class="decoder-ip">${dec.adresseIp}</p>
                            <p class="decoder-client">${dec.nomClient || "Client inconnu"}</p>
                        </div>
                    </div>
                    <span class="status-badge ${statusClass}">${statusLabel}</span>
                </div>

                <div class="decoder-actions">
                    <button class="btn-secondary" onclick="voirClient(${dec.clientId})">Voir client</button>
                    <button class="btn-danger" onclick="retirerDecodeur(${dec.id})">Retirer</button>
                </div>
            </div>
        `;

        grid.innerHTML += card;
    });
}

function renderAvailableDecoders(decodeurs) {
    const grid = document.getElementById("available-grid");
    const count = document.getElementById("available-count");

    if (!grid || !count) return;

    count.textContent = decodeurs.length;
    grid.innerHTML = "";

    if (!decodeurs.length) {
        grid.innerHTML = `<div class="empty-state">Aucun décodeur disponible.</div>`;
        return;
    }

    decodeurs.forEach(dec => {
        const statusLabel = getStatusLabel(dec.etat);
        const statusClass = getStatusClass(dec.etat);

        const card = `
            <div class="decoder-card">
                <div class="decoder-top">
                    <div class="decoder-left">
                        <div class="decoder-icon">
                            <i class="fas fa-tv"></i>
                        </div>
                        <div>
                            <p class="decoder-ip">${dec.adresseIp}</p>
                            <p class="decoder-client">Disponible</p>
                        </div>
                    </div>
                    <span class="status-badge ${statusClass}">${statusLabel}</span>
                </div>

                <div class="decoder-actions">
                    <button class="btn-primary" onclick="openAssignModal(${dec.id}, '${dec.adresseIp}')">Assigner</button>
                </div>
            </div>
        `;

        grid.innerHTML += card;
    });
}

function voirClient(clientId) {
    if (!clientId) {
        alert("Client introuvable.");
        return;
    }

    window.location.href = `admin-client-decodeurs.html?idClient=${clientId}`;
}

async function retirerDecodeur(idDecodeur) {
    const confirmation = confirm("Voulez-vous vraiment retirer ce décodeur du client ?");

    if (!confirmation) {
        return;
    }

    try {
        const response = await fetch(`http://localhost:8080/api/decoder/retirer/${idDecodeur}`, {
            method: "PUT",
            headers: {
                "Content-Type": "application/json"
            }
        });

        const result = await response.json();

        if (!response.ok) {
            throw new Error(result.message || "Erreur lors du retrait du décodeur.");
        }

        alert(result.message || "Décodeur retiré avec succès.");
        await loadDecodersPage();
    } catch (error) {
        console.error("Erreur retrait décodeur :", error);
        alert(error.message || "Impossible de retirer le décodeur.");
    }
}

async function openAssignModal(idDecodeur, adresseIp) {
    try {
        selectedDecodeurId = idDecodeur;

        const modal = document.getElementById("assign-modal");
        const select = document.getElementById("client-select");
        const text = document.getElementById("assign-modal-text");

        if (!modal || !select || !text) {
            console.error("Éléments de modale introuvables");
            return;
        }

        text.textContent = `Choisissez un client pour le décodeur ${adresseIp}.`;
        select.innerHTML = `<option value="">-- Sélectionner un client --</option>`;

        clientsCache = await fetchClients();

        clientsCache.forEach(client => {
            const option = document.createElement("option");
            option.value = client.id;
            option.textContent = client.nomClient;
            select.appendChild(option);
        });

        modal.classList.remove("hidden");
    } catch (error) {
        console.error("Erreur ouverture modale assignation :", error);
        alert("Impossible de charger la liste des clients.");
    }
}

function closeAssignModal() {
    const modal = document.getElementById("assign-modal");
    const select = document.getElementById("client-select");

    selectedDecodeurId = null;

    if (select) {
        select.value = "";
    }

    if (modal) {
        modal.classList.add("hidden");
    }
}

async function confirmAssignDecoder() {
    const select = document.getElementById("client-select");

    if (!selectedDecodeurId) {
        alert("Aucun décodeur sélectionné.");
        return;
    }

    if (!select || !select.value) {
        alert("Veuillez sélectionner un client.");
        return;
    }

    const clientId = select.value;

    try {
        const response = await fetch(`http://localhost:8080/api/decoder/assign/${selectedDecodeurId}/assigner/${clientId}`, {
            method: "PUT",
            headers: {
                "Content-Type": "application/json"
            }
        });

        const result = await response.json();

        if (!response.ok) {
            throw new Error(result.message || "Erreur lors de l'assignation du décodeur.");
        }

        if (result.success === false) {
            alert(result.message || "Impossible d'assigner le décodeur.");
            return;
        }

        alert(result.message || "Décodeur assigné avec succès.");
        closeAssignModal();
        await loadDecodersPage();
    } catch (error) {
        console.error("Erreur assignation décodeur :", error);
        alert(error.message || "Impossible d'assigner le décodeur.");
    }
}

async function loadDecodersPage() {
    try {
        const [assigned, available] = await Promise.all([
            fetchAssignedDecoders(),
            fetchAvailableDecoders()
        ]);

        renderAssignedDecoders(assigned);
        renderAvailableDecoders(available);
    } catch (error) {
        console.error("Erreur chargement page décodeurs :", error);

        const assignedGrid = document.getElementById("assigned-grid");
        const availableGrid = document.getElementById("available-grid");

        if (assignedGrid) {
            assignedGrid.innerHTML = `<div class="empty-state">Erreur lors du chargement.</div>`;
        }

        if (availableGrid) {
            availableGrid.innerHTML = `<div class="empty-state">Erreur lors du chargement.</div>`;
        }
    }
}

document.addEventListener("DOMContentLoaded", function () {
    loadDecodersPage();

    const modal = document.getElementById("assign-modal");
    if (modal) {
        modal.addEventListener("click", function (event) {
            if (event.target === modal) {
                closeAssignModal();
            }
        });
    }
});