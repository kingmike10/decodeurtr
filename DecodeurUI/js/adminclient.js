const API = "http://localhost:8080";
let clientsCache = []; // stocke les clients pour préremplir la modale sans problème d'encodage

// ===== INIT =====

document.addEventListener("DOMContentLoaded", function () {
    chargerClients();
    initialiserModaleAjout();
    initialiserModaleModification();
});

// ===== NAVIGATION =====

function openClientDecoders(idClient) {
    window.location.href = `admin-client-decodeurs.html?idClient=${idClient}`;
}

// ===== NOTIFICATION TOAST =====

function showNotif(msg, isErr = false) {
    document.querySelector(".notification")?.remove();
    const n = document.createElement("div");
    n.className = "notification" + (isErr ? " error" : "");
    n.innerHTML = `<i class="fas fa-${isErr ? "exclamation-circle" : "check-circle"}"></i> ${msg}`;
    document.body.appendChild(n);
    setTimeout(() => { n.style.opacity = "0"; setTimeout(() => n.remove(), 500); }, 4000);
}

// ===== CHARGEMENT DE LA LISTE =====

async function chargerClients() {
    const tableBody = document.getElementById("clientsTableBody");
    if (!tableBody) return;

    tableBody.innerHTML = `<tr><td colspan="5" style="text-align:center;padding:20px;color:#9ca3af">Chargement…</td></tr>`;

    try {
        const response = await fetch(`${API}/api/admin/clients/all`);
        if (!response.ok) throw new Error("Erreur HTTP : " + response.status);

        const clients = await response.json();

        // Stocker en cache — on passe seulement l'id dans onclick, les données viennent du cache
        clientsCache = clients;

        if (!Array.isArray(clients) || clients.length === 0) {
            tableBody.innerHTML = `<tr><td colspan="5" style="text-align:center;padding:20px;color:#9ca3af">Aucun client trouvé.</td></tr>`;
            return;
        }

        tableBody.innerHTML = "";

        clients.forEach(client => {
            const row = document.createElement("tr");
            row.innerHTML = `
                <td>${client.nomClient}</td>
                <td>${client.adresse}</td>
                <td style="font-size:12px;color:#9ca3af">${client.identifiantConnexion ?? "—"}</td>
                <td>${client.decodeurIds?.length ?? 0}</td>
                <td>
                    <button type="button" onclick="openClientDecoders(${client.id})">Voir décodeurs</button>
                    <button type="button" onclick="ouvrirModaleModification(${client.id})">Modifier</button>
                    <button type="button" onclick="supprimerClient(${client.id})">Supprimer</button>
                </td>
            `;
            tableBody.appendChild(row);
        });

    } catch (error) {
        console.error("Erreur chargement clients :", error);
        tableBody.innerHTML = `<tr><td colspan="5" style="text-align:center;color:red">Impossible de charger les clients.</td></tr>`;
    }
}

// ===== SUPPRESSION =====

async function supprimerClient(id) {
    const c = clientsCache.find(x => x.id === id);
    const nom = c ? c.nomClient : "ce client";

    if (!confirm(`Supprimer le client « ${nom} » et tous ses liens ?`)) return;

    try {
        const response = await fetch(`${API}/api/admin/clients/delete/${id}`, { method: "DELETE" });
        let data = {};
        try { data = await response.json(); } catch (_) {}

        if (response.ok) {
            showNotif(data.message || "Client supprimé avec succès.");
            await chargerClients();
        } else {
            showNotif(data.message || "Erreur lors de la suppression.", true);
        }
    } catch (error) {
        console.error("Erreur suppression :", error);
        showNotif("Impossible de contacter le serveur.", true);
    }
}

// ===== MODALE AJOUT =====

function initialiserModaleAjout() {
    const modal      = document.getElementById("addClientModal");
    const form       = document.getElementById("addClientForm");
    const msgEl      = document.getElementById("addClientMessage");
    const openBtn    = document.getElementById("openAddClientModal");
    const closeBtn   = document.getElementById("closeAddClientModal");
    const cancelBtn  = document.getElementById("cancelAddClient");
    const submitBtn  = form?.querySelector('button[type="submit"]');

    if (!modal || !form || !msgEl || !openBtn) {
        console.warn("Éléments de la modale d'ajout introuvables.");
        return;
    }

    function ouvrirModale() { modal.classList.remove("hidden"); msgEl.textContent = ""; }
    function fermerModale() {
        modal.classList.add("hidden");
        form.reset();
        msgEl.textContent = "";
        if (submitBtn) submitBtn.disabled = false;
    }

    openBtn.addEventListener("click", ouvrirModale);
    closeBtn?.addEventListener("click", fermerModale);
    cancelBtn?.addEventListener("click", fermerModale);
    window.addEventListener("click", e => { if (e.target === modal) fermerModale(); });

    let isSubmitting = false;

    form.addEventListener("submit", async function (e) {
        e.preventDefault();
        if (isSubmitting) return;

        const nomClient            = document.getElementById("nomClient")?.value.trim()            || "";
        const adresse              = document.getElementById("adresseClient")?.value.trim()        || "";
        const identifiantConnexion = document.getElementById("identifiantConnexion")?.value.trim() || "";
        const motDePasse           = document.getElementById("motDePasse")?.value.trim()           || "";

        if (!nomClient || !adresse || !identifiantConnexion || !motDePasse) {
            msgEl.textContent = "Veuillez remplir tous les champs.";
            msgEl.style.color = "red";
            return;
        }

        isSubmitting = true;
        if (submitBtn) submitBtn.disabled = true;
        msgEl.textContent = "";

        try {
            const response = await fetch(`${API}/api/admin/clients/create`, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ nomClient, adresse, identifiantConnexion, motDePasse })
            });

            const data = await response.json();

            if (response.ok && data.success !== false) {
                msgEl.textContent = data.message || "Client ajouté avec succès.";
                msgEl.style.color = "green";
                setTimeout(async () => { fermerModale(); await chargerClients(); }, 800);
            } else {
                msgEl.textContent = data.message || "Erreur lors de l'ajout.";
                msgEl.style.color = "red";
            }
        } catch (error) {
            console.error("Erreur ajout :", error);
            msgEl.textContent = "Impossible de contacter le serveur.";
            msgEl.style.color = "red";
        } finally {
            isSubmitting = false;
            if (submitBtn) submitBtn.disabled = false;
        }
    });
}

// ===== MODALE MODIFICATION =====

function initialiserModaleModification() {
    const modal     = document.getElementById("editClientModal");
    const closeBtn  = document.getElementById("closeEditClientModal");
    const cancelBtn = document.getElementById("cancelEditClient");
    const submitBtn = document.getElementById("submitEditClient");
    const msgEl     = document.getElementById("editClientMessage");

    if (!modal || !submitBtn || !msgEl) {
        console.warn("Éléments de la modale de modification introuvables.");
        return;
    }

    function fermerModale() {
        modal.classList.add("hidden");
        msgEl.textContent = "";
        submitBtn.disabled = false;
    }

    closeBtn?.addEventListener("click", fermerModale);
    cancelBtn?.addEventListener("click", fermerModale);
    modal.addEventListener("click", e => { if (e.target === modal) fermerModale(); });

    submitBtn.addEventListener("click", async () => {
        const id                   = document.getElementById("editClientId").value;
        const nomClient            = document.getElementById("editNomClient").value.trim();
        const adresse              = document.getElementById("editAdresse").value.trim();
        const identifiantConnexion = document.getElementById("editIdentifiant").value.trim();
        const motDePasse           = document.getElementById("editMotDePasse").value.trim();

        if (!nomClient || !adresse || !identifiantConnexion) {
            msgEl.textContent = "Le nom, l'adresse et l'identifiant sont obligatoires.";
            msgEl.style.color = "red";
            return;
        }

        submitBtn.disabled = true;
        msgEl.textContent  = "";

        const body = { nomClient, adresse, identifiantConnexion };
        if (motDePasse) body.motDePasse = motDePasse; // optionnel

        try {
            const response = await fetch(`${API}/api/admin/clients/update/${id}`, {
                method: "PUT",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(body)
            });

            const data = await response.json();

            if (data.success) {
                showNotif(data.message || "Client modifié avec succès.");
                setTimeout(async () => { fermerModale(); await chargerClients(); }, 500);
            } else {
                msgEl.textContent = data.message || "Erreur lors de la modification.";
                msgEl.style.color = "red";
            }
        } catch (error) {
            console.error("Erreur modification :", error);
            msgEl.textContent = "Impossible de contacter le serveur.";
            msgEl.style.color = "red";
        } finally {
            submitBtn.disabled = false;
        }
    });
}

// Appelé depuis le bouton Modifier du tableau — lit depuis le cache, aucun encodage
function ouvrirModaleModification(id) {
    const modal = document.getElementById("editClientModal");
    if (!modal) { console.warn("Modale de modification introuvable."); return; }

    const c = clientsCache.find(x => x.id === id);
    if (!c) { showNotif("Client introuvable dans le cache.", true); return; }

    document.getElementById("editClientId").value    = c.id;
    document.getElementById("editNomClient").value   = c.nomClient ?? "";
    document.getElementById("editAdresse").value     = c.adresse ?? "";
    document.getElementById("editIdentifiant").value = c.identifiantConnexion ?? "";
    document.getElementById("editMotDePasse").value  = "";
    document.getElementById("editClientMessage").textContent = "";

    modal.classList.remove("hidden");
    setTimeout(() => document.getElementById("editNomClient").focus(), 50);
}
