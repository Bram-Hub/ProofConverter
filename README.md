# ProofConverter

This software can be run using Java on the command line. To run this software, type "Java -jar proofConverter.jar command" into the command line. Listed below are the commands that work:

..* help
Returns a list of commands that can be used with this software.

..* parse arg1
Parses a .bram file into a readable text file.
arg1 should be the path to a .bram file using Fitch or Sequent.

..* parse arg1 arg2
Converts a .bram file from Fitch to Sequent to a .bram file of the other type, or vice versa.
arg1 should be the path to a .bram file using Fitch or Sequent that will be converted.
arg2 will be the path and file name for the new .bram file.

This software was developed by Chris Lipscomb and Max Wang for Intermediate Logic, taught by Bram van Heuveln at RPI.
