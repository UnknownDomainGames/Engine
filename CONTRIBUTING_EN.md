# Contributing to Engine

## Code Style
This project use [Google Java Style](https://google.github.io/styleguide/javaguide.html) as Standard Code Style.

Other：
1. For the file io **BE CAREFUL** use `File`，Must replace by `Path` or `URL`.
2. If the input parameters can be `null`, please add `@javax.annotation.Nullable` to this method.
3. If the return value can be `null`, please use `Optional` as return value.
4. If the input parameters and return value can't be `null`, please add `@javax.annotation.Nonnull`, and use `Validate.notNull()` to check the input parameters.
5. Any class that need to be thread safe shold mark as `@ThreadSafe`.
6. When useing JOML, use it own class（class with letter c e.g. `Vector3fc`，`Matrix4fc`）for input parameters.
7. Folder, directory, and package names are named in singular form.
8. When iterating in the **serial** scenario, **BE CAREFUL** to use the stream-API.

## Commit message format

### [Git Commit Guidelines](https://github.com/angular/angular.js/blob/master/DEVELOPERS.md#commits)
