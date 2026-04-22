function goBack() {
    window.history.back();
}

function getClientIdFromUrl() {
    const params = new URLSearchParams(window.location.search);
    return params.get("idClient");
}

function isDecoderOnline(etat) {
    return etat === "EN_LIGNE";
}

function getStatusLabel(etat) {
    return isDecoderOnline(etat) ? "En ligne" : "Hors ligne";
}

function getStatusClass(etat) {
    return isDecoderOnline(etat) ? "online" : "offline";
}

async function fetchClientDecoders(idClient) {
    const response = await fetch(`http://localhost:8080/api/admin/clients/${idClient}/decodeurs`, {
        method: "GET",
        headers: {
            "Content-Type": "application/json"
        }
    });

    if (!response.ok) {
        throw new Error("Impossible de charger les décodeurs du client");
    }

    return await response.json();
}

function renderClientDecoders(data) {
    const title = document.getElementById("client-title");
    const subtitle = document.getElementById("client-subtitle");
    const grid = document.getElementById("decoder-grid");

    if (!title || !subtitle || !grid) {
        console.error("Un ou plusieurs éléments HTML sont introuvables.");
        return;
    }

    title.textContent = `Décodeurs de ${data.nomClient || "ce client"}`;
    subtitle.textContent = `Client #${data.idClient}`;

    grid.innerHTML = "";

    if (!Array.isArray(data.decodeurs) || data.decodeurs.length === 0) {
        grid.innerHTML = `<p class="error">Aucun décodeur lié à ce client.</p>`;
        return;
    }

    data.decodeurs.forEach(dec => {
        const statusLabel = getStatusLabel(dec.etat);
        const statusClass = getStatusClass(dec.etat);

        const chainesHtml = Array.isArray(dec.chaines) && dec.chaines.length > 0
            ? dec.chaines.map(chaine => `
                <li class="chaine-item">
                    <span>${chaine}</span>
                    <button type="button" class="btn-danger btn-sm" onclick="retirerChaine(${dec.id}, '${chaine.replace(/'/g, "\\'")}')">
                        Retirer
                    </button>
                </li>
            `).join("")
            : `<li class="chaine-item empty">Aucune chaîne</li>`;

        const card = `
            <div class="decoder-card">
                <div class="card-header">
                    <span class="ip-address">${dec.adresseIp}</span>
                    <span class="status-badge ${statusClass}">
                        ${statusLabel}
                    </span>
                </div>

                <div class="card-details">
                    <p><strong>ID :</strong> ${dec.id}</p>
                </div>

                <div class="decoder-chaines">
                    <h4>Chaînes associées</h4>
                    <ul class="chaine-list">
                        ${chainesHtml}
                    </ul>
                </div>

                <div class="decoder-actions">
                    <input 
                        type="text" 
                        id="chaine-input-${dec.id}" 
                        class="chaine-input" 
                        placeholder="Nom de la chaîne"
                    />
                    <div class="action-buttons">
                        <button type="button" class="btn-primary" onclick="ajouterChaine(${dec.id})">
                            Ajouter chaîne
                        </button>
                        <button type="button" class="btn-danger" onclick="retirerDecodeurDuClient(${dec.id})">
                            Retirer le décodeur
                        </button>
                    </div>
                </div>
            </div>
        `;

        grid.innerHTML += card;
    });
}

async function loadClientDecoders() {
    const grid = document.getElementById("decoder-grid");
    const idClient = getClientIdFromUrl();

    if (!idClient) {
        if (grid) {
            grid.innerHTML = `<p class="error">Aucun idClient trouvé dans l'URL.</p>`;
        }
        return;
    }

    if (grid) {
        grid.innerHTML = `<p>Chargement des décodeurs...</p>`;
    }

    try {
        const data = await fetchClientDecoders(idClient);
        renderClientDecoders(data);
    } catch (error) {
        console.error("Erreur lors du chargement des décodeurs :", error);
        if (grid) {
            grid.innerHTML = `<p class="error">${error.message}</p>`;
        }
    }
}

/**
 * Actions branchées plus tard
 */
async function retirerDecodeurDuClient(idDecodeur) {
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

        alert(result.message || "Décodeur retiré du client avec succès.");
        await loadClientDecoders();
    } catch (error) {
        console.error("Erreur lors du retrait du décodeur :", error);
        alert(error.message || "Impossible de retirer le décodeur.");
    }
}

async function ajouterChaine(idDecodeur) {
    const input = document.getElementById(`chaine-input-${idDecodeur}`);
    const chaine = input ? input.value.trim() : "";

    if (!chaine) {
        alert("Veuillez entrer une chaîne.");
        return;
    }

    try {
        const response = await fetch("http://localhost:8080/api/decoder/ajouterChaine", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                idDecodeur: idDecodeur,
                chaine: chaine
            })
        });

        const result = await response.json();

        if (!response.ok) {
            throw new Error(result.message || "Erreur lors de l'ajout de la chaîne.");
        }

        if (result.success === false) {
            alert(result.message || "Impossible d'ajouter la chaîne.");
            return;
        }

        alert(result.message || "Chaîne ajoutée avec succès.");
        input.value = "";
        await loadClientDecoders();
    } catch (error) {
        console.error("Erreur lors de l'ajout de la chaîne :", error);
        alert(error.message || "Impossible d'ajouter la chaîne.");
    }
}

async function retirerChaine(idDecodeur, chaine) {
    const confirmation = confirm(`Voulez-vous vraiment retirer la chaîne "${chaine}" ?`);

    if (!confirmation) {
        return;
    }

    try {
        const response = await fetch("http://localhost:8080/api/decoder/retirerChaine", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                idDecodeur: idDecodeur,
                chaine: chaine
            })
        });

        const result = await response.json();

        if (!response.ok) {
            throw new Error(result.message || "Erreur lors du retrait de la chaîne.");
        }

        if (result.success === false) {
            alert(result.message || "Impossible de retirer la chaîne.");
            return;
        }

        alert(result.message || "Chaîne retirée avec succès.");
        await loadClientDecoders();
    } catch (error) {
        console.error("Erreur lors du retrait de la chaîne :", error);
        alert(error.message || "Impossible de retirer la chaîne.");
    }
}

document.addEventListener("DOMContentLoaded", async function () {
    await loadClientDecoders();
});