Assumptions:
- On first use, user should click "start the challenge today" button (or similar). This will initialize Timeline with startDate being the actual date.
- From that point going onwards, user will be getting daily notification to check status of resolutions.
- If no daily status is provided, the entry for this day is created with all resolutions set to UNKNOWN (Day.getEmpty()).
- Apart from "start the challenge today", user can choose to "import data from..." (at first, the only supported format is CSV).
- After import, the Timeline should be created with startDate set to the earliest date in data and every subsequent Day set to either values from corresponding record or empty (all UNKNOWN).

Startup
- On startup, the app should check, whether files "timeline.csv" and "resolutions.csv" exists on disk in the location specified in configuration (by default: "C:/Program Files/Sigma/"). During this check (and parsing) app should display huge caption "SIGMA". 
  - If they do, app should parse CSV and run normally. 
  - Otherwise, first-use information (described below) should be shown to the user, and both starting new challenge, and importing from existing CSV should result in creating "timeline.csv" and "resolutions.csv" in the mentioned location.

Creating new timeline
- If no CSV files mentioned earlier exist, user should have a choice between clicking on two buttons: "Start new challenge" and "Import data".
  - "Start new challenge" button should result in creating new Timeline with startDate equal to today.
  - "Import data" should enable user to select two files on disk - first CSV for Resolutions, then CSV for Timeline. After that, app should parse data from this files.

Home view
- After the Timeline is created (from the user perspective after either directly "SIGMA" caption or "SIGMA" caption and then "Start new challenge"/"Import data" options) user should be in redirected to home.
- It should display a few panels - "Calendar", "Today", and "Statistics" (in the future there will be more of them).
- Clicking on a panel should redirect to the corresponding view.

Calendar view
- Displays calendar for current month (similarly to Google Calendar). User can go to previous or following months.
- Every day is highlighted in a color that depends on result of StatisticsManager.getResult(day):
  - If the result is a fraction in range 0..1, the color should be on scale from red (close to 0) to green (close to 1).
  - If the result is -1 (empty day), the color should be gray.
  - If the result is -2 (day in the future or before startDate), the color should be white.

Today view
