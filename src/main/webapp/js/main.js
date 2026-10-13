
/* RentInstead - Home Page JavaScript */

document.addEventListener("DOMContentLoaded", function () {

    const searchForm = document.getElementById("heroSearchForm");
    const searchInput = document.getElementById("heroSearchInput");

    // Search sends the visitor to the Browse page with their query.
    if (searchForm && searchInput) {
        searchForm.addEventListener("submit", function (event) {
            event.preventDefault();

            const query = searchInput.value.trim();

            if (!query) {
                searchInput.focus();
                return;
            }

            window.location.href =
                "html/items/browse.html?q=" +
                encodeURIComponent(query);
        });
    }

    // If a search term came from the navbar, reuse it in the hero.
    const params = new URLSearchParams(window.location.search);
    const query = params.get("q");

    if (query && searchInput) {
        searchInput.value = query;
    }

});