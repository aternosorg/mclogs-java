# v5.1.0

## Improvements

- Remove exception that is never thrown from signature of `MclogsClient#analyseLog(Path)`
- Add annotations for nullability where missing

---

# v5.0.0

## Breaking Changes

- `MclogsClient#uploadLog(Path)` now throws an ExecutionException if reading the file fails
  instead of throwing an IOException immediately.

## Improvements

- Add endpoint for analysing a log without storing it
- Add endpoint for fetching storage limits
- Use instance limits for truncating logs passed by their path
- Do not read more log content than the limit from the input stream

---

# v4.1.1

- Allow more BungeeCord log file names

---

# v4.1.0

- Add Path based variants to listCrashReportsInDirectory and listLogsInDirectory

---

# v4.0.2

- Fix setting client in UploadLogResponse (Fix #9)

---

# v4.0.1

- fix accept header name

---

# v4.0.0

- Update dependencies
- Utilize `CompletableFuture` to wrap around API calls
- API calls are now async
- Moved visibility of all fields to `private`, some are also `final`
- Utilizes Java 11s `HttpClient`

---

# v3.0.1

- Update dependencies
- mark the client property of UploadLogResponse as transient
  - This should fix an issue with deserialization on Java 17+

---

# v3.0.0

### Breaking Changes
- Moved gs.mclo.java -> gs.mclo.api
- Replace static methods on MclogsAPI with Mclogs class
- You are now required to specify a user agent or project name and version
- To use a custom instance you now need to create an instance object with your urls
- Errors returned by the API are now thrown as exceptions

### New features
- Fetch log insights from the API
- Improved development ergonomics
