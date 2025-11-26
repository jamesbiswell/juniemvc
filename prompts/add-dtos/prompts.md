Analyze the file `/prompts/add_dtos/requirements-draft.md` and inspect the project. Improve and rewrite the draft 
requirements to a new file called `/prompts/add-dtos/requirements.md`.

-----------------------------------------

Analyze the `prompts/add-dtos/requirements.md` file (not `prompts/add-dtos/requirements-guru.md`) and create a detailed plan for the improvements of this project.
Write the plan to `prompts/add-dtos/plan.md` file, and use Markdown headers to organize the plan.

-----------------------------------------

Create a detailed enumerated task list (using Markdown headers and subsections) to organize the tasks according to the suggested enhancements plan in `prompts/add-dtos/plan.md`.
Task items should have a placeholder [ ] for marking as done [x] upon task completion.
Write the task list to `prompts/add-dtos/tasks.md` file.

-----------------------------------------

Complete the task list `prompts/add-dtos/tasks.md`.
Inspect the requirements.md and plan.md and task.md documents in the `prompts/add-dtos` directory.
Implement the tasks in the task list.
Focus on completing the tasks in the order they appear in the task list.
Mark the task complete only when the code changes have been done using [x].
As each task is completed, it is very important to update the task list and mark the task as done [x].

-----------------------------------------

- Refactor the project to use Lombok annotations for DTOs and entities.
- Add Lombok annotations to the DTOs and entities classes, such as Beer and BeerDto.
- Use Lombok annotations such as @Data (or @Getter/@Setter), @Builder, @NoArgsConstructor, @AllArgsConstructor.
- Test that the project still builds as expected.
- Refactor the unit tests to use the builder pattern for creating test objects.
- Check that all tests still work as expected.
- Refactor the project to use MapStruct for mapping between DTOs and entities, also using the lombok mapstruct binding.
- Test that the project still builds as expected.
- Refactor the unit tests to use MapStruct as required.
- Check that all tests still work as expected.