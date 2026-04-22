const loginForm    = document.getElementById("loginForm");
const identifiantInput = document.getElementById("identifiant");
const passwordInput    = document.getElementById("password");
const message      = document.getElementById("message");
const submitBtn    = document.getElementById("submitBtn");
const btnText      = document.getElementById("btn-text");
const btnLoader    = document.getElementById("btn-loader");

// Fonction qui gère l'état de chargement du bouton de soumission
function setLoading(loading) {
    submitBtn.disabled = loading;
    btnText.textContent = loading ? "Connexion…" : "Se connecter";
    btnLoader.classList.toggle("hidden", !loading);
}

// Fonction qui affiche un message avec un type spécifique
function showMessage(text, type) {
    message.textContent = text;
    message.className = "form-message " + type;
}

// Gestionnaire d'événement pour la soumission du formulaire de connexion
loginForm.addEventListener("submit", async function (event) {
    event.preventDefault();

    const identifiantConnexion = identifiantInput.value.trim();
    const motDePasse           = passwordInput.value.trim();

    if (!identifiantConnexion || !motDePasse) {
        showMessage("Veuillez remplir tous les champs.", "error");
        return;
    }

    setLoading(true);
    showMessage("", "");

    try {
        const response = await fetch("http://localhost:8080/api/login", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ identifiantConnexion, motDePasse })
        });

        const data = await response.json();

        if (data.success) {
            localStorage.setItem("userRole",  data.role);
            localStorage.setItem("userLogin", data.identifiantConnexion);

            showMessage("Connexion réussie…", "success");

            setTimeout(() => {
                // CORRECTION : admin-dashboard.html (et non admin.html qui n'existe pas)
                if (data.role === "ADMIN") {
                    window.location.href = "/pages/admin-dashboard.html";
                } else if (data.role === "CLIENT") {
                    window.location.href = "/pages/vueClient.html";
                } else {
                    showMessage("Rôle inconnu : " + data.role, "error");
                    setLoading(false);
                }
            }, 800);

        } else {
            showMessage(data.message || "Identifiant ou mot de passe incorrect.", "error");
            setLoading(false);
        }

    } catch (error) {
        showMessage("Impossible de contacter le serveur.", "error");
        console.error("Erreur login :", error);
        setLoading(false);
    }
});
