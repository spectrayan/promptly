/**
 * k6 HTML Report Helper
 *
 * Generates a standalone HTML report from k6 test results using
 * the k6-reporter extension's handleSummary hook.
 *
 * Usage: Import and call from handleSummary() in any k6 script.
 *
 * @see https://github.com/benc-uk/k6-reporter
 */
import { htmlReport } from 'https://raw.githubusercontent.com/benc-uk/k6-reporter/main/dist/bundle.js';
import { textSummary } from 'https://jslib.k6.io/k6-summary/0.1.0/index.js';

/**
 * Generates an HTML report and a JSON summary alongside the normal stdout output.
 *
 * @param {object} data       - The k6 summary data object passed to handleSummary()
 * @param {string} reportName - Base name for the output files (e.g. 'smoke', 'prompt-crud')
 * @returns {object}          - Object mapping output destinations to content
 */
export function generateReport(data, reportName) {
  const timestamp = new Date().toISOString().replace(/[:.]/g, '-').slice(0, 19);
  const baseName = `${reportName}-${timestamp}`;

  return {
    // Console output (always shown)
    stdout: textSummary(data, { indent: '  ', enableColors: true }),

    // HTML report — viewable in any browser
    [`/reports/${baseName}.html`]: htmlReport(data, {
      title: `Promptly — ${reportName} Performance Report`,
      debug: false,
    }),

    // JSON summary — machine-readable for CI/CD thresholds
    [`/reports/${baseName}.json`]: JSON.stringify(data, null, 2),
  };
}
