# DS_A2

### NOTE:
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

### Implementation checklist:
- [ ] Create a close opeartion
  1. clear current canvas (paint grey?)
  2. prompt clients of a server closure
  3. block all client using option pane(or simply not draw anything on board to avail the chat)
  4. On server restart, flip client state to allow board again
- [ ] Create client join gating
  1. Name Gating: prompt user to rejoin using another name (shutdown)
  2. Manager Gating: User send join request, approve-> send welcome, else send other message
  3. On client wait: prompt user waiting bar & quit button, block all operation, but allow server update
  4. Server handle user quit on waiting.
  5. Client handle server quit on waiting

### Refactoring
- [ ] Rename Announcer to Utility
- [ ] put buffered image serialisation method to utility
- [ ] Ensure concurrent operation on client list

### Misc Error handling 
- [ ] On server quit, all client should prompt quit
- [ ] On Client quit, server should remove user
- [ ] On all server -> Client operation fail: kick client, broadcast new user list
- [ ] On all client -> server operation fail: prompt connection lost & quit
- [ ] On server IO error, prompt msg & continue or quit

### Bugs
- [ ] when client click OK in waiting window, draw is allowed and error is allowed, fix to abort on wait
- [ ] when server rejects client, client does not automatically quit