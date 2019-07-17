# Contributing to NullEngine

## Code Style
This project use [Google Java Style](https://google.github.io/styleguide/javaguide.html) as Standard Code Style.

Other：
1. For the file io **NEVER** ever use `File`，Must replace by `Path` or `URL`.
2. If the input parameters can be `null`, please add `@javax.annotation.Nullable` to this method.
3. If the return value can be `null`, please use `Optional` as return value.
4. If the input parameters and return value can't be `null`, please add `@javax.annotation.Nonnull`, and use `Validate.notNull()` to check the input parameters.
5. Any class that need to be thread safe shold mark as `@ThreadSafe`.
6. When useing JOML, use it own class（class with letter c e.g. `Vector3fc`，`Matrix4fc`）for input parameters.

## Commit format

### Commit format
All commit need to be 3 parts **Title**, **Description** and **Conclusion**. The **Title** includes **Type**, **Subject** and **Briefing**:
```
<Type>(<Subject>): <Briefing>
<br>
<Description>
<br>
<Conclusion>
```
Must include **Title**, **Subject** is optional.

Every line of the commit should not exceed 100 char, it for convenience of the developer.

e.g.：
```
docs(README): update README
```
```
feat(Mod): support to load mod in development environment
```

### Type
Must be one of them:
- **build**: Modification to the buildfile or changes of the dependence
- **docs**: Modification to the documents
- **feat**: Features(bugs)!
- **fix**: Bugfixes
- **perf**: Performance enhancements
- **refactor**: Restructuring existing(hopefully behave the same as before)
- **style**: Reformat
- **test**: Test code(as name)

### Title 
The title includes brief introduction of the commit:
- Use imperative sentence.
- Use lowercase for the first word.
- End the sentence without a dot(.).

### Description
Use imperative sentence. Should include the description of the commit

### Conclusion
Conclusion include [close the issue](https://help.github.com/en/articles/closing-issues-using-keywords) or other decision.

### Revert
If you think some commit may cause more bug(or cake) and you want to revert it,the title should start with `revert:`. The optional extended description "description"(for short) need to be`This reverts commit <hash>.`,the hash is the sha code from the commit that you want to revert.

###### that cake 🍰 is a lie
