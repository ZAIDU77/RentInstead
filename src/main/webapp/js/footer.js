(() => {
  function initFooter() {
    document.querySelectorAll("[data-footer-year]").forEach(el => {
      el.textContent = new Date().getFullYear();
    });
    document.querySelectorAll("[data-back-to-top]").forEach(button => {
      button.addEventListener("click", () => {
        window.scrollTo({ top: 0, behavior: "smooth" });
      });
    });
  }
  if (document.readyState === "loading") {
    document.addEventListener("DOMContentLoaded", initFooter);
  } else {
    initFooter();
  }
})();
