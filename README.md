# Use [mcredis](https://github.com/off-by-0point5/mcredis) not this repo!
---
---

## This readme describes the final state of the repo. It's still work in progress.

### Minecraft Redis
This repo aims to enable self configuration inside a minecraft server network, provide a basis for server switching systems and add simple player groups/parties. 

That means:
- Servers get discovered automatically
- All servers know the current network state
- They can display a simple server switching menu
  
It uses a [redis](https://redis.io/) server as shared database.
### How do I use it?
1. Install the plugin on your servers and proxies
1. You don't need to configure the servers in proxy config, but configure every server's in its own plugin config and the groups in the proxy's plugin config
1. Start your servers and proxies, they will find each other
