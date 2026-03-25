# Linux Assignment Notes

These are the commands and concepts I practiced during Linux assignments.

---

## Assignment 1: Basic Commands

Created directory using mkdir and navigated using cd.  
Used pwd to check current directory.  
Used ls to list files and folders.  
Checked help for ls using man ls.

---

## Assignment 2: File & Text Operations

Created file bio.txt and added details using vi editor.  
Saved file and searched content using / inside vi.

Deleted full content using:
Esc → gg → VG → d  
This selects full file and deletes it.

Used grep command:
- To search name in file  
- To show lines not containing name using -v  

---

## Assignment 3: Permissions

Checked permissions using ls -l.  
Changed permissions using chmod.

764 means:
- Owner → read, write, execute  
- Group → read, write  
- Others → read  

r = 4, w = 2, x = 1  

777 means full access to everyone, which is not safe because anyone can modify the file.

---

## Assignment 4: Networking

Checked IP address of system.  
IP address is a unique number used to identify a device in network.

Pinged google.com:
- Got IP address
- Packet loss was 0%

Used telnet to check port 80:
- Connection successful → port is open  
- Connection refused → port is closed  
- Connection timed out → no response  

Used curl to download webpage and saved it into a file.

---

## Assignment 5: System Monitoring

Used ps to see processes (one-time snapshot).  
Used top to monitor processes in real time.

Checked:
- Disk usage using df -h  
- Memory usage  

df -h shows total, used and available disk space in readable format.

---

## Assignment 6: Shell Scripting

Printed messages using echo and commands using $( ).

Took user input and printed using variables.

Used conditions to check number.  
Used loops to print numbers and table.

$(( )) is used for arithmetic operations.

---

## Assignment 7: Mini Project

Created a backup script:
- Creates backup folder  
- Takes date and time  
- Copies all .txt files  
- Prints message after completion  

Also handled case:
If folder already exists, it prints message instead of creating again.
