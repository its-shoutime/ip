# UI Test Plan

Text-UI tests for the Kiwi chatbot. The `/test-ui` skill reads this file,
runs each test case against the program, shows the console session, and
**stops at the first failure**.

## Program launch

- **Main class:** `Kiwi`
- **Source directory:** `src/main/java`
- **Java version:** `25`
- **How tests are run:** compile all `*.java` in the source directory, then for each test case pipe **Inputs** to the program's stdin and compare full stdout to **Expected output**.

Suggested command (used by the skill runner):

```bash
python3 .cursor/skills/test-ui/scripts/run-ui-tests.py
```

## Test case format

Each case under `## Test cases` must include:

1. **Aim** — purpose of the case
2. **Inputs** — commands sent to the program (one per line)
3. **Expected output** — exact console output for that session

## Test cases

### TC01: Add todo, deadline, and event then list

**Aim:** Check that `todo`, `deadline`, and `event` create typed tasks with correct icons and date strings, and that `list` shows them.

**Inputs:**
```text
todo borrow book
deadline return book /by Sunday
event project meeting /from Mon 2pm /to 4pm
list
bye
```

**Expected output:**
```text
____________________________________________________________
 _  ___          _ 
| |/ (_)_      _(_)
| ' /| \ \ /\ / / |
| . \| |\ V  V /| |
|_|\_\_| \_/\_/ |_|
Hello! I'm Kiwi.
What can I do for you?
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] borrow book
Now you have 1 task in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [D][ ] return book (by: Sunday)
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [E][ ] project meeting (from: Mon 2pm to: 4pm)
Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] borrow book
2.[D][ ] return book (by: Sunday)
3.[E][ ] project meeting (from: Mon 2pm to: 4pm)
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

### TC02: Mark and unmark a todo

**Aim:** Check that `mark` / `unmark` update the status icon on a typed task.

**Inputs:**
```text
todo read book
mark 1
list
unmark 1
list
bye
```

**Expected output:**
```text
____________________________________________________________
 _  ___          _ 
| |/ (_)_      _(_)
| ' /| \ \ /\ / / |
| . \| |\ V  V /| |
|_|\_\_| \_/\_/ |_|
Hello! I'm Kiwi.
What can I do for you?
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] read book
Now you have 1 task in the list.
____________________________________________________________
____________________________________________________________
Marked this task as done:
1.[T][X] read book
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][X] read book
____________________________________________________________
____________________________________________________________
Marked this task as not done yet:
1.[T][ ] read book
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] read book
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

### TC03: Deadline with free-form /by text

**Aim:** Dates/times are stored as plain strings, including odd values like `no idea :-p`.

**Inputs:**
```text
deadline do homework /by no idea :-p
list
bye
```

**Expected output:**
```text
____________________________________________________________
 _  ___          _ 
| |/ (_)_      _(_)
| ' /| \ \ /\ / / |
| . \| |\ V  V /| |
|_|\_\_| \_/\_/ |_|
Hello! I'm Kiwi.
What can I do for you?
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [D][ ] do homework (by: no idea :-p)
Now you have 1 task in the list.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[D][ ] do homework (by: no idea :-p)
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```
