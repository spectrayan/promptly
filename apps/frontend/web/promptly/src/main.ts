import { bootstrapApplication } from '@angular/platform-browser';
import { appConfig } from './app/app.config';
import { App } from './app/app';

// Suppress benign ResizeObserver loop error (known browser quirk with Monaco editor).
// See: https://github.com/WICG/resize-observer/issues/38
const resizeObserverErr = /ResizeObserver loop/;
window.addEventListener('error', (e) => {
  if (resizeObserverErr.test(e.message)) {
    e.stopImmediatePropagation();
  }
});

bootstrapApplication(App, appConfig)
  .catch((err) => console.error(err));
