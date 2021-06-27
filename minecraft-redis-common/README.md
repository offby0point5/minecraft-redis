### Plugin message channel
To send players or groups of players to servers and server groups,
we use plugin messaging channels.

A message looks like following:

- 1 Byte -> 0: to server, 1: to group
- remaining Bytes -> target group/server name ASCII encoded
