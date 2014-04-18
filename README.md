ServoController
===============

Simple android UI for sending commands to arduino servo controller. 

Designed to use a 'linvor' bluetooth module (Those cheapo ones from DX)

Usage
-----
Make sure you're paired to the module first.

Click connect to connect to 'linvor'. If linvor isn't found then it tries to pair to the first device in the paired devices list.

There are 4 sliders. Each slider updates at 5Hz, sending 'a' thru 'd' then the value from 0 to 64, then LF
The toggle buttons send 'A' thru 'D' then 0 or 1, then LF
The buttons send 'p' then '1' thru '4' then LF

Receieved data is displayed in a label.
