# Jetbrains AI Prompts

Inspect the requirements document. Analyze for accuracy and completeness. Make recommendations for 
how we can improve this document. Implement the improvements in a revised version.

--------------------

For a React project using Shadcn, inspect the technologies used. Are there any missing dependencies?

--------------------
What information is needed for the vite configuration?

--------------------
Suggest further improvements for the guide?

--------------------
Can the Guide outline be improved?

--------------------
Inspect the requirements.md file. Generate a prompt to create an implementation plan from this file.

# Junie Prompts
Inspect the file `prompts/requirements-prompt-draft.md`. Use this file to create a developer guide to
implement a React front end for this project. Update and improve this developer guide using the context 
of this project. The guide should be organized into clear actionable steps. 

Write the improved guide to `prompts/requirements.md`.

-------------------------

**THIS DID NOT WORK**
Analyze the `prompts/requirements.md` file and create a detailed plan for the improvements of this project.
Write the plan to `prompts/plan.md` file.


-------------------------
**Improved Prompt via Gemini**

Based on the file`prompts/requirements.md`, generate a detailed implementation plan for creating the React front end for the Spring Boot Beer Service. `requirements.md`
The plan should be structured as a series of epics, with each epic broken down into smaller, actionable user stories or tasks. The goal is to create a clear roadmap for a development team to follow.
The implementation plan should cover the following key areas, as detailed in the requirements document:
1. **Project Setup & Configuration:**
    - Creating the initial React project with Vite and TypeScript.
    - Configuring Vite for development and production builds.
    - Setting up environment variables.
    - Installing and configuring all UI and utility libraries (Tailwind CSS, Shadcn, Radix, etc.).
    - Establishing code quality standards with ESLint and Prettier.

2. **API Integration:**
    - Generating TypeScript types and API client code from the OpenAPI specification.
    - Creating a reusable API service layer with Axios, including global error handling.
    - Implementing service modules for each resource (Beers, Customers, Beer Orders).

3. **Build Process Integration:**
    - Configuring the `frontend-maven-plugin` in the `pom.xml` to integrate the front-end build process with the main Maven lifecycle (installing dependencies, building, and testing).
    - Configuring the `maven-clean-plugin` to handle cleanup of generated front-end assets.

4. **Component Development & Routing:**
    - Setting up the main application routing using React Router.
    - Developing a set of reusable UI components based on Shadcn for forms, tables, dialogs, and navigation.
    - Creating pages/views for each primary feature.

5. **Feature Implementation (per resource):**
    - For **Beers**, create components for:
        - Listing all beers with filtering and pagination.
        - Viewing beer details.
        - Creating, updating, and deleting beers.

    - For **Customers**, create components for:
        - Listing all customers.
        - Viewing customer details.
        - Creating, updating, and deleting customers.

    - For **Beer Orders**, create components for:
        - Listing all orders.
        - Viewing order details.
        - Creating and updating orders.
        - Managing order shipments.

6. **Testing:**
    - Setting up the testing environment with Jest and React Testing Library.
    - Writing unit and integration tests for components and services.

Please ensure the plan is logical, sequential, and provides enough detail for developers to understand the scope of each task.

Write the plan to `prompts/plan.md` file.

--------------------------------------

Create a detailed enumerated task list according to the suggested enhancements plan in
`prompts/plan.md` Task items should have a placeholder [ ] for marking as done [x] upon task completion.
Write the task list to `prompts/tasks.md` file.

-------------------------------------

Complete the task list `prompts/tasks.md`. Use information from `prompts/requirements.md` and `prompts/plan.md` for
additional context when completing the tasks.

Implement the tasks in the task list. Focus on completing the tasks in order. Mark the task complete as it is done
using [x]. As each step is completed, it is very important to update the task list mark and the task as done [x]. 

-----------------------------------
**RUN THIS IN ASK MODE**
Inspect the files `prompts/requirements.md` and `prompts/plan.md`. These changes have been implemented in the project.
Review the project as needed. Plan additional sections in the `guidelines.md` file for the changes which have been 
implemented in the project. Include instructions for the project structure and for building and testing the front-end project.
Also identify any best practices used for the front-end code.

-----------------------------------
**CHANGE BACK TO CODE MODE**
The front-end project has build errors. Fix errors, verify tests are passing.

-----------------------------------

The command `npm test` is failing, fix test errors, verify all tests are passing.

-----------------------------------

The command `npm lint` is shows lint errors, inspect the lint errors, and fix, verify there are no lint errors.

-----------------------------------
Update eslint configuration to disable the warning for `Unused eslint-disable directive`.

# Project Building Without Errors

In the front-end project, running the command `npm build` produces the following error, indicating
a problem with tailwindcss. Inspect error and project to correct the problem.

```error
Error: Cannot apply unknown utility class `border-border`. Are you using CSS modules or similar and missing `@reference`? https://tailwindcss.com/docs/functions-and-directives#reference-directive
    at onInvalidCandidate (/Users/jt/src/springframework.guru/courses/junie/juniemvc/src/main/frontend/node_modules/tailwindcss/dist/lib.js:18:1312)
    at ge (/Users/jt/src/springframework.guru/courses/junie/juniemvc/src/main/frontend/node_modules/tailwindcss/dist/lib.js:13:29803)
    at /Users/jt/src/springframework.guru/courses/junie/juniemvc/src/main/frontend/node_modules/tailwindcss/dist/lib.js:18:373
    at I (/Users/jt/src/springframework.guru/courses/junie/juniemvc/src/main/frontend/node_modules/tailwindcss/dist/lib.js:3:1656)
    at je (/Users/jt/src/springframework.guru/courses/junie/juniemvc/src/main/frontend/node_modules/tailwindcss/dist/lib.js:18:172)
    at bi (/Users/jt/src/springframework.guru/courses/junie/juniemvc/src/main/frontend/node_modules/tailwindcss/dist/lib.js:35:780)
    at async yi (/Users/jt/src/springframework.guru/courses/junie/juniemvc/src/main/frontend/node_modules/tailwindcss/dist/lib.js:35:1123)
    at async _r (/Users/jt/src/springframework.guru/courses/junie/juniemvc/src/main/frontend/node_modules/@tailwindcss/node/dist/index.js:10:3384)
    at async p (/Users/jt/src/springframework.guru/courses/junie/juniemvc/src/main/frontend/node_modules/@tailwindcss/postcss/dist/index.js:10:4019)
    at async Object.Once (/Users/jt/src/springframework.guru/courses/junie/juniemvc/src/main/frontend/node_modules/@tailwindcss/postcss/dist/index.js:10:4290)
    at async LazyResult.runAsync (/Users/jt/src/springframework.guru/courses/junie/juniemvc/src/main/frontend/node_modules/postcss/lib/lazy-result.js:293:11)
    at async runPostCSS (file:///Users/jt/src/springframework.guru/courses/junie/juniemvc/src/main/frontend/node_modules/vite/dist/node/chunks/dep-DBxKXgDP.js:43824:21)
    at async compilePostCSS (file:///Users/jt/src/springframework.guru/courses/junie/juniemvc/src/main/frontend/node_modules/vite/dist/node/chunks/dep-DBxKXgDP.js:43794:18)
    at async compileCSS (file:///Users/jt/src/springframework.guru/courses/junie/juniemvc/src/main/frontend/node_modules/vite/dist/node/chunks/dep-DBxKXgDP.js:43649:27)
    at async TransformPluginContext.handler (file:///Users/jt/src/springframework.guru/courses/junie/juniemvc/src/main/frontend/node_modules/vite/dist/node/chunks/dep-DBxKXgDP.js:42965:11)
    at async EnvironmentPluginContainer.transform (file:///Users/jt/src/springframework.guru/courses/junie/juniemvc/src/main/frontend/node_modules/vite/dist/node/chunks/dep-DBxKXgDP.js:42294:18)
    at async loadAndTransform (file:///Users/jt/src/springframework.guru/courses/junie/juniemvc/src/main/frontend/node_modules/vite/dist/node/chunks/dep-DBxKXgDP.js:35735:27)
```

------------------------

Running the front-end application produces the following error. Inspect error. Fix front-end 
code and tests. Verify the front-end project builds and all tests are passing.

```error
Error: useToast must be used within a ToastProvider
    at useToast (http://localhost:3000/src/hooks/useToast.ts:6:11)
    at BeerOrderListPage (http://localhost:3000/src/pages/beerOrders/BeerOrderListPage.tsx:30:30)
    at react-stack-bottom-frame (http://localhost:3000/node_modules/.vite/deps/react-dom_client.js?v=93637eae:17424:20)
    at renderWithHooks (http://localhost:3000/node_modules/.vite/deps/react-dom_client.js?v=93637eae:4206:24)
    at updateFunctionComponent (http://localhost:3000/node_modules/.vite/deps/react-dom_client.js?v=93637eae:6619:21)
    at beginWork (http://localhost:3000/node_modules/.vite/deps/react-dom_client.js?v=93637eae:7613:199)
    at runWithFiberInDEV (http://localhost:3000/node_modules/.vite/deps/react-dom_client.js?v=93637eae:1485:72)
    at performUnitOfWork (http://localhost:3000/node_modules/.vite/deps/react-dom_client.js?v=93637eae:10868:98)
    at workLoopSync (http://localhost:3000/node_modules/.vite/deps/react-dom_client.js?v=93637eae:10728:43)
    at renderRootSync (http://localhost:3000/node_modules/.vite/deps/react-dom_client.js?v=93637eae:10711:13)
```

-----------------
Inspect the front-end project. The styling for radix and shadcn is not working properly. Make necessary
updates to fix. Verify the project builds and tests without errors.

-------------------
Inspect the front-end project. The styling for radix and shadcn is not working properly. Verify tailwindcss
version 4 is properly set up for use with radix and shadcn components. Verify plugins for radix are
installed and configured properly. Make necessary updates to fix.
Verify the project builds and tests without errors.







