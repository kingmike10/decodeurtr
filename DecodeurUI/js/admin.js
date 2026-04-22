const API = "http://localhost:8080";

// Déconnecte l'utilisateur en vidant le stockage local et en redirigeant vers la page d'accueil.
function logout() {
  localStorage.clear();
  window.location.href = "../index.html";
}

// Charge le tableau de bord en récupérant les données des clients, décodeurs assignés et disponibles depuis l'API, puis met à jour les statistiques et la liste des clients récents.
async function loadDashboard() {
  try {
    const [clients, assigned, available] = await Promise.all([
      fetch(`${API}/api/admin/clients/all`).then(r => r.json()),
      fetch(`${API}/api/decoder/assigned`).then(r => r.json()),
      fetch(`${API}/api/decoder/available`).then(r => r.json()),
    ]);

    document.getElementById("stat-clients").textContent   = clients.length;
    document.getElementById("stat-assigned").textContent  = assigned.length;
    document.getElementById("stat-available").textContent = available.length;

    const tbody = document.getElementById("recent-clients-body");
    const slice = clients.slice(0, 5);

    if (!slice.length) {
      tbody.innerHTML = `<tr><td colspan="4" style="color:var(--text-muted);text-align:center;padding:20px">Aucun client.</td></tr>`;
      return;
    }

    tbody.innerHTML = slice.map(c => `
      <tr>
        <td><strong>${c.nomClient}</strong></td>
        <td style="color:var(--text-soft)">${c.adresse}</td>
        <td><span class="section-count">${c.decodeurIds?.length ?? 0}</span></td>
        <td>
          <a href="admin-client-decodeurs.html?idClient=${c.id}" class="btn-secondary btn-sm">
            <i class="fas fa-tv"></i> Décodeurs
          </a>
        </td>
      </tr>
    `).join("");
  } catch (e) {
    console.error(e);
  }
}

document.addEventListener("DOMContentLoaded", loadDashboard);