# intellij-remote-execute

## Introduction

This is a IntelliJ plugin designed to allow you to copy your code to a server and run it. 
The logs display within IntelliJ and you can even debug the running code!

## Compatibility
This plugin works in both Community Edition and Ultimate Edition of IntelliJ IDEA.

## Installation
The current process of installation is as follows:

1) Obtain the `intellij-remote-execute.jar` (version of your choosing)
    a) You may download the tagged versions here: <link>
    b) You can build this yourself! (instructions below)
2) Launch a compatible version of IntelliJ IDEA
3) Open `Preferences` 
4) Search/navigate to `Plugins`
5) Select `Install plugin from disk...`
6) Locate and select the `intellij-remote-execute.jar` you obtained
7) Make sure you hit `Apply` before you exit `Preferences`
8) IntelliJ IDEA will probably prompt you to restart, do so at your leisure :)

## Building this plugin
`./gradlew build`

You may have to set the execution permissions `gradlew` first:

`chmod +x gradlew`
 
Alternatively just run via your native `gradle`.

## Configuring the plugin
Once the plugin is successfully installed, you can configure it via `Preferences`.

You will find it at the bottom, under `Other Settings > Remote execution`.

There you can set:

- hostname (the server you are targeting)
- user (the user you will SSH into the server as)
- Java executable (the path to Java on the server)

## Invoking the remote executor
Simply right click the Java class you wish to run and 

## Bugs
Note that there is a bug around not being able to trigger remote execution if you have a local execution config saved.
To fix this, you have to delete the local execution config and then invoke the remote executor.

## Todo List
- Handle remote process more sensibly
   - terminating etc.
   - ability to find leaked processes?
- Saving settings across restart
- JVM args
- test on real project
- work out why saving/loading config doesnt work