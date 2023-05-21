# CPD - Project 2

## Building

The project can be built by running `gradle build`.

## Running

The project can be ran using the command `java -cp build/classes/java/main pt.up.fe.cpd.proj2.Main --`,
followed by the flags required by the usecase.
These can be found in the `pt.up.fe.cpd.proj2.common.Config` class, but the most important ones are:
`-s` to run as a server, `-r` to use a ranked queue, and `-d` to run in debug mode.

