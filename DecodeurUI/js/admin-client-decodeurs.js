const API = "http://localhost:8080";

// Déconnecte l'utilisateur en vidant localStorage et en redirigeant vers index.html
function logout() { localStorage.clear(); window.location.href = "../index.html"; }

// Récupère l'ID du client depuis les paramètres de requête de l'URL
function getClientId() {
  return new URLSearchParams(window.location.search).get("idClient");
}

// Affiche un message de notification sur la page, avec un style d'erreur optionnel
function showNotif(msg, isErr = false) {
  document.querySelector(".notification")?.remove();
  const n = document.createElement("div");
  n.className = "notification" + (isErr ? " error" : "");
  n.innerHTML = `<i class="fas fa-${isErr ? "exclamation-circle" : "check-circle"}"></i> ${msg}`;
  document.body.appendChild(n);
  setTimeout(() => { n.style.opacity = "0"; setTimeout(() => n.remove(), 500); }, 4000);
}

// Retourne une chaîne HTML pour un badge de statut basé sur l'état du décodeur
function statusBadge(etat) {
  const online = etat === "EN_LIGNE";
  return `<span class="status-badge ${online ? "online" : "offline"}">${online ? "En ligne" : "Hors ligne"}</span>`;
}

// Rend la liste des décodeurs pour le client dans l'interface utilisateur
function renderDecoders(data) {
  document.getElementById("breadcrumb-client").textContent = data.nomClient || "Client";
  document.getElementById("page-title").textContent        = `Décodeurs — ${data.nomClient}`;
  document.getElementById("page-subtitle").textContent     = `Client #${data.idClient} · ${data.decodeurs?.length ?? 0} décodeur${data.decodeurs?.length !== 1 ? "s" : ""} assigné${data.decodeurs?.length !== 1 ? "s" : ""}`;
  document.getElementById("decoder-count").textContent     = data.decodeurs?.length ?? 0;

  const grid = document.getElementById("decoder-grid");

  if (!data.decodeurs?.length) {
    grid.innerHTML = `<div class="empty-state"><i class="fas fa-tv" style="display:block;font-size:22px;margin-bottom:8px"></i>Aucun décodeur assigné à ce client.</div>`;
    return;
  }

  grid.innerHTML = data.decodeurs.map(dec => {
    const chainesHtml = dec.chaines?.length
      ? dec.chaines.map(ch => `
          <li class="chaine-item">
            <span>${ch}</span>
            <button class="btn-danger btn-sm" onclick="retirerChaine(${dec.id}, '${ch.replace(/'/g,"\\'")}')">
              <i class="fas fa-times"></i>
            </button>
          </li>`).join("")
      : `<li class="chaine-item empty"><i class="fas fa-info-circle" style="margin-right:6px"></i>Aucune chaîne</li>`;

    return `
      <div class="decoder-card">
        <div class="decoder-top">
          <div class="decoder-left">
            <div class="decoder-icon"><i class="fas fa-tv"></i></div>
            <div>
              <p class="decoder-ip">${dec.adresseIp}</p>
              <p class="decoder-client">ID #${dec.id}</p>
            </div>
          </div>
          ${statusBadge(dec.etat)}
        </div>

        <div class="decoder-chaines">
          <h4><i class="fas fa-list" style="margin-right:5px"></i>Chaînes associées</h4>
          <ul class="chaine-list">${chainesHtml}</ul>
        </div>

        <div class="decoder-actions" style="flex-direction:column;gap:8px">
          <div style="display:flex;gap:8px">
            <input type="text" class="chaine-input" id="chaine-${dec.id}" placeholder="Nom de la chaîne…" style="flex:1">
            <button class="btn-primary btn-sm" onclick="ajouterChaine(${dec.id})">
              <i class="fas fa-plus"></i> Ajouter
            </button>
          </div>
          <button class="btn-danger btn-sm" style="width:100%" onclick="retirerDecodeur(${dec.id})">
            <i class="fas fa-unlink"></i> Retirer le décodeur
          </button>
        </div>
      </div>`;
  }).join("");
}

// Charge et affiche les décodeurs pour le client actuel depuis l'API
async function loadDecoders() {
  const idClient = getClientId();
  if (!idClient) {
    document.getElementById("decoder-grid").innerHTML = `<div class="empty-state error-text">Aucun client spécifié dans l'URL.</div>`;
    return;
  }
  try {
    const data = await fetch(`${API}/api/admin/clients/${idClient}/decodeurs`).then(r => r.json());
    renderDecoders(data);
  } catch (e) {
    document.getElementById("decoder-grid").innerHTML = `<div class="empty-state error-text">Impossible de charger les décodeurs.</div>`;
  }
}

// Retire un décodeur du client après confirmation
async function retirerDecodeur(id) {
  if (!confirm("Retirer ce décodeur du client ?")) return;
  try {
    const res  = await fetch(`${API}/api/decoder/retirer/${id}`, { method: "PUT" });
    const data = await res.json();
    showNotif(data.message || "Décodeur retiré.", !data.succes);
    if (data.succes) loadDecoders();
  } catch { showNotif("Erreur lors du retrait.", true); }
}

// Ajoute une chaîne à un décodeur
async function ajouterChaine(idDecodeur) {
  const input  = document.getElementById(`chaine-${idDecodeur}`);
  const chaine = input?.value.trim();
  if (!chaine) { showNotif("Entrez un nom de chaîne.", true); return; }

  try {
    const res  = await fetch(`${API}/api/decoder/ajouterChaine`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ idDecodeur, chaine })
    });
    const data = await res.json();
    showNotif(data.message || "Chaîne ajoutée.", !data.succes);
    if (data.succes) { input.value = ""; loadDecoders(); }
  } catch { showNotif("Erreur lors de l'ajout.", true); }
}

// Retire une chaîne d'un décodeur après confirmation
async function retirerChaine(idDecodeur, chaine) {
  if (!confirm(`Retirer la chaîne « ${chaine} » ?`)) return;
  try {
    const res  = await fetch(`${API}/api/decoder/retirerChaine`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ idDecodeur, chaine })
    });
    const data = await res.json();
    showNotif(data.message || "Chaîne retirée.", !data.succes);
    if (data.succes) loadDecoders();
  } catch { showNotif("Erreur lors du retrait.", true); }
}

document.addEventListener("DOMContentLoaded", loadDecoders);