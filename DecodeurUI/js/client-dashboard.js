// ===== HELPERS =====

function isDecoderOnline(etat) { return etat === "EN_LIGNE"; }
function getStatusLabel(etat)  { return isDecoderOnline(etat) ? "En ligne" : "Hors ligne"; }
function getStatusClass(etat)  { return isDecoderOnline(etat) ? "online" : "offline"; }

function getUserLogin() {
    const userLogin = localStorage.getItem("userLogin");
    if (!userLogin || userLogin === "undefined") {
        throw new Error("Aucun identifiant trouvé dans la session");
    }
    return userLogin;
}

function logout() {
    localStorage.clear();
    window.location.href = "../index.html";
}

function closeModal() {
    document.getElementById("statusModal").style.display = "none";
}

// Notification toast non-intrusive
function showNotification(message, isError = false) {
    const old = document.querySelector(".notification");
    if (old) old.remove();

    const n = document.createElement("div");
    n.className = "notification" + (isError ? " notification-error" : "");
    n.innerHTML = `<i class="fas fa-${isError ? "exclamation-circle" : "check-circle"}"></i> ${message}`;
    document.body.appendChild(n);

    setTimeout(() => {
        n.style.opacity = "0";
        setTimeout(() => n.remove(), 500);
    }, 4500);
}

// ===== API CALLS =====

async function fetchDashboardData() {
    const userLogin  = getUserLogin();
    const response   = await fetch(`http://localhost:8080/api/clients/dashboard/${userLogin}`);
    if (!response.ok) throw new Error("Erreur récupération dashboard");
    return response.json();
}

async function fetchDecoderStatus(id) {
    const response = await fetch(`http://localhost:8080/api/decoder/getEtat/${id}`);
    if (!response.ok) throw new Error("Impossible de récupérer l'état");
    return response.json();
}

// ===== RENDER =====

function renderDashboard(data) {
    document.getElementById("client-name").textContent = `Bienvenue, ${data.nomClient}`;
    document.getElementById("status-count").textContent = `${data.nbActifs} / ${data.nbTotal}`;

    const grid = document.getElementById("decoder-grid");
    if (!grid) return;
    grid.innerHTML = "";

    if (!data.decodeurs || data.decodeurs.length === 0) {
        grid.innerHTML = `<p class="error">Aucun décodeur assigné à votre compte.</p>`;
        return;
    }

    data.decodeurs.forEach(dec => {
        const statusLabel = getStatusLabel(dec.etat);
        const statusClass = getStatusClass(dec.etat);
        const restartDate = dec.lastRestart || "Aucun";
        const reinitDate  = dec.lastReinit  || "Aucune";

        // CORRECTION : 4 boutons d'opérations au lieu de 2
        // Conformément au projet1.pdf : état, redémarrer, réinitialiser, éteindre
        const card = `
            <div class="decoder-card">
                <div class="card-header">
                    <span class="ip-address">${dec.adresseIp}</span>
                    <span class="status-badge ${statusClass}">${statusLabel}</span>
                </div>

                <div class="card-details">
                    <p><span>Dernier redémarrage</span><span>${restartDate}</span></p>
                    <p><span>Dernière réinitialisation</span><span>${reinitDate}</span></p>
                </div>

                <div class="card-actions">
                    <button title="Obtenir l'état" onclick="showStatus(${dec.id})">
                        <i class="fas fa-info-circle"></i>
                    </button>
                    <button title="Redémarrer" onclick="sendRestartCommand(${dec.id})">
                        <i class="fas fa-sync-alt"></i>
                    </button>
                    <button title="Réinitialiser le mot de passe" onclick="sendReinitCommand(${dec.id})">
                        <i class="fas fa-key"></i>
                    </button>
                    <button title="Éteindre" onclick="sendShutdownCommand(${dec.id})" class="btn-danger-icon">
                        <i class="fas fa-power-off"></i>
                    </button>
                </div>
            </div>
        `;
        grid.innerHTML += card;
    });
}

async function loadDashboardData() {
    try {
        const data = await fetchDashboardData();
        renderDashboard(data);
    } catch (error) {
        console.error("Erreur dashboard:", error);
        const grid = document.getElementById("decoder-grid");
        if (grid) grid.innerHTML = `<p class="error">Impossible de charger les données : ${error.message}</p>`;
    }
}

// ===== OPÉRATIONS =====

// Opération 1 — Obtenir l'état
async function showStatus(id) {
    try {
        const data = await fetchDecoderStatus(id);
        const statusLabel = getStatusLabel(data.etat);
        const statusClass = getStatusClass(data.etat);

        document.getElementById("modal-body").innerHTML = `
            <p><strong>IP :</strong> ${data.adresseIp}</p>
            <p><strong>État :</strong> <span class="status-badge ${statusClass}">${statusLabel}</span></p>
            <p><strong>Dernier redémarrage :</strong> ${data.lastRestart || "Aucun"}</p>
            <p><strong>Dernière réinitialisation :</strong> ${data.lastReinit || "Aucune"}</p>
        `;
        document.getElementById("statusModal").style.display = "block";
    } catch (error) {
        console.error("Erreur showStatus:", error);
        showNotification("Impossible de récupérer l'état en temps réel.", true);
    }
}

// Opération 2 — Redémarrer (avec notification de complétion)
async function waitForDecoderRestart(id) {
    let attempts = 0;
    const maxAttempts = 12; // 12 × 5s = 60s max (simulateur : 10-30s)

    const interval = setInterval(async () => {
        try {
            attempts++;
            const data = await fetchDecoderStatus(id);

            if (data.etat === "EN_LIGNE") {
                clearInterval(interval);
                showNotification(`✅ Le décodeur ${data.adresseIp} a redémarré avec succès.`);
                await loadDashboardData();
                return;
            }

            if (attempts >= maxAttempts) {
                clearInterval(interval);
                showNotification("Le redémarrage prend plus de temps que prévu.", true);
            }
        } catch (error) {
            clearInterval(interval);
            showNotification("Impossible de vérifier la fin du redémarrage.", true);
        }
    }, 5000);
}

async function sendRestartCommand(id) {
    try {
        const response = await fetch(`http://localhost:8080/api/decoder/restart/${id}`, {
            method: "PUT"
        });
        const result = await response.json();

        if (response.ok) {
            showNotification(result.message || "Redémarrage en cours (10-30s)…");
            await loadDashboardData();
            waitForDecoderRestart(id); // polling asynchrone
        } else {
            showNotification("Erreur : " + (result.message || "Échec du redémarrage."), true);
        }
    } catch (error) {
        console.error("Erreur restart :", error);
        showNotification("Erreur technique lors du redémarrage.", true);
    }
}

// Opération 3 — Réinitialiser le mot de passe
async function sendReinitCommand(id) {
    if (!confirm("Confirmer la réinitialisation du mot de passe de ce décodeur ?")) return;

    try {
        const response = await fetch(`http://localhost:8080/api/decoder/reinit/${id}`, {
            method: "PUT"
        });
        const result = await response.json();

        if (response.ok) {
            showNotification(result.message || "Mot de passe réinitialisé avec succès.");
            await loadDashboardData();
        } else {
            showNotification("Erreur : " + (result.message || "Échec de la réinitialisation."), true);
        }
    } catch (error) {
        console.error("Erreur reinit :", error);
        showNotification("Erreur technique lors de la réinitialisation.", true);
    }
}

// Opération 4 — Éteindre
async function sendShutdownCommand(id) {
    if (!confirm("Confirmer l'extinction de ce décodeur ?")) return;

    try {
        const response = await fetch(`http://localhost:8080/api/decoder/shutdown/${id}`, {
            method: "PUT"
        });
        const result = await response.json();

        if (response.ok) {
            showNotification(result.message || "Décodeur éteint avec succès.");
            await loadDashboardData();
        } else {
            showNotification("Erreur : " + (result.message || "Échec de l'extinction."), true);
        }
    } catch (error) {
        console.error("Erreur shutdown :", error);
        showNotification("Erreur technique lors de l'extinction.", true);
    }
}

// ===== INIT =====

window.onclick = function (event) {
    const modal = document.getElementById("statusModal");
    if (event.target === modal) closeModal();
};

document.addEventListener("DOMContentLoaded", async () => {
    try {
        getUserLogin();
        await loadDashboardData();
    } catch (error) {
        console.error("Erreur init :", error);
        alert("Session expirée. Veuillez vous reconnecter.");
        window.location.href = "../index.html";
    }
});
