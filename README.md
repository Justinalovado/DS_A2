# DS_A2

NOTE:
1. [x] create overhaulUpdate methods for board
2. [x] create overhaulUpdate method for chat
3. [ ] add file manipulation tool option
   - [x] New: white all, file pointer to nothing, enable all operation
   - [x] Save: If file pointer==null, to save as, else create new thread to read buffered img
   - [x] Save ass: prompt file chooser, if fp==null, set fp to new fp, else fp not change, create new file
   - [ ] close: keep client, but prompt them canvas close, disable all operation
4. [ ] add notif to client on file update
5. [ ] build runnable jar with 2 main
6. [ ] switch user name to CLI args
7. [ ] Create Join approved & reject
8. [x] put overhaul call to new join, file change(open)
9. [ ] make file operation synced on Image
10. [ ] ensure all GUI update are concurrent using invokelater
11. [ ] Ensure outbound call from GUI is done with separate thread(worker/invokelater)
12. [ ] make slider also adjust text size
13. [ ] window & canvas size adjustment, make display = canvas size