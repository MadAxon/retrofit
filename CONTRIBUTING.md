Contributing
============

Our goal with this project is to build a simple, fast, and secure component that you can trust. We
have scoped the project deliberately to address common use cases easily, and made it extensible so
it'll handle exotic use cases too.

Keeping the project small and stable limits our ability to accept new contributors. We are not
seeking new committers at this time, but some small contributions are welcome.

If you've found a bug, please contribute a failing test case so we can study and fix it.


AI & Generative Tools Policy
-----------------------------

In this repository (`MadAxon/retrofit`), we embrace modern development practices, including the responsible use of AI coding assistants and LLMs by both human engineers and autonomous agents.

However, to preserve software quality, stability, and reliability, all AI-assisted contributions must strictly follow our engineering principles:

1. **Human Ownership & Review**: You are responsible for every line of code submitted. AI output must be rigorously reviewed, understood, and validated before opening a PR.
2. **Automated Verification**: AI-assisted code must be formatted with Spotless (`./gradlew spotlessApply`), satisfy ErrorProne static analysis, adhere to Java 8 & Android 21 API restrictions, and pass all test suites.
3. **Reproducers & Tests**: Any bugfix or feature must include comprehensive automated tests (using JUnit 4, Truth, and MockWebServer).
4. **Developer & Agent Guidelines**: Follow the repository instructions detailed in [AGENTS.md](AGENTS.md) and [docs/instructions/](docs/instructions/).

> **Important Note for Upstream Contributors**:
> The upstream Square repository maintains a strict policy banning LLMs and generative tools in source code, documentation, and PRs. If you plan to submit your contributions upstream to Square's official repository, you must comply with their guidelines.

