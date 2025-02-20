## Breaking Changes
- `MclogsClient#uploadLog(Path)` now throws an ExecutionException if reading the file fails
  instead of throwing an IOException immediately.

## Improvements
- Add endpoint for analysing a log without storing it
- Add endpoint for fetching storage limits
- Use instance limits for truncating logs passed by their path
- Do not read more log content than the limit from the input stream
