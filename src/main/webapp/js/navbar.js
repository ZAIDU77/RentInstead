// ```javascript
/*
==================================================
 RentInstead - Navbar JavaScript
 Session-based Login, First Name and Logout
==================================================
*/

document.addEventListener("DOMContentLoaded", function () {
    const navbarContainer = document.getElementById("siteNavbar");

    loadNavbar();

    async function loadNavbar() {
        if (!navbarContainer) {
            initializeNavbar(document);
            await loadCurrentUser(document);
            return;
        }

        try {
            const response = await fetch(
                "/RentInstead/html/navbar.html"
            );

            if (!response.ok) {
                throw new Error("Navbar could not be loaded.");
            }

            const html = await response.text();
            const temporaryContainer = document.createElement("div");
            temporaryContainer.innerHTML = html;

            const navbar = temporaryContainer.querySelector(".rent-navbar");

            if (!navbar) {
                throw new Error("Navbar element was not found.");
            }

            navbarContainer.replaceChildren(navbar);

            initializeNavbar(navbarContainer);
            await loadCurrentUser(navbarContainer);

        } catch (error) {
            console.error("Navbar loading error:", error);
        }
    }

    function initializeNavbar(root) {
        // Mobile menu
        const menuToggle = root.querySelector("#menuToggle");
        const navbarContent = root.querySelector("#navbarContent");

        if (menuToggle && navbarContent) {
            menuToggle.addEventListener("click", function () {
                const isOpen =
                    navbarContent.classList.toggle("menu-open");

                menuToggle.setAttribute(
                    "aria-expanded",
                    String(isOpen)
                );

                const icon = menuToggle.querySelector("i");

                if (icon) {
                    icon.className = isOpen
                        ? "bi bi-x-lg"
                        : "bi bi-list";
                }
            });
        }

        // Search
        const searchForm = root.querySelector("#navbarSearchForm");
        const searchInput = root.querySelector("#navbarSearch");
        const clearSearch = root.querySelector("#clearSearch");

        if (searchInput && clearSearch) {
            function updateClearButton() {
                clearSearch.hidden = searchInput.value.length === 0;
            }

            searchInput.addEventListener("input", updateClearButton);

            clearSearch.addEventListener("click", function () {
                searchInput.value = "";
                updateClearButton();
                searchInput.focus();

                document.querySelectorAll("[data-item-card]")
                    .forEach(card => card.hidden = false);

                const noResults =
                    document.getElementById("noSearchResults");

                if (noResults) {
                    noResults.hidden = true;
                }
            });

            if (searchForm) {
                searchForm.addEventListener("submit", function (event) {
                    event.preventDefault();

                    const query = searchInput.value.trim();
                    const browseUrl =
                        new URL("/RentInstead/html/items/browse.html",
                            window.location.origin);

                    if (query) {
                        browseUrl.searchParams.set("q", query);
                    }

                    window.location.href = browseUrl.href;
                });
            }

            updateClearButton();
        }

        // User dropdown
        const userMenuButton = root.querySelector("#userMenuButton");
        const userDropdown = root.querySelector("#userDropdown");
        const userMenu = root.querySelector("#userMenu");

        if (userMenuButton && userDropdown && userMenu) {
            userMenuButton.addEventListener("click", function () {
                const isOpen = userDropdown.hidden;
                userDropdown.hidden = !isOpen;

                userMenuButton.setAttribute(
                    "aria-expanded",
                    String(isOpen)
                );
            });

            // Close dropdown when clicking outside
            document.addEventListener("click", function (event) {
                if (!userMenu.contains(event.target)) {
                    userDropdown.hidden = true;
                    userMenuButton.setAttribute(
                        "aria-expanded", "false"
                    );
                }
            });

            // Open dropdown on mouse hover
            userMenu.addEventListener("mouseenter", function () {
                userDropdown.hidden = false;
                userMenuButton.setAttribute("aria-expanded", "true");
            });

            userMenu.addEventListener("mouseleave", function () {
                userDropdown.hidden = true;
                userMenuButton.setAttribute("aria-expanded", "false");
            });
        }

        // Real server logout
        const logoutButton = root.querySelector("#logoutButton");

        if (logoutButton) {
            logoutButton.addEventListener("click", function () {
                window.location.href = "/RentInstead/logout";
            });
        }
    }

    // Get the current user from the server session
    async function loadCurrentUser(root) {
        try {
            const response = await fetch(
                "/RentInstead/current-user",
                {
                    method: "GET",
                    credentials: "same-origin",
                    cache: "no-store"
                }
            );

            if (!response.ok) {
                showLoggedOutUser(root);
                return;
            }

            const user = await response.json();

            if (user && user.loggedIn && user.firstName) {
                showLoggedInUser(root, user.firstName);
            } else {
                showLoggedOutUser(root);
            }

        } catch (error) {
            console.error("Could not load current user:", error);
            showLoggedOutUser(root);
        }
    }

    function showLoggedInUser(root, firstName) {
        const authActions = root.querySelector("#authActions");
        const userMenu = root.querySelector("#userMenu");
        const userFirstName = root.querySelector("#userFirstName");
        const userAvatar = root.querySelector("#userAvatar");

        if (!authActions || !userMenu ||
            !userFirstName || !userAvatar) {
            return;
        }

        userFirstName.textContent = firstName;
        userAvatar.textContent = firstName.charAt(0).toUpperCase();

        authActions.hidden = true;
        userMenu.hidden = false;
    }

    function showLoggedOutUser(root) {
        const authActions = root.querySelector("#authActions");
        const userMenu = root.querySelector("#userMenu");
        const userDropdown = root.querySelector("#userDropdown");

        if (authActions) authActions.hidden = false;
        if (userMenu) userMenu.hidden = true;
        if (userDropdown) userDropdown.hidden = true;
    }

    window.RentInsteadNavbar = {
        showLoggedInUser: function (firstName) {
            showLoggedInUser(document, firstName);
        }
    };
});
