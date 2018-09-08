This is an attempt to build a graphical user interface for
editing SCXML finite state machines.

We are using the JGraphX library as a base and we started
by modifying the graph editor application included as an
example with JGraphX.

Main features:
  * edit of scxml networks
  * support for src and xi:include
  * search function using Apache Lucene
  * autolayout and possibility to save manual layout
  * export to DOT (graphviz) format
  * scxml listener that highlights and logs events as they happen during the finite state machine execution. (see https://github.com/bsivanantham/VirtualComputing/blob/master/extra/MySCXMLListener.java as an example of an Apache scxml listener class that sends the proper messages to the editor)

A [short guide](https://github.com/fmorbini/scxmlgui/blob/wiki/Guide.md) is available.

Here some **screenshots**:

SCXML complete File Loaded: 

![edge editing](https://github.com/bsivanantham/VirtualComputing/blob/master/extra/Screenshot%20from%202018-07-29%2023-32-37.png)

Code Editor Window :

![edge editing](https://github.com/bsivanantham/VirtualComputing/blob/master/extra/Screenshot%20from%202018-08-09%2022-45-14.png)

SCXML and Code Editor Window together (Code and SCML graph both can be edited and saved) :

![edge editing](https://github.com/bsivanantham/VirtualComputing/blob/master/extra/Screenshot%20from%202018-08-09%2022-45-04.png)

Editor window for edge properties:

![edge editing](https://github.com/bsivanantham/VirtualComputing/blob/master/extra/edge-editing.png)

Context menu to select edit operations on a node:

![node menu](https://github.com/bsivanantham/VirtualComputing/blob/master/extra/node-menu.png)

The find tool in action:

![find tool](https://github.com/bsivanantham/VirtualComputing/blob/master/extra/find-tool.png)


Source: https://github.com/fmorbini/scxmlgui