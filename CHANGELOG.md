# v6.0.0

## Breaking Changes

- Api methods now complete exceptionally if the server returns an error response
  instead of returning a response object with error details.
  - Removed `ApiException#getResponse` and `JsonResponse#throwIfError` methods.
  - Remove `JsonResponse`
  - Remove the `success` and `error` fields from response objects.
- Some response classes from `response.insights` have been moved to `response.entry`.
- Many classes have been marked as final or not-extendable.
- `LimitedReader` and `Util` are now internal
- `LogReader` has been split into multiple classes in the `reader` package.
- Removed deprecated constants and constructor from the `Log` class.

## New Features
- You can now specify a source and other metadata when uploading logs. By default, this is filled with the project name
  you can set with `MclogsClient#setProjectName(String)`
- You can delete logs after uploading them using `UploadLogResponse#delete()` or `MclogsClient#deleteLog(id, token)`
  with the token from the upload response.
- Uploading a log now returns many new fields
- These new fields, the raw content, the parsed log entries and insights can now be fetched with a single request.

## Improvements

- Remove exception that is never thrown from signature of `MclogsClient#analyseLog(Path)`
- Add annotations for nullability where missing
- Limits are now applied to all methods that upload logs before the upload happens

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
