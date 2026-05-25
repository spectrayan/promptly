/**
 * Mermaid dark-mode integration for MkDocs Material.
 *
 * Detects the active MkDocs Material color scheme and initializes Mermaid
 * with the appropriate theme (dark or default). Also watches for theme
 * toggles and re-renders diagrams when the user switches modes.
 */

// Wait for the page to be ready, then override Mermaid's theme
document.addEventListener("DOMContentLoaded", function () {
  // Observe the body's data-md-color-scheme attribute for changes
  const observer = new MutationObserver(function (mutations) {
    mutations.forEach(function (mutation) {
      if (mutation.attributeName === "data-md-color-scheme") {
        reinitMermaid();
      }
    });
  });

  observer.observe(document.body, {
    attributes: true,
    attributeFilter: ["data-md-color-scheme"],
  });
});

function reinitMermaid() {
  if (typeof mermaid === "undefined") return;

  const isDark =
    document.body.getAttribute("data-md-color-scheme") === "slate";

  mermaid.initialize({
    startOnLoad: false,
    theme: isDark ? "dark" : "default",
    themeVariables: isDark
      ? {
          // Dark theme overrides
          primaryColor: "#37474f",
          primaryTextColor: "#e0e0e0",
          primaryBorderColor: "#78909c",
          lineColor: "#90a4ae",
          secondaryColor: "#455a64",
          tertiaryColor: "#263238",
          background: "#1e1e1e",
          mainBkg: "#37474f",
          nodeBorder: "#78909c",
          clusterBkg: "#263238",
          clusterBorder: "#546e7a",
          titleColor: "#e0e0e0",
          edgeLabelBackground: "#2d2d2d",
          noteTextColor: "#e0e0e0",
          noteBkgColor: "#37474f",
          noteBorderColor: "#78909c",
          actorTextColor: "#e0e0e0",
          actorBkg: "#37474f",
          actorBorder: "#78909c",
          signalColor: "#e0e0e0",
          signalTextColor: "#e0e0e0",
          labelBoxBkgColor: "#37474f",
          labelBoxBorderColor: "#78909c",
          labelTextColor: "#e0e0e0",
        }
      : {},
  });

  // Re-render all mermaid diagrams
  document.querySelectorAll(".mermaid").forEach(function (el) {
    // Only re-render if the element has already been processed
    if (el.getAttribute("data-processed")) {
      const code = el.getAttribute("data-original-code");
      if (code) {
        el.removeAttribute("data-processed");
        el.innerHTML = code;
      }
    }
  });

  mermaid.run();
}
