const { chromium } = require('playwright');
const path = require('path');
const fs = require('fs');

(async () => {
  const screenshotsDir = path.join(__dirname, '../docs/screenshots');
  if (!fs.existsSync(screenshotsDir)) {
    fs.mkdirSync(screenshotsDir, { recursive: true });
  }

  console.log('Launching browser...');
  const browser = await chromium.launch({ headless: true });
  const page = await browser.newPage();
  
  // Debug page logs and uncaught errors
  page.on('console', msg => console.log('PAGE LOG:', msg.text()));
  page.on('pageerror', err => console.log('PAGE ERROR:', err.message));
  
  // Wait for the app to be available (simple retry logic)
  const appUrl = 'http://localhost:4200';
  let isUp = false;
  for (let i = 0; i < 30; i++) {
    try {
      console.log(`Checking if app is up at ${appUrl}...`);
      await page.goto(appUrl, { timeout: 5000 });
      isUp = true;
      break;
    } catch (e) {
      await page.waitForTimeout(2000);
    }
  }

  if (!isUp) {
    console.error('App is not responding on http://localhost:4200. Exiting.');
    await browser.close();
    process.exit(1);
  }

  // Set viewport size for good screenshots
  await page.setViewportSize({ width: 1280, height: 800 });

  console.log('Navigating to app...');
  await page.goto(appUrl, { waitUntil: 'networkidle' });
  await page.waitForTimeout(2000);

  if (page.url().includes('login')) {
    console.log('Logging in as alice@promptly.ai...');
    try {
      await page.fill('input[type="email"]', 'alice@promptly.ai');
      await page.fill('input[type="password"]', 'password123');
      await page.click('button[type="submit"]');
      await page.waitForURL('**/dashboard', { timeout: 10000 });
      await page.waitForTimeout(2000);
    } catch (e) {
      console.log('Login failed or elements not found. Will try to proceed anyway.', e.message);
    }
  }

  // Add a small delay for initial data load/animations
  await page.waitForTimeout(2000);

  // Select spectrayan-health project via the UI dropdown to keep SPA session alive
  console.log('Selecting spectrayan-health project from dropdown...');
  await page.click('#project-selector');
  await page.waitForTimeout(1000);
  await page.click('.project-menu-panel button:has-text("spectrayan-health")');
  await page.waitForTimeout(4000); // Wait for dashboard to update and load metrics

  // Take screenshot of the dashboard
  console.log('Taking screenshot of Dashboard...');
  await page.screenshot({ path: path.join(screenshotsDir, 'dashboard.png'), fullPage: true });

  // Navigate to Prompts list page by clicking sidebar link
  console.log('Navigating to Prompts list...');
  await page.click('aside.sidebar a:has-text("Prompts")');
  await page.waitForTimeout(4000);
  console.log('Taking screenshot of Project Prompts...');
  await page.screenshot({ path: path.join(screenshotsDir, 'project-prompts.png'), fullPage: true });

  // Navigate to ABA Support Assistant detail page by clicking the row
  console.log('Navigating to ABA Prompt Editing...');
  await page.click('a.prompt-link:has-text("ABA Support Assistant")');
  await page.waitForTimeout(4000);
  console.log('Taking screenshot of ABA Prompt Editing...');
  await page.screenshot({ path: path.join(screenshotsDir, 'prompt-editing.png'), fullPage: true });
  await page.screenshot({ path: path.join(screenshotsDir, 'prompt-detail-scan.png'), fullPage: true });

  // Navigate to Scanner by clicking sidebar link
  console.log('Navigating to Scanner list...');
  await page.click('aside.sidebar a:has-text("Scanner")');
  await page.waitForTimeout(4000);
  console.log('Taking screenshot of Scans...');
  await page.screenshot({ path: path.join(screenshotsDir, 'scans-view.png'), fullPage: true });
  await page.screenshot({ path: path.join(screenshotsDir, 'scanner-list.png'), fullPage: true });

  // Navigate to ABA Scan Result Detail by clicking the clickable row
  console.log('Navigating to ABA Scan Result Detail...');
  await page.click('tr.clickable-row:has-text("ABA Support Assistant")');
  await page.waitForTimeout(4000);
  console.log('Taking screenshot of Scan Report Detail...');
  await page.screenshot({ path: path.join(screenshotsDir, 'scan-report-detail.png'), fullPage: true });

  await browser.close();
  console.log('Screenshots captured successfully!');
})();
