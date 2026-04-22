const API = "http://localhost:8080";
let selectedDecodeurId = null;
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

function statusBadge(etat) {
  const online = etat === "EN_LIGNE";
  return `<span class="status-badge ${online ? "online" : "offline"}">${online ? "En ligne" : "Hors ligne"}</span>`;
}

function renderAssigned(decodeurs) {
  document.getElementById("assigned-count").textContent = decodeurs.length;
  const grid = document.getElementById("assigned-grid");

  if (!decodeurs.length) {
    grid.innerHTML = `<div class="empty-state"><i class="fas fa-tv" style="margin-bottom:8px;display:block;font-size:20px"></i>Aucun décodeur attribué.</div>`;
    return;
  }

  grid.innerHTML = decodeurs.map(d => `
    <div class="decoder-card">
      <div class="decoder-top">
        <div class="decoder-left">
          <div class="decoder-icon"><i class="fas fa-tv"></i></div>
          <div>
            <p class="decoder-ip">${d.adresseIp}</p>
            <p class="decoder-client">${d.nomClient || "Client inconnu"}</p>
          </div>
        </div>
        ${statusBadge(d.etat)}
      </div>
      <div class="decoder-actions">
        <a href="admin-client-decodeurs.html?idClient=${d.idClient}" class="btn-secondary btn-sm">
          <i class="fas fa-user"></i> Voir client
        </a>
        <button class="btn-danger btn-sm" onclick="retirerDecodeur(${d.id})">
          <i class="fas fa-unlink"></i> Retirer
        </button>
      </div>
    </div>
  `).join("");
}

function renderAvailable(decodeurs) {
  document.getElementById("available-count").textContent = decodeurs.length;
  const grid = document.getElementById("available-grid");

  if (!decodeurs.length) {
    grid.innerHTML = `<div class="empty-state"><i class="fas fa-check-circle" style="margin-bottom:8px;display:block;font-size:20px;color:var(--green)"></i>Tous les décodeurs sont attribués.</div>`;
    return;
  }

  grid.innerHTML = decodeurs.map(d => `
    <div class="decoder-card">
      <div class="decoder-top">
        <div class="decoder-left">
          <div class="decoder-icon"><i class="fas fa-tv"></i></div>
          <div>
            <p class="decoder-ip">${d.adresseIp}</p>
            <p class="decoder-client" style="color:var(--green);font-size:12px">Disponible</p>
          </div>
        </div>
        ${statusBadge(d.etat)}
      </div>
      <div class="decoder-actions">
        <button class="btn-primary btn-sm" onclick="openAssignModal(${d.id}, '${d.adresseIp}')">
          <i class="fas fa-link"></i> Assigner
        </button>
      </div>
    </div>
  `).join("");
}

async function loadPage() {
  try {
    const [assigned, available] = await Promise.all([
      fetch(`${API}/api/decoder/assigned`).then(r => r.json()),
      fetch(`${API}/api/decoder/available`).then(r => r.json()),
    ]);
    renderAssigned(assigned);
    renderAvailable(available);
  } catch (e) {
    document.getElementById("assigned-grid").innerHTML  = `<div class="empty-state error-text">Erreur de chargement.</div>`;
    document.getElementById("available-grid").innerHTML = `<div class="empty-state error-text">Erreur de chargement.</div>`;
  }
}

async function retirerDecodeur(id) {
  if (!confirm("Retirer ce décodeur de son client ?")) return;
  try {
    const res  = await fetch(`${API}/api/decoder/retirer/${id}`, { method: "PUT" });
    const data = await res.json();
    showNotif(data.message || "Décodeur retiré.", !data.succes);
    if (data.succes) loadPage();
  } catch { showNotif("Erreur lors du retrait.", true); }
}

async function openAssignModal(id, ip) {
  selectedDecodeurId = id;
  document.getElementById("assign-modal-text").textContent = `Décodeur : ${ip}`;
  const select = document.getElementById("client-select");
  select.innerHTML = `<option value="">— Choisir un client —</option>`;

  try {
    clientsCache = await fetch(`${API}/api/admin/clients/all`).then(r => r.json());
    clientsCache.forEach(c => {
      const opt = document.createElement("option");
      opt.value = c.id;
      opt.textContent = c.nomClient;
      select.appendChild(opt);
    });
  } catch { showNotif("Impossible de charger les clients.", true); return; }

  document.getElementById("assign-modal").classList.remove("hidden");
}

function closeAssignModal() {
  selectedDecodeurId = null;
  document.getElementById("assign-modal").classList.add("hidden");
  document.getElementById("client-select").value = "";
}

async function confirmAssign() {
  const clientId = document.getElementById("client-select").value;
  if (!selectedDecodeurId || !clientId) { showNotif("Sélectionnez un client.", true); return; }
  try {
    const res  = await fetch(`${API}/api/decoder/assign/${selectedDecodeurId}/assigner/${clientId}`, { method: "PUT" });
    const data = await res.json();
    showNotif(data.message || "Assigné.", !data.success);
    closeAssignModal();
    if (data.success) loadPage();
  } catch { showNotif("Erreur lors de l'assignation.", true); }
}

document.addEventListener("DOMContentLoaded", () => {
  loadPage();
  document.getElementById("assign-modal").addEventListener("click", e => {
    if (e.target === document.getElementById("assign-modal")) closeAssignModal();
  });
});