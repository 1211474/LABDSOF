# Team Methodology

### 1\. Team Composition

| Email | Name | Roles |
| ---| ---| --- |
| [1211277@isep.ipp.pt](mailto:1211277@isep.ipp.pt) | Carlos Lopes | DEV, SM |
| [1211474@isep.ipp.pt](mailto:1211474@isep.ipp.pt) | Guilherme Rodrigues | DEV, QA |
| [1211606@isep.ipp.pt](mailto:1211606@isep.ipp.pt) | Filipe Magalhães | DEV, BA |
| [1240472@isep.ipp.pt](mailto:1240472@isep.ipp.pt) | Manuel Sá | DEV, PM |

*   **PM (Project Manager):** Plans and coordinates the project, manages timelines, risks, stakeholders, and delivery.
*   **BA (Business Analyst):** Understands business needs, gathers requirements, and translates them into clear specifications for the team.
*   **Dev (Developer):** Designs, builds, and maintains the software/features based on requirements.
*   **QA (Quality Assurance):** Tests the product to identify bugs, verify requirements, and ensure quality before release.
*   **SM (Scrum Master):** Facilitates Scrum ceremonies, removes blockers, supports the team, and helps ensure Scrum practices are followed effectively.
### 2\. Team methodology (example agreement)
**Framework:** Scrum
**Sprint cadence and ceremonies:**
*   **Sprint Planning** (last day of sprint, 1 hour): pick stories from the top of the backlog that meet the Definition of Ready, and break them into tasks.
*   **Daily Stand-up** (15 min, same time daily or async in Slack/Teams): what I did yesterday, what I'll do today, any blockers.
*   **Sprint Review** (last day, 30–45 min): demo finished work to the PO or stakeholders.
*   **Retrospective** (last day, 30 min): what went well, what didn't, one or two concrete improvements for next sprint.
*   **Backlog Refinement** (mid-sprint, 45 min): clarify and estimate upcoming stories.
**Board workflow:** To Do → In Progress → Code Review → Testing → Done
**Estimation:** Story points using Fibonacci (1, 2, 3, 5, 8, 13). Anything above 8 must be split.
**Working rules:**
*   Work-in-progress limit of 2 tasks per person.
*   Every change goes through a pull request with at least 1 reviewer.
*   Branch naming: feature/JIRA-123 (feature branch workflow)
*   Commit message: prefix + short description feat, fix, chore, docs
*   Blockers are raised within 24 hours, not saved for stand-up.
*   Communication: Whatsapp for quick questions, the ticket for decisions.
### 3\. Definition of Ready (example)
A user story can enter a sprint only when:
*   It's written in user story format: _"As a \[role\], I want \[feature\], so that \[benefit\]."_
*   It has clear acceptance criteria, ideally in Given/When/Then form.
*   It has been estimated by the team (story points).
*   It's small enough to finish within one sprint (≤ 8 points).
*   Dependencies are identified and resolved, or there's a plan for them.
*   UI mockups or wireframes are attached if the story involves UI.
*   The Product Owner has prioritized it and is available to answer questions.
*   The team understands it; no open "what does this mean?" questions remain.
*   It meets INVEST: Independent, Negotiable, Valuable, Estimable, Small, Testable.
**Example of a "Ready" story:**
> _As a registered user, I want to reset my password via email, so that I can regain access if I forget it._  
> **Acceptance criteria:**  
> Given I'm on the login page, when I click "Forgot password" and enter my email, then I receive a reset link within 2 minutes.  
> The link expires after 1 hour.  
> The new password must meet the existing password policy.  
> **Estimate:** 5 points · **Mockup:** attached · **Dependency:** email service configured (done)
### 4\. Definition of Done (example)
A story is Done only when:
**Code**
*   Code is written and follows the agreed style guide or linter rules.
*   Code is committed and pushed to the repository and linked to the ticket.
*   A pull request was reviewed and approved by at least one teammate.
*   It's merged into the main/develop branch with no conflicts.
**Quality**
*   Unit tests are written and passing ( ≥ 80% coverage on new code).
*   The CI pipeline builds successfully.
*   QA has verified all acceptance criteria.
**Delivery**
*   Documentation is updated (README, API docs, user guide if relevant).
*   The ticket is moved to "Done" in the tracker.

# User Journeys

# User Journeys
## Journey 1: Find an alternative when a service fails
**Primary persona:** Ana, a time-constrained caregiver with high digital literacy.
**Situation:** Ana’s regular pharmacy does not have her mother’s medication, or another essential service is no longer available.
**Goal:** Find a relevant, nearby, and sufficiently reliable alternative in under 3 minutes.

**1\.** Ana opens the dashboard and sees the care profile, emergency contacts, and main shortcuts.
**2\.** She selects a need or writes a natural-language question, for example: “pharmacies near Boavista open now”.
**3\.** The system returns alternatives filtered by location, service type, accessibility, and known status.
**4\.** Ana compares the service status, source, last update date, and limitations of the information.
**5\.** She chooses an alternative and confirms directly with the institution using the contact button.
**6\.** If necessary, she starts a support request for someone in her network.

[journey1\_find\_alternative (1).bpmn](https://t90122138845.p.clickup-attachments.com/t90122138845/94540df3-3b7d-4f68-9760-af25aab72019/journey1_find_alternative%20\(1\).bpmn)

[diagram (3).svg](https://t90122138845.p.clickup-attachments.com/t90122138845/f1ff0d36-1ba7-46c3-bef2-df9d959833c9/diagram%20\(3\).svg)

**Expected outcome:** Ana knows the next action and can move forward without searching through directories, websites, and scattered phone calls.

**Critical requirements:** quick-access dashboard; search and filters; results with source, status, and freshness; clear language; direct contact; no result should present availability as a guarantee.

**Associated criteria:** at least 80% completion without intervention, at least 90% correct identification of the next action, and a median time to a relevant result of 3 minutes or less, as defined in Private ([https://app.clickup.com/90122138845/docs/2kxv5d6x-692/2kxv5d6x-352](https://app.clickup.com/90122138845/docs/2kxv5d6x-692/2kxv5d6x-352)).
## Journey 2: Ask for help and coordinate a support person
**Primary persona:** Ana. **Involved persona:** Rui, a family member, neighbour, or volunteer.
**Situation:** Ana becomes ill, cannot collect medication, or needs someone to accompany her mother.
**Goal:** Turn an urgent and vague need into a concrete, limited, and easy-to-accept request.

**1\.** Ana selects “Request support” from the dashboard or a service result.
**2\.** She provides only what is necessary: task, location, time window, and contact method.
**3\.** The system shows a preview of what Rui will receive, excluding unnecessary health data.
**4\.** Ana sends the request to one or more people in her support network.
**5\.** Rui receives a specific request and accepts or declines it in a few steps.
**6\.** Ana tracks the request status: pending, accepted, or declined, and decides what to do next.

**Expected outcome:** The support network receives actionable requests instead of vague group messages, and Ana knows whether the need has been covered.

**Critical requirements:** requests specifying “what, where, and when”; explicit consent and visibility; data minimisation; notifications; request statuses; the ability to decline without friction.

**Associated criteria:** at least 80% completion of the request flow, disclosure of only the necessary information, and role-based access control, as defined in Private ([https://app.clickup.com/90122138845/docs/2kxv5d6x-692/2kxv5d6x-312](https://app.clickup.com/90122138845/docs/2kxv5d6x-692/2kxv5d6x-312)) and Private ([https://app.clickup.com/90122138845/docs/2kxv5d6x-692/2kxv5d6x-352](https://app.clickup.com/90122138845/docs/2kxv5d6x-692/2kxv5d6x-352)).

[journey2\_request\_support (1).bpmn](https://t90122138845.p.clickup-attachments.com/t90122138845/9cb9fdec-0f2f-4cab-af1f-9ebebd15cd1e/journey2_request_support%20\(1\).bpmn)

[diagram (2).svg](https://t90122138845.p.clickup-attachments.com/t90122138845/a77a2588-3641-464e-84c4-5264785fc198/diagram%20\(2\).svg)

## Journey 3: Resolve a failure with accessibility and human support
**Primary persona:** Armando, an older caregiver with low digital literacy and a preference for phone calls.
**Situation:** Accessible transport cancels his wife’s trip to physiotherapy, or the day centre closes unexpectedly.
**Goal:** Quickly understand whom to contact and find an alternative without navigating a complex system.

**1\.** Armando enters the system through a simple setup, potentially prepared by a family member.
**2\.** On the dashboard, he selects a large, clearly labelled shortcut such as “Transport” or “Day centre”.
**3\.** He sees a small number of alternatives with readable text, high contrast, status, and update date.
**4\.** For each alternative, he selects “Call the institution” or another human contact option.
**5\.** If he cannot find a solution, he asks his network for help or leaves the decision to a delegated family member.

[journey3\_accessibility\_human\_support (1).bpmn](https://t90122138845.p.clickup-attachments.com/t90122138845/93dc4367-f918-4390-b3b5-171d098a55a8/journey3_accessibility_human_support%20\(1\).bpmn)

[diagram (1).svg](https://t90122138845.p.clickup-attachments.com/t90122138845/be0b40b3-2e5e-4fc7-919f-3f5d4773f37d/diagram%20\(1\).svg)

**Expected outcome:** Armando can take action without understanding the technology and is never blocked by an automated response.

**Critical requirements:** mobile-first; large typography; high contrast; few steps; simple language; large touch targets; a visible call button on every result; delegated setup; no mandatory upload of medical documents.

**Associated criteria:** at least 80% success among older users on search or contact tasks, and a human alternative available at every critical point, as defined in Private ([https://app.clickup.com/90122138845/docs/2kxv5d6x-692/2kxv5d6x-312](https://app.clickup.com/90122138845/docs/2kxv5d6x-692/2kxv5d6x-312)), Private ([https://app.clickup.com/90122138845/docs/2kxv5d6x-692/2kxv5d6x-172](https://app.clickup.com/90122138845/docs/2kxv5d6x-692/2kxv5d6x-172)), and Private ([https://app.clickup.com/90122138845/docs/2kxv5d6x-692/2kxv5d6x-352](https://app.clickup.com/90122138845/docs/2kxv5d6x-692/2kxv5d6x-352)).
## Journey 4: Keep service information up to date and reliable
**Primary persona:** Sofia, coordinator of a day centre or support organisation.
**Situation:** A closure, schedule change, vacancy, or temporary change generates repeated calls from families.
**Goal:** Publish an official update in under one minute, with appropriate accountability and controls.

**1\.** Sofia signs in to an account associated with an organisation verified by a platform admin.
**2\.** She selects a service belonging to that organisation and updates its operating status, schedule, or notice.
**3\.** The system validates her role, organisation verification, service ownership, and submitted fields.
**4\.** A valid update is saved and publicly reflected as “Official status” with its last-updated date and time in under one minute. No per-update admin approval is required.
**5\.** Caregivers see the official update distinguished from community reports and can contact the institution directly.

Validity periods, update audit history, and provider correction or withdrawal are Release 2 capabilities.

[journey4\_service\_information.bpmn](./journey4_service_information.bpmn)

**Expected outcome:** Families get more up-to-date information, and Sofia reduces repetitive calls without creating a false promise of availability.

**Critical requirements:** verified account; fast updates; separation between official and community sources; visible last-updated date/time; least-privilege permissions. Validity periods, provider correction or removal, and detailed audit history are Release 2 capabilities.

**Associated criteria:** an update in under 1 minute, source and freshness visible in every result, and role-based access control, as defined in Private ([https://app.clickup.com/90122138845/docs/2kxv5d6x-692/2kxv5d6x-312](https://app.clickup.com/90122138845/docs/2kxv5d6x-692/2kxv5d6x-312)), Private ([https://app.clickup.com/90122138845/docs/2kxv5d6x-692/2kxv5d6x-352](https://app.clickup.com/90122138845/docs/2kxv5d6x-692/2kxv5d6x-352)), and Private ([https://app.clickup.com/90122138845/docs/2kxv5d6x-692/2kxv5d6x-272](https://app.clickup.com/90122138845/docs/2kxv5d6x-692/2kxv5d6x-272)).

# Success Criteria

**Purpose.** This document defines how the caregiver-support application will be judged successful. The criteria translate the problem, research findings, personas and architectural decisions into measurable product, quality and delivery outcomes.

# 1\. Success Definition
The application is successful when an informal caregiver can find a trustworthy, relevant alternative during a care disruption and coordinate the next action with minimal effort, without exposing unnecessary personal or health information.

Success is not measured by the number of features delivered. It is measured by whether the product reduces search and coordination friction while remaining accessible, trustworthy, privacy-preserving and technically maintainable.

This definition follows the core problem identified in the Private ([https://app.clickup.com/90122138845/docs/2kxv5d6x-692/2kxv5d6x-112](https://app.clickup.com/90122138845/docs/2kxv5d6x-692/2kxv5d6x-112)): caregivers face fragmented information, weak coordination and a lack of reliable alternatives when normal care arrangements fail.
# 2\. Outcome Success Criteria
## 2.1 Caregiver task success

| Criterion | Target | Evidence |
| ---| ---| --- |
| A caregiver can find a suitable service alternative for a defined disruption scenario | At least 80% of test participants complete the task without facilitator intervention | Usability test observation and task-completion record |
| A caregiver can identify what to do next from a result | At least 90% of participants can state the next action correctly | Post-task question |
| A caregiver can contact the institution directly from a result | The call or contact action is visible and usable on every service result | Functional test |
| A caregiver can notify a backup person about a concrete support need | At least 80% of participants complete the request flow successfully | End-to-end usability test |
| The primary disruption flow is quick enough for a stressful situation | Median time from opening the dashboard to reaching a relevant result is 3 minutes or less in the MVP test scenario | Timed usability test |

These criteria directly address Ana's need for fast alternatives and Armando's need for a clear human fallback, as described in the Private ([https://app.clickup.com/90122138845/docs/2kxv5d6x-692/2kxv5d6x-312](https://app.clickup.com/90122138845/docs/2kxv5d6x-692/2kxv5d6x-312)).
## 2.2 Accessibility and inclusion

| Criterion | Target | Evidence |
| ---| ---| --- |
| Core flows are usable by users with lower digital literacy | At least 80% of older-user test participants complete the core search or contact task | Moderated usability test |
| The interface supports visual accessibility | Core screens provide large readable text, high contrast, clear focus states and touch targets suitable for mobile use | Accessibility review and automated checks |
| Users are not forced into digital-only support | A visible phone or human-contact alternative exists wherever an automated or digital action may fail | Acceptance tests |
| Onboarding does not require unnecessary complexity | A caregiver can reach the core dashboard after a short, guided setup with no mandatory medical-document upload | Functional and usability test |

The baseline is the accessibility-first direction established in the Private ([https://app.clickup.com/90122138845/docs/2kxv5d6x-692/2kxv5d6x-172](https://app.clickup.com/90122138845/docs/2kxv5d6x-692/2kxv5d6x-172)) and Private ([https://app.clickup.com/90122138845/docs/2kxv5d6x-692/2kxv5d6x-312](https://app.clickup.com/90122138845/docs/2kxv5d6x-692/2kxv5d6x-312)), not a generic visual-design preference.
## 2.3 Trust and information reliability

| Criterion | Target | Evidence |
| ---| ---| --- |
| Users can distinguish official information from community reports | Status source and verification state are visible in every relevant result | UI inspection and acceptance test |
| Service information exposes its freshness | Last-updated information is shown wherever availability or status is presented | Functional test |
| The system does not present availability as a guarantee | Results communicate limitations and encourage direct confirmation when appropriate | Content review |
| AI answers are grounded in application or verified service data | AI responses include the relevant source context and do not invent service facts | Scenario-based evaluation |
| Users can reach a human fallback | Every AI-assisted service recommendation includes a prominent institution contact action | Acceptance test |

The product must improve navigation without pretending to solve service shortages. The Private ([https://app.clickup.com/90122138845/docs/2kxv5d6x-692/2kxv5d6x-112](https://app.clickup.com/90122138845/docs/2kxv5d6x-692/2kxv5d6x-112)) identifies outdated information, limited service capacity and unclear responsibility as material risks.
# 3\. Responsible AI and Safety Criteria
The AI Service Navigator is an information-retrieval and summarisation aid, not a medical adviser, emergency decision-maker or source of truth.
*   It must not provide medical diagnosis, treatment recommendations or emergency instructions.
*   It must use data minimisation and avoid sending sensitive personal information to the external LLM provider.
*   It must communicate that responses summarise available information and may be incomplete or outdated.
*   It must provide a direct human or institutional fallback for every service recommendation.
*   It must refuse or safely redirect requests that require professional medical judgement.
*   AI functionality must be isolated behind the backend application layer, with no direct frontend-to-LLM connection.
*   The system must log sufficient technical information to investigate failures without storing unnecessary sensitive prompt content.

These criteria follow Private ([https://app.clickup.com/90122138845/docs/2kxv5d6x-692/2kxv5d6x-272](https://app.clickup.com/90122138845/docs/2kxv5d6x-692/2kxv5d6x-272)), especially ADR-007 and ADR-008, and should be validated with representative scenarios before release.
# 4\. Privacy and Security Criteria
*   Only the minimum information required for care coordination may be collected.
*   Medical documents must not be required for the MVP.
*   Care needs should use non-sensitive or minimally sensitive categories wherever possible.
*   Role-based access control must restrict information according to the user's role.
*   Authentication and authorisation must be enforced by the backend, not trusted to the frontend.
*   Data must be protected in transit and at rest where appropriate.
*   Users must be able to understand what information is shared with caregivers, volunteers and service providers.
*   Support requests must reveal only the information needed for the recipient to act.
*   The project must define retention, deletion and consent rules before handling real user data.
*   Security tests must cover unauthorised access, privilege escalation, insecure direct object access and accidental exposure through AI prompts or logs.

No release should be considered successful if it makes the care workflow easier by creating an unacceptable privacy or safety risk.
# 5\. Service Provider and Community Criteria
For Sofia, the service-provider experience must be lightweight enough to sustain current information:
*   A verified organisation can update a service's operating status, schedule, or notice in under 1 minute.
*   Official updates are visibly separated from community reports.
*   Service providers can see only the information required for their role.
*   Validity periods, update audit history, and provider correction or withdrawal are planned for Release 2.

For Rui and other backup helpers:
*   A support request states what is needed, where, and when.
*   The request excludes unnecessary health information.
*   A helper can accept or decline a request with minimal steps.
*   The caregiver can see whether the request is pending, accepted or declined.

These criteria operationalise the role needs in the Private ([https://app.clickup.com/90122138845/docs/2kxv5d6x-692/2kxv5d6x-312](https://app.clickup.com/90122138845/docs/2kxv5d6x-692/2kxv5d6x-312)) and the coordination gap in the Private ([https://app.clickup.com/90122138845/docs/2kxv5d6x-692/2kxv5d6x-112](https://app.clickup.com/90122138845/docs/2kxv5d6x-692/2kxv5d6x-112)).
# 6\. Technical and Quality Criteria
*   The frontend is implemented with React and Tailwind CSS, with reusable accessible components.
*   The backend follows the agreed separation of presentation, application/business logic, domain and infrastructure responsibilities.
*   The frontend communicates with the backend through the REST API over HTTPS.
*   PostgreSQL remains the authoritative system of record for structured application data.
*   External AI and other integrations are accessed through replaceable backend adapters.
*   Core business rules are covered by automated tests.
*   New code meets the team's Definition of Done, including review, passing tests, CI success and updated documentation, as defined in Private ([https://app.clickup.com/90122138845/docs/2kxv5d6x-692/2kxv5d6x-92](https://app.clickup.com/90122138845/docs/2kxv5d6x-692/2kxv5d6x-92)).
*   New code should achieve at least 80% test coverage where the existing team agreement applies.
*   The MVP can be deployed and operated as a coherent system without premature microservice complexity.
# 7\. MVP Release Gate
The MVP is ready for stakeholder evaluation only when all of the following are true:
*   A caregiver can create or access a care profile and reach a quick-access dashboard.
*   A caregiver can search or ask for relevant local support services.
*   Results show service type, location, status or availability where known, freshness and contact action.
*   The system clearly distinguishes official status from non-official information.
*   A caregiver can create and send a support request to a backup person.
*   The AI feature is bounded to retrieval and summarisation, with human fallback and safety messaging.
*   Core flows work on mobile-sized screens and meet the agreed accessibility checks.
*   Unauthorised users cannot access another user's care information or support requests.
*   The team has documented known limitations, especially the absence of primary-user validation.
# 8\. Validation Plan and Evidence Quality
The current evidence base is useful for defining hypotheses, but it is not sufficient to claim real-world validation. The Private ([https://app.clickup.com/90122138845/docs/2kxv5d6x-692/2kxv5d6x-172](https://app.clickup.com/90122138845/docs/2kxv5d6x-692/2kxv5d6x-172)) and Private ([https://app.clickup.com/90122138845/docs/2kxv5d6x-692/2kxv5d6x-312](https://app.clickup.com/90122138845/docs/2kxv5d6x-692/2kxv5d6x-312)) explicitly state that the work is based on desk research, empathy mapping and role-play rather than interviews with caregivers or service providers.

Before treating these criteria as validated:
*   Interview 3 to 5 informal caregivers, including at least one older or low-digital-literacy participant.
*   Interview 1 to 2 service providers to test willingness and ability to maintain current information.
*   Test the quick-access and contact flows with representative users.
*   Run AI safety and hallucination scenarios using verified and unavailable service data.
*   Revisit targets and assumptions after the first evaluation cycle.

# Personas

# 1\. Personas
The personas below synthesize the findings from Private ([https://app.clickup.com/90122138845/docs/2kxv5d6x-692/2kxv5d6x-172](https://app.clickup.com/90122138845/docs/2kxv5d6x-692/2kxv5d6x-172)) and the stakeholder map in the Private ([https://app.clickup.com/90122138845/docs/2kxv5d6x-692/2kxv5d6x-112](https://app.clickup.com/90122138845/docs/2kxv5d6x-692/2kxv5d6x-112)). They describe the main types of users the system must serve and are used to prioritize features in the Private ([https://app.clickup.com/90122138845/docs/2kxv5d6x-692/2kxv5d6x-212](https://app.clickup.com/90122138845/docs/2kxv5d6x-692/2kxv5d6x-212)).

**Proto-personas.** These personas were built from desk research, empathy mapping and stakeholder role-play, not from primary interviews. Names, ages and quotes are illustrative composites, not real people. They must be validated with real caregivers and service providers before being treated as final (see User Research, section 3.3 Limitations).

## 1.1 Persona Overview

| Persona | Type | System role | Priority |
| ---| ---| ---| --- |
| Ana, the caregiver balancing work and family | Primary user | Caregiver | Primary |
| Armando, the older spouse caregiver | Primary user | Caregiver | Primary |
| Sofia, the day-care coordinator | Secondary user | Service Provider | Secondary |
| Rui, the backup family member / neighbour | Tertiary user | Volunteer / Community Member | Tertiary |

The **person receiving care** is the main indirect beneficiary but is not modelled as a user persona, as they are not expected to operate the system directly.
* * *
## 1.2 Primary Persona: Ana Costa, the Caregiver Balancing Work and Family
![](https://u105690257.p.clickup-attachments.com/u105690257/1d38b723-ec88-5def-91c6-7eb4e227f50e/ana-framed.png?view=open)

*   **Type:** Primary user
*   **System role:** Caregiver
*   **Profile:** 52 years old, lives in Porto, works full time in an office job, two teenage children. Cares for her mother (81), who has early-stage dementia and lives alone nearby.
*   **Digital literacy:** High. Uses a smartphone all day (WhatsApp, banking apps, email), but has very little free time.
*   **Care context:** Coordinates her mother's medication, medical appointments, day-care attendance and adapted transport. Her brother helps occasionally but lives outside Porto.
> _"The services exist. The problem is that I find out something is closed when I'm already late for work and my mother is waiting at the door."_
### Goals
*   Find a working alternative quickly when part of the care routine fails
*   Spend less time on "phone trees" calling multiple institutions
*   Keep a backup person informed without long explanations
*   Keep her mother safe and her routine stable
### Pain points
*   Information about services is spread across websites, phone numbers and Facebook/WhatsApp groups
*   No easy way to know a service's **real-time** status (open, closed, stock available)
*   Switching from "caregiver mode" to "admin mode" during a crisis causes overload
*   Feels guilty and stressed when juggling work and care
### Typical disruption scenario
The usual pharmacy does not have her mother's medication in stock. Ana needs to find the nearest pharmacy that has it, during her lunch break, in under 10 minutes.
### What the system must give her
*   **Quick-Access dashboard** with the care profile, emergency contacts and nearest open pharmacy in 1 click
*   **AI Service Navigator** to ask plain-language questions (e.g. "pharmacies near Boavista open now")
*   One-tap way to notify her backup network (her brother, a neighbour)
* * *
## 1.3 Primary Persona: Armando Teles, the Older Spouse Caregiver
![](https://u105690257.p.clickup-attachments.com/u105690257/80946314-221b-547a-96c3-48a88a043a1d/armando-framed.png?view=open)
*   **Type:** Primary user
*   **System role:** Caregiver
*   **Profile:** 74 years old, retired, lives in Campanhã with his wife (72), who has reduced mobility after a stroke. Their children live abroad.
*   **Digital literacy:** Low. Has a basic smartphone his grandson set up. Mild visual impairment, prefers phone calls to apps.
*   **Care context:** Is the only daily caregiver. Depends on adapted transport for his wife's physiotherapy and on a day-care centre three times a week, which gives him time to rest and run errands.
> _"I don't understand these apps. Just tell me who I can call."_
### Goals
*   Know who to call when the transport or day-care fails
*   Keep his wife's routine and appointments going
*   Not depend on his children abroad for every small problem
### Pain points
*   Small text, complex menus and long registration processes
*   Distrust of systems that "decide things" automatically, especially about health
*   Hesitant to share health information online
*   Physically and emotionally tired, with no substitute if he gets ill
### Typical disruption scenario
The adapted transport cancels his wife's physiotherapy trip with one hour's notice. Armando needs another transport option or someone to reschedule, without navigating a complex system.
### What the system must give him
*   **Accessibility-first UI:** large text, high contrast, big touch targets, plain language, minimal steps
*   A prominent **"Call \[Institution\]"** button on every result (human fallback)
*   No requirement to upload medical documents, only simple non-sensitive tags (e.g. "Requires wheelchair access")
*   Optional delegated setup, so a family member can configure the profile for him
* * *
## 1.4 Secondary Persona: Sofia Martins, Day-Care Coordinator (Service Provider)
![](https://u105690257.p.clickup-attachments.com/u105690257/5cded62c-f3b1-59b4-b955-25da03086f86/sofia-framed.png?view=open)
*   **Type:** Secondary user
*   **System role:** Service Provider
*   **Profile:** 39 years old, social worker and coordinator at an IPSS day-care centre in Porto supporting around 40 older adults.
*   **Digital literacy:** Medium. Uses email, spreadsheets and the institution's internal systems. Very limited time for "yet another platform".
*   **Context:** Handles calls from families every time there is a closure, schedule change or lack of places.
> _"If families could see that we're closed for disinfection on Friday, I'd get thirty fewer calls."_
### Goals
*   Communicate closures, vacancies and schedule changes to families in one place
*   Reduce repetitive phone calls
*   Increase the visibility of the institution's services
*   Receive appropriate referrals, not requests the centre cannot handle
### Pain points
*   Has no time for complex data entry
*   Worried that outdated information about her institution circulates online
*   Unclear who is responsible if a family relies on wrong information
### What the system must give her
*   **Verified organisation account**, so her updates appear as "Official Status"
*   A fast way to update status (open/closed, places available) in under a minute
*   Clear separation between official updates and community reports
*   Access limited to what her role needs (least privilege)
* * *
## 1.5 Tertiary Persona: Rui Almeida, Backup Family Member / Neighbour
![](https://u105690257.p.clickup-attachments.com/u105690257/1f79efba-c2ab-5273-95c9-e95e2bcd28cb/rui-framed.png?view=open)
*   **Type:** Tertiary user
*   **System role:** Volunteer / Community Member
*   **Profile:** 34 years old, Ana's neighbour and friend. Works in shifts and is willing to help occasionally (picking up medication, a short visit).
*   **Digital literacy:** High.
> _"I'm happy to help, I just need to know exactly what's needed and when."_
### Goals
*   Receive clear, specific and manageable requests
*   Help without taking on responsibilities he can't handle
### Pain points
*   Vague requests in group chats ("can anyone help with mum?")
*   Doesn't know the care routine or the person's needs
*   Availability is irregular
### What the system must give him
*   Clear support requests with what, where and when
*   Only the minimum information needed to help (no unnecessary health data)
*   Ability to accept or decline a request easily
* * *
## 1.6 Design Implications Summary

| Need | Ana | Manuel | Sofia | Rui | Product decision |
| ---| ---| ---| ---| ---| --- |
| Fast alternatives during disruptions | ✅ | ✅ |  |  | Quick-Access dashboard, AI Service Navigator |
| Accessibility and simplicity |  | ✅ | ✅ |  | Accessibility-first React/Tailwind UI |
| Human fallback and trust | ✅ | ✅ |  |  | "Call \[Institution\]" button and AI disclaimer |
| Reliable, current information | ✅ | ✅ | ✅ |  | Verified service directory, official vs. community status |
| Privacy and data minimization | ✅ | ✅ |  | ✅ | Non-sensitive tags, no medical uploads, RBAC (ADR-006, ADR-008) |
| Coordinating backup support | ✅ |  |  | ✅ | Support requests and backup network notifications |

## 1.7 Validation Plan (to validate)
- [ ] Interview at least 3 to 5 informal caregivers (ideally one per primary persona) to confirm or adjust goals and pain points
- [ ] Interview at least 1 to 2 service providers (day-care, pharmacy, IPSS) to validate their willingness to keep status up to date
- [ ] Test a low-fidelity prototype of the Quick-Access dashboard with an older user to validate accessibility assumptions
- [ ] Update these personas and the Product Backlog based on the results

# MVP Definition

**MVP goal.** Help informal caregivers find a relevant local service alternative and coordinate the next action when normal care arrangements fail.

# MVP Definition
## 1\. Problem
Informal caregivers coordinate medication, transport, appointments and social care across fragmented websites, phone numbers and informal messages. When a service fails, they need a trustworthy next step quickly, but information may be outdated and availability cannot be guaranteed.

The MVP focuses on **reducing search and coordination effort**, not replacing healthcare or social-care services.
## 2\. Target users
*   **Primary:** informal caregivers, especially time-poor caregivers and older caregivers with lower digital literacy.
*   **Secondary:** verified service providers such as pharmacies, day-care centres, transport providers and support organisations.
*   **Support users:** family members, neighbours and volunteers who receive clearly defined requests.

The person receiving care benefits indirectly and is not expected to operate the MVP.
## 3\. Core MVP experience
### A. Quick-access care dashboard
The caregiver can:
*   Create or access a simple care profile.
*   See important contacts and basic care needs using minimal, non-sensitive information.
*   Start a service search or support request in one or two steps.
### B. Local service search
The caregiver can search using plain language or simple filters such as:
*   Service type
*   Location or distance
*   Accessibility needs
*   Known status or availability

Each result shows the service name, location, source, verification state, last update and a clear **Call or Contact** action. Results must state that availability is not guaranteed and should be confirmed directly.
### C. Bounded AI Service Navigator
The caregiver can ask a question such as “pharmacies near Boavista open now”. The AI may retrieve and summarise information from the application's service data, but it must not:
*   Diagnose, prescribe or give medical advice.
*   Make emergency decisions.
*   Invent service facts or present availability as certain.
*   Receive unnecessary personal or health information.

Every recommendation includes its source context, limitations and a human or institutional fallback.
### D. Support requests
The caregiver can send a concrete request to a backup person with:
*   What is needed
*   Where it is needed
*   When it is needed
*   The minimum contact information required

The recipient can accept or decline. The caregiver can see whether the request is pending, accepted or declined.
### E. Verified service updates
A verified organisation can update a service's status, schedule or notice in under one minute. Official information is visibly separated from community reports and includes the update time. Validity periods, audit history, and correction or withdrawal are planned for Release 2.
## 4\. Non-functional requirements
*   Mobile-first, accessible interface with large text, high contrast, clear labels and large touch targets.
*   A human contact option at every critical point.
*   Authentication and role-based access control enforced by the backend.
*   Data minimisation, consent and least-privilege sharing.
*   No mandatory medical-document upload.
*   No direct frontend connection to the AI provider.
*   Responsive React and Tailwind frontend, Java/Spring Boot REST backend and PostgreSQL system of record.
*   Core business rules covered by automated tests, with at least 80% coverage on new code where the team agreement applies.
## 5\. Out of scope for the MVP
*   Medical diagnosis, treatment recommendations or emergency response.
*   Guaranteed real-time availability or automatic reservation of services.
*   Full healthcare-record integration.
*   Mandatory sharing of sensitive medical information.
*   A self-hosted AI model.
*   Microservices or complex real-time infrastructure.
*   Expansion beyond the initial local/urban pilot before the core assumptions are validated.
## 6\. MVP release criteria
The MVP is ready for evaluation when:
*   A caregiver can reach the dashboard and search for a relevant local service.
*   A result clearly shows source, freshness, status or availability where known, and contact action.
*   A caregiver can send and track a support request.
*   A verified provider can publish a basic update in under one minute.
*   The AI remains grounded, bounded and transparent.
*   Core flows work on mobile-sized screens and meet the agreed accessibility checks.
*   Users cannot access another user's care information or requests.
*   Known limitations and unvalidated assumptions are documented.
## 7\. Success measures
*   At least **80%** of participants complete the core search task without intervention.
*   At least **90%** correctly identify the next action.
*   Median time from opening the dashboard to reaching a relevant result is **3 minutes or less**.
*   At least **80%** complete the support-request flow.
*   At least **80%** of older-user participants complete a search or contact task.
*   Service source, verification state and freshness are visible in relevant results.

# Problem and Opportunity Report

# Problem and Opportunity Report
## 1\. Target Community
The target community is **informal caregivers of dependent family members in an urban municipality**.
These caregivers provide unpaid, regular support to people who cannot fully manage their daily needs independently, including:
*   Older adults, particularly people with dementia or reduced autonomy
*   People with chronic illnesses
*   People with physical or intellectual disabilities
*   People recovering from surgery or serious illness
The **informal caregiver is the primary user**, while the person receiving care is the main indirect beneficiary.
The wider stakeholder community includes family members, pharmacies, healthcare providers, day-care centres, IPSS organisations, Santa Casa da Misericórdia, municipal social services, adapted transport providers, volunteers and neighbours.
* * *
## 2\. Problem Statement
Informal caregivers often become the **main coordinator of a dependent person's everyday care**. They may be responsible for medication, medical appointments, transportation, personal care, meals, social services and communication with different organisations.
This creates a vulnerable situation because the caregiver may depend on several services and people for care to function normally.
Small disruptions can therefore have significant consequences. Examples include:
*   A specific medication being unavailable at the usual pharmacy
*   Temporary closure of a day-care or support facility
*   Failure of adapted transportation
*   The caregiver becoming ill without a substitute
*   A family member who normally provides support becoming unavailable
*   Power outages affecting medical equipment
*   Difficulty finding accurate and up-to-date information about available services
### Core Problem
> **Informal caregivers face fragmented information, limited coordination between support services and a lack of reliable alternatives when their normal care arrangements are disrupted.**
As a result, caregivers may spend significant time searching for information, contacting multiple organisations and trying to coordinate alternatives while already managing demanding care responsibilities.
* * *
## 3\. Stakeholder Map

| Stakeholder | Role | Main Needs / Interests |
| ---| ---| --- |
| Informal caregiver | Primary user | Reliable information, quick support, continuity of care |
| Person receiving care | Main beneficiary | Safety, continuity and appropriate care |
| Family members | Informal support | Coordination, information and ability to provide assistance |
| Pharmacies | Service provider | Clear communication and efficient referrals |
| Healthcare providers | Health support | Continuity of treatment and appropriate referrals |
| Day-care centres | Care provider | Communication with caregivers and service coordination |
| IPSS / NGOs | Social support | Visibility, referrals and coordination |
| Santa Casa da Misericórdia | Social support | Coordination of community care |
| Municipal social services | Public support | Social inclusion and effective use of local resources |
| Social Security | Public institution | Support and protection for caregivers |
| Transport providers | Mobility support | Reliable service coordination |
| Volunteers / neighbours | Community support | Clear, appropriate and manageable requests |

### Stakeholder relationship
The **informal caregiver is at the centre of the ecosystem**, connecting the person receiving care with multiple external services and people.

```markdown
                 Municipal Services
                        │
             ┌──────────┴──────────┐
             │                     │
        Healthcare             Social Services
             │                     │
             └──────────┬──────────┘
                        │
                 INFORMAL CAREGIVER
                        │
          ┌─────────────┼─────────────┐
          │             │             │
      Family       Pharmacy       Day Care
                        │
                   Transport
                        │
                Person Receiving Care
```

* * *
## 4\. Problem Evidence
The problem is supported by demographic, social and service-related evidence.
### Demographic evidence
Porto has a significant older population, with **more than 60,000 residents aged 65 or older**. A substantial number of older residents also live alone, increasing the importance of formal and informal support networks.
### Informal caregiving
Informal caregiving is a significant part of the care system. People regularly provide unpaid assistance to relatives or other people who depend on them for daily activities and health-related needs.
### Caregiver burden
Caregiving can require significant amounts of time and can affect employment, family responsibilities and the caregiver's own wellbeing. The more dependent the person receiving care becomes, the more important reliable external support becomes.
### Fragmented support
There are already numerous organisations and services supporting older people and dependent individuals. However, these services are distributed across different organisations, websites, telephone numbers and administrative systems.
This creates a **coordination and information problem** for caregivers.
### Information gap
Existing local social assessments have identified difficulties related to accessing information about informal care and available assistance. This supports the hypothesis that caregivers may struggle not only because services are limited, but because **finding and navigating the available support is difficult**.
### Key evidence insight
> **The existing ecosystem contains many relevant resources, but caregivers may have difficulty knowing what is available, who to contact, whether a service is currently available, and what alternative exists when their usual arrangement fails.**
* * *
## 5\. Assumptions and Risks
### Assumptions

| Assumption | Why it matters |
| ---| --- |
| Caregivers experience difficulty finding support during disruptions | Validates the existence of the problem |
| Information about services is fragmented | Supports the need for better coordination |
| Caregivers would value a trusted source of information | Indicates potential user demand |
| Existing organisations are willing to cooperate | Necessary for an interconnected support network |
| Service providers can provide reasonably current information | Necessary for information to be reliable |
| Some caregivers have limited digital literacy | Means accessibility cannot depend entirely on technology |
| Families and community members can provide some backup support | Expands the available support network |
| Care-related information can be shared securely and appropriately | Necessary for privacy and trust |

### Risks

| Risk | Potential Impact |
| ---| --- |
| Outdated information | Caregivers may rely on incorrect service availability |
| Low user adoption | The target community may continue using existing informal methods |
| Digital exclusion | Older or less digitally confident caregivers may be left out |
| Limited service capacity | Finding a service does not guarantee that it can actually provide assistance |
| Low stakeholder participation | The support network may not have enough participating organisations |
| Privacy and GDPR concerns | Care information can involve sensitive personal data |
| Over-reliance on volunteers | Volunteer availability may be unpredictable |
| Unclear responsibility | Users may expect the network to provide emergency or professional care |
| Funding and sustainability | Maintaining accurate information and coordination requires resources |
| Trust | Caregivers may be reluctant to rely on information from an unfamiliar service |

### Key Assumption to Validate
The most important assumption is:
> **Informal caregivers experience a significant coordination and information problem when their normal care arrangements are disrupted, and existing services do not provide an easy way to identify suitable alternatives.**
This should be validated through interviews and research with caregivers and service providers before defining the final solution.
# 6\. SWOT Analysis
The SWOT analysis considers the potential of a **coordinated local support network for informal caregivers**.

| Strengths | Weaknesses |
| ---| --- |
| Addresses a clear and socially relevant problem | Depends on cooperation between multiple organisations |
| Builds on existing local services and resources | Information may be difficult to keep up to date |
| Focuses on coordination rather than duplicating existing services | Caregivers have different needs and levels of dependency |
| Could reduce the time caregivers spend searching for help | Some caregivers may have limited digital literacy |
| Connects formal and informal support networks | Requires trust from both caregivers and service providers |
| Can potentially respond to different types of care disruptions | Availability of support cannot always be guaranteed |

| Opportunities | Threats |
| ---| --- |
| Growing demand for elderly and dependent-person care | Ageing population may increase demand faster than available resources |
| Increasing recognition of informal caregivers | Limited capacity within existing social and healthcare services |
| Partnerships with municipalities, IPSS, pharmacies and healthcare providers | Organisations may be unwilling or unable to share information |
| Potential to improve visibility of existing services | Outdated information could damage user trust |
| Possibility of expanding to other municipalities | GDPR and privacy requirements |
| Development of community-based support networks | Dependence on volunteers may create inconsistent availability |
| Potential integration with existing public and social-care programmes | Funding and long-term sustainability challenges |

### SWOT Summary
The main **strength** is that the concept addresses a real coordination gap while making use of an existing ecosystem rather than attempting to replace it.
The main **weakness** is the dependency on multiple organisations and the difficulty of maintaining reliable, up-to-date information.
The main **opportunity** is to improve coordination between existing resources and make them easier for caregivers to navigate.
The main **threat** is that demand for support may exceed the capacity of available services, meaning that better coordination alone cannot solve shortages in care resources.

# Market and Competitor Analysis

## Market and Competitor Analysis
### 1\. Market Context
In Portugal and across Europe, population aging combined with resource constraints in nursing homes and public social care services transfers the primary responsibility of care onto families. Informal caregivers operate under high psychological, emotional, and operational strain, often in isolation. When day-to-day disruptions occur (e.g., medication stockouts, specialized transport failures, equipment power outages), existing support networks are reactive, fragmented, and slow to respond.
* * *
### 2\. Analysis of Existing Competitors and Alternatives

| Category / Solution | Representative Alternatives | Core Strengths | Limitations & Weaknesses |
| ---| ---| ---| --- |
| Institutional & State Portals | Informal Caregiver Status (Segurança Social), Municipal Social Action Portals. | • Official information with legal standing.<br>• Covers formal rights, subsidies, and official frameworks. | • Written in bureaucratic, complex language.<br>• Static data (cannot address real-time daily disruptions).<br>• Lacks peer-to-peer community support mechanisms. |
| Informal Social Media Groups | Facebook Groups, Local WhatsApp Groups (neighborhood/parish level). | • High user familiarity and adoption.<br>• Rapid emotional support and peer empathy. | • High risk of medical and practical misinformation.<br>• Lack of moderation, request triaging, or privacy safeguards.<br>• Chaotic and unorganized content during urgent situations. |
| Global Task / Volunteering Apps | Nextdoor, TaskRabbit, TimeRepublik. | • Modern UI/UX.<br>• Proven concepts for geolocation and neighborhood interaction. | • Focused on general tasks (DIY, gardening, errands).<br>• Lacks domain sensitivity for dependent care and health contexts.<br>• No identity verification for volunteers or integration with local healthcare ecosystems. |
| Dedicated Caregiver Apps | CareZone, Ianacare, Patient Association Apps (e.g., Alzheimer Portugal, APDP). | • Strong focus on individual medication management and health logs. | • Primarily localized to inside-the-home individual management.<br>• Disconnected from the surrounding local community (local pharmacies, local adapted transport, parish services). |

* * *
### 3\. Opportunity for Differentiation (Market Gap)
No current solution combines **real-time local disruption management** with **responsible moderation and verified community-driven support**.
Our platform addresses this gap by acting as a localized "nervous system" for informal caregivers, connecting key stakeholders in real time:
1. **Formal Actors:** Local pharmacies, parish councils (_juntas de freguesia_), social care NGOs (IPSS), and local health centers.
2. **Informal Actors:** Verified local volunteers, neighbors, and extended family members.
* * *
### 4\. Value Proposition
> _"For informal caregivers managing dependent family members under high daily stress, our platform is a real-time local support network that simplifies access to emergency resources, mitigates daily disruptions, and coordinates verified community aid—delivering peace of mind and continuous care."_
* * *
### 5\. Key Differentiators for the MVP
1. **Intelligent Disruption Management with Responsible AI:**
    *   Automated triaging and summarization of local alerts (e.g., pharmacy stockouts, service closures) translated into clear, plain-language notifications.
    *   Deduplication of redundant assistance requests during local emergency events.
2. **Privacy-Preserving Trust & Verification System:**
    *   Moderated workflows for micro-volunteering requests to protect dependent individuals' privacy.
    *   Coarse, proximity-based location sharing (parish/neighborhood level) without revealing exact residential addresses.
3. **Resilience & Degraded Operation:**
    *   Offline access to critical emergency contacts, care routines, and medication schedules, ensuring availability during network or power disruptions.
* * *
### 6\. Sustainability & Social Impact Model
*   **Adoption & Model (B2G / B2B2C):** Implemented in partnership with Municipalities, Parish Councils, and Local Social Action Networks (IPSS) seeking to optimize social resource allocation and prevent avoidable hospital admissions.
*   **Social Return Metric:** Reduction in response time during critical caregiver disruptions and measurable mitigation of caregiver burnout.

# User Research

# 1\. User Research
To validate the core assumptions outlined in the Problem and Opportunity Report, the team conducted user research focused on understanding the daily realities, pain points, and coordination challenges of informal caregivers.
**Methodological Note on Access:** Due to logistical, ethical, and privacy constraints regarding access to vulnerable populations (informal caregivers and dependent individuals), the team was unable to conduct primary interviews or direct surveys. To strictly adhere to the project's academic integrity requirement of not fabricating user research evidence, the team utilized a combination of **structured team brainstorming (Design Thinking exercises)**, **stakeholder role-play**, and **extensive desk research** to build a validated understanding of the user's context.
## 1.1 Research Method
The research was conducted using a triangulated approach, combining secondary data analysis with structured internal ideation:
1. **Desk Research & Public Data Analysis (Secondary):**
    *   **Execution:** The team analyzed demographic data for the municipality of Porto (e.g., the 60,000+ residents aged 65+), public health reports, and existing literature on "caregiver burden" and the fragmentation of social support networks (IPSS, Santa Casa, municipal services).
    *   **Focus:** Understanding the macro level statistics of aging populations, the legal and social definitions of informal caregiving, and the documented consequences of care disruptions.
2. **Empathy Mapping & "Day in the Life" Brainstorming (Internal Ideation):**
    *   **Execution:** The team conducted structured brainstorming sessions using **Empathy Mapping** and **Stakeholder Role-Play**. Team members adopted the personas of the primary user (the informal caregiver) and secondary stakeholders (e.g., a pharmacy worker, a daycare coordinator, a dependent elderly person).
    *   **Focus:** Mapping out what the caregiver _sees, hears, thinks, and feels_ during a care disruption (e.g., a sudden pharmacy stockout or daycare closure). This helped the team simulate the cognitive load and emotional stress of the user without relying on fabricated interview transcripts.
3. **Observation of Existing Informal Channels:**
    *   **Execution:** The team reviewed how communities currently attempt to solve this problem, analyzing the structure and friction points of existing informal methods (e.g., fragmented municipal directories, community Facebook groups, and WhatsApp chains).
    *   **Focus:** Identifying where misinformation spreads, how much time is wasted on "phone trees," and where the current informal systems fail during a crisis.
## 1.2 Findings
Through the synthesis of desk research and team empathy mapping, the following critical insights and hypotheses were generated regarding the caregiver experience:
### 1\. The "Phone-Tree" Burden and Cognitive Overload
While the ecosystem of care services in Porto is vast, the _discovery_ and _verification_ of these services is highly fragmented.
*   _Insight derived from Empathy Mapping:_ When a disruption occurs, the caregiver experiences immediate cognitive overload. They must transition from a "caregiving" mindset to an "administrative" mindset, spending hours calling multiple institutions to check availability. The problem is not a lack of services, but the **high friction to discover their real-time status**.
### 2\. High Stress and the Need for "Quick-Access" Alternatives
During a crisis (e.g., the caregiver falls ill or transport fails), the immediate reaction is panic regarding the safety of the dependent person.
*   _Insight derived from RolePlay:_ In a high-stress scenario, a user does not have the patience or cognitive bandwidth to navigate complex menus, read long articles, or learn a new system. They need **immediate, actionable alternatives** (e.g., "3 nearest pharmacies with this medication") and a way to quickly notify their backup support network.
### 3\. The Digital Divide and Accessibility Realities
The caregiver demographic is not monolithic. "Sandwich generation" caregivers (ages 45–60) are digitally literate but time-poor. Older caregivers (ages 65+) caring for even older spouses often have lower digital literacy, visual impairments, and a strong preference for traditional phone calls.
*   _Insight derived from Desk Research & Brainstorming:_ The application cannot rely on complex, multistep onboarding or small text. The UI must be **highly accessible, with large typography, high contrast, and minimal clicks** to ensure it does not digitally exclude the most vulnerable caregivers.
### 4\. Trust, Privacy, and the "Human Fallback"
Care related information involves sensitive health and dependency data.
*   _Insight derived from Stakeholder Analysis:_ Caregivers are highly protective of their dependent's data and would be hesitant to input sensitive medical details into a new, unproven application. Furthermore, when dealing with health or safety, users are skeptical of automated systems making definitive decisions. The system must practice **strict data minimization** and ensure that any AI generated suggestion is accompanied by a clear way to verify the information with a human or the institution directly.
## 1.3 Limitations
Because primary research with real users was not possible, the team acknowledges the following critical limitations in this research phase:
*   **Lack of Primary User Validation:** The most significant limitation is the absence of direct interviews with actual informal caregivers. The "Findings" are based on empathy mapping and secondary research, which introduces a risk of **team bias and assumption**. The team may have overlooked nuances of the caregiver experience that only direct observation or interviews could reveal.
*   **Institutional Perspective Gap:** While the team brainstormed the perspectives of service providers (pharmacies, IPSS), we did not interview actual staff at these institutions. Therefore, our assumptions regarding their willingness or technical capacity to update their availability in a shared system remain unvalidated hypotheses.
*   **Geographic Constraint:** The desk research was heavily focused on an urban context (Porto). The dynamics of rural or semi-rural municipalities, where transport distances are much greater and services are scarcer, were not deeply explored.
*   **Retrospective Simulation:** The "Day-in-the-Life" empathy exercises rely on the team's imagination and general knowledge. They cannot fully replicate the genuine emotional fatigue and stress of a real caregiver facing an actual emergency.
## 1.4 Resulting Product Decisions
Despite the limitations in primary research, the insights generated from the desk research and structured brainstorming directly influenced the Product Vision, MVP boundary, and Technical Architecture (as documented in the ADRs). The following product decisions were made:
### 1\. Simplified, Accessibility-First UI (Frontend Decision)
*   **Decision:** The React/Tailwind frontend will prioritize a "mobile first, accessibility first" design. We will implement large touch targets, high contrast modes, and plain-language interfaces.
*   **Rationale:** To accommodate older caregivers with limited digital literacy or visual impairments, ensuring the tool is usable by the demographic that needs it most.
### 2\. "Quick-Access" Emergency Dashboard
*   **Decision:** The MVP will include a "Care Profile" dashboard that displays critical, static information (emergency contacts, nearest open pharmacy, basic care routine) accessible in 1 click.
*   **Rationale:** Addresses the empathy mapping finding that cognitive overload during disruptions makes complex navigation impossible.
### 3\. Responsible AI: "Service Navigator" with Human Fallback
*   **Decision:** The AI (Managed LLM) will be used strictly for **information retrieval and summarization** (e.g., "Find me daycare centers in \[Neighborhood\] that accept wheelchair users"), _not_ for medical advice.
*   **Crucial Fallback:** Every AI-generated response will include a prominent "Call \[Institution\]" button and a disclaimer that the AI is summarizing public data.
*   **Rationale:** Aligns with the LABDSOF Responsible AI requirements and addresses the trust/privacy concerns identified during stakeholder role-play.
### 4\. Privacy-by-Design & Data Minimization
*   **Decision:** The system will not require the upload of medical documents. Care needs will be categorized using standardized, non sensitive tags (e.g., "Requires wheelchair access").
*   **Rationale:** Directly responds to the privacy concerns identified in the research. Sensitive data is kept out of the AI prompts, adhering to ADR-008 (Security and Privacy).
### 5\. Verified Service Directory (Trust Mechanism)
*   **Decision:** To prevent the spread of outdated information (a major threat identified in the SWOT), only verified organizational accounts can update their service status. User-generated reports will be visually distinguished from "Official Status".
*   **Rationale:** Builds trust with the user base by clearly delineating between verified institutional data and community crowdsourcing.

# Product Vision

# Name
CareBnB
# Goal
Help informal caregivers quickly find a reliable alternative when their normal care arrangements are disrupted (medication unavailable, day-care closed, transport cancelled, caregiver unavailable), by making existing local services easier to find, trust and coordinate.
# Metrics
*   **Time to find an alternative:** how long a caregiver takes to find a working alternative during a disruption. Target: under 10 minutes
*   **Provider update time:** how long a service provider takes to publish a status update. Target: under 1 minute
# Target Groups
Informal caregivers, service providers (day-care centres, pharmacies, etc.) and support network (family members, neighbours, volunteers,etc.)
**Indirect beneficiary:** the person receiving care (not expected to be a direct user)
# Big Picture
Main user journeys (provisional, to be validated with user research):
1. **Medication out of stock:** a caregiver finds an open pharmacy nearby, compares Official and Community results, calls to confirm and notifies a backup contact.
2. **Transport cancelled:** a low-literacy caregiver taps one button, sees options in plain language and calls the institution or asks a family member for help.
3. **Day-care centre closing:** a provider publishes an Official closure notice and families are notified.
4. **Request for help:** a caregiver sends a request (what, where, when) to a support contact, who accepts or declines.
## Product Details
**Key features (epics)**
*   **Access and care profile (E1):** secure login, role-based access, basic care profile with non-sensitive tags, emergency contacts and quick-access dashboard.
*   **Find an alternative (E2):** search and filtering of local services by type, location, accessibility and status.
*   **AI-assisted navigation (E3):** plain-language questions answered only from verified service data, with guardrails and a fallback to standard search.
*   **Check and confirm (E4):** service details with source, verification state and last update, plus a prominent “Call” button.
*   **Support network (E5):** concrete support requests (what, where, when) sent to a backup contact, with asynchronous notifications and accept/decline responses.
*   **Service information (E6):** verified providers publish Official status updates in under one minute.
**UX principles**
*   Mobile-first and accessibility-first: large text, high contrast, large touch targets, simple language and few steps.
*   A human contact option (call the institution) at every critical point.
*   Availability is never presented as a guarantee; source and freshness are always visible.
**Main non-functional requirements**
*   **Security and privacy:** backend-enforced authentication and RBAC, data minimisation, no mandatory medical documents, and no personal or sensitive data sent to the external LLM.
*   **Responsible AI:** the AI only retrieves and summarises verified data, never gives medical advice, and redirects emergencies to 112 / SNS 24.
*   **Reliability:** if the AI fails, core features keep working and users fall back to standard search.
*   **Performance:** relevant result in a median of 3 minutes or less; provider update in under 1 minute.
*   **Quality:** at least 80% test coverage on new backend code, plus CI/CD, logging and health checks.
**Technology and constraints**
*   React + Tailwind CSS, Java/Spring Boot, PostgreSQL, and a managed external LLM API.
*   Two independently deployable backend components (Care Platform API and Support Assistant Service) communicating over REST.
*   GDPR compliance with defined retention and deletion rules.
**Release plan**
*   **Release 1 (walking skeleton):** login, search (standard and AI), service details with call, support requests, and provider status updates.
*   **Release 2:** dashboard, filters, expiry warnings, request responses and status, provider validity, correction and audit history.
*   **Later:** delegated setup, community reports, multi-recipient requests and closure notifications.
See the Product Backlog and Story Map for the full list of user stories.

# Product Backlog

# Story Mapping
We built a story map to create a clear, prioritised product backlog. It shows how stories fit into the caregiver’s journey, what depends on what, and what the smallest usable first release is, before we define epics and detailed user stories.

**Structure.**
*   **Backbone (left to right):** the main activities of the user’s journey, from getting started to keeping provider information current.
*   **Priority (top to bottom):** stories are ordered by importance under each activity.
*   **Releases (horizontal slices):** Release 1 is the walking skeleton, the thinnest end-to-end version that delivers value. Release 2 extends it, and Release n holds later ideas. Empty cells mean nothing is planned.
*   **Cross-cutting band:** work that applies to every release, such as accessibility, data minimisation, testing, CI/CD, logging and failure handling.
*   **Epics (E1–E8):** each story is tagged with its epic.

**Inputs.** The map is based on the proto-personas, the user journeys and BPMN workflows, and the assignment requirements.

**Team process.** The map was built collaboratively by the team and serves as a shared reference for scope and priorities. It is a living document and will be refined as the team’s understanding of the product evolves.

![](https://t90122138845.p.clickup-attachments.com/t90122138845/961443db-aece-430e-97af-1b97222bc1ad/image.png)

# Epics

| Epic | Name | Scope |
| ---| ---| --- |
| E1 | Access and care profile | Authentication, role-based access, care profile, emergency contacts, dashboard |
| E2 | Find an alternative | Search and filtering of local services |
| E3 | AI-assisted navigation | Plain-language search grounded in verified data, AI guardrails, non-AI fallback |
| E4 | Check and confirm | Service details, source and freshness of information, confirming by phone |
| E5 | Support network | Support requests to backup contacts, asynchronous notification, responses |
| E6 | Service information | Provider verification and updates, information validity, external data source |
| E7 | Inclusive, privacy-first design | Accessible mobile-first UI, data minimisation |
| E8 | Quality and operations | Automated tests, CI/CD, logging, health checks, metrics, failure handling |

# Product Backlog
Stories are estimated with standard Fibonacci story points (1, 2, 3, 5, 8, 13) and must meet the project's Definition of Ready (DoR). Estimates are the first team proposal and should be confirmed in planning.

> **Priority (MoSCoW):** **Must** = needed for the walking skeleton (Release 1) and for the assignment's required capabilities; **Should** = important, in Release 2; **Could** = desirable if time allows; **Won't (this time)** = agreed for later, not in the current scope.

## Release 1: Walking skeleton

| ID | Epic | Title | User Story Description | Priority (MoSCoW) | Estimate (Pts) |
| ---| ---| ---| ---| ---| --- |
| US01 | E1 | Log in as caregiver or provider | As a registered user, I want to log in securely as a caregiver or a provider, so that my data and care information are protected. | Must | 5 |
| US02 | E1 | Restrict features by role | As a platform owner, I want features to be restricted by role, so that each user only accesses what their role allows. | Must | 3 |
| US03 | E1 | Create basic care profile | As an informal caregiver, I want to create a basic care profile using only non-sensitive tags, so that I can describe essential needs without sharing medical data. | Must | 3 |
| US04 | E1 | Manage emergency contacts | As an informal caregiver, I want to manage my emergency contacts, so that I can reach them quickly when my care routine fails. | Must | 3 |
| US05 | E2 | Search services by type and location | As a caregiver, I want to search for local services by type and location, so that I can find relevant alternatives when my care routine fails. | Must | 5 |
| US06 | E3 | Ask a question in plain language | As a caregiver, I want to type a question in plain language (e.g., "pharmacies open near Boavista"), so that I do not need to learn filters or menus. | Must | 3 |
| US07 | E3 | Answer built only from verified service data | As a caregiver, I want the assistant's answer to be built only from verified service data, so that I can rely on what it tells me. | Must | 5 |
| US08 | E3 | AI guardrails | As a caregiver, I want the AI assistant to show its sources and limitations and to refuse medical or emergency questions, so that I get trustworthy, non-harmful answers. | Must | 5 |
| US09 | E3 | Fall back to standard search if AI is unavailable | As a caregiver, I want to be able to use the standard search when the AI assistant is unavailable, so that I can still find alternatives. | Must | 3 |
| US10 | E4 | View service details | As a time-poor caregiver, I want to view clear service details with source and last update, so that I can judge how reliable the information is. | Must | 3 |
| US11 | E4 | Call button with "confirm by phone" notice | As a caregiver, I want a prominent "Call" button with a notice to confirm by phone, so that I can verify availability directly. | Must | 2 |
| US12 | E5 | Send support request | As a caregiver, I want to send a concrete support request (what, where, when) to a backup contact, so that I can coordinate help when I'm unavailable. | Must | 5 |
| US13 | E5 | Notify contact asynchronously | As a caregiver, I want the backup contact to be notified in the background, so that I do not have to wait while my request is delivered. | Must | 5 |
| US14 | E5 | Retry failed notifications without duplicates | As a caregiver, I want failed notifications to be retried without sending duplicates, so that my request reaches the contact even if the first attempt fails. | Must | 5 |
| US15 | E6 | Verify provider organisation | As the platform, I want to verify provider organisations, so that only verified providers publish official status updates. | Must | 5 |
| US16 | E6 | Update service status in under a minute | As a verified service provider, I want to update my service status in under one minute, so that caregivers receive accurate, up-to-date information marked as Official. | Must | 5 |

## Release 2

| ID | Epic | Title | User Story Description | Priority (MoSCoW) | Estimate (Pts) |
| ---| ---| ---| ---| ---| --- |
| US17 | E1 | Quick-access dashboard with 1-click searches | As a caregiver, I want a simplified dashboard with 1-click searches and emergency contacts, so that I can reach a solution within 3 minutes during a disruption. | Should | 5 |
| US18 | E2 | Filter by accessibility needs | As a caregiver, I want to filter services by accessibility needs, so that I only see services that fit the person I care for. | Should | 3 |
| US19 | E2 | Filter by open or closed status | As a caregiver, I want to filter services by open or closed status, so that I see only services available now. | Could | 2 |
| US20 | E4 | Warn when information has expired | As a caregiver, I want to be warned when a service's information has expired, so that I do not rely on outdated data. | Should | 3 |
| US21 | E5 | Accept or decline a request | As a backup family member or volunteer, I want to accept or decline a support request, so that the caregiver knows my availability immediately. | Should | 3 |
| US22 | E5 | See request status on the dashboard | As a caregiver, I want to see the status of my support requests on the dashboard, so that I know whether help is on the way. | Should | 3 |
| US23 | E6 | Set validity period | As a verified service provider, I want to set a validity period for my updates, so that outdated information expires automatically. | Should | 3 |
| US24 | E6 | Correct or remove information | As a verified service provider, I want to correct or remove information I published, so that mistakes do not mislead caregivers. | Should | 3 |
| US25 | E6 | Audit history of changes | As a platform owner, I want an audit history of changes to service information, so that updates are traceable. | Could | 5 |

## Release n (later)

| ID | Epic | Title | User Story Description | Priority (MoSCoW) | Estimate (Pts) |
| ---| ---| ---| ---| ---| --- |
| US26 | E1 | Grant a family member access to a care profile | As a caregiver with low digital literacy, I want to give a family member access to my care profile, so that they can help me use the platform. | Won't (this time) | 5 |
| US27 | E1 | Set up the care profile on behalf of a caregiver | As a family member, I want to set up the care profile on behalf of a caregiver, so that they can use the platform with help. | Won't (this time) | 3 |
| US28 | E4 | Submit a community report | As a caregiver, I want to submit a report about a service (e.g., it is closed today), so that other caregivers are warned. | Won't (this time) | 5 |
| US29 | E4 | Show community reports separate from Official | As a caregiver, I want community reports clearly separated from official provider information, so that I can use both without confusing them. | Won't (this time) | 3 |
| US30 | E5 | Send to several people | As a caregiver, I want to send a support request to several people, so that I have a better chance of getting help. | Won't (this time) | 5 |
| US31 | E5 | Choose next contact after a decline | As a caregiver, I want to choose the next contact when a request is declined, so that I can keep looking for help. | Won't (this time) | 3 |
| US32 | E6 | Notify families of a closure | As a caregiver, I want to be notified when a service I rely on closes, so that I can plan an alternative. | Won't (this time) | 5 |

## Cross-cutting (every release)

| ID | Epic | Title | User Story Description | Priority (MoSCoW) | Estimate (Pts) |
| ---| ---| ---| ---| ---| --- |
| US33 | E7 | Accessible, mobile-first UI | As an older caregiver, I want large text, high contrast and large touch targets across the application, so that I can navigate easily despite visual or digital literacy barriers. | Must | 5 |
| US34 | E7 | Data minimisation | As a caregiver, I want the platform not to require mandatory medical documents, so that I only share what is necessary. | Must | 2 |
| US35 | E8 | Backend unit tests (at least 80% coverage) | As the team, we want automated unit tests with at least 80% backend coverage, so that we can change the system with confidence. | Must | 5 |
| US36 | E8 | Integration tests of the main flows | As the team, we want integration tests of the main flows (search, support request, provider update), so that we know the components work together. | Must | 3 |
| US37 | E8 | CI/CD, structured logs, health checks and metrics | As the team, we want CI/CD, structured logs, health checks and metrics, so that we can deploy and monitor the system reliably. | Must | 5 |
| US38 | E8 | Failure handling and one demonstrated failure | As the team, we want the system to handle failures gracefully and to demonstrate one failure scenario, so that we show how it degrades safely. | Must | 5 |
| US39 | E6 | Choose the external data source (to decide) | As the team, we want to decide which external data source to integrate, so that we can plan the integration. | Must | 2 |
| US40 | E6 | Integrate the external data source | As the platform, I want to integrate the chosen external data source, so that service information is enriched beyond provider updates. | Must | 5 |

> US39 is a short decision task (spike); the external data source has not been decided yet.

## Summary by epic

| Epic | Stories |
| ---| --- |
| E1 Access and care profile | US01, US02, US03, US04, US17, US26, US27 |
| E2 Find an alternative | US05, US18, US19 |
| E3 AI-assisted navigation | US06, US07, US08, US09 |
| E4 Check and confirm | US10, US11, US20, US28, US29 |
| E5 Support network | US12, US13, US14, US21, US22, US30, US31 |
| E6 Service information | US15, US16, US23, US24, US25, US32, US39, US40 |
| E7 Inclusive, privacy-first design | US33, US34 |
| E8 Quality and operations | US35, US36, US37, US38 |

# Acceptance Criteria
Acceptance criteria (Given / When / Then) for the Release 1 stories. Stories without criteria still have to be defined before they meet the DoR.

### US01 / US02: Log in and restrict features by role

```gherkin
Given a registered user logs in,
When the platform returns the authenticated session,
Then it must identify the user's role and the permissions associated with that role.

Given a caregiver is authenticated,
When they use caregiver features such as the navigator,
Then the platform must allow those features.

Given a provider is authenticated,
When they access caregiver-only features,
Then the platform must not expose those features and the backend must reject caregiver-only endpoints.

Given any user accesses the public service directory,
When they search for a service,
Then the search remains available without authentication.
```

The current role permission policy is:

| Role | Permissions |
| --- | --- |
| `CAREGIVER` | `SEARCH_SERVICES`, `ASK_NAVIGATOR`, `MANAGE_SUPPORT_REQUESTS` |
| `PROVIDER` | `MANAGE_SERVICE_STATUS`, `VIEW_SERVICE_DIRECTORY` |

The backend returns these permissions in the login and current-user responses. Access is configured on REST
resources and HTTP methods, not on role-specific URL namespaces. The public service directory exposes only
`GET /api/v1/services`; authenticated resource mutations must declare the permitted role when those endpoints
are implemented.

### US03 / US04: Care profile and emergency contacts

```gherkin
Given the informal caregiver is authenticated in the platform,
When they create or edit the care profile and emergency contacts,
Then the system must allow saving only essential, non-sensitive information (e.g., accessibility tag "requires wheelchair access", emergency contacts),
And MUST NOT require or request mandatory uploads of sensitive medical documents.
```

### US05: Search services by type and location

```gherkin
Given the caregiver is on the service search screen,
When they search by service type and location,
Then the system returns a list of relevant services sorted by proximity/relevance,
And each result displays the service name, location, data source, verification state, and last update timestamp.
```

### US06 / US07 / US08: Plain-language search and AI guardrails

```gherkin
Given the caregiver asks a plain-language question to the AI (e.g., "pharmacies open near Boavista"),
When the response is generated by the backend API,
Then the response must be built strictly from verified application service data,
And must include source context and a limitation notice,
And if the user asks for medical diagnoses, prescriptions, or emergency response, the AI must explicitly refuse and provide local human emergency contacts (e.g., 112 / SNS 24).
```

### US09: Fall back to standard search
_To be defined._

### US10 / US11: Service details and call button

```gherkin
Given the caregiver selects a service result from the search,
When they view the service detail page,
Then the screen displays the source and the last update of the information,
And displays a prominent "Call / Contact" button that directly triggers a telephone call on the device,
And displays a clear disclaimer: "Real-time availability is not guaranteed. Please confirm directly via phone call."
```

### US12 / US13 / US14: Send support request, notify contact and retries

```gherkin
Given the caregiver needs assistance with a care task,
When they create and send a request specifying WHAT, WHERE, and WHEN to a backup contact (e.g., family member/neighbor),
Then the recipient receives a notification containing minimal required information,
And the notification is retried if delivery fails, without creating duplicates.
```

### US15 / US16: Verify provider and update service status

```gherkin
Given the coordinator belongs to a verified service provider organisation and is logged into their institutional account,
When they update operating status, schedules, or notices for their service and click "Save",
Then the update must be completed and publicly reflected in less than 1 minute,
And the status must be marked with an "Official Status" badge.
```

# Release Plan
The release plan follows the story map. **Allocation of stories to the actual sprints is still to be agreed by the team** (it depends on estimates and on the assignment's sprint requirements). The team consists of 4 members (Manuel Sá - PM/DEV, Carlos Lopes - SM/DEV, Filipe Magalhães - BA/DEV, Guilherme Rodrigues - QA/DEV).

| Release | Goal | Stories |
| ---| ---| --- |
| Release 1: Walking skeleton | Thinnest end-to-end version: a caregiver logs in, finds a service (standard or AI search), checks it and calls, and sends a support request; a verified provider updates status. | US01–US16 + cross-cutting (US33–US40): 97 pts |
| Release 2 | Extend the skeleton: dashboard, filters, expiry warnings, request responses and status, provider validity, correction and audit. | US17–US25: 30 pts |
| Release n (later) | Later ideas: delegated setup, community reports, multi-recipient requests, closure notifications. | US26–US32: 29 pts |

The initial release focuses on an urban pilot deployment (e.g., Porto / Campanhã / Boavista) to validate core assumptions with informal caregivers and local service providers.

## Release Evaluation Criteria (Definition of Release)
1. Search & Action: A caregiver can reach the dashboard, search for a relevant local service, and identify a direct contact action in 3 minutes or less.
2. Provider Responsiveness: A verified service provider can publish an operational status update in under 60 seconds.
3. AI Safety & Grounding: The AI assistant strictly answers from application data, provides source context, and refuses all medical/emergency queries.
4. Data Minimization: No mandatory medical document uploads; strict role-based data boundaries enforced at the backend level.
5. Quality & Accessibility: Mobile-first responsive interface with high-contrast elements, compliant with older-user testing goals, and backend core logic covered by automated tests (≥80% coverage on new code).

# Resposible AI Opportunity Assessment

## 1\. Proposed AI-assisted workflow
The system implements a **Bounded AI Service Navigator** (US08, US09) designed strictly for information retrieval and summarization, isolated behind the backend application layer to prevent direct frontend-to-LLM communication.
*   **Trigger:** The caregiver inputs a natural language query via the Quick-Access Dashboard or Search interface (e.g., _"pharmacies near Boavista open now"_ or _"wheelchair accessible day-care in Campanhã"_).
*   **Security & Guardrails (Backend):** The request hits the AIService (Application Layer). Safety filters immediately scan the prompt. If medical, diagnostic, or emergency keywords are detected, the AI refuses to answer and triggers the emergency fallback.
*   **Data Minimization & Scrubbing:** The LLMProviderAdapter (Infrastructure Layer) queries the PostgreSQL database for relevant services. **Crucially, only Tier 3 (Non-Sensitive Public/Operational Data)** is extracted. All Tier 1 (Restricted) and Tier 2 (PII) data, including exact residential addresses or care profiles, are explicitly scrubbed.
*   **Grounded Generation (RAG):** The sanitized context and the user's query are sent to the Managed External LLM API. The LLM is instructed to summarize _only_ the provided verified application data.
*   **Response Delivery:** The frontend displays the AI's summarized recommendations alongside mandatory metadata: **Source Context**, **Limitation Notice**, and a prominent **Human Fallback (Call) Button**.
## 2\. Expected value
*   **Reduction of Cognitive Overload:** During a care disruption, caregivers (like the primary persona, Ana) must rapidly switch from "caregiver mode" to "admin mode." The AI translates fragmented directory data into plain-language, actionable answers, saving hours of "phone-tree" navigation.
*   **Accessibility & Inclusion:** For older caregivers with low digital literacy or visual impairments (like Armando), natural language querying bypasses the need to navigate complex, multi-step filter menus.
*   **Rapid Disruption Mitigation:** Enables the core success metric: reaching a relevant, verified local service alternative in **3 minutes or less**.
*   **Intelligent Triage:** Automates the summarization of local alerts (e.g., pharmacy stockouts, service closures), deduplicating redundant assistance requests within the community network.
## 3\. Risks
*   **Hallucination & Outdated Information (Trust Risk):** The LLM might invent service facts, provide incorrect operating hours, or fail to reflect real-time closures, leading caregivers to rely on false information during a crisis.
*   **Information Disclosure (Privacy/STRIDE Risk):** Caregivers might inadvertently type sensitive health conditions (PHI) or exact home addresses into the prompt. If scrubbing fails, this violates GDPR and exposes vulnerable individuals to the external LLM provider.
*   **Medical/Emergency Overstep (Safety Risk):** The AI might be prompted to provide medical advice, treatment recommendations, or emergency instructions, for which it is not qualified, potentially causing harm.
*   **Over-reliance on AI (Operational Risk):** Users may treat the AI's summarized availability as a "guarantee" rather than a summary, failing to verify directly with the institution.
*   **Vendor Lock-in & Latency:** Relying on an external Managed LLM API introduces latency risks during critical emergencies and creates a dependency on a third-party provider's uptime and pricing.
## 4\. Evaluation approach
To ensure the AI remains safe, grounded, and privacy-preserving, the following evaluation methods will be employed prior to and during the MVP pilot:
*   **Scenario-Based Safety & Guardrail Testing:** Run adversarial prompts containing medical emergencies, PII, and requests for prescriptions to verify that the AI explicitly refuses and redirects to human emergency contacts (112 / SNS 24).
*   **RAG Grounding & Hallucination Audits:** Compare AI outputs against the PostgreSQL "Source of Truth" database. The AI must achieve a **zero-tolerance rate for inventing service facts** (e.g., suggesting a pharmacy that does not exist in the verified directory).
*   **Data Payload Inspection:** Audit the API logs and outgoing payloads to the LLM provider to mathematically verify that zero Tier 1 and Tier 2 data is ever transmitted externally.
*   **Usability & Trust Testing:** Conduct moderated usability tests with older caregivers to ensure they understand the "Limitation Notice" and know how to use the "Call Institution" button to verify AI suggestions.
## 5\. Baseline or fallback strategy
The architecture strictly adheres to the principle that **Data is More Authoritative Than AI**. The AI is an interface layer, not the source of truth.
*   **Human Fallback (Mandatory):** Every single AI-generated service recommendation must include a prominent, 1-click **"Call \[Institution\]"** button and a visible disclaimer: _"Real-time availability is not guaranteed. Please confirm directly via phone call."_
*   **Emergency Redirection:** If the AI detects an emergency or medical crisis context, it immediately halts the retrieval workflow and displays local human emergency contacts (e.g., **112 / SNS 24**).
*   **Degraded Operation (Traditional Search):** If the Managed LLM API experiences latency, downtime, or rate-limiting, the system seamlessly degrades to the **Traditional Form-Based Search & Filters** (US03). The caregiver can still manually filter by location, type, and accessibility without AI assistance.
*   **Offline/Resilience Fallback:** In the event of network or power disruptions, the Quick-Access Dashboard provides offline access to the caregiver's static care profile, emergency contacts, and basic care routines, ensuring the user is never left without critical baseline information.

# Technical Design

## 1\. API Outline
All REST endpoints operate over HTTPS (`/api/v1`) using JSON payloads. JWT bearer tokens enforce Authentication & Role-Based Access Control.
### 1.1 Authentication & User Management
*   `POST /api/v1/auth/login`
    *   Payload: `{ "email": "`[`ana@example.com`](mailto:ana@example.com)`", "password": "..." }`
    *   Response (`200 OK`): `{ "token": "jwt...", "role": "CAREGIVER" }`

*   `POST /api/v1/caregivers`
    *   Payload: `{ "userId": "uuid", "phoneNumber": "+351910000000"`
    *   Response (`201 Created`): `{ "id": "caregiver-uuid", "userId": "user-uuid", "phoneNumber": "+351910000000" }`
### 1.2 Care Recipient Management (Non-Sensitive / No PHI)
*   `POST /api/v1/caregivers/{caregiverId}/recipients`
    *   Payload:
    ```css
    {
      "coarseAccessibilityTags": ["wheelchair_accessible", "reduced_mobility"]
    }
    ```[cite: 2]
    ```
    *   Response (`201 Created`): `{ "id": "recipient-uuid", "caregiverId": "caregiver-uuid", "coarseAccessibilityTags": [...] }`
### 1.3 Service Directory & Provider Status Updates
*   `GET /api/v1/services` _(Traditional Form-Based Search / Fallback Mode)_
    *   Query Params: `type=PHARMACY&coarseLocation=Boavista`
    *   Response (`200 OK`):
    ```scheme
    [
      {
        "id": "service-uuid",
        "organisationId": "org-uuid",
        "type": "PHARMACY",
        "coarseLocation": "Boavista",
        "operatingHours": "08:00 - 22:00",
        "currentStatus": "OPEN",
        "lastUpdated": "2026-10-12T15:30:00Z"
      }
    ]
    ```[cite: 2]
    ```
*   `PATCH /api/v1/services/{id}/status` _(Provider Role)_
    *   Payload: `{ "currentStatus": "TEMPORARILY_CLOSED", "operatingHours": "Reopening at 18:00" }`
    *   Response (`200 OK`): `{ "id": "service-uuid", "currentStatus": "TEMPORARILY_CLOSED", "lastUpdated": "2026-10-12T16:00:00Z" }`
### 1.4 Support Requests (Backup Coordination)
*   `POST /api/v1/support-requests`
    *   Payload
    ```css
    {
      "caregiverId": "caregiver-uuid",
      "recipientId": "recipient-uuid",
      "taskDescription": "Pick up prescription medication at local pharmacy",
      "location": "Campanhã, Porto",
      "timeWindow": "Today between 17:00 and 19:00"
    }
    ```[cite: 2]
    ```
    *   Response (`201 Created`): `{ "id": "request-uuid", "status": "PENDING" }`
*   `PATCH /api/v1/support-requests/{id}/status` _(Volunteer / Backup Role)_
    *   Payload: `{ "status": "ACCEPTED" }`
    *   Response (`200 OK`): `{ "id": "request-uuid", "status": "ACCEPTED" }`
### 1.5 Bounded AI Navigator & Degraded Search Fallback
*   `POST /api/v1/ai/navigate`
    *   Payload: `{ "query": "pharmacies open near Boavista right now" }`
    *   Response (`200 OK`):

*       ```go
    {
      "answer": "Found 2 open pharmacies in Boavista based on verified records.",
      "services": [
        {
          "id": "service-uuid",
          "type": "PHARMACY",
          "coarseLocation": "Boavista",
          "currentStatus": "OPEN",
          "operatingHours": "08:00 - 22:00",
          "lastUpdated": "2026-10-12T15:30:00Z"
        }
      ],
      "isFallback": false
    }
    ```[cite: 1, 2]
    ```
*       *   Response (`503 Service Unavailable / Degraded Operation Fallback`):
*       ```go
    {
      "error": "AI Navigator temporarily unavailable or timed out.",
      "fallbackUrl": "/api/v1/services?type=PHARMACY&coarseLocation=Boavista",
      "isFallback": true
    }
    ```[cite: 1]
    ```

## 2\. Asynchronous Workflow
### 2.1 Overview
The support request flow coordinates help between an informal caregiver and a backup contact (family member, neighbor, or volunteer). Because the recipient is not required to respond immediately, this workflow is inherently asynchronous:
1. Request Dispatch: The caregiver submits a request specifying what is needed, where, and when. The backend registers the request in a `PENDING` state and dispatches an asynchronous notification (SMS/Email/Push) to the recipient without blocking the caregiver's session.
2. Asynchronous Resolution: At a later time, the recipient opens the request link, evaluates the details, and clicks "Accept" or "Decline".
3. State Propagation: The system updates the request entity in PostgreSQL (`ACCEPTED` or `DECLINED`) and asynchronously triggers a notification back to the caregiver, whose Quick-Access Dashboard reflects the status change.

![](https://t90122138845.p.clickup-attachments.com/t90122138845/d7e62a93-466a-41ef-9695-b918c0f53837/image.png)

## 3\. Container/Component View and Data Model
![](https://t90122138845.p.clickup-attachments.com/t90122138845/edb2eb3a-986f-46bf-a302-80f69c63d525/image.png)
/
![](https://t90122138845.p.clickup-attachments.com/t90122138845/9ab9a629-dee9-42b7-b2a0-8a2fd81ee18a/image.png)
## 4\. External AI Service Integration (US08, US09)
### 4.1. Architecture and Workflow
The system integrates a managed external Large Language Model (LLM) through a backend-mediated `AIService` and `LLMProviderAdapter`. The frontend never communicates directly with the LLM provider.
When a caregiver submits a natural-language query, `AIService` validates the request and applies safety checks. Medical or emergency-related queries are redirected to approved human support channels. For valid service-discovery queries, the backend retrieves relevant records from PostgreSQL, allowing only explicitly approved Tier 3 public and operational data to be included in the external request.
The `LLMProviderAdapter` sends the sanitized context to the external LLM for grounded summarization. The backend validates the response against the retrieved records before returning recommendations with source information, limitation notices, and a direct institution contact action.
### 4.2. Security and Privacy
The integration follows data minimization and defense-in-depth principles. Tier 1 restricted data and Tier 2 personal data must never be transmitted to the external provider. Explicit field allowlists, payload validation, and sensitive-input screening enforce this boundary. The external LLM is treated as an untrusted dependency and cannot access the database directly.
### 4.3. Risks and Mitigations
The main risks are hallucinations, outdated service information, accidental disclosure of personal data, unsafe medical advice, and provider outages. These are mitigated through database-grounded responses, backend output validation, deterministic emergency routing, privacy controls, and bounded provider timeouts.
### 4.4. Fallback and Resilience
Every recommendation includes a limitation notice and a direct contact option for verification. Emergency queries are redirected to approved local contacts, including 112 and SNS 24, as appropriate. If the LLM is unavailable or its response fails validation, the system falls back to traditional search and filters (US03). Supported offline access to emergency contacts remains independent of the AI integration.
### 4.5. Evaluation
The integration is evaluated through safety and adversarial testing, automated inspection of outbound payloads, verification of recommendations against PostgreSQL, provider-failure testing, and usability testing with caregivers. Acceptance criteria include zero prohibited-data egress, no unsupported service recommendations, and achievement of the target of finding a relevant, verified service alternative within three minutes.
### 5.0 Domain Model

[domainmodel.svg](https://t90122138845.p.clickup-attachments.com/t90122138845/0de68449-5b2c-41d4-be47-39f7d93d90ff/domainmodel.svg)

[domainmodel.puml](https://t90122138845.p.clickup-attachments.com/t90122138845/6f0cbb89-df47-4e9f-982f-9bff96a0da4a/domainmodel.puml)

[domainmodel-simple.svg](https://t90122138845.p.clickup-attachments.com/t90122138845/8efc3bf0-7c95-45c8-97c7-2b10effcdd25/domainmodel-simple.svg)

[domainmodel-simple.puml](https://t90122138845.p.clickup-attachments.com/t90122138845/056c0ff2-1dd0-4168-990b-0d6fe351a1d7/domainmodel-simple.puml)

# Architectural Decision Records

# 8\. Architecture Decision Records
## 8.1 Architectural Context
The system will support informal caregivers by providing access to information and coordination services related to healthcare, social support, transportation, pharmacies, day-care centres and community resources.
The architecture must support:
*   A web-based user interface.
*   Secure user authentication and authorisation.
*   Management of caregivers and relevant care information.
*   Management of support organisations and services.
*   Search and filtering of available resources.
*   Communication between users and services.
*   Integration with external services where required.
*   An AI-powered feature.
*   Protection of potentially sensitive personal information.
*   Future expansion to additional municipalities and services.
*   At least two independently deployable backend components.
The development team has established preferences for:
*   **Backend:** Java, Spring Boot and Maven.
*   **Frontend:** React and Tailwind CSS.
*   **Database:** PostgreSQL.
*   **AI:** A managed external Large Language Model (LLM) API, given the team's limited previous experience with AI technologies.
The architecture should balance maintainability, security, implementation effort and the assessment requirement for independently deployable backend components.
The selected approach is to implement two backend components:
1. **Care Platform API:** Responsible for core care-related workflows, users, organisations, support services, availability and support requests.
2. **Support Assistant Service:** Responsible for AI-assisted service navigation, natural-language questions and integration with the external LLM provider.
Both components will use Java, Spring Boot and Maven, and each will follow a Traditional Layered Architecture internally. They will be built and deployed independently and communicate through explicitly defined APIs.
This separation allows the core platform to remain operational when AI assistance is unavailable and allows the AI functionality to evolve independently of the core care-management workflows.
* * *

# ADR-001 — Backend Technology
## Decision
Use **Java with Spring Boot and Maven** for both backend components.
### Alternatives Considered
*   Node.js / TypeScript.
*   Python / FastAPI.
*   .NET / [ASP.NET](http://ASP.NET) Core.
*   Java / Spring Boot.
### Decision Rationale
Spring Boot provides a mature ecosystem for building REST APIs, authentication, database access, validation, testing and integration with external services.
Using the same technology stack for both backend components reduces the learning curve, allows code and development practices to remain consistent, and simplifies recruitment, maintenance and deployment.
Maven will be used for dependency management, project configuration and build automation. Each component will have its own build and deployable artifact.
The backend will expose REST APIs consumed by the React frontend. The Support Assistant Service will also expose an API for AI-assisted functionality and use an external LLM API through a dedicated integration client.
### Consequences and Trade-offs
**Advantages:**
*   Mature ecosystem and long-term support.
*   Familiar technology for the development team.
*   Strong support for REST APIs and enterprise integrations.
*   Testing and dependency-management capabilities.
*   Security support through Spring Security.
*   Straightforward integration with PostgreSQL and external services.
*   Consistent technology stack across backend components.
**Trade-offs:**
*   Higher memory footprint than some lightweight alternatives.
*   Additional configuration and architectural concepts.
*   Some boilerplate compared with certain newer frameworks.
*   Two separately deployed applications require additional build, configuration and operational management.
### Decision Status
**Approved.**
The benefits of team familiarity, ecosystem maturity and maintainability outweigh the additional complexity.
* * *

# ADR-002 — Frontend Technology
## Decision
Use **React with Tailwind CSS** for the frontend application.
### Alternatives Considered
*   Angular.
*   Vue.js.
*   React with traditional CSS.
*   React with a component library.
### Decision Rationale
React provides a mature ecosystem for developing interactive web applications and is well suited to applications with multiple user flows and dynamic information.
Tailwind CSS will be used to establish a consistent design system while allowing the team to develop responsive interfaces efficiently.
The frontend will communicate with the backend components through their published REST APIs. The frontend must not access the database or call the LLM provider directly.
Accessibility and responsive design are particularly important because the target users may include older caregivers and people with different levels of digital literacy.
### Consequences and Trade-offs
**Advantages:**
*   Large ecosystem and community.
*   Component-based architecture.
*   Reusable UI components.
*   Consistent styling with Tailwind CSS.
*   Suitable for responsive applications.
*   Straightforward integration with REST APIs.
**Trade-offs:**
*   Requires decisions around state management and component architecture.
*   React does not provide a complete application architecture by itself.
*   Tailwind can result in complex markup if components are not structured properly.
*   The frontend must handle errors from two backend components and communicate their availability clearly to users.
### Decision Status
**Approved.**
The frontend will use reusable components, consistent UI patterns and a centralised approach to API communication and error handling.
* * *

# ADR-003 — Backend Architecture: Traditional Layered Architecture
## Decision
Adopt a **Traditional Layered Architecture within each independently deployable backend component**.
The overall system will consist of two backend deployment units, while each component will organise its internal code into layers with clearly defined responsibilities.
This separates two architectural concerns:
*   **Deployment architecture:** Determines which backend components can be built, released and deployed independently.
*   **Internal architecture:** Determines how code and responsibilities are organised within each component.
### Internal Layers
Each backend component will use the following layers.
#### 1\. Presentation Layer
Responsibilities:
*   REST controllers.
*   Request and response DTOs.
*   Input validation.
*   Authentication and authorisation enforcement at API boundaries.
*   HTTP status codes and error responses.
*   API documentation.
Controllers should remain thin and delegate business operations to the service layer.
#### 2\. Business / Service Layer
Responsibilities:
*   Application use cases.
*   Business workflows.
*   Coordination of domain operations.
*   Transaction boundaries.
*   Application-level validation and rules.
*   Interaction with repositories and external clients.
The service layer should contain the application's main workflows and should not expose database or provider-specific implementation details directly to controllers.
#### 3\. Data Access Layer
Responsibilities:
*   Repository interfaces and implementations.
*   Database queries.
*   Spring Data JPA.
*   Persistence operations.
*   Persistence mapping where appropriate.
Each component must access only the data it owns, except through explicitly defined interfaces or APIs where cross-component access is required.
#### 4\. Infrastructure and Integration Layer
Responsibilities:
*   PostgreSQL connectivity.
*   External API clients.
*   LLM provider integration.
*   Email and notification integrations.
*   Logging and technical configuration.
*   Other infrastructure-specific concerns.
Infrastructure code should be kept separate from business workflows.
### Component 1: Care Platform API
The Care Platform API will contain the core care-related workflows.
Its internal layers will include:
*   **Presentation:** Caregiver, care recipient, organisation, support service, availability and support request controllers.
*   **Business / Service:** Caregiver management, service discovery, availability management and support request workflows.
*   **Data Access:** Repositories for core care and service data.
*   **Infrastructure / Integration:** PostgreSQL access, security configuration and any required external service integrations.
### Component 2: Support Assistant Service
The Support Assistant Service will contain the AI-assisted functionality.
Its internal layers will include:
*   **Presentation:** REST endpoints for assistant conversations and service-navigation questions.
*   **Business / Service:** Question processing, retrieval orchestration, prompt construction, response handling and AI-specific rules.
*   **Data Access:** Optional persistence for conversation metadata, subject to privacy and retention requirements.
*   **Infrastructure / Integration:** LLM API client, retrieval adapters, timeouts, retry policies and provider-specific configuration.
The Support Assistant Service will obtain authoritative care-service information from the Care Platform API or another explicitly approved, controlled data-access mechanism. It will not directly modify the core platform's database.
### Example Internal Package Structure
The following structure is illustrative and can be adapted to the project.

```verilog
care-platform-api/
├── controller/
├── service/
├── repository/
├── entity/
├── dto/
├── integration/
└── config/

support-assistant-service/
├── controller/
├── service/
├── repository/
├── dto/
├── integration/
└── config/
```

Each component will be a separate Spring Boot application with its own build configuration and deployable artifact. A shared repository may be used for the project, but the build and deployment boundaries must remain explicit.
### Alternatives Considered
**1\. Onion Architecture**
Provides stronger separation between business/domain logic and technical infrastructure. It was considered because of its testability and ability to isolate external dependencies.
**2\. Clean Architecture**
Provides strict dependency rules and strong separation of concerns but introduces additional abstractions that may not be necessary for this project's initial scope.
**3\. Hexagonal Architecture**
Provides explicit ports and adapters around the application core. It would be suitable if the system had many integrations or frequently replaced external technologies.
**4\. Microservices with different internal architectures**
The system could use different internal architectural patterns for each service. This was not selected because a consistent, familiar structure is more practical for the development team.
**5\. Traditional Layered Architecture**
Selected because it is straightforward, well understood and supported by Spring Boot. It can be applied consistently to both independently deployable components.
### Decision Rationale
The project needs independently deployable backend components but does not need a complex internal architecture within each component.
Traditional Layered Architecture provides:
*   A low learning curve for the development team.
*   Clear separation of API, business logic, persistence and integration concerns.
*   Straightforward development, debugging and testing.
*   Strong Spring Boot and Spring Data JPA support.
*   A consistent structure across both components.
*   Clear locations for AI integration and provider-specific code.
*   A practical foundation for independently building and deploying each component.
The architecture must also enforce clear component ownership. Layering alone does not guarantee separation between services, so the team must maintain independent data ownership, explicit API contracts and separate deployment pipelines.
### Consequences and Trade-offs
**Advantages:**
*   Simple and familiar architecture.
*   Clear separation of responsibilities.
*   Straightforward testing and debugging.
*   Good integration with Java and Spring Boot.
*   Fast initial development.
*   Suitable for independently deployable backend applications.
*   Lower internal architectural overhead than Onion or Clean Architecture.
**Disadvantages:**
*   Service classes can become large if responsibilities are not controlled.
*   Business logic may become coupled to persistence models if good practices are not followed.
*   Replacing infrastructure can require more changes than in stricter dependency architectures.
*   Two independently deployed components introduce network communication, deployment configuration and operational overhead.
*   Cross-component transactions are more complicated than transactions within a single application.
### Architectural Guidelines
The team should avoid putting business rules directly into controllers or repositories.
The preferred internal flow is:

```markdown
Controller
    ↓
Service
    ↓
Repository
    ↓
Component-owned database
```

For external integrations:

```markdown
Controller
    ↓
Service
    ↓
Integration Client
    ↓
External API
```

Cross-component communication must use published API contracts or explicitly defined events. Components must not bypass these boundaries by directly accessing each other's internal classes or database tables.
### Decision Status
**Approved.**
Traditional Layered Architecture will be used inside each backend component, while independent deployment and data ownership will be enforced at the system level.
* * *

# ADR-004 — Backend Component Boundaries and Deployment
## Decision
Implement two independently deployable backend components:
1. **Care Platform API**
2. **Support Assistant Service**
Each component will have its own Spring Boot application, build artifact, configuration and deployment process.
### Component 1: Care Platform API
**Primary responsibility:** Provide the authoritative core platform for managing caregivers, care recipients, organisations, support services, availability and support requests.
Responsibilities include:
*   Core user and care-related workflows.
*   Management of organisations and service listings.
*   Service availability and disruption information.
*   Search and filtering of support services.
*   Creation and management of support requests.
*   Authorisation of access to core platform data.
*   Publishing authoritative information through documented APIs.
*   Maintaining the consistency and integrity of core records.
**Data ownership:**
The Care Platform API owns the authoritative records for caregivers, care recipients, organisations, support services, service availability and support requests.
PostgreSQL will be used as its primary system of record.
Other components must not directly modify these records in the database. They must use the Care Platform API or another explicitly approved interface.
### Component 2: Support Assistant Service
**Primary responsibility:** Provide AI-assisted information and navigation to help caregivers find relevant support services.
Responsibilities include:
*   Accepting natural-language questions.
*   Interpreting user intent.
*   Retrieving relevant, verified service information.
*   Constructing prompts and managing LLM requests.
*   Returning understandable responses grounded in retrieved information.
*   Applying AI-specific validation and safety controls.
*   Handling AI-provider timeouts, errors and usage limits.
*   Recording minimal operational metadata when justified.
**Data ownership:**
The Support Assistant Service owns its AI-specific processing and any operational data it needs to retain, such as minimal request metadata or conversation state where justified.
It does not own the authoritative records for caregivers, support services, service availability or support requests. Those remain the responsibility of the Care Platform API.
The assistant must not present cached or generated information as current if it cannot verify that information against an authoritative source.
### Why These Boundaries Were Selected
The two components have different primary responsibilities and external dependencies.
The Care Platform API is the core transactional system. Its primary concerns are data consistency, access control and reliable execution of care-related workflows.
The Support Assistant Service depends on an external AI provider and may have different latency, cost, scaling and failure characteristics. Isolating it allows AI functionality to evolve without requiring every AI-related change to be released as part of the core platform.
This division also allows the core platform to continue providing non-AI functionality when the assistant is unavailable.
### Coupling and Cohesion
The design aims for:
*   **High cohesion:** Each component groups closely related responsibilities.
*   **Loose coupling:** Components communicate through explicit APIs rather than internal implementation details.
*   **Clear data ownership:** Each component is responsible for maintaining its own authoritative data.
*   **Controlled dependencies:** The AI component depends on published core-platform interfaces, not direct database access.
*   **Independent evolution:** Changes to AI provider integration should not require changes to unrelated care-management workflows.
The components should not share a database schema in a way that allows one component to modify another component's data. Any shared libraries should be small, stable and limited to genuinely common technical concerns or API contracts.
### Deployment Implications
Each component will:
*   Be built as a separate Spring Boot artifact.
*   Have its own configuration and secrets.
*   Be deployable independently.
*   Have separate health checks and operational logs.
*   Support its own release process.
*   Be versioned so that API compatibility can be maintained.
The components may initially run on the same hosting platform, but they must remain separate deployment units.
The AI service can be scaled independently if demand or latency requires it. The core platform can be deployed without requiring a new AI-service release, provided the API contracts remain compatible.
### Consequences and Trade-offs
**Advantages:**
*   Meets the requirement for at least two independently deployable backend components.
*   Separates core care workflows from AI-specific processing.
*   Allows independent releases and scaling.
*   Limits the impact of some AI-specific failures.
*   Makes component responsibilities and data ownership explicit.
**Trade-offs:**
*   More deployment and monitoring effort than a single backend.
*   Network communication introduces latency and failure scenarios.
*   API contracts require versioning and compatibility management.
*   Authentication and authorisation must be enforced across service boundaries.
*   Distributed transactions should be avoided where possible.
*   Integration and end-to-end testing become more important.
### Decision Status
**Approved.**
The Care Platform API and Support Assistant Service will be treated as separate deployment units with explicit responsibilities and data ownership.
* * *

# ADR-005 — API Communication and Asynchronous Processing
## Decision
Use **RESTful HTTP APIs for synchronous communication** between the frontend and backend components.
Introduce asynchronous messaging only where a demonstrated requirement justifies it, such as background notifications or processing of service-change events.
### Synchronous Communication
REST will be used when the caller needs an immediate response.
Examples include:
*   The React frontend requesting available support services from the Care Platform API.
*   A caregiver submitting a support request.
*   The frontend sending a question to the Support Assistant Service.
*   The Support Assistant Service retrieving verified service information from the Care Platform API.
*   The frontend retrieving the result of an AI request.
Example flow:

```gherkin
React Frontend
      |
      | REST: ask a question
      v
Support Assistant Service
      |
      | REST: retrieve verified services
      v
Care Platform API
      |
      | response with authoritative data
      v
Support Assistant Service
      |
      | request to external LLM
      v
LLM Provider
      |
      | generated response
      v
React Frontend
```

The exact sequence may vary according to the use case. The key requirement is that the assistant uses verified platform data when answering questions about local service availability.
### Asynchronous Communication
Asynchronous messaging may be introduced for operations that do not need to complete before the user receives an initial response.
Potential examples include:
*   Sending notifications after a support request is created.
*   Processing service availability updates.
*   Sending background reminders.
*   Processing operational events or analytics where appropriate.
If asynchronous messaging is implemented, the team should select and configure a message broker and define event contracts, retry policies, idempotency and failed-message handling.
A message broker is **not assumed to be part of the initial implementation** unless the project requirements or implementation plan justify it.
### Alternatives Considered
*   REST APIs only.
*   GraphQL.
*   gRPC.
*   WebSockets.
*   Message broker for all inter-component communication.
*   A combination of REST and asynchronous messaging.
### Decision Rationale
REST is familiar to the development team and works well for the system's primary user-driven operations. It also allows the components to evolve independently as long as their API contracts remain compatible.
Asynchronous messaging can improve resilience for background tasks, but it introduces operational complexity and eventual consistency. It should therefore be used selectively rather than introduced without a clear need.
### Consequences and Trade-offs
**Advantages:**
*   REST is straightforward to develop, test and debug.
*   API contracts make component responsibilities explicit.
*   Synchronous requests are appropriate for interactive user workflows.
*   Asynchronous processing can decouple notifications and background tasks.
*   The architecture can introduce messaging incrementally.
**Trade-offs:**
*   REST calls can increase latency and create runtime dependencies between components.
*   A synchronous dependency may fail if the receiving component is unavailable.
*   Asynchronous communication requires monitoring, retry policies and duplicate-message protection.
*   Event-driven workflows may be eventually consistent rather than immediately consistent.
### Decision Status
**Approved.**
REST will be the default communication mechanism. Asynchronous messaging will be introduced for clearly identified background-processing requirements.
* * *

# ADR-006 — Database
## Decision
Use **PostgreSQL as the primary relational database for the Care Platform API**.
The Support Assistant Service will access core care and service information through the Care Platform API. It will not directly read or modify the core platform's database.
If the assistant requires persistent AI-specific state, its storage requirements will be assessed separately. A separate database is not required unless the use case justifies it.
### Alternatives Considered
*   PostgreSQL.
*   MySQL.
*   MongoDB.
*   Firebase / Firestore.
*   A separate database for each backend component from the beginning.
### Decision Rationale
The core platform contains strongly related entities, including:

```gherkin
Caregiver
    |
    v
Care Recipient
    |
    v
Care Requirements
    |
    v
Support Requests
    |
    v
Services / Organisations
```

A relational database is appropriate for maintaining referential integrity and transactional consistency across these relationships.
PostgreSQL provides mature support for transactions, indexing and structured data, together with strong integration with Spring Boot and Spring Data JPA.
The database ownership boundary is more important than the number of databases: the Care Platform API is the only component authorised to modify authoritative core records.
### Consequences and Trade-offs
**Advantages:**
*   Strong consistency and transactional support.
*   Referential integrity.
*   Mature open-source ecosystem.
*   Good Spring integration.
*   Suitable for structured domain data.
*   Clear ownership of core records.
**Trade-offs:**
*   Schema changes require migration management.
*   Database availability becomes important to the core platform.
*   Direct access from other components must be prevented.
*   Separate storage for future AI-specific state could introduce additional operational work.
### Decision Status
**Approved.**
PostgreSQL will be the system of record for core care and service data. Other components will use explicit interfaces to access that data.
* * *

# ADR-007 — Authentication and Authorisation
## Decision
Use **Spring Security with standard token-based authentication**, supported by role-based access control and explicit authorisation checks.
Potential roles include:
*   Caregiver.
*   Service Provider.
*   Administrator.
*   Volunteer / Community Member.
The final roles and permissions must be based on the implemented use cases.
### Alternatives Considered
*   Custom authentication system.
*   Session-based authentication.
*   OAuth 2.0 / OpenID Connect.
*   External Identity Provider.
### Decision Rationale
The application may eventually need to integrate with external identity providers or other public-sector systems. Established authentication standards reduce the risks associated with implementing custom security mechanisms.
Because there are two backend components, authentication and authorisation must be considered across service boundaries.
The architecture must define how the Support Assistant Service validates the identity and permissions of the caller and how it obtains only the data that caller is authorised to access.
The team should select a concrete token-validation and service-to-service authentication approach during implementation rather than assuming that frontend authentication automatically secures internal APIs.
### Consequences and Trade-offs
**Advantages:**
*   Established security foundation.
*   Clear access boundaries.
*   Support for future identity integrations.
*   Reduced risks compared with custom authentication logic.
*   Consistent application of least-privilege principles.
**Trade-offs:**
*   Authentication and token validation add implementation complexity.
*   Each exposed API requires appropriate access controls.
*   Service-to-service authentication and secrets management require additional configuration.
*   Sensitive care-related data requires careful permission design and testing.
### Decision Status
**Approved.**
Authentication and authorisation will be treated as cross-component architectural requirements rather than features added only at the frontend.
* * *

# ADR-008 — AI Integration
## Decision
Use a **managed external Large Language Model (LLM) API** for the initial AI functionality.
The AI integration will be implemented within the Support Assistant Service rather than directly in the Care Platform API or React frontend.
### Proposed Flow

```gherkin
React Frontend
      |
      | REST
      v
Support Assistant Service
      |
      +----> Care Platform API
      |      Retrieve verified service information
      |
      +----> Managed LLM Provider
             Generate a grounded response
```

The Support Assistant Service will be responsible for orchestrating AI requests, retrieving relevant information, constructing prompts, validating responses and handling provider errors.
The frontend must never call the LLM provider directly. Provider credentials must remain on the backend.

Implementation note (US08): the selected provider is the Gemini Developer API (Google AI Studio),
called through the `generateContent` REST endpoint with the API key sent in the `x-goog-api-key`
header. Input guardrails run before the call and output validation runs before any generated text
is returned; if either fails, the deterministic grounded answer is used instead.
### Alternatives Considered
#### Alternative A — Develop and train an AI model
Rejected for the initial project.
The development team does not have sufficient AI/ML expertise, and training a model would introduce significant cost, complexity and infrastructure requirements.
#### Alternative B — Host an open-source LLM
Potentially useful in the future, but not recommended initially.
It would require additional knowledge and operational effort around model selection, infrastructure, GPU resources, deployment, optimisation, security, monitoring and updates.
#### Alternative C — Managed LLM API
Selected.
A managed API allows the team to introduce AI functionality without managing model infrastructure.
### Decision Rationale
The team has limited AI experience. The architecture should therefore minimise AI-specific infrastructure while keeping the provider integration isolated and replaceable.
Separating AI functionality into the Support Assistant Service also prevents AI-specific latency, provider outages and changes from being unnecessarily coupled to the core care-management application.
### Consequences and Trade-offs
**Advantages:**
*   Lower initial development complexity.
*   No need to train or host a model.
*   Faster implementation.
*   Appropriate for a team with limited AI experience.
*   AI provider integration can evolve independently.
*   Core platform functions can remain available when AI is unavailable.
**Trade-offs:**
*   Dependency on an external provider.
*   API usage costs.
*   Potential latency and rate limits.
*   Vendor lock-in risk.
*   External processing of prompts and data.
*   AI responses may be inaccurate or misleading.
*   Additional monitoring and failure-handling requirements.
### Security and Privacy Requirements
Sensitive personal information should not be sent to the AI provider unless there is a clearly justified, legally compliant and technically protected use case.
The service should apply data minimisation and anonymisation where possible, and should avoid sending unnecessary names, contact details, health details or identifying information.
The AI feature must not be treated as an authoritative medical or emergency decision-maker.
### Decision Status
**Approved.**
The Support Assistant Service will use a managed LLM API, with all provider-specific communication isolated within that component.
* * *

# ADR-009 — AI Use Case and Verified Information
## Decision
The initial AI feature will focus on **information assistance and service navigation**, rather than medical diagnosis, autonomous care decisions or treatment recommendations.
### Potential Use Cases
*   Helping caregivers describe a support need.
*   Finding relevant local support services.
*   Summarising verified service information.
*   Answering questions about how to navigate available support.
*   Translating natural-language questions into structured service searches.
*   Explaining available options based on verified platform information.
Example question:
> “My father normally goes to physiotherapy on Tuesday, but our transport is unavailable. What alternatives are available?”
The assistant should identify the need, retrieve relevant and current information through the Care Platform API, and present suitable options based on the data available.
It must not invent availability or claim that a service has been booked or confirmed unless the relevant system has confirmed that action.
### Alternatives Considered
*   Medical diagnosis or treatment recommendations.
*   Autonomous care decisions.
*   A general-purpose chatbot without access to verified local data.
*   AI-assisted service navigation grounded in verified platform information.
### Decision Rationale
Service navigation is directly aligned with the project's purpose: helping informal caregivers identify support when their usual arrangements are disrupted.
It is also a more appropriate initial scope for a team with limited AI experience than medical decision support or autonomous care planning.
The AI should interpret and assist; the Care Platform API remains the authoritative source for the platform's service information.
### Consequences and Trade-offs
**Advantages:**
*   Strong alignment with the project's problem statement.
*   More controlled scope than medical AI.
*   Natural-language interaction can make service discovery easier.
*   Verified data can be used to ground responses.
*   The core platform remains responsible for authoritative records.
**Trade-offs:**
*   Retrieval quality depends on the quality and completeness of service information.
*   AI-generated summaries can still be inaccurate.
*   The assistant must clearly communicate uncertainty and missing information.
*   Some questions will require human assistance or direct contact with a service provider.
### Decision Status
**Approved.**
AI will assist with service navigation and information discovery, while verified platform records and authorised service providers remain the sources of truth.
* * *

# ADR-010 — Security, Privacy and Data Protection
## Decision
Security and privacy will be treated as **cross-cutting architectural requirements across both backend components**.
The system may process sensitive information concerning:
*   Health conditions.
*   Disability.
*   Care requirements.
*   Family relationships.
*   Contact information.
*   Support requests.
### Security Requirements
The architecture should apply:
*   Data minimisation.
*   Role-based access control.
*   Authentication at protected API boundaries.
*   Encryption in transit.
*   Encryption at rest where appropriate.
*   Secure secret and credential management.
*   Audit logging for relevant operations.
*   Consent management where required.
*   Data retention and deletion policies.
*   GDPR compliance.
*   Appropriate controls for external data processing.
*   Separation of personal data from unnecessary application data.
### Cross-Component Requirements
The Support Assistant Service must not assume that a user is authorised to access information simply because the request originated from the frontend.
Access to caregiver-specific or otherwise restricted information must be authenticated and authorised. Service-to-service communication must use an appropriate trust mechanism, and the AI component should receive only the minimum information required for its task.
Logs must not unnecessarily expose sensitive prompts, personal data, access tokens or other confidential information.
### Decision Rationale
Inappropriate access to care-related information can have significant consequences for users and their families. Separating the backend into two deployment units increases the importance of explicit access boundaries and secure communication.
Security must therefore be considered during design and implementation rather than added after the system is built.
### Consequences and Trade-offs
**Advantages:**
*   Increased user trust.
*   Reduced security exposure.
*   Clearer data governance.
*   Better alignment with privacy obligations.
*   More explicit access boundaries between components.
**Trade-offs:**
*   Additional development and testing effort.
*   More complex identity and permission management.
*   Additional operational requirements for secrets, logs and monitoring.
*   External AI processing requires specific privacy and compliance assessment.
### Decision Status
**Approved.**
Privacy and security controls must apply consistently across the frontend, both backend components, the database and external AI integrations.
* * *

# ADR-011 — Failure Handling and Resilience
## Decision
Each backend component must handle its own failures and expose health information appropriate to its role. The failure of the Support Assistant Service or its LLM provider must not prevent the Care Platform API from performing unrelated core operations.
### Failure Scenarios and Responses
#### 1\. Support Assistant Service Unavailable
If the assistant is unavailable, the frontend should show that AI assistance is temporarily unavailable and allow the user to continue using the core platform.
Searching for support services, viewing service information and managing core workflows should remain possible through the Care Platform API, subject to its own availability.
#### 2\. LLM Provider Unavailable
The Support Assistant Service should use bounded timeouts and limited retries where appropriate.
If the provider remains unavailable, the assistant should return a controlled error or a clear fallback message. It must not fabricate an answer or claim that a request was completed.
#### 3\. Care Platform API Unavailable
If the assistant cannot retrieve current service information, it should explain that it cannot verify the relevant options.
The assistant must not present generated or cached information as confirmed current availability.
#### 4\. Network Timeout or Slow Response
The frontend and backend components should use appropriate request timeouts and return clear error states.
Retries should be limited and should not repeat non-idempotent operations in ways that create duplicate support requests or other unintended effects.
#### 5\. Database Failure
The Care Platform API should report a controlled service error, log appropriate diagnostic information and avoid returning misleading success responses when a transaction fails.
Database recovery, backups and restoration procedures must be defined as part of operational planning.
#### 6\. Asynchronous Processing Failure
If a message broker is introduced, message consumers should be idempotent, retry policies should be bounded, and repeatedly failing messages should be routed to an appropriate failed-message handling mechanism.
These mechanisms are conditional on implementing asynchronous messaging.
### Consequences and Trade-offs
**Advantages:**
*   AI failures have a more limited impact on core functionality.
*   Users receive clearer failure messages.
*   Prevents unverified service information from being presented as authoritative.
*   Encourages explicit timeout and retry behaviour.
*   Supports more reliable independent deployments.
**Trade-offs:**
*   Requires additional error handling and monitoring.
*   Some workflows cannot complete if a required dependency is unavailable.
*   Independent components introduce network-related failure modes.
*   Operational recovery procedures require additional effort.
### Decision Status
**Approved.**
Both components must have explicit failure-handling behaviour, with the core platform remaining independent of the availability of AI assistance wherever possible.
* * *

# ADR-012 — Architecture Principles
The following principles should guide future architectural decisions.
### 1\. Clear Component Ownership
Each independently deployable component must have a defined purpose, responsibilities and data ownership.
### 2\. Traditional Layered Architecture
Each component will separate presentation, business logic, data access and infrastructure responsibilities.
### 3\. High Cohesion, Low Coupling
Responsibilities should be grouped by purpose. Cross-component dependencies must use explicit contracts rather than internal implementation details.
### 4\. Keep It Simple
Avoid introducing infrastructure or technologies that do not solve a demonstrated problem.
### 5\. Independent Deployment
Each component must be buildable, configurable and deployable without requiring the other component to be rebuilt or redeployed, provided API compatibility is maintained.
### 6\. Explicit Data Ownership
The Care Platform API owns authoritative care and service records. The Support Assistant Service must use approved interfaces to retrieve this information.
### 7\. Prefer Managed Services Where Expertise Is Limited
This is particularly relevant to the initial AI implementation.
### 8\. Verified Data Is More Authoritative Than AI
AI should interpret and assist with information, not become the source of truth for service availability or care records.
### 9\. Privacy by Design
Collect and process only the information required for the service.
### 10\. Human Support Remains Possible
The architecture should not assume that every caregiver can or wants to use a digital interface.
### 11\. Resilience at Component Boundaries
Failures of external providers and dependent services should be handled explicitly. Optional AI functionality should not unnecessarily block core platform workflows.
### 12\. Avoid Unnecessary Distributed Complexity
Two deployment units are required, but additional microservices, message brokers and databases should only be introduced when justified by the actual requirements.
* * *

# 8.2 Proposed High-Level Architecture
## 8.2.1 System Overview
The system will consist of a React frontend and two independently deployable Spring Boot backend components.
The **Care Platform API** will manage core care-related data and workflows. The **Support Assistant Service** will provide AI-assisted navigation and retrieve verified information through the Care Platform API.
Each backend component will use Traditional Layered Architecture internally.

```gherkin
                         ┌─────────────────────────┐
                         │     React Frontend      │
                         │      + Tailwind CSS     │
                         └────────────┬────────────┘
                                      │
                             HTTPS / REST APIs
                                      │
                    ┌─────────────────┴─────────────────┐
                    │                                   │
                    ▼                                   ▼
       ┌─────────────────────────┐        ┌─────────────────────────┐
       │   CARE PLATFORM API     │        │ SUPPORT ASSISTANT       │
       │                         │        │ SERVICE                 │
       │ Presentation Layer      │        │ Presentation Layer      │
       │ Business / Service      │        │ Business / Service      │
       │ Data Access Layer       │        │ Data Access (if needed) │
       │ Infrastructure Layer    │        │ Infrastructure Layer    │
       └────────────┬────────────┘        └────────────┬────────────┘
                    │                                  │
                    ▼                                  ├──────────────┐
       ┌─────────────────────────┐                     │              │
       │ PostgreSQL              │                     ▼              ▼
       │ Core care and service   │              ┌────────────┐ ┌──────────────┐
       │ records                 │              │ Care       │ │ Managed LLM  │
       └─────────────────────────┘              │ Platform   │ │ Provider     │
                                                │ API        │ └──────────────┘
                                                └────────────┘
```

The diagram shows logical dependencies, not a requirement to deploy each layer separately. The four layers inside each backend component belong to that component's own deployment unit.
The Support Assistant Service calls the Care Platform API when it needs verified service information. It does not directly access the core PostgreSQL database.
## 8.2.2 Component Responsibilities

| Concern | Care Platform API | Support Assistant Service |
| ---| ---| --- |
| Primary purpose | Core care and service workflows | AI-assisted service navigation |
| Main users | Frontend and authorised backend clients | Frontend and authorised backend clients |
| Core responsibilities | Caregivers, care recipients, organisations, services, availability and support requests | Natural-language interpretation, information retrieval orchestration, prompt construction and AI response handling |
| Data ownership | Authoritative care and service records | AI-specific operational state, if persistence is required |
| External dependencies | PostgreSQL and any required core-platform integrations | LLM provider and Care Platform API |
| Deployment | Independent Spring Boot application | Independent Spring Boot application |
| Failure impact | May affect core care workflows | Should primarily affect AI-assisted functionality |

## 8.2.3 Internal Architecture of Each Component
Each backend component follows this internal structure:

```gherkin
┌─────────────────────────────────────────┐
│          PRESENTATION LAYER             │
│ REST Controllers, DTOs, Validation      │
└────────────────────┬────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────┐
│        BUSINESS / SERVICE LAYER         │
│ Use Cases, Workflows, Business Rules    │
└────────────────────┬────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────┐
│           DATA ACCESS LAYER             │
│ Repositories and Persistence Operations │
└────────────────────┬────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────┐
│       DATABASE / INFRASTRUCTURE         │
│ PostgreSQL or Approved External Clients │
└─────────────────────────────────────────┘
```

The diagram represents the typical internal flow. Calls to external services, such as an LLM provider, are made through dedicated integration clients from the appropriate service workflows.
The Care Platform API and Support Assistant Service each maintain their own internal layers. Their interaction occurs through explicit external contracts, not by calling one another's internal service or repository classes.
## 8.2.4 Communication Patterns
The initial architecture will use synchronous REST communication for interactive operations.
Examples:
*   The frontend requests service listings from the Care Platform API.
*   The frontend submits support requests to the Care Platform API.
*   The frontend sends an AI question to the Support Assistant Service.
*   The Support Assistant Service requests relevant verified information from the Care Platform API.
*   The Support Assistant Service sends a request to the managed LLM provider.
Asynchronous messaging may be introduced later for background notifications, service-change events or other workflows that do not need to complete synchronously.
The initial design does not require a message broker unless implementation requirements justify one.
## 8.2.5 Data Ownership and Consistency
The Care Platform API owns the authoritative records for core care and service information.
The Support Assistant Service must retrieve that information through approved interfaces and must not directly modify the core database.
This approach provides a clear boundary for validation, authorisation and consistency. It also avoids the need to coordinate database transactions across the two backend components.
Where AI-specific state is stored, it should be limited to what is necessary and should follow appropriate privacy and retention rules.
## 8.2.6 Deployment Architecture
The two backend components will be packaged and deployed independently.

```dpr
Deployment Unit 1
Care Platform API
├── Spring Boot application
├── Independent build artifact
├── Component configuration
├── Health checks and logs
└── Connection to core PostgreSQL

Deployment Unit 2
Support Assistant Service
├── Spring Boot application
├── Independent build artifact
├── Component configuration
├── Health checks and logs
└── Connections to Care Platform API and LLM provider
```

The components may initially share a hosting environment, but each must have a separate application process or container and its own deployment configuration.
Independent deployment does not mean the components have no runtime dependencies. The Support Assistant Service depends on the Care Platform API for verified service information and on the LLM provider for AI generation. Those dependencies must be documented and handled through timeouts, controlled retries and clear error responses.
## 8.2.7 Failure Handling
The architecture should support graceful degradation.
*   If the Support Assistant Service is unavailable, core platform features should remain available.
*   If the LLM provider is unavailable, the assistant should return a controlled error or indicate that AI assistance is temporarily unavailable.
*   If the Care Platform API cannot provide current service information, the assistant should not claim that service availability has been verified.
*   If PostgreSQL is unavailable, the Care Platform API should fail safely rather than report successful operations that were not persisted.
*   If asynchronous messaging is introduced, the relevant component must implement retry, idempotency and failed-message handling.
These behaviours should be validated through integration tests and, where practical, failure-simulation tests.
* * *

# 8.3 Architectural Summary
The proposed solution uses a **Java, Spring Boot and Maven backend composed of two independently deployable components**, a **React and Tailwind CSS frontend**, and **PostgreSQL as the primary store for authoritative care and service data**.
The two backend components are:
1. **Care Platform API:** Owns the core care-management workflows and authoritative records for caregivers, care recipients, organisations, support services, availability and support requests.
2. **Support Assistant Service:** Provides AI-assisted information and service navigation through a managed LLM provider and retrieves verified local service information through the Care Platform API.
Both components use Traditional Layered Architecture internally, separating presentation, business logic, data access and infrastructure concerns.
REST is the default synchronous communication mechanism. Asynchronous messaging may be added for background workflows when justified by requirements.
The architecture prioritises:
*   **Independent deployment:** Each backend component can be built, configured and released separately.
*   **Clear component boundaries:** Each component has a defined purpose and responsibility.
*   **Explicit data ownership:** The Care Platform API owns authoritative care and service records.
*   **High cohesion and low coupling:** Components communicate through explicit contracts.
*   **Maintainability and testability:** Both components follow a familiar layered structure.
*   **Team familiarity:** The same Java and Spring Boot stack is used across the backend.
*   **Security and privacy:** Access controls apply across component boundaries.
*   **Resilience:** AI-specific failures should not unnecessarily prevent core platform operations.
*   **Replaceable external dependencies:** The AI provider is isolated within the Support Assistant Service.
*   **Pragmatic complexity:** Additional databases, message brokers and services are introduced only when justified.
This architecture meets the intended direction of the assessment more directly than a single deployable backend. It introduces some additional operational complexity, but keeps the internal design of each component straightforward and gives the system clear boundaries for responsibility, data ownership, communication and failure handling.
The architecture can evolve as the project grows, provided the team preserves independent deployment, explicit API contracts and clear ownership of core data.

# Security and Privacy Assessment

## 1\. Initial Threat Model
The threat model follows the STRIDE framework to identify, evaluate, and mitigate risks across the system's primary components (React Frontend, Spring Boot REST API, PostgreSQL Database, and External Managed LLM).

| Threat Category (STRIDE) | Identified Threat | System Impact | Mitigation Strategy |
| ---| ---| ---| --- |
| Spoofing | Unauthorized user impersonates an informal caregiver, backup contact, or verified service provider. | Access to support requests, unauthorized updates to institutional availability. | JWT Authentication & RBAC: Enforce signed JWT tokens with role-specific claims (ADR-006). HTTPS enforced end-to-end. |
| Tampering | Unverified entity or attacker modifies local service status or alters support request details in transit. | Caregivers receive false information about open pharmacies or altered support locations. | Backend Authorization Checks: Enforce DB-level ownership and role verification on `PATCH /services/{id}`. Input sanitization and TLS 1.3 encryption in transit. |
| Repudiation | A verified organization denies changing its operating status or a user denies accepting a request. | Trust degradation and operational disputes. | Release 1 exposes the service's last-updated timestamp. The immutable service-change audit history (`created_at`, `updated_by`, `action_type`) is US25 / Release 2; security and role changes remain subject to platform audit logging. |
| Information Disclosure | Leakage of caregiver location, phone numbers, or non-sensitive accessibility notes to external parties or LLM providers. | Violation of user privacy and potential exposure of vulnerable individuals. | Data Minimization & AI Grounding (ADR-007, ADR-008): Never send personal user data or exact residential addresses to external LLM providers. Coarse/parish-level location sharing for support requests. |
| Denial of Service | Malicious bot or user floods search endpoints or AI navigation APIs. | Application unavailability during critical caregiver disruption scenarios. | API Rate Limiting & Throttling: Implement Spring Security rate limiting (e.g., Bucket4j or API gateway limits) on `/api/v1/ai/navigate` and public search APIs. |
| Elevation of Privilege | A registered caregiver attempts to modify organisation verified status or access another user's care profile. | Unauthorized access to system capabilities or another user's care data. | Method-Level Security: Enforce `@PreAuthorize("hasRole('SERVICE_PROVIDER')")` or custom Spring Security expressions validating resource ownership (`user_id == current_user_id`). |

## 2\. Sensitive Data Classification & Privacy Tiers
Based on the Data Model & Privacy Classification, data assets are strictly categorized into three distinct tiers:

```gherkin
+-----------------------------------------------------------------------------------+
|                        PRIVACY CLASSIFICATION & DATA TIERS                        |
+-------------------+-----------------------------------+---------------------------+
| TIER 1: HIGHLY    | TIER 2: PERSONAL IDENTIFIABLE     | TIER 3: NON-SENSITIVE     |
| RESTRICTED        | INFORMATION (PII)                 | PUBLIC & OPERATIONAL      |
+-------------------+-----------------------------------+---------------------------+
| • password_hash   | • User email & phone_number       | • Service type (Enum)     |
| • Exact Addresses | • Caregiver & CareRecipient IDs   | • coarse_location         |
| • Credentials     | • task_description (What)         |   (Parish/Neighborhood)   |
|                   | • location (Where) & time_window  | • operating_hours         |
|                   | • coarse_accessibility_tags       | • current_status (Enum)   |
+-------------------+-----------------------------------+---------------------------+
|   AI ACCESSIBLE?  |           AI ACCESSIBLE?          |       AI ACCESSIBLE?      |
|      BLOCKED      |              BLOCKED              |            ALLOWED        |
+-------------------+-----------------------------------+---------------------------+
```

### Core Privacy Rules
1. Strict Exclusion of Health Records (No PHI): The platform stores zero medical diagnoses, doctor notes, or clinical health records. Only high-level operational accessibility tags (e.g., `"wheelchair_accessible"`) are permitted on the `CareRecipient` entity.
2. AI Security Boundary: The `Data Scrubber` component in the Infrastructure Layer inspects and sanitizes all prompts, guaranteeing that no Tier 1 or Tier 2 fields ever leave the system boundary.
## 3\. Access-Control Requirements (RBAC)
Access control enforces role-based isolation at the API controller and service layers.
### Role Permission Matrix

| Endpoint / Capability | Anonymous | Caregiver | Service Provider | Volunteer / Backup | Admin |
| ---| ---| ---| ---| ---| --- |
| `POST /api/v1/auth/login` | yes | yes | yes | yes | yes |
| `GET /api/v1/services` (Public Search) | yes | yes | yes | yes | yes |
| `POST /api/v1/caregivers` | no | yes (Own User) | no | no | yes |
| `POST /api/v1/caregivers/{id}/recipients` | no | yes (Own Profile) | no | no | yes |
| `PATCH /api/v1/services/{id}/status` | no | no | yes (Own Org) | no | yes |
| `POST /api/v1/support-requests` | no | yes | no | no | yes |
| `PATCH /api/v1/support-requests/{id}/status` | no | no | no | yes (Assigned) | yes |
| `POST /api/v1/ai/navigate` | no | yes | yes | yes | yes |

### Key Access Control Rules
*   User Isolation: Caregivers can only read and write their own `Caregiver` profile, associated `CareRecipient` entities, and created `SupportRequest` items.
*   Provider Scope: `Service Provider` accounts can only update status (`current_status`, `operating_hours`) for services tied to their `Organisation`.
*   Recipient Scope: Backup volunteers can only view and update the status of `SupportRequest` items where they are designated as the assigned backup contact.
## 4\. Data-Minimization Measures
1. Coarse Location Granularity: Stores and processes location using parish or neighborhood names (`coarse_location`) rather than precise residential coordinates wherever feasible.
2. Tag-Based Care Needs: Limits recipient requirements to a simple `coarse_accessibility_tags` list/JSON, avoiding unstructured free-text fields where medical information could accidentally be entered.
3. Data Scrubber Adapter: Intercepts LLM prompt generation, stripping user identifiers, email addresses, phone numbers, and request details, feeding the LLM only Tier 3 service records and anonymized search queries.
4. No Mandatory Uploads: Eliminates any requirement for medical documentation or identity proof uploads.
## 5\. Retention Considerations
To ensure compliance with GDPR storage limitation requirements:

```gherkin
+-----------------------------------------------------------------------------------+
|                            DATA RETENTION SCHEDULE                                |
+-----------------------+-----------------------+-----------------------------------+
| DATA ASSET            | ACTIVE PERIOD         | RETENTION / PURGE RULE            |
+-----------------------+-----------------------+-----------------------------------+
| User Account & Auth   | Active account usage  | Deleted within 30 days upon       |
| Data (`User`)         |                       | explicit account closure request  |
+-----------------------+-----------------------+-----------------------------------+
| Support Requests      | Active coordination   | Automated purge 30 days after     |
| (`SupportRequest`)    | (Pending/Accepted)    | resolution/decline                |
+-----------------------+-----------------------+-----------------------------------+
| Service Availability  | Real-time state       | Overwritten upon next provider    |
| (`SupportService`)    |                       | update; historical state archived |
+-----------------------+-----------------------+-----------------------------------+
| AI Navigation Logs    | Immediate processing  | Zero retention on LLM provider;   |
|                       |                       | ephemeral backend logs (7 days)   |
+-----------------------+-----------------------+-----------------------------------+
```

*   Right to Erasure (GDPR): Triggering account deletion cascades through `User` → `Caregiver` → `CareRecipient` and anonymizes past `SupportRequest` logs.
*   Non-Retention Integration Clause: The contract with the external managed LLM provider mandates a zero-data-retention policy, preventing prompt data from being stored or used for model retraining.
1.

# Operating Costs Estimation

## 1\. Objective and Scope
This estimation evaluates the costs of developing, testing and demonstrating the Support Network for Informal Caregivers MVP (CareBnb) over two sprints.
The project consists of four team members combining development with project management, Scrum, business analysis and quality assurance responsibilities. The solution uses React and Tailwind CSS for the frontend, Java and Spring Boot for the backend, PostgreSQL for persistent data, and an AI service for bounded natural-language navigation.
The estimate separates three cost categories:
*   **Direct project expenses:** hosting, database, AI API usage and development tools.
*   **Development labour value:** the estimated monetary value of implementing the backlog, calculated from story points using an explicitly stated planning assumption.
*   **Labour contingency:** an additional 20% of the estimated labour value to account for bugs, integration problems and additional development work.
The calculation covers the MVP project rather than a full year of operation. All amounts are estimates in euros, with provider prices originally published in other currencies converted only approximately where necessary. Taxes and currency fluctuations are excluded.
## 2\. Development Backlog and Story Points
The MVP scope is the **Release 1 (walking skeleton)** stories plus the **cross-cutting** items, all prioritised as _Must_ in the product backlog. This is 24 user stories and 97 story points. Release 2 and later stories are outside this estimate.

| ID | Epic | User story | Story points |
| ---| ---| ---| --- |
| US01 | E1 | Log in as caregiver or provider | 5 |
| US02 | E1 | Restrict features by role | 3 |
| US03 | E1 | Create basic care profile | 3 |
| US04 | E1 | Manage emergency contacts | 3 |
| US05 | E2 | Search services by type and location | 5 |
| US06 | E3 | Ask a question in plain language | 3 |
| US07 | E3 | Answer built only from verified service data | 5 |
| US08 | E3 | AI guardrails | 5 |
| US09 | E3 | Fall back to standard search if AI is unavailable | 3 |
| US10 | E4 | View service details | 3 |
| US11 | E4 | Call button with "confirm by phone" notice | 2 |
| US12 | E5 | Send support request | 5 |
| US13 | E5 | Notify contact asynchronously | 5 |
| US14 | E5 | Retry failed notifications without duplicates | 5 |
| US15 | E6 | Verify provider organisation | 5 |
| US16 | E6 | Update service status in under a minute | 5 |
| US33 | E7 | Accessible, mobile-first UI | 5 |
| US34 | E7 | Data minimisation | 2 |
| US35 | E8 | Backend unit tests (at least 80% coverage) | 5 |
| US36 | E8 | Integration tests of the main flows | 3 |
| US37 | E8 | CI/CD, structured logs, health checks and metrics | 5 |
| US38 | E8 | Failure handling and one demonstrated failure | 5 |
| US39 | E6 | Choose the external data source (to decide) | 2 |
| US40 | E6 | Integrate the external data source | 5 |
|  |  | Total | 97 |

## 3\. Development Labour Value
Story points measure relative complexity, effort and uncertainty; they do not have a universal monetary value. To estimate the economic value of the work, this report uses an explicit planning assumption of four team-hours per story point and an illustrative labour rate of €15 per hour.
These values are assumptions for budgeting, not measured productivity or an agreed salary. Actual labour value should be recalculated using recorded team hours and an agreed rate if available.
### Calculation

| Item | Value |
| ---| --- |
| Story points | 97 SP |
| Hours per story point | 4 h |
| Hourly rate | €15 |

The calculation is:
*   Estimated effort = 97 SP × 4 hours/SP = **388 hours**
*   Labour value = 388 hours × €15/hour = **€5,820.00**
For a four-person team, this represents an average of 97 hours per person across the project if effort is distributed equally. Actual effort will vary according to responsibilities, implementation complexity and testing requirements.
This labour value represents the economic value of the development work. Since the project is an academic exercise and the team is assumed not to receive salaries, it is not necessarily a direct cash expense.
## 4\. Infrastructure and Hosting Costs
The application requires a frontend deployment, two independently deployable Spring Boot backend components and a PostgreSQL database. To keep the MVP affordable, the initial estimate assumes GitHub for source control and automation, static hosting for React, and low-cost or free hosting for the backend and database.

| Component | Assumed solution | Estimated cost |
| ---| ---| --- |
| Source control and automation | GitHub / GitHub Actions | €0 (to verify) |
| Frontend (React) | GitHub Pages (static hosting) | €0 |
| Backend (two Spring Boot components) | Free or low-cost hosting (e.g., Render) | €0 (to verify) |
| Database (PostgreSQL) | Free tier (e.g., Supabase) | €0 (to verify) |

**Provider references**
*   GitHub Pages: [https://pages.github.com/](https://pages.github.com/)
*   GitHub Actions billing: [https://docs.github.com/en/billing/concepts/product-billing/github-actions](https://docs.github.com/en/billing/concepts/product-billing/github-actions)
*   Render compute plans: [https://render.com/docs/compute-plans](https://render.com/docs/compute-plans)
*   Supabase billing and plan limits: [https://supabase.com/docs/guides/platform/billing-on-supabase](https://supabase.com/docs/guides/platform/billing-on-supabase)
GitHub Pages only serves static websites; it cannot execute Spring Boot applications or host PostgreSQL directly. The backend components and database therefore need suitable external hosting. GitHub Actions can automate builds and deployments, but its usage limits and billing depend on the repository and account plan.
The €0 infrastructure estimate is conditional, not a guarantee. Free hosting may introduce sleeping services, limited compute, database pausing or other restrictions. The team should verify these limits before the final demonstration.
### Paid-hosting alternative
If free backend hosting proves unreliable, a paid backend plan can be considered. For example, Render's documentation identifies a small paid web-service plan with 0.5 CPU and 512 MB RAM under its current plan naming. The actual price should be checked on its official pricing page before committing.
If a paid instance costs P euros per month and two backend components each require an instance for one billed month, the additional cost is:
> Additional backend cost = 2 × P × number of billed months
This is a contingency scenario and is not included in the baseline budget because the exact selected plan and billing period have not yet been confirmed.
## 5\. AI API Costs
The plain-language search (bounded AI navigator) allows caregivers to ask natural-language questions about support services. During development, the team will need to test prompt construction, retrieval from verified service data, source context, refusal behaviour and concise responses.
GitHub Copilot and the application's AI API are separate services. Copilot assists developers while coding; the application API is used when the running platform answers caregiver questions.
### Token assumptions
For budgeting, assume the team performs 500 AI test requests during implementation and demonstration.

| Item | Value |
| ---| --- |
| AI test requests | 500 |
| Input tokens per request | \[to be filled in from the original table\] |
| Output tokens per request | \[to be filled in from the original table\] |

For an illustrative model-price reference, the official OpenAI API pricing page lists GPT-6 Luna at $0.10 per million input tokens and $0.50 per million output tokens for standard short-context processing. Prices and model availability should be verified when the project selects its provider.
Reference: [https://developers.openai.com/api/docs/pricing](https://developers.openai.com/api/docs/pricing)
### Calculation
The calculation gives an illustrative total of **$0.20**, before any applicable taxes, currency conversion or additional services. A project budget of **€5.00** is allocated for AI API usage to allow for additional prompt iterations, repeated tests, and usage beyond the initial estimate.
This €5.00 is a budget allowance, not a prediction that the API will consume the full amount. Costs can increase if the team uses a more expensive model, sends longer retrieved contexts, tests more requests, or adds embedding or other paid services.
## 6\. Development Tools

| Tool | Assumption | Estimated cost |
| ---| ---| --- |
| GitHub Copilot | Student access for the four team members | €0 |
| GitHub Actions | Within included usage limits | €0 |

Official references:
*   GitHub Copilot Student: [https://docs.github.com/en/copilot/how-tos/copilot-on-github/set-up-copilot/enable-copilot/set-up-for-students](https://docs.github.com/en/copilot/how-tos/copilot-on-github/set-up-copilot/enable-copilot/set-up-for-students)
*   GitHub Actions billing: [https://docs.github.com/en/billing/concepts/product-billing/github-actions](https://docs.github.com/en/billing/concepts/product-billing/github-actions)
Any paid Copilot subscription, overage charge or paid development service should be added if it becomes necessary. The estimate assumes the four team members are eligible for student access and that the project stays within included usage limits.
## 7\. Direct Project Expense Summary
The baseline cash budget includes the planned AI allowance and assumes free infrastructure and development tools.

| Category | Estimated cost |
| ---| --- |
| Infrastructure and hosting | €0.00 |
| AI API usage (allowance) | €5.00 |
| Development tools | €0.00 |
| Total direct cash budget | €5.00 |

The direct cash estimate is low because the project is assumed to use free plans and unpaid student labour. It is not a quote for a production-ready deployment. Paid backend hosting, a paid database, a custom domain or other services would increase this figure.
## 8\. Labour Contingency (20%)
A contingency of 20% is applied specifically to the estimated labour value. This reserve accounts for bugs, integration issues, additional testing, security fixes and small changes discovered during implementation.
### Calculation

| Item | Value |
| ---| --- |
| Estimated labour value | €5,820.00 |
| Contingency rate | 20% |
| Labour contingency | €1,164.00 |
| Labour value with contingency | €6,984.00 |

The contingency is a planning reserve rather than a guaranteed expense. If additional work increases the number of story points, the team should revisit the estimate and determine whether the extra work fits within the existing schedule and capacity.
The contingency does not automatically cover additional paid hosting or API usage; those are separate cash expenses and should be revised if the project's technical requirements change.
## 9\. Final Project Cost Estimate
To avoid confusing economic labour value with actual spending, the project budget is presented in two parts.

| Part | Amount |
| ---| --- |
| Direct cash budget | €5.00 |
| Labour value | €5,820.00 |
| Labour contingency (20%) | €1,164.00 |
| Estimated economic value of the project | €6,989.00 |

The estimated direct cash budget is €5.00, assuming free infrastructure and tools. The estimated economic value of the project, including labour and the 20% labour contingency, is €6,989.00.
These are different measures: the first represents expected out-of-pocket expenditure under the assumptions; the second also assigns a notional monetary value to the team's development effort.
## 10\. Conclusion
The Support Network for Informal Caregivers MVP can potentially be developed and demonstrated with very low direct cash expenditure by using GitHub, free hosting tiers, open-source technologies and eligible GitHub Copilot Student access.
The main project resource is development effort, estimated at 388 team-hours for 97 story points under the stated planning assumption. At an illustrative rate of €15 per hour, this represents €5,820 in labour value. The 20% labour contingency adds €1,164, producing an estimated labour value of €6,984 before the direct cash expenses are added.
The estimate should be updated when the team confirms the sprint allocation, selects the hosting providers, measures actual effort, and records AI token consumption. Free-tier restrictions must be evaluated against the project's deployment, reliability, privacy and demonstration requirements.

# Non-Functional Requirements

# Main Non-Functional Requirements — CareBnB (FURPS+)
* * *
## F — Functionality

| ID | Requirement | Source |
| ---| ---| --- |
| F-01 | Backend-enforced authentication and RBAC. Authentication (JWT/Spring Security) and role-based access control with least privilege are enforced by the backend; users can only access their own care data and support requests. | Success Criteria §4; ADR-007; Security Assessment §3 |
| F-02 | Data minimisation and no PHI. Collect only the minimum necessary; no mandatory medical-document upload; care needs expressed as non-sensitive tags only. | Success Criteria §4; MVP §4; US34; Security Assessment §2 |
| F-03 | No sensitive data to the LLM. Only Tier 3 (non-sensitive public/operational) data may reach the external LLM; Tier 1 and Tier 2 data must never leave the system (allowlists, payload validation, Data Scrubber). | Security Assessment §2, §4; Technical Design §4.2 |
| F-04 | Bounded and grounded AI. The AI only retrieves and summarises verified application data; it must not invent service facts, give medical advice or make emergency decisions. Medical/emergency queries are redirected to 112 / SNS 24. | Success Criteria §3; Responsible AI §1, §4; ADR-009 |
| F-05 | Trust signals on every result. Every result shows its source, verification state (Official vs community) and last-update time, and never presents availability as a guarantee. | Success Criteria §2.3; MVP §3B; US10 |
| F-06 | Human fallback. Every service result and AI recommendation includes a prominent "Call / Contact" action and a limitation notice. | Success Criteria §2.3, §3; MVP §4; US11 |
| F-07 | Encryption and audit. Data protected in transit (HTTPS) and at rest where appropriate; security and role changes are audited; immutable service-update history is US25 / Release 2; logs must not expose sensitive content. | Success Criteria §4; ADR-010; Security Assessment §1 |

* * *
## U — Usability

| ID | Requirement | Source |
| ---| ---| --- |
| U-01 | Mobile-first, accessible UI. Large text, high contrast, clear focus states, large touch targets, simple language and few steps. | Success Criteria §2.2; MVP §4; US33 |
| U-02 | Not digital-only. A human/phone alternative must exist at every critical point; onboarding is short and requires no medical documents. | Success Criteria §2.2; MVP §4 |
| U-03 | Task-success targets. ≥ 80% complete the search task unaided; ≥ 90% identify the next action; ≥ 80% complete the support-request flow; ≥ 80% of older users complete a search or contact task. | Success Criteria §2.1, §2.2; MVP §7 |

* * *
## R — Reliability

| ID | Requirement | Source |
| ---| ---| --- |
| R-01 | Graceful degradation. Failure of the Support Assistant Service or LLM provider must not block core platform operations; if the AI is unavailable, the user falls back to standard search/filters. | ADR-011; Responsible AI §5; US09 |
| R-02 | No fabricated results on failure. Bounded timeouts and limited retries; the system must not invent answers or present unverified/cached data as confirmed availability. | ADR-011 |
| R-03 | Idempotent asynchronous notifications. Support-request notifications are sent asynchronously, retried on failure, and never duplicated. | Technical Design §2; US13; US14; ADR-011 |
| R-04 | Offline access to emergency contacts, care profile and basic routines during network or power disruptions. | Responsible AI §5; Technical Design §4.4 |

* * *
## P — Performance

| ID | Requirement | Source |
| ---| ---| --- |
| P-01 | Find an alternative fast. Median time from opening the dashboard to a relevant result ≤ 3 minutes. | Success Criteria §2.1; MVP §7; Journey 1 |
| P-02 | Fast provider updates. A verified provider can publish a status update in under 1 minute, and it is publicly reflected within that time. | Success Criteria §5; US16; Release Evaluation Criteria |

> The documentation defines no technical performance targets (API latency, throughput, concurrent users, availability %).
* * *
## S — Supportability

| ID | Requirement | Source |
| ---| ---| --- |
| S-01 | Automated testing. Core business rules covered by automated tests, with ≥ 80% coverage on new backend code, plus integration tests of the main flows. | Success Criteria §6; US35; US36; Definition of Done |
| S-02 | CI/CD and observability. CI/CD, structured logs, health checks per component and metrics. | US37; ADR-004; ADR-011 |
| S-03 | Layered, loosely coupled architecture. Traditional layered architecture inside each backend component; components communicate only through versioned API contracts; external integrations (e.g. the LLM) behind replaceable adapters. | ADR-003; ADR-004; ADR-008; ADR-012 |

* * *
## \+ Constraints

| ID | Type | Requirement | Source |
| ---| ---| ---| --- |
| D-01 | Design | Tech stack: React + Tailwind CSS (frontend), Java/Spring Boot/Maven (backend), PostgreSQL (system of record), managed external LLM API. | ADR-001, 002, 006, 008 |
| D-02 | Design | Two independently deployable backends (Care Platform API and Support Assistant Service); the assistant never accesses the core database directly, and the frontend never calls the LLM directly. | ADR-004; ADR-006; MVP §4 |
| N-01 | Interface | REST over HTTPS (JSON, `/api/v1`) between frontend and backends; asynchronous messaging only where justified. | Technical Design §1; ADR-005 |
| I-01 | Implementation | Team process: Scrum; every change via pull request with ≥ 1 reviewer; Definition of Ready/Done respected. | Team Methodology §2–4 |
| PH-01 | Physical | Low-cost deployment: free/low-cost hosting assumed (GitHub Pages for the frontend, external hosting for backends and database); direct cash budget €5.00. Free-tier limits must be verified before the final demo. | Operating Costs §4, §7 |
| L-01 | Legal | GDPR compliance with defined consent, retention and deletion rules (e.g. accounts deleted within 30 days of request, support requests purged 30 days after resolution, zero data retention at the LLM provider). | Success Criteria §4; ADR-010; Security Assessment §5 |