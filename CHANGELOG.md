### Breaking Changes
- Moved gs.mclo.java -> gs.mclo.api
- Replace static methods on MclogsAPI with Mclogs class
- You are now required to specify a user agent or project name and version
- To use a custom instance you now need to create an instance object with your urls
- Errors returned by the API are now thrown as exceptions

### New features
- Fetch log insights from the API
- Improved development ergonomics
