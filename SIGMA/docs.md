Assumptions:
- On first use, user should click "start the challenge today" button (or similar). This will initialize Timeline with startDate being the actual date.
- From that point going onwards, user will be getting daily notification to check status of resolutions.
- If no daily status is provided, the entry for this day is created with all resolutions set to UNKNOWN (Day.getEmpty()).
- Apart from "start the challenge today", user can choose to "import data from..." (at first, the only supported format is CSV).
- After import, the Timeline should be created with startDate set to the earliest date in data and every subsequent Day set to either values from corresponding record or empty (all UNKNOWN).

- On startup, the app should check, whether file "timeline.csv" exists on disk in the location specified in configuration (by default: "C:/Program Files/Sigma/"). 
  - If it does, app should parse CSV and run normally. 
  - Otherwise, first-use information (described above) should be shown to the user, and both starting new challenge, and importing from existing CSV should result in creating "timeline.csv" in the mentioned location.
