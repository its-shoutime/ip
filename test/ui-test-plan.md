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

### TC04: Empty todo and unknown command leave list empty

**Aim:** Negative inputs must not create tasks; a later valid `todo` should be the only item.

**Inputs:**
```text
todo
blah
list
todo after errors
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
A todo needs a description — try: todo borrow book
____________________________________________________________
____________________________________________________________
Hmm, Kiwi doesn't recognize that. Try todo, deadline, event, list, mark, unmark, or bye.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] after errors
Now you have 1 task in the list.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] after errors
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

### TC05: Bad deadline/event/mark then successful mark

**Aim:** Incomplete commands and a bad `mark` must not corrupt a later valid todo; `mark 1` should still work.

**Inputs:**
```text
deadline
event project /from Mon
mark 1
todo read book
mark abc
list
mark 1
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
A deadline needs details — try: deadline return book /by Sunday
____________________________________________________________
____________________________________________________________
Events need /from and /to — e.g. event meeting /from Mon 2pm /to 4pm
____________________________________________________________
____________________________________________________________
There is no task number 1 in your list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] read book
Now you have 1 task in the list.
____________________________________________________________
____________________________________________________________
That task number doesn't look like a number: abc
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] read book
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
Bye. Hope to see you again soon!
____________________________________________________________
```

### TC06: Interleave empty todo and unknown command with valid adds

**Aim:** After a failed `todo` and an unknown word, valid adds still accumulate with the correct counts.

**Inputs:**
```text
todo
todo buy milk
list
blah
deadline submit report /by Friday
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
A todo needs a description — try: todo borrow book
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] buy milk
Now you have 1 task in the list.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] buy milk
____________________________________________________________
____________________________________________________________
Hmm, Kiwi doesn't recognize that. Try todo, deadline, event, list, mark, unmark, or bye.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [D][ ] submit report (by: Friday)
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] buy milk
2.[D][ ] submit report (by: Friday)
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

### TC07: Malformed deadline/event then valid pair

**Aim:** Missing `/by` pieces and an empty `event` must not add tasks; only the following valid deadline and event appear.

**Inputs:**
```text
deadline return book /by
deadline /by Sunday
deadline return book /by Sunday
event
event meeting /from 2pm /to 4pm
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
Deadlines need both a description and /by <when> — e.g. deadline return book /by Sunday
____________________________________________________________
____________________________________________________________
Deadlines need both a description and /by <when> — e.g. deadline return book /by Sunday
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [D][ ] return book (by: Sunday)
Now you have 1 task in the list.
____________________________________________________________
____________________________________________________________
An event needs details — try: event meeting /from Mon 2pm /to 4pm
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [E][ ] meeting (from: 2pm to: 4pm)
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[D][ ] return book (by: Sunday)
2.[E][ ] meeting (from: 2pm to: 4pm)
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

### TC08: Bad mark/unmark must not change done status incorrectly

**Aim:** Out-of-range, non-numeric, and missing mark/unmark args leave existing status alone; valid marks still apply to the right tasks.

**Inputs:**
```text
todo task a
mark 2
mark 1
unmark 9
list
todo task b
mark abc
unmark
mark 2
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
  [T][ ] task a
Now you have 1 task in the list.
____________________________________________________________
____________________________________________________________
There is no task number 2 in your list.
____________________________________________________________
____________________________________________________________
Marked this task as done:
1.[T][X] task a
____________________________________________________________
____________________________________________________________
There is no task number 9 in your list.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][X] task a
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] task b
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
That task number doesn't look like a number: abc
____________________________________________________________
____________________________________________________________
Please give a task number, e.g. unmark 1
____________________________________________________________
____________________________________________________________
Marked this task as done:
2.[T][X] task b
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][X] task a
2.[T][X] task b
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

### TC09: Burst of negatives then one valid add

**Aim:** Several failed commands in a row leave the list empty; the first successful add is task 1.

**Inputs:**
```text
todo
deadline
event
mark
unmark 1
list
todo only real task
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
A todo needs a description — try: todo borrow book
____________________________________________________________
____________________________________________________________
A deadline needs details — try: deadline return book /by Sunday
____________________________________________________________
____________________________________________________________
An event needs details — try: event meeting /from Mon 2pm /to 4pm
____________________________________________________________
____________________________________________________________
Please give a task number, e.g. mark 1
____________________________________________________________
____________________________________________________________
There is no task number 1 in your list.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] only real task
Now you have 1 task in the list.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] only real task
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```
