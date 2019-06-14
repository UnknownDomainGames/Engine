# Contributing to NullEngine

## Commit 消息规范

### Commit 消息格式
每一条提交消息由**标头**、**详情**和**结尾**组成。其中**标头**具有特殊格式，包含**类型**、**范围**和**主题**：
```
<类型>(<范围>): <主题>
<空行>
<详情>
<空行>
<结尾>
```
**标头**必写的，标头中的**范围**是选写的。

Commit消息中的任一行都不应超过100个字符！这可以使Commit消息在Github和各种Git更易阅读。

结尾应包含一个[issue的关闭引用](https://help.github.com/en/articles/closing-issues-using-keywords)（如果有的话）

例如：
```
docs(README): update README
```
```
feat(Mod): support to load mod in development environment
```

### 类型
必须是下述类型之一:
- **build**: 构建过程或辅助工具的变动
- **docs**: 文档的更改
- **feat**: 添加一个新特性
- **fix**: 修复一个bug
- **pref**: 提高性能的更改
- **refactor**: 重构
- **style**: 不影响代码的更改(空格, 格式化等)
- **test**: 添加测试

### 主题
主题包含了对更改的简洁描述：
- 使用祈使句，现在时：“change”而不是“changed”也不是“changes”。
- 不要大写第一个字符
- 结尾没有句号(.)

### 详情
和主题中的一样，使用祈使句，现在时：“change”而不是“changed”也不是“changes”。详情应该包含更改的原因，和其与此前的对比。

### 结尾
结尾可包含[issue的关闭引用](https://help.github.com/en/articles/closing-issues-using-keywords)或破坏性更改的详细信息。

### Revert
如果要恢复以前的Commit，则其标头应由`revert:`开头，后接标头的主题。在详情中应写`This reverts commit <hash>.`，其中hash是要还原的commit的SHA。
