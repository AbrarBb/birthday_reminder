# Birthday Reminder App

A personal Android application to manage and remember the birthdays of your friends and family. Each user has their own private list, secured with a login system.

##  Features

*   **Multi-User Support**: Secure login and signup system where each user has their own private birthday list.
*   **Detailed Birthday Management**: Add, edit, and delete birthday records including Name, Phone Number, and Date of Birth.
*   **Photo Uploads**: Attach a photo to each birthday record from your gallery.
*   **Automatic Age Calculation**: Displays the precise age in **Years, Months, and Days**.
*   **Formatted Dates**: View dates in a clean "DD MMMM, YYYY" format.
*   **Data Persistence**: Uses SQLite for birthday data and SharedPreferences for user sessions and credentials.
*   **Clean UI**: Custom list layouts for an intuitive user experience.

---

##  How it was built (Step-by-Step)

### 1. Database Schema & Persistence
We started by creating `BirthdayDB.java`, an `SQLiteOpenHelper`. It manages a table called `dobinfo` with columns for:
*   `ID`: Primary key.
*   `name` & `phone`: Contact details.
*   `dob`: Stored as milliseconds (Long) for easy calculation.
*   `image`: Stored as a Base64 encoded String.
*   `user_id`: Used to filter the list so users only see their own data.

### 2. The Data Model
The `Birthday.java` class was created to act as our data object. It contains helper methods:
*   `getFormattedDOB()`: Uses `SimpleDateFormat` to turn milliseconds into "01 January, 2022".
*   `getAge()`: Uses `java.util.Calendar` to calculate the exact difference between the DOB and the current date.

### 3. Authentication System
In `SignupActivity.java`, we implemented a dual-mode screen:
*   **Signup**: Validates inputs and stores a user-specific password in `SharedPreferences` (e.g., `PASS_john`).
*   **Login**: Checks credentials against `SharedPreferences` and starts a session by saving the `LOGGED_IN_USER`.

### 4. The Main List
`MainActivity.java` acts as the hub:
*   It retrieves the `LOGGED_IN_USER` from session data.
*   It queries the database using `SELECT * FROM dobinfo WHERE user_id = '...'`.
*   A `CustomListAdapter` binds the data to a custom XML layout (`birthday_row.xml`) which includes an `ImageView` for the photo.

### 5. Adding & Editing Data
`BirthdayInfoActivity.java` handles the form:
*   **Image Picker**: Uses the modern `ActivityResultContracts.GetContent()` to let users select a photo.
*   **Image Encoding**: Converts selected Bitmaps into Base64 strings for storage.
*   **Form Validation**: Ensures the date format is correct (DD-MM-YYYY) and fields aren't empty.
*   **Insert/Update**: Detects if it's a new entry or an edit based on the presence of a `PERSON_ID` intent extra.

### 6. User Experience Tweaks
*   Implemented **Long Click to Delete** on the main list.
*   Added **Logout** functionality via the Exit button in the main screen.
*   Handled database version upgrades to add new columns like `image` and `user_id` without losing data.

---

##  Screenshots
*(Add your screenshots here later)*

##  Technologies Used
*   **Language**: Java
*   **Database**: SQLite
*   **Storage**: SharedPreferences
*   **UI Components**: ListView, Custom Adapters, ImageViews, Material Design Buttons.
