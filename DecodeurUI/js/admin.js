// Gestion de la navigation (visuel)
document.querySelectorAll(".sidebar-link").forEach(link => {
    link.addEventListener("click", () => {
        document.querySelectorAll(".sidebar-link").forEach(l => l.classList.remove("active"));
        link.classList.add("active");
    });
});

// Interaction avec les cartes décodeurs
document.querySelectorAll(".decoder-card").forEach(card => {
    card.addEventListener("click", () => {
        const ip = card.querySelector(".decoder-ip").textContent;
        const location = card.querySelector(".decoder-location").textContent;
        const status = card.querySelector(".status").textContent.trim();

        alert(
            "📡 Décodeur sélectionné\n\n" +
            "IP : " + ip + "\n" +
            "Lieu : " + location + "\n" +
            "Statut : " + status
        );
    });
});


