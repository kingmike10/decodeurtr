const API = "http://localhost:8080";
let clientsCache = [];

function logout() { localStorage.clear(); window.location.href = "../index.html"; }

function showNotif(msg, isErr = false) {
  document.querySelector(".notification")?.remove();
  const n = document.createElement("div");
  n.className = "notification" + (isErr ? " error" : "");
  n.innerHTML = `<i class="fas fa-${isErr ? "exclamation-circle" : "check-circle"}"></i> ${msg}`;
  document.body.appendChild(n);
  setTimeout(() => { n.style.opacity = "0"; setTimeout(() => n.remove(), 500); }, 4000);
}

async function loadClients() {
  const tbody = document.getElementById("clients-tbody");
  try {
    const res  = await fetch(`${API}/api/admin/clients/all`);
    const data = await res.json();

    clientsCache = data;

    document.getElementById("clients-count").textContent = data.length;
    document.getElementById("clients-count-label").textContent =
      `${data.length} client${data.length !== 1 ? "s" : ""} enregistré${data.length !== 1 ? "s" : ""}`;

    if (!data.length) {
      tbody.innerHTML = `<tr><td colspan="5" style="color:var(--text-muted);text-align:center;padding:24px">Aucun client enregistré.</td></tr>`;
      return;
    }

    tbody.innerHTML = data.map(c => `
      <tr>
        <td><strong>${c.nomClient}</strong></td>
        <td style="color:var(--text-soft)">${c.adresse}</td>
        <td style="font-family:var(--font-mono);font-size:12px;color:var(--text-soft)">${c.identifiantConnexion ?? "—"}</td>
        <td><span class="section-count">${c.decodeurIds?.length ?? 0}</span></td>
        <td>
          <div style="display:flex;gap:6px;flex-wrap:wrap">
            <a href="admin-client-decodeurs.html?idClient=${c.id}" class="btn-secondary btn-sm">
              <i class="fas fa-tv"></i> Décodeurs
            </a>
            <button class="btn-danger btn-sm" onclick="deleteClient(${c.id})">
              <i class="fas fa-trash"></i> Supprimer
            </button>
          </div>
        </td>
      </tr>
    `).join("");
  } catch (e) {
    tbody.innerHTML = `<tr><td colspan="5" class="error-text">Impossible de charger les clients.</td></tr>`;
  }
}

async function deleteClient(id) {
  const c = clientsCache.find(x => x.id === id);
  const nom = c ? c.nomClient : "ce client";
  if (!confirm(`Supprimer le client « ${nom} » et tous ses liens ?`)) return;
  try {
    const res  = await fetch(`${API}/api/admin/clients/delete/${id}`, { method: "DELETE" });
    const data = await res.json();
    showNotif(data.message || "Client supprimé.", !data.success);
    if (data.success) loadClients();
  } catch { showNotif("Erreur lors de la suppression.", true); }
}

// ── MODAL AJOUT ──────────────────────────────────────────────────────────────
const modal = document.getElementById("addModal");
const msgEl = document.getElementById("modal-message");

function openModal()  { modal.classList.remove("hidden"); msgEl.textContent = ""; }
function closeModal() {
  modal.classList.add("hidden");
  ["f-nom","f-adresse","f-identifiant","f-mdp"].forEach(id => document.getElementById(id).value = "");
  msgEl.textContent = "";
}

document.getElementById("openModal").addEventListener("click", openModal);
document.getElementById("closeModal").addEventListener("click", closeModal);
document.getElementById("cancelModal").addEventListener("click", closeModal);
modal.addEventListener("click", e => { if (e.target === modal) closeModal(); });

document.getElementById("submitClient").addEventListener("click", async () => {
  const nomClient            = document.getElementById("f-nom").value.trim();
  const adresse              = document.getElementById("f-adresse").value.trim();
  const identifiantConnexion = document.getElementById("f-identifiant").value.trim();
  const motDePasse           = document.getElementById("f-mdp").value.trim();

  if (!nomClient || !adresse || !identifiantConnexion || !motDePasse) {
    msgEl.textContent = "Tous les champs sont obligatoires.";
    msgEl.style.color = "var(--danger)";
    return;
  }

  const btn = document.getElementById("submitClient");
  btn.disabled = true;

  try {
    const res  = await fetch(`${API}/api/admin/clients/create`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ nomClient, adresse, identifiantConnexion, motDePasse })
    });
    const data = await res.json();

    if (res.ok && data.success !== false) {
      showNotif(data.message || "Client créé avec succès.");
      setTimeout(() => { closeModal(); loadClients(); }, 500);
    } else {
      msgEl.textContent = data.message || "Erreur lors de la création.";
      msgEl.style.color = "var(--danger)";
    }
  } catch {
    msgEl.textContent = "Impossible de contacter le serveur.";
    msgEl.style.color = "var(--danger)";
  } finally {
    btn.disabled = false;
  }
});

document.addEventListener("DOMContentLoaded", loadClients);