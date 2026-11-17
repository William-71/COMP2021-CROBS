# COMP2021 Crobs — Requirements (from project_description.pdf)

Source of truth extracted from `project_description.pdf` (Fall 2026).

## Overview

Command-line Campus ROom Booking System (Crobs) in Java.

- User types: **admin** and **regular**.
- Unique **superadmin** (a type of admin); only superadmin can modify other users’ access privileges.
- When Crobs starts, the system automatically creates a superadmin user.
- All rooms and users have unique IDs.
- No persistent storage required.
- Gracefully handle ill-formed commands and unauthorized / impossible actions.
- Package for model classes: `hk.edu.polyu.comp.comp2021.crobs.model`
- Main entry: `java hk.edu.polyu.comp.comp2021.crobs.Application -html <path> -txt <path>`

---

## Member A — REQ1–REQ6

### [REQ1] (3 points) Command logging

All operations executed during a Crobs session are logged into **two** files:

1. **HTML**: commands in a table; each row has two columns — operation index and operation command.
2. **TXT**: commands in execution order; one command per line.

File names are launch parameters, e.g.:

```text
java hk.edu.polyu.comp.comp2021.crobs.Application -html d:\log.html -txt d:\log.txt
```

### [REQ2] (1.5 points) Add regular user (admin)

- **Command:** `addUser name`
- **Effect:** Creates a new regular user with the given name. System auto-generates a unique user ID.

### [REQ3] (1.5 points) Remove regular user (admin)

- **Command:** `removeUser targetUserID`
- **Effect:** Removes the regular user with ID `targetUserId`. Also cancels all bookings made by that user.

### [REQ4] (2 points) Change role (superadmin only)

- **Command:** `changeRole targetUserId newRole`
- **Effect:** Changes the role of the target user to `newRole`.

### [REQ5] (1.5 points) Login

- **Command:** `login userID`
- **Effect:** Logs in as the user with the specified user ID. At session start, no user is logged in.

### [REQ6] (1 point) Logout

- **Command:** `logout`
- **Effect:** Logs out the currently logged-in user. After logout, no user is logged in.

---

## Other requirements (other members / later)

| ID | Command | Summary |
|----|---------|---------|
| REQ7 | `addRoom roomType capacity features` | Admin adds room |
| REQ8 | `modifyRoom roomID field newValue` | Admin modifies room |
| REQ9 | `deleteRoom roomID` | Admin deletes room (+ cancel future bookings) |
| REQ10 | `book roomId date startTime endTime purpose` | Create booking |
| REQ11 | `modifyBooking bookingID roomId date startTime endTime purpose` | Modify own booking |
| REQ12 | `deleteBooking bookingID` | Delete/cancel own booking |
| REQ13 | `search date startTime endTime minCapacity features` | Search available rooms |
| REQ14 | `schedule roomId weekStartDate` | Weekly schedule |
| REQ15 | `listRoom roomID` | List one room |
| REQ16 | `listAllRooms` | List all rooms (by room ID asc) |
| REQ17 | `listBookings` | List current user’s bookings |
| REQ18 | `quit` | Terminate program |

Room types: `lectureTheatre`, `computerLab`, `seminarRoom`, `meetingRoom`.  
Features: comma-separated list without spaces.

---

## Output / error handling convention (project-defined)

The official PDF does **not** prescribe exact success/error strings; it requires graceful handling. This project uses:

### Success messages

| Command | stdout |
|---------|--------|
| `addUser` | `User <userId> created.` |
| `removeUser` | `User <userId> removed.` |
| `changeRole` | `User <userId> role changed to <role>.` |
| `login` | `Logged in as <userId>.` |
| `logout` | `Logged out.` |
| `quit` | *(no message; program exits)* |

### Error messages

| Situation | stdout |
|-----------|--------|
| Unknown command | `Error: Unknown command.` |
| Wrong arity / ill-formed | `Error: Invalid arguments.` |
| Not logged in (when required) | `Error: No user is logged in.` |
| Insufficient privilege | `Error: Access denied.` |
| User ID not found | `Error: User not found.` |
| Cannot remove non-regular / superadmin | `Error: Cannot remove this user.` |
| Invalid role token | `Error: Invalid role.` |
| Cannot change superadmin’s role | `Error: Cannot change the role of the superadmin.` |
| Cannot assign superadmin via changeRole | `Error: Invalid role.` |
| Login to unknown user | `Error: User not found.` |
| Logout when nobody logged in | `Error: No user is logged in.` |

### Role tokens for `changeRole`

- `admin`
- `regular`

### System bootstrap

- Superadmin user ID: `u0`
- Superadmin name: `superadmin`
- Subsequent user IDs: `u1`, `u2`, …
