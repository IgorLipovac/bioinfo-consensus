bioinfo-consensus > ReAligner
=============================

This program is created as a project assignment for Bioinformatics class (Faculty of Electrical Engineering and Computing, University of Zagreb).
It implements iterative realigning algorithm described in:

**ReAligner: a program for refining DNA sequence multi-alignments.**
*Anson EL, Myers EW.*

Program requires Java installed.
Program usage - just call jar executable from terminal -> example:

*java -jar realigner.jar test_2/readsInput2.fasta test_2/layouts2.afg -e(some_double_value) -l(some_integer_value)*

Where -e parameter stands for epsilon - expected average layout error; and -i stands for maximum iteration number - we recommend epsilon of 0.05-0.1 and max iteration of 10-15 for test examples given. First parameter is always a path to file containing FASTA format reads and second parameter is path to file containing layout data from previous OLC phase (LAYOUT) given in AMOS/Minimus .afg format.

You can also try with example files given in test_1, test_2 and test_3 folders.

For some testing purposes we used scripts from: https://github.com/vzupanovic/skripte

More about the algorithm, results and program itself - see documentation (coming soon)!

And also - please support our friends:
https://www.facebook.com/24hprojectchallenge




