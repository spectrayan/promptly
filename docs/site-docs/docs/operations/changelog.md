# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added
- Notification system with unread counts and real-time SSE delivery.
- Java Spring (WebClient) query SDK generation for downstream consumers.
- Contributor Covenant Code of Conduct and Security Policy.

### Changed
- Refactored backend configuration to use hierarchical `@ConfigurationProperties` (`PromptlyProperties`).
- Open-sourced the repository under the Apache 2.0 License.

### Fixed
- Addressed transient 401 errors during frontend page refresh by gating SSE connections behind auth state.

## [1.0.0] - 2026-04-20

### Added
- Initial stable release of the Promptly control plane.
- Prompt Registry with full CRUD, versioning, rollback, and diff viewer.
- Workflow engine for approval state machines.
- AI-powered vulnerability scanner and quality improver (Spring AI).
- Semantic search using MongoDB Atlas Vector Search.
- Export / Import API for CI/CD deployments.
- Angular 21 frontend with Monaco Editor.
